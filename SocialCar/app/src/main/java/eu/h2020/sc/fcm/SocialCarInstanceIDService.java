package eu.h2020.sc.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import eu.h2020.sc.SocialCarApplication;

/**
 * Created by Pietro on 22/06/16.
 */
public class SocialCarInstanceIDService extends FirebaseInstanceIdService {

    private final String TAG = this.getClass().getName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        SocialCarApplication.getInstance().storeFCMToken(refreshedToken);

        Log.i(TAG, "New FCM TOKEN: " + refreshedToken);
    }

}
