package eu.h2020.sc.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import eu.h2020.sc.R;

/**
 * Created by Nicola on 18/03/2015.
 */
public class MaterialDialogHelper {

    public static MaterialDialog.Builder createDialogGotIt(Context context, int titleResource, int messageResource, int positiveButtonTextRes) {
        return new MaterialDialog.Builder(context)
                .title(titleResource)
                .content(messageResource)
                .positiveText(positiveButtonTextRes)
                .positiveColor(ContextCompat.getColor(context, R.color.primary_color));

    }

    public static MaterialDialog.Builder createDialog(Context context, int titleResource, int messageResource, int positiveButtonTextRes, int negativeButtonTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback) {
        return new MaterialDialog.Builder(context)
                .title(titleResource)
                .content(messageResource)
                .positiveText(positiveButtonTextRes)
                .negativeText(negativeButtonTextRes)
                .negativeColor(ContextCompat.getColor(context, R.color.primary_color))
                .positiveColor(ContextCompat.getColor(context, R.color.primary_color))
                .onPositive(positiveButtonCallback);
    }

    public static MaterialDialog.Builder createDialogOnNegative(Context context, int titleResource, int messageResource, int positiveButtonTextRes, int negativeButtonTextRes, MaterialDialog.SingleButtonCallback positiveButtonCallback, MaterialDialog.SingleButtonCallback negativeButtonCallback) {
        return new MaterialDialog.Builder(context)
                .title(titleResource)
                .content(messageResource)
                .positiveText(positiveButtonTextRes)
                .negativeText(negativeButtonTextRes)
                .negativeColor(ContextCompat.getColor(context, R.color.primary_color))
                .positiveColor(ContextCompat.getColor(context, R.color.primary_color))
                .onPositive(positiveButtonCallback)
                .onNegative(negativeButtonCallback);
    }

    public static MaterialDialog.Builder createDialogLongContent(Context context, int titleResource, int messageResource, int positiveButtonTextRes, MaterialDialog.ButtonCallback popup_callback) {
        return new MaterialDialog.Builder(context)
                .title(titleResource)
                .content(messageResource)
                .positiveText(positiveButtonTextRes)
                .callback(popup_callback);

    }

    public static MaterialDialog createProgressDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .content("Loading...")
                .progress(true, 0)
                .widgetColor(ContextCompat.getColor(context, R.color.primary_color))
                .build();

    }

    public static MaterialDialog createCustomViewDialog(Context context, int titleResource, int customView) {
        return new MaterialDialog.Builder(context)
                .title(titleResource)
                .customView(customView, true)
                .theme(Theme.LIGHT)
                .build();
    }

    public static MaterialDialog createSingleChoice(Context context, int titleResource, String[] array, int choose, MaterialDialog.ListCallbackSingleChoice callback) {
        return new MaterialDialog.Builder(context)
                .title(titleResource)
                .widgetColor(ContextCompat.getColor(context, R.color.primary_color))
                .items(array)
                .itemsCallbackSingleChoice(0, callback)
                .positiveText(choose)
                .positiveColorRes(R.color.primary_color)
                .build();
    }

}
