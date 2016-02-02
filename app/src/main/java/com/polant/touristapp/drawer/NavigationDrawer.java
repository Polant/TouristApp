package com.polant.touristapp.drawer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.MarksActivity;
import com.polant.touristapp.activity.SearchActivity;
import com.polant.touristapp.activity.SettingsActivity;

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
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Настройки").withIcon(R.drawable.ic_settings).withIdentifier(4),
                        new PrimaryDrawerItem().withName("Помощь").withIcon(R.drawable.ic_help).withIdentifier(5),
                        new PrimaryDrawerItem().withName("Обратная связь").withIcon(R.drawable.ic_contact_mail).withIdentifier(6)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //TODO: реализовать обработку клика по navigation drawer.
                        switch (position) {
                            case 2://Поиск.
                                Intent search = new Intent(activity, SearchActivity.class);
                                search.putExtra(Constants.USER_ID, Constants.DEFAULT_USER_ID_VALUE);
                                activity.startActivityForResult(search, Constants.SHOW_SEARCH_ACTIVITY);
                                break;
                            case 3://Метки.
                                Intent marks = new Intent(activity, MarksActivity.class);
                                marks.putExtra(Constants.USER_ID, Constants.DEFAULT_USER_ID_VALUE);
                                marks.putExtra(MarksActivity.CALL_FILTER_OR_ADD_MARKS, false);
                                activity.startActivityForResult(marks, Constants.SHOW_MARKS_ACTIVITY);
                                break;
                            case 4://Настройки.
                                Intent settings = new Intent(activity, SettingsActivity.class);
                                activity.startActivityForResult(settings, Constants.SHOW_SETTINGS_ACTIVITY);
                                break;
                        }
                        return false;
                    }
                })
                .withSelectedItem(1)
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
