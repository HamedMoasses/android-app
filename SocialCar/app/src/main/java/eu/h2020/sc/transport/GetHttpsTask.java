package eu.h2020.sc.transport;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.utils.Utils;

/**
 * Performs get http operation
 * Created by fminori on 27/10/15.
 */
public class GetHttpsTask extends HttpsConnectionSetUp {

    protected final String TAG = this.getClass().getName();

    public String makeRequest(SocialCarRequest socialCarRequest) throws ConnectionException, ServerException, UnauthorizedException, NotFoundException {

        HttpsURLConnection conn = null;
        String responseContent = null;
        InputStream requestIS = null;

        try {
            URL url = socialCarRequest.urllize();
            Log.i(TAG, "URL : " + url);
            conn = setUpConnection(url);

            conn.setRequestMethod(HttpConstants.GET);
            conn.setDoOutput(false);
            conn.setDoInput(true);

            int responseStatusCode = conn.getResponseCode();
            Log.i(TAG, String.format("Response status code: %d", responseStatusCode));


            switch (responseStatusCode) {
                case HttpsURLConnection.HTTP_OK:
                    requestIS = conn.getInputStream();
                    responseContent = Utils.toString(requestIS);
                    Log.i(TAG, String.format("GET Response : %s", responseContent));
                    break;

                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                    throw new ServerException();

                case HttpsURLConnection.HTTP_BAD_GATEWAY:
                    throw new ServerException();

                case HttpConstants.UNPROCESSABLE_ENTITY:
                    Log.e(TAG, String.format("Response body: %s", Utils.toString(conn.getErrorStream())));
                    throw new ServerException();

                case HttpsURLConnection.HTTP_UNAUTHORIZED:
                    throw new UnauthorizedException();

                case HttpsURLConnection.HTTP_NOT_FOUND:
                    throw new NotFoundException();
            }
        } catch (IOException e) {
            throw new ConnectionException("Problems during connection ...", e);
        } finally {
            Utils.closeQuietly(requestIS);
            if (conn != null)
                conn.disconnect();
        }
        return responseContent;
    }


    public byte[] makeBinaryRequest(SocialCarRequest socialCarRequest) throws ConnectionException, ServerException, UnauthorizedException, NotFoundException {

        HttpsURLConnection conn = null;
        byte[] responseContent = null;
        InputStream requestIS = null;

        try {
            URL url = socialCarRequest.urllize();
            Log.i(TAG, "URL : " + url);
            conn = setUpConnection(url);
            setCredentials(conn);

            conn.setRequestMethod(HttpConstants.GET);
            conn.setDoOutput(false);
            conn.setDoInput(true);

            int responseStatusCode = conn.getResponseCode();
            Log.i(TAG, String.format("Response status code: %d", responseStatusCode));

            switch (responseStatusCode) {
                case HttpsURLConnection.HTTP_OK:
                    requestIS = conn.getInputStream();
                    responseContent = Utils.toByteArray(requestIS);
                    break;
                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                    throw new ServerException();

                case HttpsURLConnection.HTTP_UNAUTHORIZED:
                    throw new UnauthorizedException();
                case HttpsURLConnection.HTTP_NOT_FOUND:
                    throw new NotFoundException();
            }

        } catch (IOException e) {
            throw new ConnectionException("Problems during connection ...", e);
        } finally {
            Utils.closeQuietly(requestIS);
            if (conn != null)
                conn.disconnect();
        }

        return responseContent;

    }
}
