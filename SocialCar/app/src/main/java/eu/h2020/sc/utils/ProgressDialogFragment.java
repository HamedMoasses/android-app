package eu.h2020.sc.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;

import eu.h2020.sc.R;

/**
 * Created by Pietro on 13/06/16.
 */
public class ProgressDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.message_progress_dialog));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);

        // Disable the back button
        OnKeyListener keyListener = new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                return keyCode == KeyEvent.KEYCODE_BACK;
            }

        };
        dialog.setOnKeyListener(keyListener);
        return dialog;
    }
}
