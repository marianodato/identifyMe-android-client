package com.example.marianodato.identifyme_android_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marianodato.identifyme_android_client.model.UserLogin;
import com.example.marianodato.identifyme_android_client.remote.APIUtils;
import com.example.marianodato.identifyme_android_client.remote.UserService;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtUserLoginUsername;
    EditText edtUserLoginPassword;
    Button btnLogin;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("identifyMe-android-client");

        edtUserLoginUsername = (EditText) findViewById(R.id.edtUserLoginUsername);
        edtUserLoginPassword = (EditText) findViewById(R.id.edtUserLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        userService = APIUtils.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userLoginUsername = edtUserLoginUsername.getText().toString();
                String userLoginPassword = edtUserLoginPassword.getText().toString();
                //validate form
                if (validateLoginFields(userLoginUsername, userLoginPassword)) {
                    UserLogin userLoginRequest = new UserLogin(userLoginUsername, userLoginPassword);
                    //do login
                    doLogin(userLoginRequest);
                }
            }
        });

    }

    private boolean validateLoginFields(String username, String password) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(this, "El campo usuario es obligatorio!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(this, "El campo clave es obligatorio!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doLogin(final UserLogin userLoginRequest) {
        Call<UserLogin> call = userService.login(userLoginRequest);
        call.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                if (response.isSuccessful()) {
                    UserLogin userLoginResponse = (UserLogin) response.body();
                    //login start main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userLoginUsername", userLoginRequest.getUsername());
                    intent.putExtra("userLoginAccessToken", userLoginResponse.getAccessToken());
                    startActivity(intent);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERROR: ", jObjError.getString("message"));
                        Toast.makeText(LoginActivity.this, jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("ERROR: ", e.getMessage());
                        Toast.makeText(LoginActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(LoginActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
