package eu.h2020.sc.protocol;

import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class FindUserPictureRequest extends SocialCarRequest {

    public static final String TAG = FindUserPictureRequest.class.getSimpleName();

    private String userID;

    public FindUserPictureRequest(String userID) {
        this.userID = userID;
    }

    @Override
    public URL urllize() {

        final String PICTURE_URI = "/pictures/users/";
        final String GET_USER_PICTURE_URI = String.format("%s%s%s", SystemConfiguration.SERVER_PATH_VERSION, PICTURE_URI, this.userID);

        try {
            StringBuilder stringBuilder = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilder.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilder.append(SystemConfiguration.CONTEXT_ROOT).append(GET_USER_PICTURE_URI);

            return new URL(stringBuilder.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
            return null;
        }
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public String getApiName() {
        return TAG;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.GET;
    }

}
