package com.example.marianodato.identifyme_android_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marianodato.identifyme_android_client.model.UserLogin;
import com.example.marianodato.identifyme_android_client.remote.APIUtils;
import com.example.marianodato.identifyme_android_client.remote.UserService;
import com.example.marianodato.identifyme_android_client.utils.CommonKeys;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.marianodato.identifyme_android_client.remote.APIUtils.onFailureGenericLogic;
import static com.example.marianodato.identifyme_android_client.remote.APIUtils.onResponseErrorGenericLogic;

public class LoginActivity extends AppCompatActivity implements CommonKeys {

    private EditText edtUserLoginUsername;
    private EditText edtUserLoginPassword;
    private Button btnLogin;

    private static UserService userService;

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
        toolbar.setTitle(getString(R.string.INICIAR_SESION));
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
                } else {
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnLogin.setEnabled(true);
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
            Toast.makeText(this, getString(R.string.CAMPO_USUARIO_OBLIGATORIO), Toast.LENGTH_LONG).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(this, getString(R.string.CAMPO_CLAVE_OBLIGATORIO), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void doLogin(final UserLogin userLoginRequest) {
        Call<UserLogin> call = userService.doLogin(userLoginRequest);
        call.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(@NonNull Call<UserLogin> call, @NonNull Response<UserLogin> response) {
                if (response.isSuccessful()) {
                    UserLogin userLoginResponse = response.body();

                    SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
                    editor.putString(ACCESS_TOKEN_KEY, userLoginResponse.getAccessToken());
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    onResponseErrorGenericLogic(LoginActivity.this, response, false);
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnLogin.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserLogin> call, @NonNull Throwable t) {
                onFailureGenericLogic(LoginActivity.this, t);
                btnLogin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnLogin.setEnabled(true);
            }
        });
    }
}
