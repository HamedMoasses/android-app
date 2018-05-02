/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.transport;

import android.util.Log;

import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.utils.Utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by fminori on 12/09/16.
 */

public class PutHttpTask extends HttpsConnectionSetUp {

    protected final String TAG = this.getClass().getName();

    public void makeRequest(SocialCarRequest socialCarRequest) throws ConnectionException, ServerException, UnauthorizedException, NotFoundException {

        HttpsURLConnection conn = null;
        Writer requestOS = null;

        try {
            URL url = socialCarRequest.urllize();
            Log.i(TAG, "URL : " + url);
            conn = setUpConnection(url);
            setCredentials(conn);

            // PUT performs input only
            conn.setRequestMethod(HttpConstants.PUT);
            conn.setDoOutput(true);
            conn.setDoInput(false);

            conn.setRequestProperty(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
            String body = socialCarRequest.getBody();
            Log.i(TAG, "PUT body: " + body);

            requestOS = new OutputStreamWriter(conn.getOutputStream());
            requestOS.write(body);
            requestOS.flush();


            // perform call

            int responseStatusCode = conn.getResponseCode();
            Log.i(TAG, String.format("Response status code: %d", responseStatusCode));


            switch (responseStatusCode) {
                case HttpsURLConnection.HTTP_NO_CONTENT:
                    break;
                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                    throw new ServerException();
                case HttpConstants.UNPROCESSABLE_ENTITY:
                    Log.i(TAG, String.format("Response body: %s", Utils.toString(conn.getErrorStream() )));
                    throw new ServerException();
                case HttpsURLConnection.HTTP_UNAUTHORIZED:
                    throw new UnauthorizedException();
                case HttpsURLConnection.HTTP_NOT_FOUND:
                    throw new NotFoundException();
            }

        } catch (IOException e) {
            throw new ConnectionException("Problems during connection ...", e);
        } finally {
            Utils.closeQuietly(requestOS);
            if (conn != null)
                conn.disconnect();
        }


    }
}