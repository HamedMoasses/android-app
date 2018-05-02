package eu.h2020.sc.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.config.Globals;
import eu.h2020.sc.protocol.FindMediaRequest;
import eu.h2020.sc.protocol.FindUserPictureRequest;
import eu.h2020.sc.transport.HttpConstants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */
public class PicassoHelper {

    private static final String TAG = PicassoHelper.class.getName();

    private static OkHttpClient client = new OkHttpClient.Builder()
            .authenticator(new Authenticator() {
                @Override
                public Request authenticate(Route route, Response response) throws IOException {

                    String username = SocialCarApplication.getSharedPreferences().getString(Globals.EMAIL_KEY, null);
                    String password = SocialCarApplication.getSharedPreferences().getString(Globals.PASSWORD_KEY, null);

                    String credential = okhttp3.Credentials.basic(username, password);
                    return response.request().newBuilder()
                            .header(HttpConstants.AUTHORIZATION, credential)
                            .build();
                }
            }).build();

    public static void loadImage(Context context, ImageView imageView, Bitmap imageBitmap) {
        try {
            File imageFile = ImageUtils.fromByteArrayToFile(imageBitmap, context);
            Picasso.with(context).load(imageFile).into(imageView);
        } catch (IOException e) {
            Log.e(TAG, String.format("Error during get image! Error : %s", e.getMessage()));
        }
    }

    public static void loadCircleImageFromDisk(Context context, ImageView imageView, Uri uri) {
        try {
            Picasso.with(context).invalidate(uri);
            Picasso.with(context).load(uri).transform(new CircleTransform()).into(imageView);
        } catch (Exception e) {
            Log.e(TAG, String.format("Error during get resource from URI : %s, error : %s", uri, e.getMessage()));
        }
    }

    public static void loadCircleImage(Context context, ImageView imageView, Bitmap imageBitmap) {
        try {
            File imageFile = ImageUtils.fromByteArrayToFile(imageBitmap, context);
            Picasso.with(context).invalidate(imageFile);
            Picasso.with(context).load(imageFile).transform(new CircleTransform()).into(imageView);
        } catch (IOException e) {
            Log.e(TAG, String.format("Error during get image! Error : %s", e.getMessage()));
        }
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        CircleTransform circleTransform = new CircleTransform();
        return circleTransform.transform(bitmap);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     */

    public static void loadMedia(Context context, ImageView imageView, String resourceID, Integer resAvatarError, boolean isCircle) {
        loadMedia(context, imageView, resourceID, resAvatarError, R.drawable.gif_loading, isCircle);
    }

    public static void loadMedia(Context context, ImageView imageView, String resourceID, Integer resAvatarError, Integer resAvatarPlaceholder, boolean isCircle) {

        FindMediaRequest findMediaRequest = new FindMediaRequest(resourceID);

        URL pictureURL = findMediaRequest.urllize();

        Log.i(TAG, String.format("GET MEDIA RESOURCE URL : %s", pictureURL.toString()));

        buildPicasso(context, imageView, pictureURL.toString(), resAvatarError, resAvatarPlaceholder, isCircle);
    }

    public static void loadPictureByUserID(Context context, ImageView imageView, String resourceID, Integer resAvatarError, boolean isCircle) {
        loadPictureByUserID(context, imageView, resourceID, resAvatarError, R.drawable.gif_loading, isCircle);
    }

    public static void loadPictureByUserID(Context context, ImageView imageView, String resourceID, Integer resAvatarError, Integer resAvatarPlaceholder, boolean isCircle) {

        FindUserPictureRequest userPictureRequest = new FindUserPictureRequest(resourceID);

        URL pictureURL = userPictureRequest.urllize();

        Log.i(TAG, String.format("GET USER PICTURE BY ID : %s, URL : %s", resourceID, pictureURL.toString()));

        buildPicasso(context, imageView, pictureURL.toString(), resAvatarError, resAvatarPlaceholder, isCircle);
    }


    private static void buildPicasso(Context context, ImageView imageView, String url, Integer resAvatarError, Integer resAvatarPlaceholder, boolean isCircle) {

        Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build();
        picasso.invalidate(url);

        RequestCreator requestCreator = picasso.load(url);

        if (isCircle)
            requestCreator.transform(new CircleTransform());

        if (resAvatarPlaceholder != null)
            requestCreator = requestCreator.placeholder(resAvatarPlaceholder);

        if (resAvatarError != null)
            requestCreator = requestCreator.error(resAvatarError);

        requestCreator.into(imageView);
    }


    private static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
