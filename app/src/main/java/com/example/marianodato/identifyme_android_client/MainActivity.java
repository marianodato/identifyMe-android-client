package com.example.marianodato.identifyme_android_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marianodato.identifyme_android_client.model.User;
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

    TextView txtUserLoginUsername;
    Button btnAddUser;
    Button btnGetUsersList;
    ListView listView;

    UserService userService;
    List<User> list = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("identifyMe-android-client");

        txtUserLoginUsername = (TextView) findViewById(R.id.txtUserLoginUsername);

        Bundle extras = getIntent().getExtras();
        final String userLoginAccessToken = extras.getString("userLoginAccessToken");
        final String userLoginUsername = extras.getString("userLoginUsername");
        txtUserLoginUsername.setText("Bienvenido " + userLoginUsername);

        btnAddUser = (Button) findViewById(R.id.btnAddUser);
        btnGetUsersList = (Button) findViewById(R.id.btnGetUsersList);
        listView = (ListView) findViewById(R.id.listView);
        userService = APIUtils.getUserService();

        btnGetUsersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsersList(userLoginAccessToken, userLoginUsername);
            }
        });

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                intent.putExtra("userLoginAccessToken", userLoginAccessToken);
                intent.putExtra("userLoginUsername", userLoginUsername);
                startActivity(intent);
            }
        });
    }

    public void getUsersList(final String userLoginAccessToken, final String userLoginUsername) {
        Call<UserResults> call = userService.getUsers(userLoginAccessToken, 0, 10);
        call.enqueue(new Callback<UserResults>() {
            @Override
            public void onResponse(Call<UserResults> call, Response<UserResults> response) {
                if(response.isSuccessful()){
                    list = response.body().getResults();
                    listView.setAdapter(new UserAdapter(MainActivity.this, R.layout.list_user, list, userLoginAccessToken, userLoginUsername));
                    Toast.makeText(MainActivity.this, "Lista actualizada!", Toast.LENGTH_SHORT).show();
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
}
