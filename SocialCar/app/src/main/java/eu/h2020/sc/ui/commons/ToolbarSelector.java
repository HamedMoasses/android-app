package eu.h2020.sc.ui.commons;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import eu.h2020.sc.R;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class ToolbarSelector {

    private boolean isDrawerActivity;
    private boolean isAfterKitKatVersion;

    private Activity activity;
    private Toolbar currentToolbar;

    public ToolbarSelector(Activity activity) {
        this.activity = activity;
    }

    public Toolbar obtainToolbar() {

        Toolbar mainToolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        Toolbar drawerActivityToolbar = (Toolbar) activity.findViewById(R.id.drawer_activity_toolbar);

        if (this.isAfterKitKatVersion && this.isDrawerActivity) {
            mainToolbar.setVisibility(View.GONE);

            this.currentToolbar = this.setToolbarStyle(drawerActivityToolbar, R.color.white_google, R.color.primary_color);
            return drawerActivityToolbar;

        } else {
            drawerActivityToolbar.setVisibility(View.GONE);

            this.currentToolbar = this.setToolbarStyle(mainToolbar, R.color.white_google, R.color.primary_color);
            return mainToolbar;
        }
    }

    public Toolbar getCollapseToolbar() {
        Toolbar collapseToolbar = (Toolbar) activity.findViewById(R.id.collapse_toolbar);
        this.currentToolbar = this.setToolbarStyle(collapseToolbar, R.color.white_google, R.color.transparent);

        return collapseToolbar;
    }

    private Toolbar setToolbarStyle(Toolbar toolbar, int titleTextColor, int backgroundColor) {
        toolbar.setTitleTextColor(ContextCompat.getColor(activity, titleTextColor));
        toolbar.setBackgroundColor(ContextCompat.getColor(activity, backgroundColor));

        return toolbar;
    }

    public Toolbar getToolbar() {
        return this.currentToolbar;
    }

    public void setDrawerActivity(boolean drawerActivity) {
        isDrawerActivity = drawerActivity;
    }

    public void setAfterKitKatVersion(boolean afterKitKatVersion) {
        isAfterKitKatVersion = afterKitKatVersion;
    }
}
