package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Pietro on 06/10/16.
 */

public class GetCarRequest extends SocialCarRequest {

    public static final String TAG = GetCarRequest.class.getSimpleName();

    public static final String CAR_URI = "/cars";

    private String carID;

    public GetCarRequest(String carID) {
        this.carID = carID;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.GET;
    }

    @Override
    public URL urllize() {
        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilderInitial.append(SystemConfiguration.CONTEXT_ROOT).append(SystemConfiguration.SERVER_PATH_VERSION).append(CAR_URI);

            String finalUrl = Uri.parse(stringBuilderInitial.toString()).buildUpon()
                    .appendPath(this.carID)
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
