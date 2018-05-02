/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class CreateCarPictureRequest extends SocialCarPictureRequest {

    private static final String TAG = CreateCarPictureRequest.class.getName();

    private static final String POST_PICTURE_URI = "/car_pictures";
    private static final String CAR_ID_PARAM = "car_id";

    private String carID;
    private Uri mediaFileUri;

    public CreateCarPictureRequest(String carID, Uri mediaFileUri) {
        this.carID = carID;
        this.mediaFileUri = mediaFileUri;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.POST;
    }

    @Override
    public URL urllize() {
        try {
            String pictureUri = String.format("%s%s", SystemConfiguration.SERVER_PATH_VERSION, POST_PICTURE_URI);

            StringBuilder stringBuilder = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilder.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilder.append(SystemConfiguration.CONTEXT_ROOT).append(pictureUri);

            return new URL(stringBuilder.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
            return null;
        }
    }

    @Override
    public String getResourceID() {
        return carID;
    }

    @Override
    public Uri getMediaFileUri() {
        return mediaFileUri;
    }

    @Override
    public String getMultipartBodyKey() {
        return CAR_ID_PARAM;
    }
}
