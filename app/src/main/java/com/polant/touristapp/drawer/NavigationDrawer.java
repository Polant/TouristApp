package com.polant.touristapp.drawer;

import android.app.Activity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.polant.touristapp.R;

/**
 * Реализация NavigationDrawer - для использования его во всех activity.
 */
public class NavigationDrawer {

    private Activity activity;
    private Toolbar toolbar;

    public NavigationDrawer(Activity activity, Toolbar toolbar) {
        this.activity = activity;
        this.toolbar = toolbar;
    }

    public Drawer getMaterialDrawer(){
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .withTranslucentStatusBar(false)
                .build();

        Drawer result =  new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Карта").withIcon(R.drawable.ic_map).withIdentifier(1),
                        new PrimaryDrawerItem().withName("Поиск").withIcon(R.drawable.ic_magnify).withIdentifier(2),
                        new PrimaryDrawerItem().withName("Метки").withIcon(R.drawable.ic_bookmark).withIdentifier(3),
                        new PrimaryDrawerItem().withName("Настройки").withIcon(R.drawable.ic_settings).withIdentifier(4),
                        new PrimaryDrawerItem().withName("Помощь").withIcon(R.drawable.ic_help).withIdentifier(5)
                )
                .addStickyDrawerItems(
                        new PrimaryDrawerItem().withName("Обратная связь").withIcon(R.drawable.ic_contact_mail).withIdentifier(6)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //TODO: реализовать обработку.
                        return false;
                    }
                })
                .withSelectedItem(-1)
                .build();

        //Анимация проворота иконки при клике на нее для вызова drawer-а.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity,
                result.getDrawerLayout(),
                toolbar,
                R.string.navigation_view_open,
                R.string.navigation_view_close);
        result.getDrawerLayout().setDrawerListener(toggle);
        toggle.syncState();

        return result;
    }
}
