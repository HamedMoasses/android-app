/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.protocol;

import android.util.Log;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.transport.HttpConstants;

import java.net.MalformedURLException;
import java.net.URL;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class UpdatePictureRequest extends BaseRequest {

    private String resourceID;
    private String resourcePath;
    private byte[] payload;

    public UpdatePictureRequest(byte[] payload, String resourcePath, String resourceID) {
        this.payload = payload;
        this.resourcePath = resourcePath;
        this.resourceID = resourceID;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.PUT;
    }

    @Override
    public URL urllize() {
        try {
            String pictureUri = String.format("%s/pictures/%s/%s", SystemConfiguration.SERVER_PATH_VERSION, resourcePath, resourceID);

            StringBuilder stringBuilder = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            stringBuilder.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            stringBuilder.append(SystemConfiguration.CONTEXT_ROOT).append(pictureUri);

            return new URL(stringBuilder.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
            return null;
        }
    }

    public byte[] getPayload() {
        return this.payload;
    }
}
