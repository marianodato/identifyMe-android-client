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
import com.example.marianodato.identifyme_android_client.utils.CommonKeys;

import java.util.List;

class UserAdapter extends ArrayAdapter<User> implements CommonKeys {

    private final Context context;
    private final List<User> users;

    public UserAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        this.context = context;
        this.users = objects;
    }

    @NonNull
    @Override
    public View getView(final int pos, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_user, parent, false);

        TextView txtUserUsername = rowView.findViewById(R.id.txtUserUsername);
        TextView txtUserName = rowView.findViewById(R.id.txtUserName);
        TextView txtUserFingerprintStatus = rowView.findViewById(R.id.txtUserFingerprintStatus);
        TextView txtUserIsAdmin = rowView.findViewById(R.id.txtUserIsAdmin);

        txtUserUsername.setText(context.getString(R.string.USUARIO_VALOR, users.get(pos).getUsername()));
        txtUserName.setText(context.getString(R.string.NOMBRE_VALOR, users.get(pos).getName()));

        switch (users.get(pos).getFingerprintStatus()) {
            case STATUS_ENROLLED:
                txtUserFingerprintStatus.setText(context.getString(R.string.ESTADO_HUELLA_CARGADA));
                break;
            case STATUS_PENDING:
                txtUserFingerprintStatus.setText(context.getString(R.string.ESTADO_HUELLA_PENDIENTE));
                break;
            default:
                txtUserFingerprintStatus.setText(context.getString(R.string.ESTADO_HUELLA_NO_CARGADA));
                break;
        }

        txtUserIsAdmin.setText(context.getString(R.string.ADMIN_VALOR, users.get(pos).getAdmin() ? context.getString(R.string.SI) : context.getString(R.string.NO)));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserActivity.class);

                intent.putExtra(USER_ID, String.valueOf(users.get(pos).getId()));
                intent.putExtra(USER_USERNAME, String.valueOf(users.get(pos).getUsername()));
                intent.putExtra(USER_PASSWORD, String.valueOf(users.get(pos).getPassword()));
                intent.putExtra(USER_NAME, String.valueOf(users.get(pos).getName()));
                intent.putExtra(USER_FINGERPRINT_ID, users.get(pos).getFingerprintId() != null ? String.valueOf(users.get(pos).getFingerprintId()) : context.getString(R.string.GUION));
                intent.putExtra(USER_FINGERPRINT_STATUS, String.valueOf(users.get(pos).getFingerprintStatus()));
                intent.putExtra(USER_DNI, String.valueOf(users.get(pos).getDni()));
                intent.putExtra(USER_GENDER, String.valueOf(users.get(pos).getGender()));
                intent.putExtra(USER_EMAIL, String.valueOf(users.get(pos).getEmail()));
                intent.putExtra(USER_PHONE_NUMBER, String.valueOf(users.get(pos).getPhoneNumber()));
                intent.putExtra(USER_IS_ADMIN, users.get(pos).getAdmin());
                intent.putExtra(USER_DATE_CREATED, String.valueOf(users.get(pos).getDateCreated()));
                intent.putExtra(USER_LAST_UPDATED, String.valueOf(users.get(pos).getLastUpdated()));
                intent.putExtra(IS_PUT_FORM, true);
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}
