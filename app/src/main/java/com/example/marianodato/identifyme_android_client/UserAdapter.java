package com.example.marianodato.identifyme_android_client;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.marianodato.identifyme_android_client.model.User;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> users;
    private String userLoginAccessToken;
    private String userLoginUsername;

    public UserAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> objects, @NonNull String userLoginAccessToken, String userLoginUsername) {
        super(context, resource, objects);
        this.context = context;
        this.users = objects;
        this.userLoginAccessToken = userLoginAccessToken;
        this.userLoginUsername = userLoginUsername;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_user, parent, false);

        TextView txtUserId = (TextView) rowView.findViewById(R.id.txtUserId);
        TextView txtUserUsername = (TextView) rowView.findViewById(R.id.txtUserUsername);
        TextView txtUserName = (TextView) rowView.findViewById(R.id.txtUserName);
        TextView txtUserFingerprintId = (TextView) rowView.findViewById(R.id.txtUserFingerprintId);
        TextView txtUserFingerprintStatus = (TextView) rowView.findViewById(R.id.txtUserFingerprintStatus);
        TextView txtUserDni = (TextView) rowView.findViewById(R.id.txtUserDni);
        TextView txtUserGender = (TextView) rowView.findViewById(R.id.txtUserGender);
        TextView txtUserEmail = (TextView) rowView.findViewById(R.id.txtUserEmail);
        TextView txtUserPhoneNumber = (TextView) rowView.findViewById(R.id.txtUserPhoneNumber);
        TextView txtUserIsAdmin = (TextView) rowView.findViewById(R.id.txtUserIsAdmin);
        TextView txtUserDateCreated = (TextView) rowView.findViewById(R.id.txtUserDateCreated);
        TextView txtUserLastUpdated = (TextView) rowView.findViewById(R.id.txtUserLastUpdated);

        txtUserId.setText(String.format("ID: %d", users.get(pos).getId()));
        txtUserUsername.setText(String.format("USUARIO: %s", users.get(pos).getUsername()));
        txtUserName.setText(String.format("NOMBRE: %s", users.get(pos).getName()));
        if (users.get(pos).getFingerprintId() != null) {
            txtUserFingerprintId.setText(String.format("ID HUELLA: %d", users.get(pos).getFingerprintId()));
        } else {
            txtUserFingerprintId.setText(String.format("ID HUELLA: -"));
        }

        if (users.get(pos).getFingerprintStatus().equals("enrolled")) {
            txtUserFingerprintStatus.setText(String.format("ESTADO DE HUELLA: %s", "Cargada"));
        } else if (users.get(pos).getFingerprintStatus().equals("pending")) {
            txtUserFingerprintStatus.setText(String.format("ESTADO DE HUELLA: %s", "Pendiente"));
        } else {
            txtUserFingerprintStatus.setText(String.format("ESTADO DE HUELLA: %s", "No cargada"));
        }

        txtUserDni.setText(String.format("DNI: %d", users.get(pos).getDni()));
        txtUserGender.setText(String.format("GÉNERO: %s", users.get(pos).getGender().equals("male") ? "Hombre" : "Mujer"));
        txtUserEmail.setText(String.format("EMAIL: %s", users.get(pos).getEmail()));
        txtUserPhoneNumber.setText(String.format("TELÉFONO: %s", users.get(pos).getPhoneNumber()));
        txtUserIsAdmin.setText(String.format("ADMIN: %s", users.get(pos).getAdmin() ? "Sí" : "No"));
        txtUserDateCreated.setText(String.format("CREADO: %s", users.get(pos).getDateCreated()));
        txtUserLastUpdated.setText(String.format("MODIFICADO: %s", users.get(pos).getLastUpdated()));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start Activity User Form
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("userLoginAccessToken", userLoginAccessToken);
                intent.putExtra("userLoginUsername", userLoginUsername);
                intent.putExtra("userId", String.valueOf(users.get(pos).getId()));
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}