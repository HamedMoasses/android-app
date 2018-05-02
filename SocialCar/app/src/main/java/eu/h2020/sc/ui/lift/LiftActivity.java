package eu.h2020.sc.ui.lift;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.TextView;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.ui.home.adapter.ViewPagerAdapter;
import eu.h2020.sc.utils.Utils;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class LiftActivity extends GeneralActivity implements ViewPager.OnPageChangeListener {

    public static final String TAG = LiftActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private Integer fragmentToOpen;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Utils.isAfterKitKatVersion())
            setTheme(R.style.Theme_SocialCar_Status_Transparent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift);

        this.fragmentToOpen = getIntent().getIntExtra(TAG, 0);

        initToolBar(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.initUI();
        this.initDrawer(getString(R.string.myLifts));
    }

    private void initUI() {

        setTitle(getString(R.string.myLifts));

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        assert viewPager != null;
        viewPager.addOnPageChangeListener(this);

        viewPager.setAdapter(prepareLiftPagerAdapter());

        tabLayout = (TabLayout) findViewById(R.id.liftTabs);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.accent_color));
        tabLayout.setupWithViewPager(viewPager);

        this.initTabs(this.fragmentToOpen);
    }

    private PagerAdapter prepareLiftPagerAdapter() {
        ViewPagerAdapter liftPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        liftPagerAdapter.addFrag(new LiftRequestedFragment(), getString(R.string.tab_title_lift_requested));
        liftPagerAdapter.addFrag(new LiftOfferedFragment(), getString(R.string.tab_title_lift_offered));
        return liftPagerAdapter;
    }

    private void initTabs(int startFragmentPosition) {

        TextView customViewLiftRequested = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        customViewLiftRequested.setText(getString(R.string.tab_title_lift_requested));

        TextView customViewLiftOffered = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        customViewLiftOffered.setText(getString(R.string.tab_title_lift_offered));

        if (startFragmentPosition == 0) {
            customViewLiftRequested.setAlpha(1.0f);
            customViewLiftRequested.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_passenger_24dp, 0, 0);

            customViewLiftOffered.setAlpha(0.5f);
            customViewLiftOffered.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_driver_24dp, 0, 0);

        } else {
            customViewLiftRequested.setAlpha(0.5f);
            customViewLiftRequested.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_passenger_24dp, 0, 0);

            customViewLiftOffered.setAlpha(1.0f);
            customViewLiftOffered.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_driver_24dp, 0, 0);
        }

        tabLayout.getTabAt(0).setCustomView(customViewLiftRequested);
        tabLayout.getTabAt(1).setCustomView(customViewLiftOffered);

        this.viewPager.setCurrentItem(startFragmentPosition);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.fragmentToOpen = position;
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
    public void onPageScrollStateChanged(int state) {

    }
}
