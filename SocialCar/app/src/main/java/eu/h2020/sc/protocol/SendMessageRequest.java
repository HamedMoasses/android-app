package eu.h2020.sc.protocol;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.messages.Message;
import eu.h2020.sc.transport.HttpConstants;

/**
 * Created by pietro on 20/03/2018.
 */
public class SendMessageRequest extends SocialCarRequest {

    private static final String TAG = SendMessageRequest.class.getSimpleName();

    private static final String URI = "/messages";

    private Message message;

    public SendMessageRequest(Message message) {
        this.message = message;
    }

    @Override
    public String getBody() {
        return this.message.toJsonForRequest();
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
            stringBuilderInitial.append(SystemConfiguration.CONTEXT_ROOT).append(SystemConfiguration.SERVER_PATH_VERSION).append(URI);

            return new URL(stringBuilderInitial.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }
        return null;
    }
}
