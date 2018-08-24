package com.example.marianodato.identifyme_android_client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    TextView txtUserId;
    TextView txtUserUsername;
    TextView txtUserPassword;
    TextView txtUserName;
    TextView txtUserFingerprintId;
    TextView txtUserFingerprintStatus;
    TextView txtUserDni;
    TextView txtUserGender;
    TextView txtUserEmail;
    TextView txtUserPhoneNumber;
    TextView txtUserIsAdmin;
    TextView txtUserDateCreated;
    TextView txtUserLastUpdated;
    EditText edtUserId;
    EditText edtUserUsername;
    EditText edtUserPassword;
    EditText edtUserName;
    EditText edtUserFingerprintId;
    EditText edtUserDni;
    EditText edtUserEmail;
    EditText edtUserPhoneNumber;
    EditText edtUserDateCreated;
    EditText edtUserLastUpdated;
    RadioGroup radioGrpUserGender;
    RadioGroup radioGrpUserIsAdmin;
    RadioGroup radioGrpUserFingerprintStatus;
    RadioButton radioUserGender;
    RadioButton radioUserIsAdmin;
    RadioButton radioUserFingerprintStatus;
    Button btnSave;
    Button btnDel;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("identifyMe-android-client");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtUserId = findViewById(R.id.txtUserId);
        txtUserUsername = findViewById(R.id.txtUserUsername);
        txtUserPassword = findViewById(R.id.txtUserPassword);
        txtUserName = findViewById(R.id.txtUserName);
        txtUserFingerprintId = findViewById(R.id.txtUserFingerprintId);
        txtUserFingerprintStatus = findViewById(R.id.txtUserFingerprintStatus);
        txtUserDni = findViewById(R.id.txtUserDni);
        txtUserGender = findViewById(R.id.txtUserGender);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        txtUserPhoneNumber = findViewById(R.id.txtUserPhoneNumber);
        txtUserIsAdmin = findViewById(R.id.txtUserIsAdmin);
        txtUserDateCreated = findViewById(R.id.txtUserDateCreated);
        txtUserLastUpdated = findViewById(R.id.txtUserLastUpdated);
        edtUserId = findViewById(R.id.edtUserId);
        edtUserUsername = findViewById(R.id.edtUserUsername);
        edtUserPassword = findViewById(R.id.edtUserPassword);
        edtUserName = findViewById(R.id.edtUserName);
        edtUserFingerprintId = findViewById(R.id.edtUserFingerprintId);
        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtUserPhoneNumber = findViewById(R.id.edtUserPhoneNumber);
        edtUserDni = findViewById(R.id.edtUserDni);
        edtUserDateCreated = findViewById(R.id.edtUserDateCreated);
        edtUserLastUpdated = findViewById(R.id.edtUserLastUpdated);
        radioGrpUserGender = findViewById(R.id.radioGrpUserGender);
        radioGrpUserIsAdmin = findViewById(R.id.radioGrpUserIsAdmin);
        radioGrpUserFingerprintStatus = findViewById(R.id.radioGrpUserFingerprintStatus);
        btnSave = findViewById(R.id.btnSave);
        btnDel = findViewById(R.id.btnDel);

        userService = APIUtils.getUserService();

        Bundle extras = getIntent().getExtras();
        final String userLoginAccessToken = extras.getString("userLoginAccessToken");
        final String userId = extras.getString("userId");
        final String userUsername = extras.getString("userUsername");
        final String userPassword = extras.getString("userPassword");
        final String userName = extras.getString("userName");
        final String userFingerprintId = extras.getString("userFingerprintId");
        final String userFingerprintStatus = extras.getString("userFingerprintStatus");
        final String userDni = extras.getString("userDni");
        final String userGender = extras.getString("userGender");
        final String userEmail = extras.getString("userEmail");
        final String userPhoneNumber = extras.getString("userPhoneNumber");
        final boolean userIsAdmin = extras.getBoolean("userIsAdmin");
        final String userDateCreated = extras.getString("userDateCreated");
        final String userLastUpdated = extras.getString("userLastUpdated");

        final boolean isPutForm = extras.getBoolean("isPutForm");

        if (isPutForm) {
            edtUserId.setText(userId);
            edtUserId.setFocusable(false);
            edtUserUsername.setText(userUsername);
            edtUserUsername.setFocusable(false);
            edtUserPassword.setText(userPassword);
            edtUserName.setText(userName);
            edtUserFingerprintId.setText(userFingerprintId);
            edtUserFingerprintId.setFocusable(false);
            edtUserEmail.setText(userEmail);
            edtUserPhoneNumber.setText(userPhoneNumber);
            edtUserDni.setText(userDni);
            edtUserDateCreated.setText(userDateCreated);
            edtUserDateCreated.setFocusable(false);
            edtUserLastUpdated.setText(userLastUpdated);
            edtUserLastUpdated.setFocusable(false);

            if (userGender.equals("male")) {
                radioGrpUserGender.check(R.id.radioGenderMale);
            } else {
                radioGrpUserGender.check(R.id.radioGenderFemale);
            }

            if (userIsAdmin) {
                radioGrpUserIsAdmin.check(R.id.radioIsAdmin);
            } else {
                radioGrpUserIsAdmin.check(R.id.radioIsNotAdmin);
            }

            if (userFingerprintStatus.equals("enrolled")) {
                radioGrpUserFingerprintStatus.check(R.id.radioFingerprintStatusEnrolled);
            } else if (userFingerprintStatus.equals("pending")) {
                radioGrpUserFingerprintStatus.check(R.id.radioFingerprintStatusPending);
            } else {
                radioGrpUserFingerprintStatus.check(R.id.radioFingerprintStatusUnenrolled);
            }

        } else {
            txtUserId.setVisibility(View.GONE);
            edtUserId.setVisibility(View.GONE);
            txtUserFingerprintId.setVisibility(View.GONE);
            edtUserFingerprintId.setVisibility(View.GONE);
            txtUserFingerprintStatus.setVisibility(View.GONE);
            radioGrpUserFingerprintStatus.setVisibility(View.GONE);
            txtUserDateCreated.setVisibility(View.GONE);
            edtUserDateCreated.setVisibility(View.GONE);
            txtUserLastUpdated.setVisibility(View.GONE);
            edtUserLastUpdated.setVisibility(View.GONE);
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
                radioUserGender = findViewById(selectedId);
                String userGender = radioUserGender.getText().equals("HOMBRE") ? "male" : "female";
                selectedId = radioGrpUserIsAdmin.getCheckedRadioButtonId();
                radioUserIsAdmin = findViewById(selectedId);
                boolean userIsAdmin = radioUserIsAdmin.getText().equals("SÍ");

                if (isPutForm) {
                    selectedId = radioGrpUserFingerprintStatus.getCheckedRadioButtonId();
                    radioUserFingerprintStatus = findViewById(selectedId);
                    String userFingerprintStatus;
                    if (radioUserFingerprintStatus.getText().equals("CARGADA")) {
                        userFingerprintStatus = "enrolled";
                    } else if (radioUserFingerprintStatus.getText().equals("PENDIENTE")) {
                        userFingerprintStatus = "pending";
                    } else {
                        userFingerprintStatus = "unenrolled";
                    }
                    if (validateUserFields(userUsername, userPassword, userName, userDni, userEmail, userPhoneNumber, isPutForm)) {
                        userPassword = userPassword.equals("null") ? null : userPassword;
                        User user = new User(userPassword, userName, Long.parseLong(userDni), userGender, userEmail, userPhoneNumber, userIsAdmin, userFingerprintStatus);
                        updateUser(userLoginAccessToken, Long.parseLong(userId), user);
                    }
                } else {
                    if (validateUserFields(userUsername, userPassword, userName, userDni, userEmail, userPhoneNumber, isPutForm)) {
                        User user = new User(userUsername, userPassword, userName, Long.parseLong(userDni), userGender, userEmail, userPhoneNumber, userIsAdmin);
                        addUser(userLoginAccessToken, user);
                    }
                }
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(userLoginAccessToken, Long.parseLong(userId));
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

    private boolean validateUserFields(String username, String password, String name, String dni, String email, String phoneNumber, boolean isPutForm) {
        if (username == null || !username.matches("(?=^.{6,20}$)^[a-zA-Z][a-zA-Z0-9]*[._-]?[a-zA-Z0-9]+$")) {
            Toast.makeText(this, "Valor incorrecto para el campo usuario! Formato: Sólo un caracter especial (._-) permitido y no debe estar en los extremos. El primer caracter no puede ser numérico. Todos los demás caracteres permitidos son letras y números. La longitud total debe estar entre 6 y 20 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isPutForm || !password.equals("null")) {
            if (password == null || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!#%*?&._-])[A-Za-z\\d$@!#%*?&._-]{8,}")) {
                Toast.makeText(this, "Valor incorrecto para el campo clave! Formato: Mínimo 8 caracteres, al menos 1 en mayúscula, 1 en minúscula, 1 número y 1 caracter especial", Toast.LENGTH_SHORT).show();
                return false;
            }
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

    private void addUser(final String userLoginAccessToken, User user) {
        Call<User> call = userService.addUser(userLoginAccessToken, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserActivity.this, "Usuario creado exitosamente!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
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

    private void updateUser(final String userLoginAccessToken, long userId, User user) {
        Call<User> call = userService.updateUser(userId, userLoginAccessToken, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserActivity.this, "Usuario modificado exitosamente!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
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

    private void deleteUser(final String userLoginAccessToken, long userId) {
        Call<User> call = userService.deleteUser(userId, userLoginAccessToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserActivity.this, "Usuario eliminado exitosamente!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
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
}