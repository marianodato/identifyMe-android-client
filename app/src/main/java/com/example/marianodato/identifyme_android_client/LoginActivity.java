package com.example.marianodato.identifyme_android_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marianodato.identifyme_android_client.model.UserLogin;
import com.example.marianodato.identifyme_android_client.remote.APIUtils;
import com.example.marianodato.identifyme_android_client.remote.UserService;
import com.example.marianodato.identifyme_android_client.utils.PreferenceKeys;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements PreferenceKeys {

    private EditText edtUserLoginUsername;
    private EditText edtUserLoginPassword;
    private Button btnLogin;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        if (prefs.getString(ACCESS_TOKEN_KEY, null) != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Iniciar sesión");
        setSupportActionBar(toolbar);

        edtUserLoginUsername = findViewById(R.id.edtUserLoginUsername);
        edtUserLoginPassword = findViewById(R.id.edtUserLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        userService = APIUtils.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setBackgroundColor(Color.GRAY);
                btnLogin.setEnabled(false);
                String userLoginUsername = edtUserLoginUsername.getText().toString();
                String userLoginPassword = edtUserLoginPassword.getText().toString();

                if (validateLoginFields(userLoginUsername, userLoginPassword)) {
                    UserLogin userLoginRequest = new UserLogin(userLoginUsername, userLoginPassword);
                    doLogin(userLoginRequest);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnLogin.setEnabled(true);
    }

    private boolean validateLoginFields(String username, String password) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(this, "El campo usuario es obligatorio!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(this, "El campo clave es obligatorio!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void doLogin(final UserLogin userLoginRequest) {
        Call<UserLogin> call = userService.doLogin(userLoginRequest);
        call.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                if (response.isSuccessful()) {
                    UserLogin userLoginResponse = response.body();

                    SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
                    editor.putString(ACCESS_TOKEN_KEY, userLoginResponse.getAccessToken());
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERROR: ", jObjError.getString("message"));
                        Toast.makeText(LoginActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("ERROR: ", e.getMessage());
                        Toast.makeText(LoginActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_LONG).show();
                    }
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnLogin.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(LoginActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_LONG).show();
                btnLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnLogin.setEnabled(true);
            }
        });
    }
}
