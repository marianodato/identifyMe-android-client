package com.example.marianodato.identifyme_android_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {

    ListView listView;

    UserService userService;
    List<User> list = new ArrayList<User>();
    String userLoginAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("identifyMe-android-client");
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        final String userLoginAccessToken = extras.getString("userLoginAccessToken");
        this.userLoginAccessToken = userLoginAccessToken;

        listView = findViewById(R.id.listView);
        userService = APIUtils.getUserService();

        getUsersList(userLoginAccessToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUsersList(userLoginAccessToken);
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
                intent.putExtra("userLoginAccessToken", userLoginAccessToken);
                startActivity(intent);
                break;

            case R.id.menuAbout:
                Toast.makeText(MainActivity.this, "Versión 1.0.0", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuLogout:
                doLogout(userLoginAccessToken);
                break;
        }
        return true;
    }

    private void getUsersList(final String userLoginAccessToken) {
        Call<UserResults> call = userService.getUsers(userLoginAccessToken, 0, 10);
        call.enqueue(new Callback<UserResults>() {
            @Override
            public void onResponse(Call<UserResults> call, Response<UserResults> response) {
                if(response.isSuccessful()){
                    list = response.body().getResults();
                    listView.setAdapter(new UserAdapter(MainActivity.this, R.layout.list_user, list, userLoginAccessToken));
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERROR: ", jObjError.getString("message"));
                        Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("ERROR: ", e.getMessage());
                        Toast.makeText(MainActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResults> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(MainActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doLogout(final String userLoginAccessToken) {
        Call<UserLogin> call = userService.doLogout(userLoginAccessToken);
        call.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Sesión finalizada!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERROR: ", jObjError.getString("message"));
                        Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("ERROR: ", e.getMessage());
                        Toast.makeText(MainActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(MainActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
