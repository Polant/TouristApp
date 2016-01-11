package com.polant.touristapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.MarksListFragment;

public class MarksActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_marks;

    public static final String IS_ADD_MARK_TO_PHOTO = "IS_ADD_MARK_TO_PHOTO"; //Используется в Extras intent-ов.

    private Database db;

    private int userId;
    private boolean isAddMarkToPhoto = false;   //Флаг добавления меток к выбранному фото.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        openDatabase();
        getDataFromIntent();
        initToolbar();
        initMarksListFragment();
    }

    private void openDatabase() {
        db = new Database(this);
        db.open();
    }

    private void getDataFromIntent() {
        Intent responseIntent = getIntent();
        if (responseIntent != null && responseIntent.getExtras() != null){
            userId = responseIntent.getIntExtra(Constants.USER_ID, 1);
            isAddMarkToPhoto = responseIntent.getBooleanExtra(IS_ADD_MARK_TO_PHOTO, false);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_marks);

        if (isAddMarkToPhoto) {
            toolbar.inflateMenu(R.menu.toolbar_marks_edit);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.item_check_confirm:
                            //TODO: реализовать обработчик выбора пункта меню toolbar-а.
                            return true;
                    }
                    return false;
                }
            });
        }

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: реализовать обработчик навигации toolbar-а.
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    //Добавляю фрагмент со списком меток в контейнер.
    private void initMarksListFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        MarksListFragment fragment = new MarksListFragment();
        //Передаю Id пользователя и флаг добавления меток во фрагмент.
        Bundle args = new Bundle();
        args.putInt(Constants.USER_ID, userId);
        args.putBoolean(IS_ADD_MARK_TO_PHOTO, isAddMarkToPhoto);

        fragment.setArguments(args);
        transaction.add(R.id.container_marks,
                fragment,
                MarksListFragment.class.toString());
        transaction.commit();
    }

}
