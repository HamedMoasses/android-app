package eu.h2020.sc.protocol;

import android.net.Uri;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public abstract class SocialCarPictureRequest extends BaseRequest {

    public abstract String getResourceID();

    public abstract Uri getMediaFileUri();

    public abstract String getMultipartBodyKey();
}
