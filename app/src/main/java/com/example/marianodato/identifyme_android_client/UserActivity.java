package com.example.marianodato.identifyme_android_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marianodato.identifyme_android_client.model.User;
import com.example.marianodato.identifyme_android_client.remote.APIUtils;
import com.example.marianodato.identifyme_android_client.remote.UserService;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    UserService userService;
    EditText edtUserId;
    EditText edtUserUsername;
    EditText edtUserPassword;
    EditText edtUserName;
    EditText edtUserDni;
    RadioGroup radioGrpUserGender;
    RadioButton radioUserGender;
    EditText edtUserEmail;
    EditText edtUserPhoneNumber;
    RadioGroup radioGrpUserIsAdmin;
    RadioButton radioUserIsAdmin;
    Button btnSave;
    Button btnDel;
    TextView txtUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        setTitle("identifyMe-android-client");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtUserId = (TextView) findViewById(R.id.txtUserId);
        edtUserId = (EditText) findViewById(R.id.edtUserId);
        edtUserUsername = (EditText) findViewById(R.id.edtUserUsername);
        edtUserPassword = (EditText) findViewById(R.id.edtUserPassword);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtUserDni = (EditText) findViewById(R.id.edtUserDni);
        radioGrpUserGender = (RadioGroup) findViewById(R.id.radioGrpUserGender);
        edtUserEmail = (EditText) findViewById(R.id.edtUserEmail);
        edtUserPhoneNumber = (EditText) findViewById(R.id.edtUserPhoneNumber);
        radioGrpUserIsAdmin = (RadioGroup) findViewById(R.id.radioGrpUserIsAdmin);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDel = (Button) findViewById(R.id.btnDel);

        userService = APIUtils.getUserService();

        Bundle extras = getIntent().getExtras();
        final String userLoginAccessToken = extras.getString("userLoginAccessToken");
        final String userLoginUsername = extras.getString("userLoginUsername");
        final String userId = extras.getString("userId");

        edtUserId.setText(userId);

        if(userId != null && userId.trim().length() > 0 ){
            edtUserId.setFocusable(false);
        } else {
            txtUserId.setVisibility(View.GONE);
            edtUserId.setVisibility(View.GONE);
            btnDel.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userUsername = edtUserUsername.getText().toString();
                String userPassword = edtUserPassword.getText().toString();
                String userName = edtUserName.getText().toString();
                String userDni = edtUserDni.getText().toString();
                String userEmail = edtUserEmail.getText().toString();
                String userPhoneNumber = edtUserPhoneNumber.getText().toString();
                int selectedId = radioGrpUserGender.getCheckedRadioButtonId();
                radioUserGender = (RadioButton) findViewById(selectedId);
                String userGender = radioUserGender.getText().equals("HOMBRE") ? "male" : "female";
                selectedId = radioGrpUserIsAdmin.getCheckedRadioButtonId();
                radioUserIsAdmin = (RadioButton) findViewById(selectedId);
                boolean userIsAdmin = radioUserIsAdmin.getText().equals("SÍ");

                if(userId != null && userId.trim().length() > 0){
                    //update user
                    User user = new User();
                    user.setUsername(userUsername);
                    updateUser(userLoginAccessToken, userLoginUsername, Long.parseLong(userId), user);
                } else {
                    if (validateCreateUserFields(userUsername, userPassword, userName, userDni, userEmail, userPhoneNumber)) {
                        //add user
                        User user = new User(userUsername, userPassword, userName, Long.parseLong(userDni), userGender, userEmail, userPhoneNumber, userIsAdmin);
                        addUser(userLoginAccessToken, userLoginUsername, user);
                    }
                }
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(userLoginAccessToken, userLoginUsername, Long.parseLong(userId));
            }
        });

    }

    private boolean validateCreateUserFields(String username, String password, String name, String dni, String email, String phoneNumber) {
        if (username == null || !username.matches("(?=^.{6,20}$)^[a-zA-Z][a-zA-Z0-9]*[._-]?[a-zA-Z0-9]+$")) {
            Toast.makeText(this, "Valor incorrecto para el campo usuario! Formato: Sólo un caracter especial (._-) permitido y no debe estar en los extremos. El primer caracter no puede ser numérico. Todos los demás caracteres permitidos son letras y números. La longitud total debe estar entre 6 y 20 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!#%*?&._-])[A-Za-z\\d$@!#%*?&._-]{8,}")) {
            Toast.makeText(this, "Valor incorrecto para el campo clave! Formato: Mínimo 8 caracteres, al menos 1 en mayúscula, 1 en minúscula, 1 número y 1 caracter especial", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.equals(password)) {
            Toast.makeText(this, "El usuario no puede ser igual a la clave!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name == null || name.trim().length() == 0) {
            Toast.makeText(this, "El campo nombre es obligatorio!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dni == null || dni.trim().length() == 0) {
            Toast.makeText(this, "El campo dni es obligatorio!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email == null || email.trim().length() == 0 || !email.contains("@")) {
            Toast.makeText(this, "Valor incorrecto para el campo email! Debes ingresar un mail válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneNumber == null || !phoneNumber.matches("[\\+]\\d{2}[\\(]\\d{2}[\\)]\\d{4}[\\-]\\d{4}")) {
            Toast.makeText(this, "Valor incorrecto para el campo telefono! Formato: +54(11)1234-5678", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void addUser(final String userLoginAccessToken, final String userLoginUsername, User user) {
        Call<User> call = userService.addUser(userLoginAccessToken, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserActivity.this, "Usuario creado exitosamente!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserActivity.this, MainActivity.class);
                    intent.putExtra("userLoginAccessToken", userLoginAccessToken);
                    intent.putExtra("userLoginUsername", userLoginUsername);
                    startActivity(intent);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERROR: ", jObjError.getString("message"));
                        Toast.makeText(UserActivity.this, jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("ERROR: ", e.getMessage());
                        Toast.makeText(UserActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(UserActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUser(final String userLoginAccessToken, final String userLoginUsername, long userId, User user) {
        Call<User> call = userService.updateUser(userId, userLoginAccessToken, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserActivity.this, "Usuario modificado exitosamente!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserActivity.this, MainActivity.class);
                    intent.putExtra("userLoginAccessToken", userLoginAccessToken);
                    intent.putExtra("userLoginUsername", userLoginUsername);
                    startActivity(intent);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERROR: ", jObjError.getString("message"));
                        Toast.makeText(UserActivity.this, jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("ERROR: ", e.getMessage());
                        Toast.makeText(UserActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(UserActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteUser(final String userLoginAccessToken, final String userLoginUsername, long userId) {
        Call<User> call = userService.deleteUser(userId, userLoginAccessToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserActivity.this, "Usuario eliminado exitosamente!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserActivity.this, MainActivity.class);
                    intent.putExtra("userLoginAccessToken", userLoginAccessToken);
                    intent.putExtra("userLoginUsername", userLoginUsername);
                    startActivity(intent);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.e("ERROR: ", jObjError.getString("message"));
                        Toast.makeText(UserActivity.this, jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("ERROR: ", e.getMessage());
                        Toast.makeText(UserActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                Toast.makeText(UserActivity.this, "Ups! Algo salio mal...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}