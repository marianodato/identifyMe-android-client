package com.example.marianodato.identifyme_android_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marianodato.identifyme_android_client.model.User;
import com.example.marianodato.identifyme_android_client.model.UserLogin;
import com.example.marianodato.identifyme_android_client.model.UserResults;
import com.example.marianodato.identifyme_android_client.remote.APIUtils;
import com.example.marianodato.identifyme_android_client.remote.UserService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PreferenceKeys {

    private ListView listView;
    private UserAdapter userAdapter;
    private SwipeRefreshLayout swipeLayout;
    private SharedPreferences prefs;

    private UserService userService;
    private List<User> list = new ArrayList<User>();
    private static final int LIMIT = 10;
    private int total = 0;
    private int offset = 0;
    private boolean isLoadingList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Usuarios");
        setSupportActionBar(toolbar);

        swipeLayout = findViewById(R.id.swipeContainer);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsersList();
            }
        });
        swipeLayout.setColorSchemeColors(
                0xFFFF4081,
                0xFFFF4081,
                0xFFFF4081,
                0xFFFF4081
        );

        listView = findViewById(R.id.listView);
        userService = APIUtils.getUserService();

        getUsersList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUsersList();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAddUser:
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                intent.putExtra("isPutForm", false);
                startActivity(intent);
                break;

            case R.id.menuAbout:
                Toast.makeText(MainActivity.this, "Versión 1.0.0", Toast.LENGTH_LONG).show();
                break;

            case R.id.menuLogout:
                doLogout();
                break;
        }
        return true;
    }

    private void loadNextData() {
        swipeLayout.setRefreshing(true);
        Call<UserResults> call = userService.getUsers(prefs.getString(ACCESS_TOKEN_KEY, null), offset, LIMIT);
        call.enqueue(new Callback<UserResults>() {
            @Override
            public void onResponse(Call<UserResults> call, Response<UserResults> response) {
                if (response.isSuccessful()) {
                    total = response.body().getPaging().getTotal();
                    list = response.body().getResults();

                    for (int i = 0; i < list.size(); i++) {
                        userAdapter.add(list.get(i));
                    }

                    userAdapter.notifyDataSetChanged();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERROR: ", jObjError.getString("message"));
                        Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("ERROR: ", e.getMessage());
                        Toast.makeText(MainActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_LONG).show();
                    }
                }
                isLoadingList = false;
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<UserResults> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(MainActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_LONG).show();
                isLoadingList = false;
                swipeLayout.setRefreshing(false);
            }
        });
    }

    private void getUsersList() {
        swipeLayout.setRefreshing(true);
        listView.setVisibility(View.GONE);
        offset = 0;
        Call<UserResults> call = userService.getUsers(prefs.getString(ACCESS_TOKEN_KEY, null), offset, LIMIT);
        call.enqueue(new Callback<UserResults>() {
            @Override
            public void onResponse(Call<UserResults> call, Response<UserResults> response) {
                if(response.isSuccessful()){
                    total = response.body().getPaging().getTotal();
                    list = response.body().getResults();
                    userAdapter = new UserAdapter(MainActivity.this, R.layout.list_user, list);
                    listView.setAdapter(userAdapter);
                    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                        }

                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            // Si llego al final de la lista
                            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                                if (!isLoadingList) {
                                    int offsetCount = total / LIMIT;

                                    if (total % LIMIT != 0) {
                                        offsetCount++;
                                    }

                                    // Si hay mas informacion para traer
                                    if (offset + 1 < offsetCount) {
                                        offset++;
                                        isLoadingList = true;
                                        loadNextData();
                                    }
                                }
                            }
                        }
                    });
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERROR: ", jObjError.getString("message"));
                        Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("ERROR: ", e.getMessage());
                        Toast.makeText(MainActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_LONG).show();
                    }
                }
                swipeLayout.setRefreshing(false);
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<UserResults> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(MainActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_LONG).show();
                swipeLayout.setRefreshing(false);
                listView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void doLogout() {
        swipeLayout.setRefreshing(true);
        Call<UserLogin> call = userService.doLogout(prefs.getString(ACCESS_TOKEN_KEY, null));
        call.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
                    editor.remove(ACCESS_TOKEN_KEY);
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Sesión finalizada!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERROR: ", jObjError.getString("message"));
                        Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("ERROR: ", e.getMessage());
                        Toast.makeText(MainActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_LONG).show();
                    }
                }
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(MainActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_LONG).show();
                swipeLayout.setRefreshing(false);
            }
        });
    }
}
