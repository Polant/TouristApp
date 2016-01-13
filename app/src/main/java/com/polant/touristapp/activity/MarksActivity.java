package com.polant.touristapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.IWorkWithDatabaseActivity;
import com.polant.touristapp.fragment.MarksListFragment;
import com.polant.touristapp.model.Mark;

public class MarksActivity extends AppCompatActivity implements IWorkWithDatabaseActivity {

    private static final int LAYOUT = R.layout.activity_marks;

    public static final String IS_ADD_MARK_TO_PHOTO = "IS_ADD_MARK_TO_PHOTO"; //Используется в Extras intent-ов.
    private static final String MARK_LIST_FRAGMENT_TAG = MarksListFragment.class.toString();

    private Database db;

    private int userId;
    private boolean isAddMarkToPhoto = false;   //Флаг добавления меток к выбранному фото.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        openDatabase();
        getDataFromIntent();
        initToolbar();
        initMarksListFragment();
        initFAB();
    }

    //База открывается и закрывается в onStart() и onStop().
    private void openDatabase() {
        db = new Database(this);
        db.open();
    }

    private void getDataFromIntent() {
        Intent responseIntent = getIntent();
        if (responseIntent != null && responseIntent.getExtras() != null){
            userId = responseIntent.getIntExtra(Constants.USER_ID, Constants.DEFAULT_USER_ID_VALUE);
            isAddMarkToPhoto = responseIntent.getBooleanExtra(IS_ADD_MARK_TO_PHOTO, false);
        }
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
                MARK_LIST_FRAGMENT_TAG);
        transaction.commit();
    }

    private MarksListFragment findMarksListFragmentByTag(){
        return (MarksListFragment)getSupportFragmentManager()
                .findFragmentByTag(MARK_LIST_FRAGMENT_TAG);
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
                            //TODO: даный код нужно убрать, т.к. он только для отдалки.
                            // Вместо него стоит например получить массив выбранных Id.
                            MarksListFragment mlf = findMarksListFragmentByTag();
                            mlf.showSelectedItemsId();
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

    private void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildNewMarkDialog(view);
            }
        });
    }

    private void buildNewMarkDialog(final View fab){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Передаю не id лайаута, а ссылку View, чтобы потом получить доступ к нему.
        final View alertView = getLayoutInflater().inflate(R.layout.alert_new_mark, null);

        builder.setTitle(R.string.alertNewMarkTilte)
                .setMessage(R.string.alertNewMarkMessage)
                .setCancelable(true)
                .setIcon(R.drawable.ic_bookmark)
                .setView(alertView)
                .setPositiveButton(getString(R.string.alertResultPositive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addMark(fab, alertView);
                    }
                })
                .setNegativeButton(getString(R.string.alertResultNegative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Обработчик добавления новой метки с помощью FAB.
    private void addMark(View fab, View alertView) {
        EditText nameText = (EditText) alertView.findViewById(R.id.editTextNewMarkName);
        EditText descriptionText = (EditText) alertView.findViewById(R.id.editTextNewMarkDescription);

        //TODO: сделать проверки на входные параметры: пустая строка и т.д.
        String name = nameText.getText().toString();
        String description = descriptionText.getText().toString();

        Mark mark = new Mark(name, description, userId);
        db.insertMark(mark);    //Вставляю метку в БД.

        //Обновляю ListView во фрагменте.
        MarksListFragment fragment = findMarksListFragmentByTag();
        fragment.notifyList();
        //Уведобляю пользователя.
        Snackbar.make(fab, "Метка добавлена", Snackbar.LENGTH_SHORT).show();
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
