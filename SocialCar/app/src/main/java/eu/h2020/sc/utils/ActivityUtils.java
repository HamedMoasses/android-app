package eu.h2020.sc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by fminori on 14/06/16.
 */
public class ActivityUtils {

    public static void openActivity(Activity activity, Class activityToOpen) {
        Intent intent = new Intent(activity, activityToOpen);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }

    public static void openActivityDeleteBack(Activity activity, Class activityToOpen) {
        Intent intent = new Intent(activity, activityToOpen);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void openActivityWithParam(Activity activity, Class aClass, String key, Integer value) {
        Intent intent = new Intent(activity, aClass);
        Bundle mBundle = new Bundle();
        mBundle.putInt(key, value);
        intent.putExtras(mBundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        activity.startActivity(intent);
    }


    public static void openActivityWithParam(Activity activity, Class aClass, String key, String value) {
        Intent intent = new Intent(activity, aClass);
        Bundle mBundle = new Bundle();
        mBundle.putString(key, value);
        intent.putExtras(mBundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }


    public static void openActivityWithObjectParams(Activity activity, Class aClass, HashMap<String, Serializable> params) {
        Intent intent = new Intent(activity, aClass);
        Bundle b = new Bundle();

        for (String key : params.keySet())
            b.putSerializable(key, params.get(key));

        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void openActivityWithObjectParam(Activity activity, Class aClass, String key, Parcelable object) {
        Intent intent = new Intent(activity, aClass);
        Bundle b = new Bundle();
        b.putParcelable(key, object);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void openActivityNoBack(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }


    public static Intent generatePushIntent(Context context, Class aClass, String key, Serializable serializableObj) {
        Intent intent = new Intent(context, aClass);
        Bundle b = new Bundle();
        b.putSerializable(key, serializableObj);
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        return intent;
    }

    public static Intent generatePassengerPushIntent(Context context, Class aClass, String key, Integer tabToOpen) {
        Intent intent = new Intent(context, aClass);
        Bundle b = new Bundle();
        b.putInt(key, tabToOpen);
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        return intent;
    }

    public static Intent buildChatIntent(Context context, Class aClass, String key, String value) {
        Intent intent = new Intent(context, aClass);
        Bundle mBundle = new Bundle();
        mBundle.putString(key, value);
        intent.putExtras(mBundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        return intent;
    }

    public static Serializable getSerializableFromIntent(Activity activity, String key) {
        return activity.getIntent().getSerializableExtra(key);
    }

    public static Parcelable getParcelableFromIntent(Activity activity, String key) {
        return activity.getIntent().getParcelableExtra(key);
    }


    public static void openApplicationDetailsSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void openBrowserAtURL(String url, Activity activity) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }
}
