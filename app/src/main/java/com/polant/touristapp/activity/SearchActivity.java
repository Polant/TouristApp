package com.polant.touristapp.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.SearchFragment;
import com.polant.touristapp.interfaces.ISearchableFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;
import com.polant.touristapp.model.UserMedia;

public class SearchActivity extends AppCompatActivity
        implements IWorkWithDatabaseActivity, SearchFragment.SearchFragmentListener {

    private static final int LAYOUT = R.layout.activity_search;

    private static final String SEARCH_FRAGMENT_TAG = SearchFragment.class.toString();

    private Database db;

    private ISearchableFragment mSearchableFragment;

    private int mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        openDatabase();
        getDataFromIntent();
        initSearchFragment();
        initToolbar();
    }

    private void openDatabase() {
        db = new Database(this);
        db.open();//База открывается и закрывается в onStart() и onStop().
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
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        toolbar.inflateMenu(R.menu.toolbar_search);
        Menu toolbarMenu = toolbar.getMenu();

        MenuItem searchItem = toolbarMenu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)searchItem.getActionView();
        initSearchView(searchView);
    }

    private void initSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(Constants.APP_LOG_TAG + " submit: ", query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(Constants.APP_LOG_TAG + " change: ", newText);
                mSearchableFragment.search(newText);
                return false;
            }
        });
    }

    @Override
    public void showPhotosByMark(long markId) {

    }

    @Override
    public void showSelectedPhoto(UserMedia photo) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        openDatabase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
    }

    @Override
    public Database getDatabase() {
        return db;
    }
}
