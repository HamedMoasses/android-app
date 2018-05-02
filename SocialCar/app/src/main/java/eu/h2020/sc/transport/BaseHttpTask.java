/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.transport;

import android.os.AsyncTask;
import android.util.Log;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.config.Globals;
import eu.h2020.sc.domain.Credentials;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.utils.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fminori on 12/09/16.
 */

public abstract class BaseHttpTask extends AsyncTask<SocialCarRequest, Void, HttpTaskResult> {

    protected final String TAG = this.getClass().getName();

    @Override
    protected HttpTaskResult doInBackground(SocialCarRequest... socialCarRequests) {

        HttpTaskResult taskResult = null;
        HttpURLConnection conn = null;
        SocialCarRequest request = socialCarRequests[0];

        try {
            URL url = request.urllize();
            Log.i(TAG, "URL : " + url);
            conn = setUpConnection(url);
            setCredentials(conn);
            taskResult = doCall(conn, request);
        } catch (IOException e) {
            Log.e(TAG, "No internet connection...", e);
            taskResult = null;
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return taskResult;
    }


    protected abstract HttpTaskResult doCall(HttpURLConnection connection, SocialCarRequest socialCarRequest);


    private static void setCredentials(HttpURLConnection conn) {
        String username = SocialCarApplication.getSharedPreferences().getString(Globals.EMAIL_KEY, null);
        String password = SocialCarApplication.getSharedPreferences().getString(Globals.PASSWORD_KEY, null);

        Credentials credentials = new Credentials(username, password);
        conn.setRequestProperty(HttpConstants.AUTHORIZATION, credentials.getBasicAuthenticationCredentials());
    }


    private static HttpURLConnection setUpConnection(URL url) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty(HttpConstants.CONNECTION, HttpConstants.CONNECTION_VALUE);
        conn.setRequestProperty(HttpConstants.CACHE_CONTROL, HttpConstants.CACHE_CONTROL_VALUE);
        conn.setUseCaches(false);
        conn.setConnectTimeout(HttpConstants.TIMEOUT_TIME);
        conn.setRequestProperty(HttpConstants.USER_AGENT, String.format("%s %s", Globals.SOCIALCAR_APP_VERSION, Utils.getAndroidVersion()));
        return conn;
    }

}
