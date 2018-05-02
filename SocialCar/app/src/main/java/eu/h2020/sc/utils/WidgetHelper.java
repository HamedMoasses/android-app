package eu.h2020.sc.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import eu.h2020.sc.R;

/**
 * Created by Nicola on 18/03/2015.
 */
public class WidgetHelper {

    public static void showToast(Context context, int message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }

    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }

    public static void showSnackbar(Context context, CoordinatorLayout v, String message, String messageAction, View.OnClickListener onClickListener, Snackbar.Callback callback) {
        Snackbar snackbar = Snackbar
                .make(v, message, Snackbar.LENGTH_LONG)
                .setCallback(callback)
                .setAction(messageAction, onClickListener);

        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.accent_color));
        snackbar.show();
    }
}
