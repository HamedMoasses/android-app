package eu.h2020.sc.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class ImageUtils {

    private static final int MAX_KB_SIZE_IMAGE = 200;
    private static final int WIDTH_IMAGE_TO_UPLOAD = 1080;
    private static final int HEIGHT_IMAGE_TO_UPLOAD = 1080;

    public static byte[] convertBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
    }

    public static File fromByteArrayToFile(Bitmap bitmapImage, Context context) throws IOException {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        String fileName = "temp.jpg";
        File file = new File(directory, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.flush();
        fos.close();
        return file;
    }

    public static Bitmap generateBitmapToUpload(Bitmap bCropped, Context context) throws IOException {
        Bitmap bResized = resizeBitmap(bCropped, WIDTH_IMAGE_TO_UPLOAD, HEIGHT_IMAGE_TO_UPLOAD);

        File fileToUpload = ImageUtils.fromByteArrayToFile(bResized, context);
        int i = 0;
        while ((retrieveFileSizeInKB(fileToUpload) > MAX_KB_SIZE_IMAGE) && (i < 3)) {
            fileToUpload.delete();
            Bitmap bCompress = compress(bResized, 50 - (20 * i));
            fileToUpload = ImageUtils.fromByteArrayToFile(bCompress, context);
            i++;
        }
        return generateBitmapFromFile(fileToUpload);
    }

    public static Uri getUriFromBitmap(Bitmap bitmap, Context context) throws IOException {
        return Uri.fromFile(ImageUtils.fromByteArrayToFile(bitmap, context));
    }

    private static long retrieveFileSizeInKB(File file) {
        return file.length() / 1024;
    }

    private static Bitmap resizeBitmap(Bitmap b, int width, int height) {
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    private static Bitmap compress(Bitmap b, int quality) {
        Bitmap result = b;
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

        if (b.compress(Bitmap.CompressFormat.JPEG, quality, bytearrayoutputstream)) {

            byte[] bytes = bytearrayoutputstream.toByteArray();
            result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return result;
    }

    private static Bitmap generateBitmapFromFile(File f) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(f.getPath(), options);
    }

    public static Bitmap fromByteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
