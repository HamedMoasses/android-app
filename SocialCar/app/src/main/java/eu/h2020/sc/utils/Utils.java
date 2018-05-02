package eu.h2020.sc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import eu.h2020.sc.SocialCarApplication;

public class Utils {

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) SocialCarApplication.getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    public static void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) SocialCarApplication.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public static String getAndroidVersion() {
        return String.format("Android/%s", Build.VERSION.RELEASE);
    }

    public static int fromDptoPx(int dpMeasure, Context context) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpMeasure,
                r.getDisplayMetrics()
        );
        return px;
    }

    public static boolean isAfterKitKatVersion() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT);
    }

    public static void closeQuietly(Closeable closeable) {

        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException e) {
                // does nothing ...
            }
    }

    public static String toString(InputStream is) throws IOException {
        Reader responseIs = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();

        char[] buffer = new char[8192];
        int count;
        while ((count = responseIs.read(buffer)) > 0) {
            sb.append(buffer, 0, count);
        }

        return sb.toString();
    }


    public static byte[] toByteArray(InputStream is) throws IOException {


        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead = 0;
        byte[] data = new byte[8192];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    public static boolean hasInternetConnection(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }

        return false;
    }
}
