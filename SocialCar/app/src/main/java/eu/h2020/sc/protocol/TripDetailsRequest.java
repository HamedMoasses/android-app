package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */
public class TripDetailsRequest extends SocialCarRequest {

    private static final String TAG = TripDetailsRequest.class.getSimpleName();

    public static final String URI = "trips/";

    private String tripID;

    public TripDetailsRequest(String tripID) {
        this.tripID = tripID;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.GET;
    }

    @Override
    public URL urllize() {
        try {
            StringBuilder sb = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            sb.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT).append("/");
            sb.append(SystemConfiguration.CONTEXT_ROOT).append("/");
            sb.append(SystemConfiguration.SERVER_PATH_VERSION).append(URI);

            String finalUrl = Uri.parse(sb.toString()).buildUpon()
                    .appendEncodedPath(this.tripID)
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

}
