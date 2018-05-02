package eu.h2020.sc.ui.signup.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.socialnetwork.RetrievePhotoListener;
import eu.h2020.sc.persistence.PersistenceException;
import eu.h2020.sc.persistence.SocialCarStore;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Pietro Ditta on 18/01/2017.
 */

public class SocialUserPhotoTask extends AsyncTask<String, Void, Integer> {

    private static final int ON_SUCCESS = 0;
    private static final int ON_ERROR = 1;

    private RetrievePhotoListener retrievePhotoListener;
    private SocialCarStore socialCarStore;

    public SocialUserPhotoTask(RetrievePhotoListener retrievePhotoListener) {
        this.retrievePhotoListener = retrievePhotoListener;
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        String url = strings[0];
        String socialID=strings[1];

        try {
            URL imageURL = new URL(url);
            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            this.socialCarStore.cacheImage(bitmap, socialID);
            return ON_SUCCESS;
        } catch (IOException | PersistenceException e) {
            return ON_ERROR;
        }
    }

    @Override
    protected void onPostExecute(final Integer resultCode) {
        switch (resultCode) {
            case ON_SUCCESS:
                this.retrievePhotoListener.onPhotoCached();
                break;
            case ON_ERROR:
                this.retrievePhotoListener.onPhotoCachedError();
                break;
        }
    }
}
