package eu.h2020.sc.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.ui.home.adapter.ViewPagerAdapter;
import eu.h2020.sc.ui.home.driver.DriverFragment;
import eu.h2020.sc.ui.home.trip.search.TripSearchOSMFragment;
import eu.h2020.sc.ui.reports.creation.SeverityAndCategoryReportActivity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.Utils;

public class HomeActivity extends GeneralActivity implements ViewPager.OnPageChangeListener {

    public static final String HOME_ACTIVITY_INDEX_SELECTED_TAB = HomeActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Integer fragmentToOpen;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Utils.isAfterKitKatVersion())
            setTheme(R.style.Theme_SocialCar_Status_Transparent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.fragmentToOpen = getIntent().getIntExtra(HOME_ACTIVITY_INDEX_SELECTED_TAB, 0);

        initToolBar(true);
        this.initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDrawer(getString(R.string.home));
    }

    private void initUI() {

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.addOnPageChangeListener(this);
        this.setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.accent_color));
        tabLayout.setupWithViewPager(viewPager);

        this.initTabs(this.fragmentToOpen);
    }

    private void initTabs(int startFragmentPosition) {

        TextView customViewPassenger = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        customViewPassenger.setText(getString(R.string.tab_title_passenger));

        TextView customViewDriver = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        customViewDriver.setText(getString(R.string.tab_title_driver));

        if (startFragmentPosition == 0) {
            customViewPassenger.setAlpha(1.0f);
            customViewPassenger.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_passenger_24dp, 0, 0);

            customViewDriver.setAlpha(0.5f);
            customViewDriver.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_driver_24dp, 0, 0);

        } else {
            customViewPassenger.setAlpha(0.5f);
            customViewPassenger.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_passenger_24dp, 0, 0);

            customViewDriver.setAlpha(1.0f);
            customViewDriver.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_driver_24dp, 0, 0);
        }

        tabLayout.getTabAt(0).setCustomView(customViewPassenger);
        tabLayout.getTabAt(1).setCustomView(customViewDriver);

        this.viewPager.setCurrentItem(startFragmentPosition);
    }

    private void setupViewPager(ViewPager viewPager) {
        this.adapter = new ViewPagerAdapter(getSupportFragmentManager());
        this.adapter.addFrag(new TripSearchOSMFragment(), getString(R.string.tab_title_passenger));
        this.adapter.addFrag(new DriverFragment(), getString(R.string.tab_title_driver));
        viewPager.setAdapter(this.adapter);
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tabLayout.getTabAt(0).getCustomView().setAlpha(1.0f);
            tabLayout.getTabAt(1).getCustomView().setAlpha(0.5f);
        } else {
            tabLayout.getTabAt(0).getCustomView().setAlpha(0.5f);
            tabLayout.getTabAt(1).getCustomView().setAlpha(1.0f);
        }
        fragmentToOpen = position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.adapter.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_report) {
            ActivityUtils.openActivity(this, SeverityAndCategoryReportActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }


}
