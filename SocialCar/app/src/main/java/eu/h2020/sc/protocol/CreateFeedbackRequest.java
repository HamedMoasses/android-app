package eu.h2020.sc.protocol;

import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;

import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class CreateFeedbackRequest extends SocialCarRequest {

    public static final String FEEDBACK_URI = "/feedbacks";
    private static final String TAG = CreateFeedbackRequest.class.getSimpleName();
    private Feedback feedback;

    public CreateFeedbackRequest(Feedback feedback) {
        this.feedback = feedback;
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
        return HttpConstants.POST;
    }

    @Override
    public URL urllize() {
        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilderInitial.append(SystemConfiguration.CONTEXT_ROOT).append(SystemConfiguration.SERVER_PATH_VERSION).append(FEEDBACK_URI);

            return new URL(stringBuilderInitial.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }

        return null;
    }
}
