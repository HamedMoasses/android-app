package eu.h2020.sc.protocol;

import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.Car;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class CreateCarRequest extends SocialCarRequest {

    public static final String CAR_URI = "/cars";
    private static final String TAG = CreateCarRequest.class.getSimpleName();
    private Car car;

    public CreateCarRequest(Car car) {
        this.car = car;
    }

    @Override
    public URL urllize() {

        try {
            StringBuilder stringBuilderInitial = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilderInitial.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilderInitial.append(SystemConfiguration.CONTEXT_ROOT).append(SystemConfiguration.SERVER_PATH_VERSION).append(CAR_URI);

            return new URL(stringBuilderInitial.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }

        return null;
    }

    @Override
    public String getBody() {
        return this.car.toJson();
    }

    @Override
    public String getApiName() {
        return TAG;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.POST;
    }

}
