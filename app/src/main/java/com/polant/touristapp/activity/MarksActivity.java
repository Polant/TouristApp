package com.polant.touristapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.MarksRecyclerFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;

public class MarksActivity extends AppCompatActivity implements IWorkWithDatabaseActivity{

    private static final int LAYOUT = R.layout.activity_marks_recycler;
    private static final String MARKS_FRAGMENT_TAG = MarksRecyclerFragment.class.toString();

    private Database db;

    private int mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        openDatabase();
        getDataFromIntent();
        initMarksFragment();
        initToolbar();
        initFAB();
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

    private void initMarksFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        MarksRecyclerFragment fragment = new MarksRecyclerFragment();
        //Передаю Id пользователя во фрагмент.
        Bundle args = new Bundle();
        args.putInt(Constants.USER_ID, mUserId);
        fragment.setArguments(args);

        transaction.add(R.id.container_marks,
                fragment,
                MARKS_FRAGMENT_TAG);
        transaction.commit();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_marks);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: сделать обработчик добавления новой метки через диалог.
            }
        });
    }

    @Override
    public Database getDatabase() {
        return db;
    }

    //------------------------------Открытие и закрытие базы----------------------------//

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
}
