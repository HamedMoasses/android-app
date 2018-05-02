package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alam on 19/01/17.
 */
public class FindStopsAroundRequest extends SocialCarRequest {

    public static final String TAG = TripSolutionsRequest.class.getSimpleName();
    public static final String URI = "/public-transport/stops-around?";
    public static final String JSON_KEY_STOPS = "stops";

    private Point point;

    public FindStopsAroundRequest(Point point) {
        this.point = point;
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

    @Override
    public URL urllize() {
        try {
            StringBuilder sb = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            sb.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            sb.append(SystemConfiguration.CONTEXT_ROOT);
            sb.append(SystemConfiguration.SERVER_PATH_VERSION).append(URI);

            Uri.Builder builder = Uri.parse(sb.toString()).buildUpon()
                    .appendQueryParameter("lat", String.valueOf(this.point.getLat()))
                    .appendQueryParameter("lon", String.valueOf(this.point.getLon()));

            return new URL(builder.build().toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }
        return null;
    }
}
