package eu.h2020.sc.ui.reports.creation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphContent;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.report.Report;
import eu.h2020.sc.socialnetwork.facebook.FacebookManager;
import eu.h2020.sc.socialnetwork.facebook.ShareEventListener;
import eu.h2020.sc.ui.home.HomeActivity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.WidgetHelper;

public class PostReportFacebookActivity extends GeneralActivity implements ShareEventListener {

    public static final String REPORT_KEY = "REPORT_KEY";
    private static final String FB_SHARE_HASHTAG = "#RideMyRoute";

    private FacebookManager facebookManager;
    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_report_facebook);

        this.facebookManager = new FacebookManager(this, this);
        this.report = (Report) ActivityUtils.getSerializableFromIntent(this, REPORT_KEY);
    }

    public void closeActivity(View view) {
        ActivityUtils.openActivityNoBack(this, HomeActivity.class);
    }

    public void postOnFacebook(View view) {
        ShareOpenGraphContent shareOpenGraphContent = this.facebookManager.buildShareContent(this.report, FB_SHARE_HASHTAG);
        this.facebookManager.sharePost(this, shareOpenGraphContent);
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.openActivityNoBack(this, HomeActivity.class);
    }

    @Override
    public void onSuccess(Sharer.Result result) {
        WidgetHelper.showToast(this, getString(R.string.share_on_facebook_success_message));
        ActivityUtils.openActivityNoBack(this, HomeActivity.class);
    }

    @Override
    public void onError(FacebookException error) {
        WidgetHelper.showToast(this, getString(R.string.share_on_facebook_error_message));
        Log.e(getTagFromActivity(this), error.getMessage());
        ActivityUtils.openActivityNoBack(this, HomeActivity.class);
    }

    @Override
    public void onCancel() {
        ActivityUtils.openActivityNoBack(this, HomeActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.facebookManager.onActivityResult(requestCode, resultCode, data);
    }
}
