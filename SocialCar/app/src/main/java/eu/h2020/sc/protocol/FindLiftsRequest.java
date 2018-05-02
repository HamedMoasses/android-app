package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.LiftType;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Pietro on 19/07/16.
 */
public class FindLiftsRequest extends SocialCarRequest {

    public static final String TAG = FindLiftsRequest.class.getSimpleName();

    public static final String LIFTS = "lifts";

    private static final String LIFT_REQUEST_URI = "/lifts?";

    private String userID;
    private LiftType liftType;

    public FindLiftsRequest(String userID, LiftType liftType) {
        this.userID = userID;
        this.liftType = liftType;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.GET;
    }

    @Override
    public URL urllize() {

        String findLiftsURI = String.format("%s%s", SystemConfiguration.SERVER_PATH_VERSION, LIFT_REQUEST_URI);

        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilderInitial.append(SystemConfiguration.CONTEXT_ROOT).append(findLiftsURI);

            String userKey = this.getFindLiftsKey(this.liftType);

            String finalUrl = Uri.parse(stringBuilderInitial.toString()).buildUpon()
                    .appendQueryParameter(userKey, this.userID)
                    .build().toString();

            return new URL(finalUrl);

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }

        return null;
    }

    private String getFindLiftsKey(LiftType liftType) {
        if (liftType.equals(LiftType.REQUESTED))
            return "passenger_id";
        else
            return "driver_id";
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
