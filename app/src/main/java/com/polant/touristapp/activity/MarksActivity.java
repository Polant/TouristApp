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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.MarksFragment;
import com.polant.touristapp.fragment.PhotosFragment;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;
import com.polant.touristapp.model.Mark;

public class MarksActivity extends AppCompatActivity implements IWorkWithDatabaseActivity,
        MarksFragment.PhotosFragmentListener{

    private static final int LAYOUT = R.layout.activity_marks_multi_choice;

    private static final String MARKS_FRAGMENT_TAG = MarksFragment.class.toString();
    private static final String PHOTOS_FRAGMENT_TAG = PhotosFragment.class.toString();

    public static final String OUTPUT_CHECKED_LIST_ITEMS_IDS = "OUTPUT_CHECKED_LIST_ITEMS_IDS";
    public static final String INPUT_CHECKED_LIST_ITEMS_IDS = "INPUT_CHECKED_LIST_ITEMS_IDS";

    public static final String CALL_FILTER_OR_ADD_MARKS = "CALL_FILTER_OR_ADD_MARKS";

    private Database db;

    private int userId;
    private long[] inputMarks;

    //true - если Активити вызвана для фильтрования фото по меткам на карте
    //или добавления меток для нового фото.
    private boolean isCallToFilterOrAddMarksToPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        openDatabase();
        getDataFromIntent();
        initToolbar();
        initMarksRecyclerFragment();
        initFAB();
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
            isCallToFilterOrAddMarksToPhoto = responseIntent.getBooleanExtra(CALL_FILTER_OR_ADD_MARKS, false);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_marks_multi_choice);

        if (isCallToFilterOrAddMarksToPhoto) {
            toolbar.inflateMenu(R.menu.toolbar_marks_clear_filter);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case R.id.item_filter_remove:
                            //Возвращаю ПУСТОЙ массив обратно в вызвавшую Активити.
                            Intent backIntent = new Intent();
//                        backIntent.putExtra(OUTPUT_CHECKED_LIST_ITEMS_IDS, (long[])null);
                            setResult(RESULT_OK, backIntent);
                            finish();
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
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initMarksRecyclerFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        MarksFragment fragment = new MarksFragment();
        //Передаю Id пользователя и массив выбранных Id меток во фрагмент.
        Bundle args = new Bundle();
        args.putInt(Constants.USER_ID, userId);
        if (inputMarks != null && inputMarks.length > 0){
            args.putLongArray(INPUT_CHECKED_LIST_ITEMS_IDS, inputMarks);
        }
        //Передаю тип вызова Активити.
        args.putBoolean(CALL_FILTER_OR_ADD_MARKS, isCallToFilterOrAddMarksToPhoto);

        fragment.setArguments(args);
        transaction.add(R.id.container_marks,
                fragment,
                MARKS_FRAGMENT_TAG);
        transaction.commit();
    }

    public void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setMarksFABClickListener(fab);
    }

    //-----------------------------Marks------------------------------//

    private void setMarksFABClickListener(FloatingActionButton fab){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildNewMarkDialog(v);
            }
        });
    }

    private MarksFragment findMarksListMultiFragmentByTag(){
        return (MarksFragment)getSupportFragmentManager()
                .findFragmentByTag(MARKS_FRAGMENT_TAG);
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

    private void addMark(View fab, View alertView) {
        EditText nameText = (EditText) alertView.findViewById(R.id.editTextNewMarkName);
        EditText descriptionText = (EditText) alertView.findViewById(R.id.editTextNewMarkDescription);

        //TODO: сделать проверки на входные параметры: пустая строка и т.д.
        String name = nameText.getText().toString();
        String description = descriptionText.getText().toString();

        Mark mark = new Mark(name, description, userId);
        db.insertMark(mark);    //Вставляю метку в БД.

        //Обновляю ListView во фрагменте.
        MarksFragment fragment = findMarksListMultiFragmentByTag();
        fragment.notifyRecyclerView();

        showSnackbar(fab, R.string.mark_was_added);
    }

    private void showSnackbar(View view, int stringResource) {
        Snackbar.make(view, stringResource, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.snackbar_close_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
    }

    //-------------------------------Photos------------------------------------//


    @Override
    public void showPhotosByMark(long markId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        PhotosFragment fragment = new PhotosFragment();
        //Передаю Id пользователя и метку во фрагмент.
        Bundle args = new Bundle();
        args.putInt(Constants.USER_ID, userId);
        args.putLong(PhotosFragment.INPUT_MARK_ID, markId);

        fragment.setArguments(args);
        transaction.replace(R.id.container_marks,
                fragment,
                PHOTOS_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //------------------------------------------------------------------------//

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
