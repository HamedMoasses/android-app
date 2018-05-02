package eu.h2020.sc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class CropImageHelper {

    public static final int ASPECT_RATIO_WIDTH = 1;
    public static final int ASPECT_RATIO_HEIGHT = 1;

    private int aspectRatioWidth;
    private int aspectRatioHeight;
    private int maxZoom;
    private int minCropSizeWindowWidth;
    private int minCropSizeWindowHeight;
    private boolean allowedRotation;

    public CropImageHelper() {
        this.aspectRatioWidth = ASPECT_RATIO_WIDTH;
        this.aspectRatioHeight = ASPECT_RATIO_HEIGHT;
        this.allowedRotation = true;
    }

    public void startCropperActivity(Activity activity, Uri imageURI) {
        CropImage.activity(imageURI)
                .setAspectRatio(this.aspectRatioWidth, this.aspectRatioHeight)
                .setMinCropWindowSize(this.minCropSizeWindowWidth, this.minCropSizeWindowHeight)
                .setMaxZoom(this.maxZoom)
                .setAllowRotation(this.allowedRotation)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);
    }

    public Uri getPickImageResultUri(Context context, Intent intent) {
        return CropImage.getPickImageResultUri(context, intent);
    }

    public Uri getActivityResult(Intent intent) {
        CropImage.ActivityResult result = CropImage.getActivityResult(intent);
        return result.getUri();
    }

    public Bitmap getBitmapFromUri(Uri uri, Activity activity) throws IOException {
        return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
    }

    public void setMinCropSizeWindowWidth(int minCropSizeWindowWidth) {
        this.minCropSizeWindowWidth = minCropSizeWindowWidth;
    }

    public void setMinCropSizeWindowHeight(int minCropSizeWindowHeight) {
        this.minCropSizeWindowHeight = minCropSizeWindowHeight;
    }

    public void setMaxZoom(int maxZoom) {
        this.maxZoom = maxZoom;
    }
}
