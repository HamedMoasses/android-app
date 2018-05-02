package eu.h2020.sc.transport;

import android.util.Log;

import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.UpdatePictureRequest;
import eu.h2020.sc.utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Performs http operation
 * Created by fminori on 27/10/15.
 */
public class BinaryHttpTask extends HttpsConnectionSetUp {

    private final String TAG = this.getClass().getName();

    public void makeRequest(UpdatePictureRequest baseRequest) throws ConnectionException, ServerException, UnauthorizedException, NotFoundException {

        HttpsURLConnection conn = null;
        OutputStream requestOs = null;

        try {
            URL url = baseRequest.urllize();
            Log.i(TAG, "URL : " + url);
            conn = setUpConnection(url);
            setCredentials(conn);

            // PUT performs input only
            conn.setRequestMethod(HttpConstants.PUT);
            conn.setDoOutput(true);
            conn.setDoInput(false);

            conn.setRequestProperty(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_IMAGE_PNG);
            byte[] body = baseRequest.getPayload();
            Log.i(TAG, "PUT body: " + body);

            // FIXME pump content
            requestOs = new DataOutputStream(conn.getOutputStream());
            requestOs.write(body);
            requestOs.flush();


            // perform call
            int responseStatusCode = conn.getResponseCode();
            Log.i(TAG, String.format("Response status code: %d", responseStatusCode));

            switch (responseStatusCode) {
                case HttpURLConnection.HTTP_NO_CONTENT:
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    throw new ServerException();
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new UnauthorizedException();
                case HttpURLConnection.HTTP_NOT_FOUND:
                    throw new NotFoundException();
            }

        } catch (IOException e) {
            throw new ConnectionException("Problems during connection ...", e);
        } finally {
            Utils.closeQuietly(requestOs);
            if (conn != null)
                conn.disconnect();
        }


    }
}
