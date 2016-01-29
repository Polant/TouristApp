package com.polant.touristapp.activity;

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

public class SearchActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        initToolbar();
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
                return false;
            }
        });
    }

}
