package com.example.marianodato.identifyme_android_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.example.marianodato.identifyme_android_client.utils.CommonKeys;

public class ToolbarDialogFragment extends DialogFragment implements CommonKeys {

    private static NoticeDialogListener noticeDialogListener;
    private static int selectedItemIndex = 0;
    private static String dialogType;

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String dialogType, int selectedItemIndex);
        void onDialogNegativeClick(DialogFragment dialog, String dialogType, int selectedItemIndex);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            noticeDialogListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            Log.e(LOG_ERROR, e.getMessage());
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        CharSequence[] items = null;
        String title = null;
        if (bundle != null) {
            items = bundle.getCharSequenceArray(DIALOG_ITEMS);
            title = bundle.getString(DIALOG_TITLE);
            selectedItemIndex = bundle.getInt(DIALOG_SELECTED_INDEX);
            dialogType = bundle.getString(DIALOG_TYPE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setTitle(title)
                .setSingleChoiceItems(items, selectedItemIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selected) {
                                selectedItemIndex = selected;
                            }
                        })
                .setNegativeButton(getString(R.string.CANCELAR), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        noticeDialogListener.onDialogNegativeClick(ToolbarDialogFragment.this, dialogType, selectedItemIndex);
                    }
                })
                .setPositiveButton(getString(R.string.GUARDAR), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        noticeDialogListener.onDialogPositiveClick(ToolbarDialogFragment.this, dialogType, selectedItemIndex);
                    }
                });
        return builder.create();
    }
}
