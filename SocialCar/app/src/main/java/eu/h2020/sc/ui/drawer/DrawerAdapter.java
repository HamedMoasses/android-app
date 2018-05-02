package eu.h2020.sc.ui.drawer;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.h2020.sc.R;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;

import java.util.List;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class DrawerAdapter extends RecyclerView.Adapter {

    private RecyclerViewItemClickListener recyclerViewItemClickListener;
    private List<DrawerItem> drawerItems;
    private AppCompatActivity activity;
    private DrawerLayout drawerLayout;
    private MyActionBarDrawerToggle drawerToggle;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    public DrawerAdapter(List<DrawerItem> items, AppCompatActivity activity, DrawerLayout drawerLayout, MyActionBarDrawerToggle drawerToggle) {
        this.drawerItems = items;
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.drawerToggle = drawerToggle;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header_view, parent, false);
            return new ViewHolderDrawerHeader(v);
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drawer, parent, false);
        return new ViewHolderDrawer(v, activity);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderDrawerHeader)
            try {
                ((ViewHolderDrawerHeader) holder).init();
            } catch (Exception e) {
            }
        else if (holder instanceof ViewHolderDrawer) {
            ((ViewHolderDrawer) holder).init(drawerItems, this.getItem(position), drawerLayout, this.drawerToggle);
            ((ViewHolderDrawer) holder).setRecyclerViewItemClickListener(recyclerViewItemClickListener);
        }
    }

    public DrawerItem getItem(int position) {
        return drawerItems.get(position);
    }

    @Override
    public int getItemCount() {
        return drawerItems.size();
    }

    public void setRecyclerViewItemClickListener(RecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }


}
