/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.transport;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.utils.Utils;

/**
 * Created by fminori on 12/09/16.
 */


public class PostHttpTask extends HttpsConnectionSetUp {

    protected final String TAG = this.getClass().getName();


    public String makeRequest(SocialCarRequest socialCarRequest) throws ConnectionException, ServerException, UnauthorizedException, ConflictException {

        HttpsURLConnection conn = null;
        Writer requestOS = null;
        String responseContent = null;
        InputStream requestIS = null;

        try {
            URL url = socialCarRequest.urllize();
            Log.i(TAG, "URL : " + url);
            conn = setUpConnection(url);
            setCredentials(conn);


            conn.setRequestMethod(HttpConstants.POST);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
            String body = socialCarRequest.getBody();
            Log.i(TAG, "POST body: " + body);

            requestOS = new OutputStreamWriter(conn.getOutputStream());
            requestOS.write(body);
            requestOS.flush();


            int responseStatusCode = conn.getResponseCode();
            Log.i(TAG, String.format("Response status code: %d", responseStatusCode));


            switch (responseStatusCode) {

                case HttpsURLConnection.HTTP_CREATED:
                    requestIS = conn.getInputStream();
                    responseContent = Utils.toString(requestIS);
                    break;

                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                    throw new ServerException();

                case HttpConstants.UNPROCESSABLE_ENTITY:
                    Log.i(TAG, String.format("Response body: %s", Utils.toString(conn.getErrorStream())));
                    throw new ServerException();

                case HttpsURLConnection.HTTP_UNAUTHORIZED:
                    throw new UnauthorizedException();


                case HttpsURLConnection.HTTP_CONFLICT:
                    throw new ConflictException();

                case HttpsURLConnection.HTTP_NOT_FOUND:
                    Log.e(TAG, "404 during POST? Check it!");
                    throw new ServerException();
            }


        } catch (IOException e) {
            throw new ConnectionException("Problems during connection ...", e);
        } finally {
            Utils.closeQuietly(requestIS);
            Utils.closeQuietly(requestOS);
            if (conn != null)
                conn.disconnect();
        }
        return responseContent;

    }


}

