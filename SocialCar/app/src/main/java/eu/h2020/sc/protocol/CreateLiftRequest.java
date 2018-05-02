package eu.h2020.sc.protocol;

import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Pietro on 27/06/16.
 */
public class CreateLiftRequest extends SocialCarRequest {

    private static final String TAG = CreateLiftRequest.class.getSimpleName();

    private static final String LIFT_URI = "/lifts";

    private Lift lift;

    public CreateLiftRequest(Lift lift) {
        this.lift = lift;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.POST;
    }

    @Override
    public URL urllize() {

        String CREATE_LIFT_URI = String.format("%s%s", SystemConfiguration.SERVER_PATH_VERSION, LIFT_URI);

        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilderInitial.append(SystemConfiguration.CONTEXT_ROOT).append(CREATE_LIFT_URI);

            return new URL(stringBuilderInitial.toString());

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

}
