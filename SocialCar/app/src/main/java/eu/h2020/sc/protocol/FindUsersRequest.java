package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by fminori on 13/06/16.
 */
public class FindUsersRequest extends SocialCarRequest {

    private static final String TAG = FindUsersRequest.class.getSimpleName();
    private static final String USERS_URI = "/users?";

    private Map<String, String> parameters;

    public FindUsersRequest(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public URL urllize() {
        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT)
                    .append(SystemConfiguration.CONTEXT_ROOT)
                    .append(SystemConfiguration.SERVER_PATH_VERSION).append(USERS_URI);

            Uri.Builder builder = Uri.parse(stringBuilderInitial.toString()).buildUpon();

            for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }

            return new URL(builder.build().toString());

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
