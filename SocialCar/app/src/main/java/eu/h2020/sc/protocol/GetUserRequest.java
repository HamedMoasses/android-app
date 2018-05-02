package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implement Read User Api
 * <p>
 * Created by
 * Fabio Lombardi <fabio.lombardi@movenda.com> on 05/09/2016.
 * © All rights reserved by Movenda S.p.A..
 */
public class GetUserRequest extends SocialCarRequest {

    private static final String TAG = GetUserRequest.class.getSimpleName();
    public static final String USER_READ_URI = "/users";

    private String userID;

    public GetUserRequest(String userID) {
        this.userID = userID;
    }

    @Override
    public URL urllize() {
        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilderInitial.append(SystemConfiguration.CONTEXT_ROOT).append(SystemConfiguration.SERVER_PATH_VERSION).append(USER_READ_URI);

            String finalUrl = Uri.parse(stringBuilderInitial.toString()).buildUpon()
                    .appendEncodedPath(this.userID)
                    .build().toString();

            return new URL(finalUrl);

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }
        return null;
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
