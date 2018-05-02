package eu.h2020.sc.transport;

import android.util.Log;

import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.SocialCarPictureRequest;
import eu.h2020.sc.utils.MultipartUtility;
import eu.h2020.sc.utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Performs http POST multipart operation
 * Created by Nicola on 12/10/16.
 */
public class MultipartPostTask extends HttpsConnectionSetUp {

    private final String TAG = this.getClass().getName();

    public String makeRequest(SocialCarPictureRequest socialCarPictureRequest) throws ConnectionException, ServerException, UnauthorizedException, NotFoundException, ConflictException {

        HttpsURLConnection conn = null;
        DataOutputStream requestOs = null;
        InputStream is = null;

        try {
            URL url = socialCarPictureRequest.urllize();
            Log.i(TAG, "URL : " + url);

            conn = setUpConnection(url);
            setCredentials(conn);

            conn.setRequestMethod(socialCarPictureRequest.getHttpMethod());
            conn.setDoOutput(true);
            conn.setDoInput(true);

            MultipartUtility multipartUtility = new MultipartUtility(conn);
            return multipartUtility.executeMultipartPost(socialCarPictureRequest.getMediaFileUri(), socialCarPictureRequest.getMultipartBodyKey(), socialCarPictureRequest.getResourceID());

        } catch (IOException e) {
            throw new ConnectionException("Problems during connection ...", e);
        } finally {
            Utils.closeQuietly(requestOs);
            Utils.closeQuietly(is);
            if (conn != null)
                conn.disconnect();
        }
    }
}
