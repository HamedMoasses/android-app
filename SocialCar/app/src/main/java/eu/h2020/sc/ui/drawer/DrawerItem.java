package eu.h2020.sc.ui.drawer;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class DrawerItem {

    private String drawerItemTitle;
    private Integer drawerItemIcon;
    private boolean itemSelected;

    public String getDrawerItemTitle() {
        return drawerItemTitle;
    }

    public void setDrawerItemTitle(String drawerItemTitle) {
        this.drawerItemTitle = drawerItemTitle;
    }

    public Integer getDrawerItemIcon() {
        return drawerItemIcon;
    }

    public void setDrawerItemIcon(int drawerItemIcon) {
        this.drawerItemIcon = drawerItemIcon;
    }

    public boolean isItemSelected() {
        return itemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        this.itemSelected = itemSelected;
    }
}
