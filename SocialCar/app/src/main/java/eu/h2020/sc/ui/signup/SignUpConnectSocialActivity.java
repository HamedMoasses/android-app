package eu.h2020.sc.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.internal.CallbackManagerImpl;
import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.socialnetwork.SocialUser;
import eu.h2020.sc.socialnetwork.RetrievePhotoListener;
import eu.h2020.sc.socialnetwork.SocialEventListener;
import eu.h2020.sc.socialnetwork.facebook.FacebookManager;
import eu.h2020.sc.socialnetwork.googleplus.GooglePlusManager;
import eu.h2020.sc.ui.signup.task.SocialUserPhotoTask;
import eu.h2020.sc.utils.ActivityUtils;

import java.io.Serializable;
import java.util.HashMap;

public class SignUpConnectSocialActivity extends GeneralActivity implements SocialEventListener, RetrievePhotoListener {

    private static final String TAG = SignUpConnectSocialActivity.class.getName();

    private FacebookManager facebookManager;
    private GooglePlusManager googlePlusManager;
    private SocialUser socialUser;
    private HashMap<String, Serializable> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_connect_social);

        this.params = new HashMap<>();
        this.googlePlusManager = new GooglePlusManager(this, this);
        this.facebookManager = new FacebookManager(this, this);

        this.initUI();
    }

    private void initUI() {
        initToolBar(false);
        initBack();
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitle(String.format("%s 1 %s 2", getResources().getString(R.string.step), getResources().getString(R.string.of)));
    }

    public void facebookSignIn(View view) {
        facebookManager.signIn();
    }


    public void googleSignIn(View view) {
        showProgressDialog();
        this.googlePlusManager.signIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GooglePlusManager.RC_SIGN_IN) {
            this.googlePlusManager.onActivityResult(requestCode, resultCode, data);
        } else if ((requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode())) {
            this.facebookManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPhotoCached() {
        this.params.put(SignUpActivity.USER_KEY, this.socialUser.toUser());
        dismissDialog();
        this.goToSignUp(null);
    }

    @Override
    public void onPhotoCachedError() {
        dismissDialog();
        showServerGenericError();
    }

    public void goToSignUp(View view) {
        this.params.put(SignUpActivity.SIGNUP_TITLE_KEY, String.format("%s 2 %s 2", getResources().getString(R.string.step), getResources().getString(R.string.of)));
        ActivityUtils.openActivityWithObjectParams(this, SignUpActivity.class, this.params);
    }

    @Override
    public void onAuthenticationSuccess(SocialUser user) {
        Log.i(TAG, "onAuthenticationSuccess: " + user.toString());
        this.socialUser = user;
        SocialUserPhotoTask socialUserPhotoTask = new SocialUserPhotoTask(this);
        socialUserPhotoTask.execute(this.socialUser.getPhotoUrl(), socialUser.getSocialProvider().getSocialID());
    }

    @Override
    public void onAuthenticationError() {
        Log.i(TAG, "onAuthenticationError: ");
        dismissDialog();
        showServerGenericError();
    }

    @Override
    public void onAuthenticationCancel() {
        dismissDialog();
    }
}