package com.example.marianodato.identifyme_android_client;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.example.marianodato.identifyme_android_client.utils.CommonKeys;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.marianodato.identifyme_android_client.remote.APIUtils.onFailureGenericLogic;
import static com.example.marianodato.identifyme_android_client.remote.APIUtils.onResponseErrorGenericLogic;

public class UserActivity extends AppCompatActivity implements CommonKeys {

    private EditText edtUserUsername;
    private EditText edtUserPassword;
    private EditText edtUserName;
    private EditText edtUserDni;
    private EditText edtUserEmail;
    private EditText edtUserPhoneNumber;
    private RadioGroup radioGrpUserGender;
    private RadioGroup radioGrpUserIsAdmin;
    private RadioGroup radioGrpUserFingerprintStatus;
    private RadioButton radioUserGender;
    private RadioButton radioUserIsAdmin;
    private RadioButton radioUserFingerprintStatus;
    private Button btnSave;
    private Button btnDel;

    private static SharedPreferences prefs;
    private static UserService userService;

    private static final String NULL_STRING = "null";
    private static final String USERNAME_REGEX = "(?=^.{6,20}$)^[a-zA-Z][a-zA-Z0-9]*[._-]?[a-zA-Z0-9]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!#%*?&._-])[A-Za-z\\d$@!#%*?&._-]{8,}";
    private static final String PHONE_NUMBER_REGEX = "[\\+]\\d{2}[\\(]\\d{2}[\\)]\\d{4}[\\-]\\d{4}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        final boolean isPutForm = extras.getBoolean(IS_PUT_FORM);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (isPutForm) {
            toolbar.setTitle(getString(R.string.MODIFICAR_USUARIO));
        } else {
            toolbar.setTitle(getString(R.string.NUEVO_USUARIO));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView txtUserId = findViewById(R.id.txtUserId);
        TextView txtUserFingerprintId = findViewById(R.id.txtUserFingerprintId);
        TextView txtUserFingerprintStatus = findViewById(R.id.txtUserFingerprintStatus);
        TextView txtUserDateCreated = findViewById(R.id.txtUserDateCreated);
        TextView txtUserLastUpdated = findViewById(R.id.txtUserLastUpdated);
        EditText edtUserId = findViewById(R.id.edtUserId);
        edtUserUsername = findViewById(R.id.edtUserUsername);
        edtUserPassword = findViewById(R.id.edtUserPassword);
        edtUserName = findViewById(R.id.edtUserName);
        EditText edtUserFingerprintId = findViewById(R.id.edtUserFingerprintId);
        edtUserEmail = findViewById(R.id.edtUserEmail);
        edtUserPhoneNumber = findViewById(R.id.edtUserPhoneNumber);
        edtUserDni = findViewById(R.id.edtUserDni);
        EditText edtUserDateCreated = findViewById(R.id.edtUserDateCreated);
        EditText edtUserLastUpdated = findViewById(R.id.edtUserLastUpdated);
        radioGrpUserGender = findViewById(R.id.radioGrpUserGender);
        radioGrpUserIsAdmin = findViewById(R.id.radioGrpUserIsAdmin);
        radioGrpUserFingerprintStatus = findViewById(R.id.radioGrpUserFingerprintStatus);
        btnSave = findViewById(R.id.btnSave);
        btnDel = findViewById(R.id.btnDel);

        userService = APIUtils.getUserService();

        final String userId = extras.getString(USER_ID);
        final String userUsername = extras.getString(USER_USERNAME);
        final String userPassword = extras.getString(USER_PASSWORD);
        final String userName = extras.getString(USER_NAME);
        final String userFingerprintId = extras.getString(USER_FINGERPRINT_ID);
        final String userFingerprintStatus = extras.getString(USER_FINGERPRINT_STATUS);
        final String userDni = extras.getString(USER_DNI);
        final String userGender = extras.getString(USER_GENDER);
        final String userEmail = extras.getString(USER_EMAIL);
        final String userPhoneNumber = extras.getString(USER_PHONE_NUMBER);
        final boolean userIsAdmin = extras.getBoolean(USER_IS_ADMIN);
        final String userDateCreated = extras.getString(USER_DATE_CREATED);
        final String userLastUpdated = extras.getString(USER_LAST_UPDATED);

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

            if (userGender.equals(MALE_GENDER)) {
                radioGrpUserGender.check(R.id.radioGenderMale);
            } else {
                radioGrpUserGender.check(R.id.radioGenderFemale);
            }

            if (userIsAdmin) {
                radioGrpUserIsAdmin.check(R.id.radioIsAdmin);
            } else {
                radioGrpUserIsAdmin.check(R.id.radioIsNotAdmin);
            }

            switch (userFingerprintStatus) {
                case STATUS_ENROLLED:
                    radioGrpUserFingerprintStatus.check(R.id.radioFingerprintStatusEnrolled);
                    break;
                case STATUS_PENDING:
                    radioGrpUserFingerprintStatus.check(R.id.radioFingerprintStatusPending);
                    break;
                default:
                    radioGrpUserFingerprintStatus.check(R.id.radioFingerprintStatusUnenrolled);
                    break;
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
                btnSave.setBackgroundColor(Color.GRAY);
                btnSave.setEnabled(false);
                String userUsername = edtUserUsername.getText().toString();
                String userPassword = edtUserPassword.getText().toString();
                String userName = edtUserName.getText().toString();
                String userDni = edtUserDni.getText().toString();
                String userEmail = edtUserEmail.getText().toString();
                String userPhoneNumber = edtUserPhoneNumber.getText().toString();
                int selectedId = radioGrpUserGender.getCheckedRadioButtonId();
                radioUserGender = findViewById(selectedId);
                String userGender = radioUserGender.getText().equals(getString(R.string.HOMBRE)) ? MALE_GENDER : FEMALE_GENDER;
                selectedId = radioGrpUserIsAdmin.getCheckedRadioButtonId();
                radioUserIsAdmin = findViewById(selectedId);
                boolean userIsAdmin = radioUserIsAdmin.getText().equals(getString(R.string.SI));

                if (isPutForm) {
                    selectedId = radioGrpUserFingerprintStatus.getCheckedRadioButtonId();
                    radioUserFingerprintStatus = findViewById(selectedId);
                    String userFingerprintStatus;
                    if (radioUserFingerprintStatus.getText().equals(getString(R.string.CARGADA))) {
                        userFingerprintStatus = STATUS_ENROLLED;
                    } else if (radioUserFingerprintStatus.getText().equals(getString(R.string.PENDIENTE))) {
                        userFingerprintStatus = STATUS_PENDING;
                    } else {
                        userFingerprintStatus = STATUS_UNENROLLED;
                    }
                    if (validateUserFields(userUsername, userPassword, userName, userDni, userEmail, userPhoneNumber, true)) {
                        userPassword = userPassword.equals(NULL_STRING) ? null : userPassword;
                        User user = new User(userPassword, userName, Long.parseLong(userDni), userGender, userEmail, userPhoneNumber, userIsAdmin, userFingerprintStatus);
                        updateUser(Long.parseLong(userId), user);
                    }
                } else {
                    if (validateUserFields(userUsername, userPassword, userName, userDni, userEmail, userPhoneNumber, false)) {
                        User user = new User(userUsername, userPassword, userName, Long.parseLong(userDni), userGender, userEmail, userPhoneNumber, userIsAdmin);
                        addUser(user);
                    }
                }

                btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                btnSave.setEnabled(true);
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDel.setBackgroundColor(Color.GRAY);
                btnDel.setEnabled(false);
                deleteUser(Long.parseLong(userId));
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
        if (username == null || !username.matches(USERNAME_REGEX)) {
            Toast.makeText(this, getString(R.string.VALOR_INCORRECTO_CAMPO_USUARIO), Toast.LENGTH_LONG).show();
            return false;
        }

        if (!isPutForm || !password.equals(NULL_STRING)) {
            if (password == null || !password.matches(PASSWORD_REGEX)) {
                Toast.makeText(this, getString(R.string.VALOR_INCORRECTO_CAMPO_CLAVE), Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (username.equals(password)) {
            Toast.makeText(this, getString(R.string.USUARIO_NO_PUEDE_SER_IGUAL_CLAVE), Toast.LENGTH_LONG).show();
            return false;
        }
        if (name == null || name.trim().length() == 0) {
            Toast.makeText(this, getString(R.string.CAMPO_NOMBRE_OBLIGATORIO), Toast.LENGTH_LONG).show();
            return false;
        }
        if (dni == null || dni.trim().length() == 0) {
            Toast.makeText(this, getString(R.string.CAMPO_DNI_OBLIGATORIO), Toast.LENGTH_LONG).show();
            return false;
        }
        if (email == null || email.trim().length() == 0 || !email.contains("@")) {
            Toast.makeText(this, getString(R.string.CAMPO_EMAIL_INCORRECTO), Toast.LENGTH_LONG).show();
            return false;
        }
        if (phoneNumber == null || !phoneNumber.matches(PHONE_NUMBER_REGEX)) {
            Toast.makeText(this, getString(R.string.CAMPO_TELEFONO_INCORRECTO), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void addUser(User user) {
        Call<User> call = userService.addUser(prefs.getString(ACCESS_TOKEN_KEY, null), user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserActivity.this, getString(R.string.USUARIO_CREADO), Toast.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    onResponseErrorGenericLogic(UserActivity.this, response, true);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    btnSave.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                onFailureGenericLogic(UserActivity.this, t);
                btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                btnSave.setEnabled(true);
            }
        });
    }

    private void updateUser(long userId, User user) {
        Call<User> call = userService.updateUser(userId, prefs.getString(ACCESS_TOKEN_KEY, null), user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserActivity.this, getString(R.string.USUARIO_MODIFICADO), Toast.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    onResponseErrorGenericLogic(UserActivity.this, response, true);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    btnSave.setEnabled(true);
                    btnDel.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    btnDel.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                onFailureGenericLogic(UserActivity.this, t);
                btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                btnSave.setEnabled(true);
                btnDel.setBackgroundColor(getResources().getColor(R.color.colorRed));
                btnDel.setEnabled(true);
            }
        });
    }

    private void deleteUser(long userId) {
        Call<User> call = userService.deleteUser(userId, prefs.getString(ACCESS_TOKEN_KEY, null));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UserActivity.this, getString(R.string.USUARIO_ELIMINADO), Toast.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    onResponseErrorGenericLogic(UserActivity.this, response, true);
                    btnDel.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    btnDel.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                onFailureGenericLogic(UserActivity.this, t);
                btnDel.setBackgroundColor(getResources().getColor(R.color.colorRed));
                btnDel.setEnabled(true);
            }
        });
    }
}
