package eu.h2020.sc.ui.drawer;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class MyActionBarDrawerToggle extends ActionBarDrawerToggle {

    private Runnable runnable;
    private AppCompatActivity activity;

    public MyActionBarDrawerToggle(AppCompatActivity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        this.activity = activity;
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        this.activity.invalidateOptionsMenu();
    }

    @Override
    public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
        this.activity.invalidateOptionsMenu();
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        super.onDrawerStateChanged(newState);
        if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
            runnable.run();
            runnable = null;
        }
    }

    public void runWhenIdle(Runnable runnable) {
        this.runnable = runnable;
    }
}
