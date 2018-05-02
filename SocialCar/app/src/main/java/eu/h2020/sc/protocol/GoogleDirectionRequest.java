package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.domain.google.direction.GoogleDirectionConstant;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Pietro on 27/06/16.
 */
public class GoogleDirectionRequest extends SocialCarRequest {

    private static final String TAG = GoogleDirectionRequest.class.getSimpleName();

    public static final String GOOGLE_DIRECTION_URI = "https://maps.googleapis.com/maps/api/directions/json?";

    private String originAddress;
    private String destinationAddress;

    public GoogleDirectionRequest(String originAddress, String destinationAddress) {
        this.originAddress = originAddress;
        this.destinationAddress = destinationAddress;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.GET;
    }

    @Override
    public URL urllize() {

        try {
            String finalUrl = Uri.parse(GOOGLE_DIRECTION_URI).buildUpon()
                    .appendQueryParameter(GoogleDirectionConstant.PARAM_ORIGIN, originAddress)
                    .appendQueryParameter(GoogleDirectionConstant.PARAM_DESTINATION, destinationAddress)
                    .appendQueryParameter(GoogleDirectionConstant.PARAM_ALTERNATIVES, "true")
                    .appendQueryParameter(GoogleDirectionConstant.PARAM_MODE, GoogleDirectionConstant.VALUE_MODE)
                    .appendQueryParameter(GoogleDirectionConstant.PARAM_UNITS, GoogleDirectionConstant.VALUE_UNITS)
                    .appendQueryParameter(GoogleDirectionConstant.PARAM_SERVER_API_KEY, GoogleDirectionConstant.SOCIAL_CAR_SERVER_API_KEY)
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
