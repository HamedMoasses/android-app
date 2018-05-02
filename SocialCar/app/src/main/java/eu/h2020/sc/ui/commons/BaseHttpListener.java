package eu.h2020.sc.ui.commons;

import android.app.Activity;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.transport.HttpTaskListener;
import eu.h2020.sc.transport.HttpTaskResult;
import eu.h2020.sc.ui.signin.SignInActivity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.WidgetHelper;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class BaseHttpListener implements HttpTaskListener {

    private Activity activity;

    public BaseHttpListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onSuccess(HttpTaskResult taskResult) {
    }

    @Override
    public void onCreated(HttpTaskResult taskResult) {

    }

    @Override
    public void onNoContent() {

    }

    @Override
    public void onConflict() {

    }

    @Override
    public void onInternalServerError() {
        WidgetHelper.showToast(activity, activity.getString(R.string.generic_error));
    }

    @Override
    public void onNotFound() {
        WidgetHelper.showToast(activity, activity.getString(R.string.generic_error));
    }

    @Override
    public void onConnectionError() {
        WidgetHelper.showToast(activity, activity.getString(R.string.no_connection_error));
    }


    @Override
    public void onUnauthorized() {
        WidgetHelper.showToast(activity, activity.getString(R.string.unauthorized_error));
        SocialCarApplication.getInstance().removeAllUserInfo();
        ActivityUtils.openActivityNoBack(activity, SignInActivity.class);
    }
}
