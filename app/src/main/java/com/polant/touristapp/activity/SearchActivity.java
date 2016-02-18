package com.polant.touristapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.base.BaseTouristActivity;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.MarksFragment;
import com.polant.touristapp.fragment.PhotosFragment;
import com.polant.touristapp.fragment.SearchFragment;
import com.polant.touristapp.interfaces.ISearchableFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;
import com.polant.touristapp.model.database.Mark;
import com.polant.touristapp.model.database.UserMedia;

public class SearchActivity extends BaseTouristActivity
        implements IWorkWithDatabaseActivity, MarksFragment.MarksListener, PhotosFragment.PhotosListener {

    private static final int LAYOUT = R.layout.activity_search;

    private static final String SEARCH_FRAGMENT_TAG = SearchFragment.class.toString();
    private static final String PHOTOS_FRAGMENT_TAG = PhotosFragment.class.toString();

    private ISearchableFragment mSearchableFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        openDatabase();
        getDataFromIntent();
        initSearchFragment();
        initToolbar();
    }

    private void getDataFromIntent() {
        Intent responseIntent = getIntent();
        if (responseIntent != null && responseIntent.getExtras() != null){
            mUserId = responseIntent.getIntExtra(Constants.USER_ID, Constants.DEFAULT_USER_ID_VALUE);
        }
    }

    private void initSearchFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        SearchFragment fragment = new SearchFragment();

        mSearchableFragment = fragment;

        Bundle args = new Bundle();
        args.putInt(Constants.USER_ID, mUserId);
        fragment.setArguments(args);

        transaction.add(R.id.container_search_info,
                fragment,
                SEARCH_FRAGMENT_TAG);
        transaction.commit();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.activity_search);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_search);

        Menu toolbarMenu = toolbar.getMenu();
        final MenuItem searchItem = toolbarMenu.findItem(R.id.action_search);
        initSearchView(searchItem);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_search_show_all:
                        searchItem.collapseActionView();
                        mSearchableFragment.search(null);
                        return true;
                }
                return false;
            }
        });
    }

    private void initSearchView(final MenuItem searchItem) {
        final SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchableFragment.search(query);
                searchItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    mSearchableFragment.search(newText);
                }
                return false;
            }
        });
    }

    @Override
    public void showPhotosByMark(Mark mark) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        PhotosFragment fragment = new PhotosFragment();
        //Передаю Id пользователя и метку во фрагмент.
        Bundle args = new Bundle();
        args.putInt(Constants.USER_ID, mUserId);
        args.putLong(PhotosFragment.INPUT_MARK_ID, mark.getId());

        fragment.setArguments(args);
        transaction.replace(R.id.container_search_info,
                fragment,
                PHOTOS_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();

        //Заголовком делаю название метки.
        setToolbarMarkData(mark);
    }

    @Override
    public void showSelectedPhoto(UserMedia photo) {
        collapseSearchView(((Toolbar) findViewById(R.id.toolbar)).getMenu());

        Intent intent = new Intent(this, SelectedPhotoActivity.class);

        intent.putExtra(Constants.USER_ID, mUserId);
        intent.putExtra(SelectedPhotoActivity.INPUT_MEDIA, photo);

        startActivityForResult(intent, Constants.SHOW_SELECTED_PHOTO_ACTIVITY);
    }

    //------------------------------------------------------------------//

    private void setToolbarMarkData(Mark mark){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mark.getName());

        Menu menu = toolbar.getMenu();
        collapseSearchView(menu).setVisible(false);

        MenuItem showAllDataItem = menu.findItem(R.id.action_search_show_all);
        showAllDataItem.setVisible(false);
    }

    private void setToolbarSearchData(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.activity_search);

        Menu menu = toolbar.getMenu();
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);

        MenuItem showAllDataItem = menu.findItem(R.id.action_search_show_all);
        showAllDataItem.setVisible(true);
    }

    private MenuItem collapseSearchView(Menu menu){
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.collapseActionView();

        return searchItem;
    }

    //-----------------------------------------------------------------//

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            //Обновляю список на случай того, если удалил фото.
            mSearchableFragment.search(null);

            setToolbarSearchData();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SHOW_SELECTED_PHOTO_ACTIVITY && resultCode == RESULT_OK){
            openDatabase();
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0){
                //Если открыт PhotosFragment.
                PhotosFragment photosFragment = (PhotosFragment)fm.findFragmentByTag(PHOTOS_FRAGMENT_TAG);
                photosFragment.notifyRecyclerView();
            }
            else {
                //Если открыт SearchFragment.
                mSearchableFragment.search(null);
            }
        }
    }

    @Override
    public Database getDatabase() {
        return db;
    }
}
