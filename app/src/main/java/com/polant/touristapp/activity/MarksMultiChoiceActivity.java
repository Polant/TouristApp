package com.polant.touristapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;
import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;
import com.polant.touristapp.interfaces.InitFABListener;
import com.polant.touristapp.fragment.MarksListMultiFragment;
import com.polant.touristapp.model.Mark;

public class MarksMultiChoiceActivity extends AppCompatActivity implements IWorkWithDatabaseActivity,
        InitFABListener {

    private static final int LAYOUT = R.layout.activity_marks;

    public static final String OUTPUT_CHECKED_LIST_ITEMS_IDS = "OUTPUT_CHECKED_LIST_ITEMS_IDS";
    public static final String INPUT_CHECKED_LIST_ITEMS_IDS = "INPUT_CHECKED_LIST_ITEMS_IDS";
    private static final String MARK_LIST_FRAGMENT_TAG = MarksListMultiFragment.class.toString();

    private Database db;

    private int userId;
    private long[] inputMarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        openDatabase();
        getDataFromIntent();
        initToolbar();
        initMarksListFragment();
        //initFAB();инициализирую во фрагменте.
    }

    private void openDatabase() {
        db = new Database(this);
        db.open();//База открывается и закрывается в onStart() и onStop().
    }

    private void getDataFromIntent() {
        Intent responseIntent = getIntent();
        if (responseIntent != null && responseIntent.getExtras() != null){
            userId = responseIntent.getIntExtra(Constants.USER_ID, Constants.DEFAULT_USER_ID_VALUE);
            inputMarks = responseIntent.getLongArrayExtra(INPUT_CHECKED_LIST_ITEMS_IDS);
        }
    }

    //Добавляю фрагмент со списком меток в контейнер.
    private void initMarksListFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        MarksListMultiFragment fragment = new MarksListMultiFragment();
        //Передаю Id пользователя и массив выбранных Id меток во фрагмент.
        Bundle args = new Bundle();
        args.putInt(Constants.USER_ID, userId);
        if (inputMarks != null && inputMarks.length > 0){
            args.putLongArray(INPUT_CHECKED_LIST_ITEMS_IDS, inputMarks);
        }

        fragment.setArguments(args);
        transaction.add(R.id.container_marks,
                fragment,
                MARK_LIST_FRAGMENT_TAG);
        transaction.commit();
    }

    private MarksListMultiFragment findMarksListMultiFragmentByTag(){
        return (MarksListMultiFragment)getSupportFragmentManager()
                .findFragmentByTag(MARK_LIST_FRAGMENT_TAG);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_marks_multi_choice);

        toolbar.inflateMenu(R.menu.toolbar_marks_edit);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.item_check_confirm:
                        //Получаю из фрагмента массив выбранных Id элементов списка.
                        MarksListMultiFragment mlf = findMarksListMultiFragmentByTag();
                        long[] markIds = mlf.getSelectedItemsIdsArray();

                        Intent backIntent = new Intent();
                        backIntent.putExtra(OUTPUT_CHECKED_LIST_ITEMS_IDS, markIds);
                        setResult(RESULT_OK, backIntent);
                        finish();
                        return true;
                }
                return false;
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void initFAB(ListView listView) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildNewMarkDialog(v);
            }
        });
        fab.attachToListView(listView);
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
        MarksListMultiFragment fragment = findMarksListMultiFragmentByTag();
        fragment.notifyList();
        //Уведомляю пользователя. TODO: snackbar перекрывает FAB.
        Snackbar.make(fab, "Метка добавлена", Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.snackbar_close_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
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
