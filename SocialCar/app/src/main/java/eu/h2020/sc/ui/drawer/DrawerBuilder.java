package eu.h2020.sc.ui.drawer;

import android.content.res.TypedArray;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class DrawerBuilder {

    private AppCompatActivity activity;
    private DrawerLayout drawerLayout;
    private MyActionBarDrawerToggle mDrawerToggle;
    private List<DrawerItem> drawerItems;

    public DrawerBuilder(AppCompatActivity activity) {
        this.activity = activity;
        this.drawerItems = new ArrayList<>();
    }

    public void setUpNavigationDrawer(Toolbar toolbar, DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
        this.mDrawerToggle = new MyActionBarDrawerToggle(this.activity, this.drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        this.drawerLayout.addDrawerListener(mDrawerToggle);
    }

    public void initItems(String drawerItemTitle) {
        this.drawerItems = new ArrayList<>();
        this.drawerItems.add(new DrawerItem());

        for (int i = 0; i < this.activity.getResources().getStringArray(R.array.drawer_items_titles).length; i++) {
            DrawerItem drawerItem = new DrawerItem();
            drawerItem.setDrawerItemTitle(this.activity.getResources().getStringArray(R.array.drawer_items_titles)[i]);

            if (drawerItem.getDrawerItemTitle().equals(drawerItemTitle))
                drawerItem.setItemSelected(true);

            this.addDrawerItems(i, drawerItem);
        }
    }

    private void addDrawerItems(int i, DrawerItem drawerItem) {

        TypedArray icons = this.activity.getResources().obtainTypedArray(R.array.drawer_items_icons);
        drawerItem.setDrawerItemIcon(icons.getResourceId(i, -1));

        this.drawerItems.add(drawerItem);
        icons.recycle();
    }

    public void initDrawerList(RecyclerView.LayoutManager mLayoutManager, RecyclerView recyclerViewDrawer, RecyclerViewItemClickListener recyclerViewItemClickListener) {

        DrawerAdapter drawerAdapter = new DrawerAdapter(this.drawerItems, this.activity, this.drawerLayout, this.mDrawerToggle);
        drawerAdapter.setRecyclerViewItemClickListener(recyclerViewItemClickListener);
        drawerAdapter.notifyDataSetChanged();

        recyclerViewDrawer.setLayoutManager(mLayoutManager);
        recyclerViewDrawer.setHasFixedSize(true);
        recyclerViewDrawer.setAdapter(drawerAdapter);
    }

    public List<DrawerItem> getDrawerItems() {
        return drawerItems;
    }
}
