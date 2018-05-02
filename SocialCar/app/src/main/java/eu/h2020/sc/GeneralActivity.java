package eu.h2020.sc;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.ui.commons.ToolbarSelector;
import eu.h2020.sc.ui.drawer.DrawerBuilder;
import eu.h2020.sc.ui.drawer.task.RendezvousLiftTask;
import eu.h2020.sc.ui.signin.SignInActivity;
import eu.h2020.sc.ui.signin.SocialSigninActivity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.MaterialDialogHelper;
import eu.h2020.sc.utils.ProgressDialogFragment;
import eu.h2020.sc.utils.Utils;
import eu.h2020.sc.utils.WidgetHelper;

public class GeneralActivity extends AppCompatActivity implements MaterialDialog.SingleButtonCallback {

    private DialogFragment progressDialog;
    private DrawerLayout drawerLayout;
    private ToolbarSelector toolbarSelector;
    private DrawerBuilder drawerBuilder;

    private long mLastClickTime = 0;

    /**
     * **************************************************|| ItemClickListener ||****************************************************************************************************
     */

    private RecyclerViewItemClickListener recyclerViewItemClickListener = new RecyclerViewItemClickListener() {
        @Override
        public void onItemClickListener(View v, int position) {
            if (drawerBuilder.getDrawerItems().get(position).getDrawerItemTitle().equalsIgnoreCase(getResources().getString(R.string.logout)))
                MaterialDialogHelper.createDialog(GeneralActivity.this, R.string.logout, R.string.logout_dialog_text, R.string.yes, R.string.no, GeneralActivity.this).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        this.toolbarSelector = new ToolbarSelector(this);
        this.drawerBuilder = new DrawerBuilder(this);

        this.progressDialog = new ProgressDialogFragment();
        this.progressDialog.setCancelable(false);

        RendezvousLiftTask rendezvousLiftTask = new RendezvousLiftTask(this);
        rendezvousLiftTask.execute();

    }

    public void initDrawer(String drawerItemTitle) {

        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        this.drawerBuilder.setUpNavigationDrawer(getToolbar(), this.drawerLayout);

        this.drawerBuilder.initItems(drawerItemTitle);

        this.initDrawerList();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
    }

    private void initDrawerList() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerViewDrawer = (RecyclerView) findViewById(R.id.recyclerViewDrawer);

        this.drawerBuilder.initDrawerList(mLayoutManager, recyclerViewDrawer, this.recyclerViewItemClickListener);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            finish();
    }

    public String getTagFromActivity(Activity activity) {
        return activity.getClass().getSimpleName();
    }

    public void showProgressDialog() {
        if (!progressDialog.isAdded())
            progressDialog.show(getSupportFragmentManager(), null);
    }

    public void dismissDialog() {
        if (progressDialog.isAdded())
            progressDialog.dismiss();
    }

    public Toolbar getToolbar() {
        return this.toolbarSelector.getToolbar();
    }

    public void initToolBar(boolean isDrawerActivity) {

        this.toolbarSelector.setDrawerActivity(isDrawerActivity);
        this.toolbarSelector.setAfterKitKatVersion(Utils.isAfterKitKatVersion());

        setSupportActionBar(this.toolbarSelector.obtainToolbar());
    }

    public void initCollapseToolBar() {
        setSupportActionBar(this.toolbarSelector.getCollapseToolbar());
    }

    public void initBack() {
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        } catch (Exception ex) {
            Log.e(getTagFromActivity(GeneralActivity.this), "Error Back", ex);
        }
    }

    public boolean checkClickTime() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return true;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return false;
    }

    public void goToSignInActivity() {
        ActivityUtils.openActivityNoBack(this, SignInActivity.class);
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        showProgressDialog();
        LogoutTask logoutTask = new LogoutTask(GeneralActivity.this);
        logoutTask.execute();
    }

    @NonNull
    protected Drawable setDrawableTint(int res, int color) {
        Drawable genericDrawable = ContextCompat.getDrawable(this, res);
        genericDrawable = DrawableCompat.wrap(genericDrawable);
        DrawableCompat.setTint(genericDrawable, ContextCompat.getColor(this, color));
        return genericDrawable;
    }

    public void replaceFragment(int container, Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (addToBackStack)
            fragmentTransaction.addToBackStack(fragment.getClass().getName());

        fragmentTransaction.replace(container, fragment, fragment.getClass().getName());
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    public void hideSoftKeyboard() {
        Utils.hideSoftKeyboard(this);
    }

    public void showUnauthorizedError() {
        WidgetHelper.showToast(this, getString(R.string.unauthorized_error));
    }

    public void showConnectionError() {
        WidgetHelper.showToast(this, getString(R.string.no_connection_error));
    }

    public void showServerGenericError() {
        WidgetHelper.showToast(this, getString(R.string.generic_error));
    }

    public void showMissingFieldsMessage() {
        WidgetHelper.showToast(this, getString(R.string.error_filled_field));
    }

    public void goToSocialSignIn() {
        ActivityUtils.openActivityNoBack(this, SocialSigninActivity.class);
    }

    public void showUploadProblem() {
        WidgetHelper.showToast(this, getString(R.string.error_upload_picture));
    }

    public void hideServerError() {
        Log.e(getTagFromActivity(this), "Generic error during rendezvous lift execution....");
    }

    public void hideNoConnectionError() {
        Log.e(getTagFromActivity(this), "No connection error during rendezvous lift execution....");
    }
}
