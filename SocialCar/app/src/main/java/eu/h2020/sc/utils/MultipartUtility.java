package eu.h2020.sc.utils;

import android.net.Uri;
import android.util.Log;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class MultipartUtility {

    private static final String TAG = MultipartUtility.class.getName();
    private static final String ATTACHMENT_NAME = "file";
    private HttpURLConnection httpConn;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @throws IOException
     */
    public MultipartUtility(HttpURLConnection httpConn)
            throws IOException {
        this.httpConn = httpConn;
    }

    /**
     * Completes the multipart request
     *
     * @throws IOException
     */
    public String executeMultipartPost(Uri uploadFileUri, String multipartValueKey, String resourceID) throws IOException {

        MultipartEntity reqEntity = this.generateMultipartEntity(uploadFileUri, multipartValueKey, resourceID);

        this.httpConn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());

        OutputStream outputStream = this.httpConn.getOutputStream();
        reqEntity.writeTo(this.httpConn.getOutputStream());

        outputStream.close();
        this.httpConn.connect();

        if (this.httpConn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {

            Log.i(TAG, "Response status code: " + this.httpConn.getResponseCode());

            return readStream(this.httpConn.getInputStream());

        } else {

            this.httpConn.disconnect();
            Log.e("Multipart response : ", Utils.toString(this.httpConn.getErrorStream()));
            throw new IOException("Server returned non-CREATED status: " + this.httpConn.getResponseCode());
        }
    }

    private MultipartEntity generateMultipartEntity(Uri uploadFileUri, String multipartValueKey, String resourceID) throws IOException {

        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart(multipartValueKey, new StringBody(resourceID));
        reqEntity.addPart(ATTACHMENT_NAME, new FileBody(new File(uploadFileUri.getPath())));

        return reqEntity;
    }

    private String readStream(InputStream in) throws IOException {

        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
}
