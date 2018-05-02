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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by fminori on 12/09/16.
 */

public class DeleteHttpTask extends HttpsConnectionSetUp {

    protected final String TAG = this.getClass().getName();


    public Void makeRequest(SocialCarRequest socialCarRequest) throws ConnectionException, ServerException, UnauthorizedException, NotFoundException {

        HttpsURLConnection conn = null;

        try {
            URL url = socialCarRequest.urllize();
            Log.i(TAG, "URL : " + url);
            conn = setUpConnection(url);
            setCredentials(conn);

            conn.setRequestMethod(HttpConstants.DELETE);
            conn.setDoOutput(false);
            conn.setDoInput(false);


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
            if (conn != null)
                conn.disconnect();
        }
        return null;
    }
}
