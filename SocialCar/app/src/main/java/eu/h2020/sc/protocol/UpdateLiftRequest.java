package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class UpdateLiftRequest extends SocialCarRequest {

    private static final String TAG = UpdateLiftRequest.class.getSimpleName();
    private static final String LIFT_URI = "/lifts";

    private Lift lift;

    public UpdateLiftRequest(Lift lift) {
        this.lift = lift;
    }

    @Override
    public URL urllize() {

        String UPDATE_LIFT_URI = String.format("%s%s", SystemConfiguration.SERVER_PATH_VERSION, LIFT_URI);

        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilderInitial.append(SystemConfiguration.CONTEXT_ROOT).append(UPDATE_LIFT_URI);

            String finalUrl = Uri.parse(stringBuilderInitial.toString()).buildUpon()
                    .appendEncodedPath(this.lift.getID())
                    .build().toString();

            Log.i(TAG, finalUrl);

            return new URL(finalUrl);

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }

        return null;
    }

    @Override
    public String getBody() {
        return this.lift.toJsonForRequest();
    }

    @Override
    public String getApiName() {
        return TAG;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.PUT;
    }

}