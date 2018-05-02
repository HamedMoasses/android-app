package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.UserType;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class FindFeedbackRequest extends SocialCarRequest {

    private static final String TAG = FindFeedbackRequest.class.getSimpleName();

    private static final String FEEDBACK_URI = "/feedbacks";

    private String userID;
    private UserType userType;

    public FindFeedbackRequest(String userID, UserType userType) {
        this.userID = userID;
        this.userType = userType;
    }

    @Override
    public URL urllize() {
        try {
            StringBuilder stringBuilder = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilder.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT).append(SystemConfiguration.CONTEXT_ROOT);

            String userFeedbackURI = String.format("%s%s/users/%s%s", SystemConfiguration.SERVER_PATH_VERSION, FEEDBACK_URI, this.userID, this.getPathUserType(this.userType));
            stringBuilder.append(userFeedbackURI);

            String finalUrl = Uri.parse(stringBuilder.toString()).buildUpon()
                    .build().toString();

            return new URL(finalUrl);

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
            return null;
        }
    }

    private Object getPathUserType(UserType userType) {
        if (userType.equals(UserType.DRIVER))
            return String.format("/%s", User.DRIVER);

        return String.format("/%s", User.PASSENGER);
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
