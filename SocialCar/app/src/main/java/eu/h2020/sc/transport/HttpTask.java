package eu.h2020.sc.transport;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.config.Globals;
import eu.h2020.sc.domain.Credentials;
import eu.h2020.sc.protocol.FindMediaRequest;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.utils.Utils;

/**
 * Performs http operation
 * Created by fminori on 27/10/15.
 */
public class HttpTask extends AsyncTask<SocialCarRequest, Void, HttpTaskResult> {

    // TODO do we still need this !!

    private final String TAG = this.getClass().getName();
    private HttpTaskListener listener;

    public HttpTask(HttpTaskListener httpTaskListener) {
        this.listener = httpTaskListener;
    }

    @Override
    protected HttpTaskResult doInBackground(SocialCarRequest... socialCarRequests) {

        HttpTaskResult taskResult = new HttpTaskResult();
        HttpsURLConnection conn = null;
        Writer requestOs = null;

        String httpMethod = socialCarRequests[0].getHttpMethod();
        URL url = socialCarRequests[0].urllize();

        String apiName = socialCarRequests[0].getApiName();

        Log.i(TAG, "URL : " + url);

        try {
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty(HttpConstants.CONNECTION, HttpConstants.CONNECTION_VALUE);
            conn.setRequestProperty(HttpConstants.CACHE_CONTROL, HttpConstants.CACHE_CONTROL_VALUE);
            conn.setUseCaches(false);
            conn.setConnectTimeout(HttpConstants.TIMEOUT_TIME);
            conn.setRequestProperty(HttpConstants.USER_AGENT, String.format("%s %s", Globals.SOCIALCAR_APP_VERSION, Utils.getAndroidVersion()));


            // extract credentials
            String username = SocialCarApplication.getSharedPreferences().getString(Globals.EMAIL_KEY, null);
            String password = SocialCarApplication.getSharedPreferences().getString(Globals.PASSWORD_KEY, null);

            Credentials credentials = new Credentials(username, password);
            conn.setRequestProperty(HttpConstants.AUTHORIZATION, credentials.getBasicAuthenticationCredentials());


            // DO INPUT/OUTPUT

            conn.setRequestMethod(httpMethod);
            if (httpMethod.equals(HttpConstants.POST) || httpMethod.equals(HttpConstants.PUT)) {

                conn.setRequestProperty(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
                conn.setDoOutput(true);

                String body = socialCarRequests[0].getBody();
                Log.i(TAG, "POST body: " + body);

                requestOs = new OutputStreamWriter(conn.getOutputStream());
                requestOs.write(body);
                requestOs.flush();
            }

            taskResult.setHttpStatusCode(conn.getResponseCode());
            Log.i(TAG, "RESPONSE STATUS CODE : " + conn.getResponseCode() + "");


            if (taskResult.getHttpStatusCode() == HttpsURLConnection.HTTP_OK && apiName.equals(FindMediaRequest.TAG)) {

                InputStream is = conn.getInputStream();

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead = 0;
                byte[] data = new byte[2048];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                byte[] image = buffer.toByteArray();
                taskResult.setPictureData(image);
            }

            if (taskResult.getHttpStatusCode() == HttpsURLConnection.HTTP_OK || taskResult.getHttpStatusCode() == HttpsURLConnection.HTTP_CREATED) {

                Reader responseIs = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();

                char[] buffer = new char[8192];
                int count;
                while ((count = responseIs.read(buffer)) > 0) {
                    sb.append(buffer, 0, count);
                }
                taskResult.setContent(sb.toString());
            }


        } catch (IOException e) {
            Log.e(TAG, "No internet connection..." + e.getMessage());
            taskResult = null;

        } finally {
            closeQuietly(requestOs);
            if (conn != null)
                conn.disconnect();
        }

        return taskResult;

    }

    @Override
    protected void onPostExecute(HttpTaskResult taskResult) {

        if (taskResult != null) {
            try {
                switch (taskResult.getHttpStatusCode()) {

                    case HttpsURLConnection.HTTP_OK:
                        this.listener.onSuccess(taskResult);
                        break;

                    case HttpsURLConnection.HTTP_NO_CONTENT:
                        this.listener.onNoContent();
                        break;

                    case HttpsURLConnection.HTTP_CREATED:
                        this.listener.onCreated(taskResult);
                        break;

                    case HttpsURLConnection.HTTP_CONFLICT:
                        this.listener.onConflict();
                        break;

                    case HttpsURLConnection.HTTP_UNAUTHORIZED:
                        this.listener.onUnauthorized();
                        break;

                    case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                        this.listener.onInternalServerError();
                        break;

                    case HttpsURLConnection.HTTP_NOT_FOUND:
                        this.listener.onNotFound();
                        break;

                    default:
                        this.listener.onInternalServerError();
                        break;
                }

            } catch (Exception e) {
                Log.e(TAG, "OnPostExecute error...", e);
            }
        } else
            this.listener.onConnectionError();
    }

    private static void closeQuietly(Closeable closeable) {

        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException e) {
                // does nothing ...
            }
    }

}
