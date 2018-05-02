package eu.h2020.sc.protocol;

import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class FindMediaRequest extends SocialCarRequest {

    public static final String TAG = FindMediaRequest.class.getSimpleName();

    private String mediaUri;

    public FindMediaRequest(String mediaUri) {
        this.mediaUri = mediaUri;
    }

    @Override
    public URL urllize() {

        try {
            StringBuilder stringBuilder = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilder.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilder.append(SystemConfiguration.CONTEXT_ROOT).append(this.mediaUri);

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
