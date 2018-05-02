package eu.h2020.sc.socialnetwork.googleplus;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.SocialProvider;
import eu.h2020.sc.domain.socialnetwork.SocialNetwork;
import eu.h2020.sc.domain.socialnetwork.SocialUser;
import eu.h2020.sc.socialnetwork.SocialEventListener;

/**
 * Created by fabiolombardi on 25/01/17.
 */

public class GooglePlusManager implements GoogleApiClient.OnConnectionFailedListener, OnCompleteListener<AuthResult> {

    private static final String TAG = GooglePlusManager.class.getName();
    public static final int RC_SIGN_IN = 9001;

    private final static String GOOGLE_PLUS_URL = "https://plus.google.com/";
    private final static String GOOGLE_PLUS_PKG = "com.google.android.apps.plus";

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private SocialEventListener socialEventListener;
    private GeneralActivity activity;
    private GoogleSignInAccount account;


    public GooglePlusManager(GeneralActivity activity, SocialEventListener socialEventListener) {
        this.socialEventListener = socialEventListener;
        this.activity = activity;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        this.mGoogleApiClient = new GoogleApiClient.Builder(activity.getBaseContext())
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult onConnectionFailed) {
        Log.d(TAG, "onConnectionFailed: " + onConnectionFailed.getErrorMessage());
        this.socialEventListener.onAuthenticationError();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            this.account = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            this.mAuth.signInWithCredential(credential).addOnCompleteListener(this.activity, this);

            Log.i(TAG, String.format("onActivityResult: googlePlusID : %s, token : %s", this.account.getId(), account.getIdToken()));
        } else {
            this.socialEventListener.onAuthenticationCancel();
        }
    }


    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        this.activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onComplete(@NonNull Task task) {
        Log.d(TAG, "signInWithCredential: onComplete:" + task.isSuccessful());
        if (task.isSuccessful()) {
            try {
                SocialUser socialUser = new SocialUser(this.mAuth.getCurrentUser().getDisplayName(), this.mAuth.getCurrentUser().getPhotoUrl().toString(), new SocialProvider(this.account.getId(), SocialNetwork.GOOGLE_PLUS));
                socialUser.setEmail(this.mAuth.getCurrentUser().getEmail());
                this.socialEventListener.onAuthenticationSuccess(socialUser);
                Log.d(TAG, String.format("signInWithCredential: onComplete: user %s", this.mAuth.getCurrentUser().getUid()));
            } catch (Exception e) {
                Log.e(TAG, "signInWithCredential", e);
                this.socialEventListener.onAuthenticationError();
            }
        } else {
            Log.e(TAG, "signInWithCredential", task.getException());
            this.socialEventListener.onAuthenticationError();
        }
    }


    public static Intent createIntentToGooglePlusProfile(Context context, String googlePlusID) {

        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(GOOGLE_PLUS_PKG, 0);

            if (!applicationInfo.enabled) {
                return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s%s", GOOGLE_PLUS_URL, googlePlusID))).addCategory(Intent.CATEGORY_BROWSABLE);
            }

            String url = String.format("%s%s", GOOGLE_PLUS_URL, googlePlusID);
            return new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        } catch (Exception e) {
            Log.e(TAG, "Error during create intent to Google Plus profile...", e);
            return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s%s", GOOGLE_PLUS_URL, googlePlusID))).addCategory(Intent.CATEGORY_BROWSABLE);
        }
    }
}