package eu.h2020.sc.ui.drawer;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.messages.ChatController;
import eu.h2020.sc.ui.ContactUsActivity;
import eu.h2020.sc.ui.FaqActivity;
import eu.h2020.sc.ui.TermsConditionsActivity;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.ui.drawer.task.LiftsController;
import eu.h2020.sc.ui.home.HomeActivity;
import eu.h2020.sc.ui.lift.LiftActivity;
import eu.h2020.sc.ui.messages.ChatActivity;
import eu.h2020.sc.ui.privacypolicy.PrivacyPolicyActivity;
import eu.h2020.sc.ui.profile.ProfileActivity;
import eu.h2020.sc.ui.reports.ReportsActivity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.MaterialRippleLayout;


public class ViewHolderDrawer extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final static int NUMBER_OF_DRAWER_ICONS_ITEMS = 5;

    public TextView textViewRowText;
    public View viewDivider;
    public RelativeLayout relativeLayoutRow;
    public AppCompatActivity activity;
    public MaterialRippleLayout rippleRowDrawer;
    public RecyclerViewItemClickListener recyclerViewItemClickListener;
    public List<DrawerItem> drawerItems;
    public DrawerLayout drawerLayout;
    public MyActionBarDrawerToggle drawerToggle;

    public ViewHolderDrawer(View itemView, AppCompatActivity activity) {
        super(itemView);

        this.activity = activity;

        textViewRowText = (TextView) itemView.findViewById(R.id.textView_row_drawer_primary);
        viewDivider = itemView.findViewById(R.id.divider);
        relativeLayoutRow = (RelativeLayout) itemView.findViewById(R.id.row);
        rippleRowDrawer = (MaterialRippleLayout) itemView.findViewById(R.id.rippleRowDrawer);
    }

    public void init(List<DrawerItem> drawerItems, DrawerItem item, DrawerLayout drawerLayout, MyActionBarDrawerToggle drawerToggle) {

        int iconDefaultIndex = -1;

        this.drawerItems = drawerItems;
        this.drawerLayout = drawerLayout;
        this.drawerToggle = drawerToggle;

        this.rippleRowDrawer.setEnabled(true);
        this.relativeLayoutRow.setClickable(true);


        this.textViewRowText.setText(item.getDrawerItemTitle());

        if (item.getDrawerItemIcon() != iconDefaultIndex)
            textViewRowText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(itemView.getContext(), item.getDrawerItemIcon()), null, null, null);

        if (drawerItems.indexOf(item) == NUMBER_OF_DRAWER_ICONS_ITEMS)
            viewDivider.setVisibility(View.VISIBLE);

        if (drawerItems.indexOf(item) > NUMBER_OF_DRAWER_ICONS_ITEMS)
            textViewRowText.setCompoundDrawables(null, null, null, null);

        this.manageSelectedItem(item);

        this.relativeLayoutRow.setOnClickListener(this);
        this.relativeLayoutRow.setTag(item);
    }

    @Override
    public void onClick(View v) {

        this.drawerLayout.closeDrawer(GravityCompat.START);

        DrawerItem item = (DrawerItem) v.getTag();

        if (item.getDrawerItemTitle().equals(itemView.getResources().getString(R.string.home))) {
            this.drawerToggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.openActivity(activity, HomeActivity.class);
                }
            });
        }

        if (item.getDrawerItemTitle().equals(itemView.getResources().getString(R.string.myLifts))) {
            this.drawerToggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.openActivity(activity, LiftActivity.class);
                }
            });
        }

        if (item.getDrawerItemTitle().equals(itemView.getResources().getString(R.string.reports))) {
            this.drawerToggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.openActivity(activity, ReportsActivity.class);
                }
            });
        }

        if (item.getDrawerItemTitle().equals(itemView.getResources().getString(R.string.profile))) {
            this.drawerToggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.openActivity(activity, ProfileActivity.class);
                }
            });
        }

        if (item.getDrawerItemTitle().equals(itemView.getResources().getString(R.string.privacy))) {
            this.drawerToggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.openActivity(activity, PrivacyPolicyActivity.class);
                }
            });
        }

        if (item.getDrawerItemTitle().equals(itemView.getResources().getString(R.string.faq))) {
            this.drawerToggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.openActivity(activity, FaqActivity.class);
                }
            });
        }

        if (item.getDrawerItemTitle().equals(itemView.getResources().getString(R.string.contact_us))) {
            this.drawerToggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.openActivity(activity, ContactUsActivity.class);
                }
            });
        }

        if (item.getDrawerItemTitle().equals(itemView.getResources().getString(R.string.terms))) {
            this.drawerToggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.openActivity(activity, TermsConditionsActivity.class);
                }
            });
        }

        if (item.getDrawerItemTitle().equals(itemView.getResources().getString(R.string.messages))) {
            this.drawerToggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.openActivity(activity, ChatActivity.class);
                }
            });
        }

        recyclerViewItemClickListener.onItemClickListener(v, drawerItems.indexOf(item));
    }

    private void manageSelectedItem(DrawerItem item) {

        if (item.isItemSelected()) {

            relativeLayoutRow.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            textViewRowText.setTextColor(ContextCompat.getColor(activity, R.color.primary_color));

            if (this.drawerItems.indexOf(item) <= NUMBER_OF_DRAWER_ICONS_ITEMS) {

                if (item.getDrawerItemTitle().equals(SocialCarApplication.getContext().getString(R.string.messages))) {
                    this.setMessagesIcon(item, R.drawable.ic_unread_messages, R.color.primary_color);

                } else if (item.getDrawerItemTitle().equals(SocialCarApplication.getContext().getString(R.string.myLifts))) {
                    this.setTripIcon(item, R.drawable.ic_unread_trips, R.color.primary_color);

                } else
                    this.setDrawableColorFilter(item, R.color.primary_color);
            }

        } else {
            relativeLayoutRow.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
            textViewRowText.setTextColor(ContextCompat.getColor(activity, R.color.primary_text_color));

            if (this.drawerItems.indexOf(item) > NUMBER_OF_DRAWER_ICONS_ITEMS)
                textViewRowText.setCompoundDrawables(null, null, null, null);

            else {
                if (item.getDrawerItemTitle().equals(SocialCarApplication.getContext().getString(R.string.messages))) {
                    this.setMessagesIcon(item, R.drawable.ic_unread_messages_default, R.color.secondary_text_color);

                } else if (item.getDrawerItemTitle().equals(SocialCarApplication.getContext().getString(R.string.myLifts))) {
                    this.setTripIcon(item, R.drawable.ic_unread_trips_default, R.color.secondary_text_color);

                } else
                    this.setDrawableColorFilter(item, R.color.secondary_text_color);
            }
        }
    }

    private void setDrawableColorFilter(DrawerItem item, int color) {
        Drawable drawerItemIcon = ContextCompat.getDrawable(itemView.getContext(), item.getDrawerItemIcon());
        drawerItemIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), color), PorterDuff.Mode.SRC_ATOP);
        textViewRowText.setCompoundDrawablesWithIntrinsicBounds(drawerItemIcon, null, null, null);
    }

    private void setMessagesIcon(DrawerItem drawerItem, int drawable, int color) {

        if (ChatController.getInstance().areAllMessagesNotRead()) {
            Drawable drawerItemIcon = ContextCompat.getDrawable(itemView.getContext(), drawable);
            textViewRowText.setCompoundDrawablesWithIntrinsicBounds(drawerItemIcon, null, null, null);
        } else
            this.setDrawableColorFilter(drawerItem, color);
    }

    private void setTripIcon(DrawerItem drawerItem, int drawable, int color) {

        if (!LiftsController.getInstance().areAllLiftsReviewed()) {
            Drawable drawerItemIcon = ContextCompat.getDrawable(itemView.getContext(), drawable);
            textViewRowText.setCompoundDrawablesWithIntrinsicBounds(drawerItemIcon, null, null, null);
        } else
            this.setDrawableColorFilter(drawerItem, color);
    }

    public void setRecyclerViewItemClickListener(RecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }
}
