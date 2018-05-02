package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class UpdateFeedbackRequest extends SocialCarRequest {
    private static final String TAG = UpdateFeedbackRequest.class.getSimpleName();
    private static final String FEEDBACK_URI = "/feedbacks";

    private Feedback feedback;

    public UpdateFeedbackRequest(Feedback feedback) {
        this.feedback = feedback;
    }

    @Override
    public URL urllize() {

        String UPDATE_LIFT_URI = String.format("%s%s", SystemConfiguration.SERVER_PATH_VERSION, FEEDBACK_URI);

        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilderInitial.append(SystemConfiguration.CONTEXT_ROOT).append(UPDATE_LIFT_URI);

            String finalUrl = Uri.parse(stringBuilderInitial.toString()).buildUpon()
                    .appendEncodedPath(this.feedback.getFeedbackID())
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
        return this.feedback.toJson();
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
