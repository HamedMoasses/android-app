package eu.h2020.sc.socialnetwork.facebook;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.SocialProvider;
import eu.h2020.sc.domain.report.Category;
import eu.h2020.sc.domain.report.Report;
import eu.h2020.sc.domain.socialnetwork.SocialNetwork;
import eu.h2020.sc.domain.socialnetwork.SocialUser;
import eu.h2020.sc.socialnetwork.SocialEventListener;


/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class FacebookManager implements FacebookCallback<LoginResult>, GraphRequest.GraphJSONObjectCallback {

    private static final String TAG = FacebookManager.class.getName();

    private final static String PUBLIC_PROFILE_PERMISSION = "public_profile";

    private static final String GRAPH_PICTURE_URL = "https://graph.facebook.com/%s/picture?type=large";

    private final static String FACEBOOK_URL = "https://facebook.com/";
    private final static String FACEBOOK_PKG = "com.facebook.katana";

    private final static String ID = "id";
    private final static String NAME = "name";
    private final static String EMAIL = "email";
    private final static String GENDER = "gender";

    private final static String CONTENT_TITLE = "og:title";
    private final static String CONTENT_DESC = "og:description";
    private final static String CONTENT_TYPE = "og:type";
    private final static String CONTENT_URL = "og:url";
    private final static String POST_IMAGE_TAG = "og:image";
    private final static String CONTENT_ARTICLE = "article";
    private final static String ACTION_PUBLISH_ARTICLE = "news.publishes";

    private final static String URL_IMAGE_FACEBOOK_APP = "https://scontent-mxp1-1.xx.fbcdn.net/v/t39.2081-6/c0.0.129.129/p128x128/20044924_2008374099381870_6712836615800094720_n.png?oh=7707e9557525f09c57981e702201079c&oe=59FCC092";

    private CallbackManager callbackManager;
    private GeneralActivity activity;

    private SocialEventListener socialEventListener;
    private ShareEventListener shareEventListener;

    public FacebookManager(GeneralActivity activity, SocialEventListener socialEventListener) {
        this.activity = activity;
        this.callbackManager = CallbackManager.Factory.create();
        this.socialEventListener = socialEventListener;

        LoginManager.getInstance().registerCallback(this.callbackManager, this);
    }

    public FacebookManager(GeneralActivity activity, ShareEventListener shareEventListener) {
        this.activity = activity;
        this.callbackManager = CallbackManager.Factory.create();
        this.shareEventListener = shareEventListener;

        LoginManager.getInstance().registerCallback(this.callbackManager, this);
    }

    public boolean isLogged() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && !accessToken.isExpired();
    }

    public void signIn() {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList(PUBLIC_PROFILE_PERMISSION, EMAIL));
    }

    public static void signOut() {
        LoginManager.getInstance().logOut();
    }

    private void fetchUserInfo() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), this);
        Bundle parameters = new Bundle();
        parameters.putString(GraphRequest.FIELDS_PARAM, String.format("%s,%s,%s,%s", ID, NAME, EMAIL, GENDER));
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static Intent createIntentFacebookProfile(Context context, String facebookID) {

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(FACEBOOK_PKG, 0);
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(FACEBOOK_PKG, 0);

            if (!applicationInfo.enabled) {
                return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s%s", FACEBOOK_URL, facebookID))).addCategory(Intent.CATEGORY_BROWSABLE);
            }
            String url;

            int versionCode = packageInfo.versionCode;

            if (versionCode >= 3002850) {
                url = String.format("fb://facewebmodal/f?href=%s%s", FACEBOOK_URL, facebookID);
            } else {
                url = String.format("fb://profile/%s", facebookID);
            }
            return new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s%s", FACEBOOK_URL, facebookID))).addCategory(Intent.CATEGORY_BROWSABLE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCompleted(JSONObject jsonObject, GraphResponse response) {
        Log.i(TAG, "Facebook Auth completed");
        if (response.getError() == null) {
            try {
                String url = String.format(GRAPH_PICTURE_URL, jsonObject.getString(ID));
                SocialUser user = new SocialUser(jsonObject.getString(NAME), url, new SocialProvider(jsonObject.getString(ID), SocialNetwork.FACEBOOK));

                try {
                    user.setEmail(jsonObject.getString(EMAIL));
                } catch (JSONException e) {
                    Log.w(TAG, "Missing optional parameter Email...");
                }

                try {
                    user.setGender(jsonObject.getString(GENDER));
                } catch (JSONException e) {
                    Log.w(TAG, "Missing optional parameter Gender...");
                }

                socialEventListener.onAuthenticationSuccess(user);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing User Info JSON from Facebook", e);
                socialEventListener.onAuthenticationError();
            }
        } else {
            Log.e(FacebookManager.class.getSimpleName(), "Unable to get user info..." + response.getError());
            this.socialEventListener.onAuthenticationError();
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.i(TAG, "Facebook LogIn onSuccess");
        this.activity.showProgressDialog();
        this.fetchUserInfo();
    }


    @Override
    public void onCancel() {
        Log.e(TAG, "Facebook Auth Cancelled");
        this.socialEventListener.onAuthenticationCancel();
    }

    @Override
    public void onError(FacebookException error) {
        Log.e(TAG, "Facebook Auth onError", error);
        if (error instanceof FacebookAuthorizationException) {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
        }
        this.socialEventListener.onAuthenticationError();
    }

    public void sharePost(GeneralActivity activity, ShareOpenGraphContent content) {

        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.show(content);

        shareDialog.registerCallback(this.callbackManager, shareFacebookCallback);
    }

    public void shareLinkContent(GeneralActivity activity, ShareLinkContent content) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.show(content);

        shareDialog.registerCallback(this.callbackManager, shareFacebookCallback);
    }

    private FacebookCallback<Sharer.Result> shareFacebookCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            shareEventListener.onSuccess(result);
        }

        @Override
        public void onCancel() {
            shareEventListener.onCancel();
        }

        @Override
        public void onError(FacebookException error) {
            shareEventListener.onError(error);
        }
    };

    public ShareOpenGraphContent buildShareContent(Report report, String hashtag) {

        ShareHashtag shareHashtag = new ShareHashtag.Builder().setHashtag(hashtag).build();


        String reportCategory = null;
        switch (report.getCategory()) {
            case WORKS:
                reportCategory = SocialCarApplication.getContext().getString(R.string.report_category_works);
                break;
            case ACCIDENT:
                reportCategory = SocialCarApplication.getContext().getString(R.string.report_category_accident);
                break;
            case TRAFFIC:
                reportCategory = SocialCarApplication.getContext().getString(R.string.report_category_traffic);
                break;
            default:
                reportCategory = "";
                break;
        }

        ShareOpenGraphObject article = new ShareOpenGraphObject.Builder()
                .putString(CONTENT_TYPE, CONTENT_ARTICLE)
                .putString(CONTENT_TITLE, reportCategory)
                .putString(CONTENT_DESC, report.generateReportMessage())
                .putString(CONTENT_URL, this.activity.getString(R.string.social_car_url_facebook_page))
                .putString(POST_IMAGE_TAG, URL_IMAGE_FACEBOOK_APP)
                .build();

        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType(ACTION_PUBLISH_ARTICLE)
                .putObject(CONTENT_ARTICLE, article)
                .build();

        return new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName(CONTENT_ARTICLE)
                .setAction(action)
                .setShareHashtag(shareHashtag)
                .build();
    }
}

