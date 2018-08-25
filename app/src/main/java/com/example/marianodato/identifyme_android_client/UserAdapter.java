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

    public UserAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> objects, @NonNull String userLoginAccessToken) {
        super(context, resource, objects);
        this.context = context;
        this.users = objects;
        this.userLoginAccessToken = userLoginAccessToken;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_user, parent, false);

        TextView txtUserId = rowView.findViewById(R.id.txtUserId);
        TextView txtUserUsername = rowView.findViewById(R.id.txtUserUsername);
        TextView txtUserName = rowView.findViewById(R.id.txtUserName);
        TextView txtUserFingerprintId = rowView.findViewById(R.id.txtUserFingerprintId);
        TextView txtUserFingerprintStatus = rowView.findViewById(R.id.txtUserFingerprintStatus);
        TextView txtUserDni = rowView.findViewById(R.id.txtUserDni);
        TextView txtUserGender = rowView.findViewById(R.id.txtUserGender);
        TextView txtUserEmail = rowView.findViewById(R.id.txtUserEmail);
        TextView txtUserPhoneNumber = rowView.findViewById(R.id.txtUserPhoneNumber);
        TextView txtUserIsAdmin = rowView.findViewById(R.id.txtUserIsAdmin);
        TextView txtUserDateCreated = rowView.findViewById(R.id.txtUserDateCreated);
        TextView txtUserLastUpdated = rowView.findViewById(R.id.txtUserLastUpdated);

        txtUserId.setText(String.format("Id: %d", users.get(pos).getId()));
        txtUserUsername.setText(String.format("Usuario: %s", users.get(pos).getUsername()));
        txtUserName.setText(String.format("Nombre: %s", users.get(pos).getName()));
        if (users.get(pos).getFingerprintId() != null) {
            txtUserFingerprintId.setText(String.format("Id de huella: %d", users.get(pos).getFingerprintId()));
        } else {
            txtUserFingerprintId.setText("Id de huella: -");
        }

        if (users.get(pos).getFingerprintStatus().equals("enrolled")) {
            txtUserFingerprintStatus.setText("Estado de huella: Cargada");
        } else if (users.get(pos).getFingerprintStatus().equals("pending")) {
            txtUserFingerprintStatus.setText("Estado de huella: Pendiente");
        } else {
            txtUserFingerprintStatus.setText("Estado de huella: No cargada");
        }

        txtUserDni.setText(String.format("Dni: %d", users.get(pos).getDni()));
        txtUserGender.setText(String.format("Género: %s", users.get(pos).getGender().equals("male") ? "Hombre" : "Mujer"));
        txtUserEmail.setText(String.format("Email: %s", users.get(pos).getEmail()));
        txtUserPhoneNumber.setText(String.format("Teléfono: %s", users.get(pos).getPhoneNumber()));
        txtUserIsAdmin.setText(String.format("Admin: %s", users.get(pos).getAdmin() ? "Sí" : "No"));
        txtUserDateCreated.setText(String.format("Creado: %s", users.get(pos).getDateCreated()));
        txtUserLastUpdated.setText(String.format("Modificado: %s", users.get(pos).getLastUpdated()));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("userLoginAccessToken", userLoginAccessToken);

                intent.putExtra("userId", String.valueOf(users.get(pos).getId()));
                intent.putExtra("userUsername", String.valueOf(users.get(pos).getUsername()));
                intent.putExtra("userPassword", String.valueOf(users.get(pos).getPassword()));
                intent.putExtra("userName", String.valueOf(users.get(pos).getName()));
                intent.putExtra("userFingerprintId", users.get(pos).getFingerprintId() != null ? String.valueOf(users.get(pos).getFingerprintId()) : "-");
                intent.putExtra("userFingerprintStatus", String.valueOf(users.get(pos).getFingerprintStatus()));
                intent.putExtra("userDni", String.valueOf(users.get(pos).getDni()));
                intent.putExtra("userGender", String.valueOf(users.get(pos).getGender()));
                intent.putExtra("userEmail", String.valueOf(users.get(pos).getEmail()));
                intent.putExtra("userPhoneNumber", String.valueOf(users.get(pos).getPhoneNumber()));
                intent.putExtra("userIsAdmin", users.get(pos).getAdmin());
                intent.putExtra("userDateCreated", String.valueOf(users.get(pos).getDateCreated()));
                intent.putExtra("userLastUpdated", String.valueOf(users.get(pos).getLastUpdated()));
                intent.putExtra("isPutForm", true);
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}