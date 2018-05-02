package eu.h2020.sc.protocol;

import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class CreateUserRequest extends SocialCarRequest {

    public static final String SIGN_UP_URI = "/users";
    private static final String TAG = CreateUserRequest.class.getSimpleName();
    private User user;

    public CreateUserRequest(User user) {
        this.user = user;
    }

    @Override
    public URL urllize() {

        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT)
                    .append(SystemConfiguration.CONTEXT_ROOT).append(SystemConfiguration.SERVER_PATH_VERSION)
                    .append(SIGN_UP_URI);

            return new URL(stringBuilderInitial.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }

        return null;
    }

    @Override
    public String getBody() {
        return this.user.toJsonForRequest();
    }

    @Override
    public String getApiName() {
        return TAG;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.POST;
    }

}
