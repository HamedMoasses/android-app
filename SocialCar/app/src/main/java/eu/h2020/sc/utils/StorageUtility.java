package eu.h2020.sc.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageUtility {

    private File directory;
    private File directoryCache;
    private ContextWrapper cw;

    public StorageUtility(Context context) {
        this.cw = new ContextWrapper(context);
        directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        directoryCache = cw.getCacheDir();
    }

    public void saveImageToStorage(Bitmap bitmap, String fileName) throws IOException {
        this.saveImage(bitmap, fileName, this.directory);
    }


    public Bitmap loadImageFromStorage(String fileName) throws FileNotFoundException {
        return loadBitmap(fileName, this.directory);
    }


    public void removeAllImage() {
        File[] files = this.directory.listFiles();
        for (File file : files) {
            file.delete();
        }
    }

    public void saveImageToCache(Bitmap picture, String fileName) throws IOException {
        saveImage(picture, fileName, this.directoryCache);
    }

    public Bitmap loadImageFromCache(String fileName) throws FileNotFoundException {
        return loadBitmap(fileName, this.directoryCache);
    }

    public void removeFromCache(String fileName) {
        String fname = fileName + ".bitmap";
        File file = new File(this.directoryCache, fname);
        file.delete();
    }

    private void saveImage(Bitmap picture, String fileName, File directory) throws IOException {
        String fname = fileName + ".bitmap";
        File file = new File(directory, fname);
        FileOutputStream fos = new FileOutputStream(file);
        picture.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }

    private Bitmap loadBitmap(String fileName, File directory) throws FileNotFoundException {
        String fname = fileName + ".bitmap";
        File file = new File(directory, fname);
        return BitmapFactory.decodeStream(new FileInputStream(file));
    }

}
