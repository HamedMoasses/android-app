package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;

import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class DeleteRideRequest extends SocialCarRequest {

    public static final String TAG = DeleteRideRequest.class.getSimpleName();

    public static final String RIDE_URI = "/rides";

    private String rideID;

    public DeleteRideRequest(String rideID) {
        this.rideID = rideID;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.DELETE;
    }

    @Override
    public URL urllize() {
        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT)
                    .append(SystemConfiguration.CONTEXT_ROOT).append(SystemConfiguration.SERVER_PATH_VERSION).append(RIDE_URI);

            String finalUrl = Uri.parse(stringBuilderInitial.toString()).buildUpon()
                    .appendEncodedPath(this.rideID)
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
