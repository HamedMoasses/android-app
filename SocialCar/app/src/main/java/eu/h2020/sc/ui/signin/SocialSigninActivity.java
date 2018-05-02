package eu.h2020.sc.ui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.internal.CallbackManagerImpl;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.socialnetwork.SocialUser;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.socialnetwork.RetrievePhotoListener;
import eu.h2020.sc.socialnetwork.SocialEventListener;
import eu.h2020.sc.socialnetwork.facebook.FacebookManager;
import eu.h2020.sc.socialnetwork.googleplus.GooglePlusManager;
import eu.h2020.sc.ui.home.HomeActivity;
import eu.h2020.sc.ui.signin.task.SocialSignInTask;
import eu.h2020.sc.ui.signup.SignUpActivity;
import eu.h2020.sc.ui.signup.SignUpConnectSocialActivity;
import eu.h2020.sc.utils.ActivityUtils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by khairul.alam on 09/06/16
 */

public class SocialSigninActivity extends GeneralActivity implements SocialEventListener, RetrievePhotoListener {

    private FacebookManager facebookManager;
    private SocialUser socialUser;
    private HashMap<String, Serializable> params;
    private GooglePlusManager googlePlusManager;

    private LinearLayout layoutSigninOptions;
    private LinearLayout layoutAccessOptions;
    private ImageView signinOptionsArrowBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_signin);

        SocialCarStore socialCarStore = SocialCarApplication.getInstance();

        if (socialCarStore.getUser() != null) {
            ActivityUtils.openActivityDeleteBack(this, HomeActivity.class);
        } else {
            this.params = new HashMap<>();
            facebookManager = new FacebookManager(this, this);
            this.googlePlusManager = new GooglePlusManager(this, this);
        }

        initUI();
    }

    private void initUI() {
        this.layoutSigninOptions = (LinearLayout) findViewById(R.id.signin_options_layout);
        this.layoutAccessOptions = (LinearLayout) findViewById(R.id.access_layout);
        this.signinOptionsArrowBack = (ImageView) findViewById(R.id.signin_options_arrow_back);
    }

    public void facebookSignIn(View view) {
        facebookManager.signIn();
    }

    public void googleSignIn(View view) {
        showProgressDialog();
        this.googlePlusManager.signIn();
    }

    public void goToSignInActivity(View view) {
        ActivityUtils.openActivity(this, SignInActivity.class);
    }

    public void goToSocialSignUp(View view) {
        ActivityUtils.openActivity(this, SignUpConnectSocialActivity.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GooglePlusManager.RC_SIGN_IN) {
            this.googlePlusManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            this.facebookManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPhotoCached() {
        this.params.put(SignUpActivity.USER_KEY, this.socialUser.toUser());
        this.params.put(SignUpActivity.SIGNUP_TITLE_KEY, getString(R.string.complete_signup));
        dismissDialog();
        this.goToSignUp();
    }

    @Override
    public void onPhotoCachedError() {
        dismissDialog();
        showServerGenericError();
    }

    public void goToHome() {
        ActivityUtils.openActivityNoBack(this, HomeActivity.class);
    }

    private void goToSignUp() {
        ActivityUtils.openActivityWithObjectParams(this, SignUpActivity.class, this.params);
    }

    @Override
    public void onAuthenticationSuccess(SocialUser socialUser) {
        this.socialUser = socialUser;
        SocialSignInTask socialSignInTask = new SocialSignInTask(this);
        socialSignInTask.execute(socialUser);
    }

    @Override
    public void onAuthenticationError() {
        dismissDialog();
        showServerGenericError();
    }

    @Override
    public void onAuthenticationCancel() {
        dismissDialog();
    }

    public void showSigninOptionsLayout(View view) {

        this.layoutSigninOptions.setVisibility(View.VISIBLE);
        this.layoutAccessOptions.setVisibility(View.GONE);
        this.signinOptionsArrowBack.setVisibility(View.VISIBLE);

    }

    public void hideSigninOptionsLayout(View view) {

        this.layoutSigninOptions.setVisibility(View.GONE);
        this.layoutAccessOptions.setVisibility(View.VISIBLE);
        this.signinOptionsArrowBack.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (this.layoutSigninOptions.getVisibility() == View.VISIBLE)
            this.hideSigninOptionsLayout(null);
        else
            super.onBackPressed();
    }
}