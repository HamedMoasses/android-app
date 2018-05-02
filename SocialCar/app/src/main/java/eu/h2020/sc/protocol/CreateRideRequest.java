package eu.h2020.sc.protocol;

import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alam on 01/09/16.
 */
public class CreateRideRequest extends SocialCarRequest {

    private static final String TAG = CreateLiftRequest.class.getSimpleName();

    public static final String CREATE_RIDE_URI = "/rides";

    private Ride ride;

    public CreateRideRequest(Ride ride) {
        this.ride = ride;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.POST;
    }

    @Override
    public URL urllize() {
        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT)
                    .append(SystemConfiguration.CONTEXT_ROOT).append(SystemConfiguration.SERVER_PATH_VERSION)
                    .append(CREATE_RIDE_URI);

            return new URL(stringBuilderInitial.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
            return null;
        }

    }

    @Override
    public String getBody() {
        return this.ride.toJsonForRequest();
    }

    @Override
    public String getApiName() {
        return TAG;
    }
}
