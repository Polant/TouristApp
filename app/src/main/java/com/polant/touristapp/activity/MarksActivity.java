package com.polant.touristapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.activity.base.BaseTouristActivity;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.fragment.MarksFragment;
import com.polant.touristapp.fragment.PhotosFragment;
import com.polant.touristapp.interfaces.ICollapsedToolbarActionModeActivity;
import com.polant.touristapp.interfaces.IWorkWithDatabaseActivity;
import com.polant.touristapp.model.Mark;
import com.polant.touristapp.model.UserMedia;
import com.polant.touristapp.utils.alert.AlertUtil;

public class MarksActivity extends BaseTouristActivity implements IWorkWithDatabaseActivity,
        MarksFragment.MarksListener, PhotosFragment.PhotosListener, ICollapsedToolbarActionModeActivity {

    private static final int LAYOUT = R.layout.activity_marks_multi_choice;

    private static final String MARKS_FRAGMENT_TAG = MarksFragment.class.toString();
    private static final String PHOTOS_FRAGMENT_TAG = PhotosFragment.class.toString();

    public static final String OUTPUT_CHECKED_LIST_ITEMS_IDS = "OUTPUT_CHECKED_LIST_ITEMS_IDS";
    public static final String INPUT_CHECKED_LIST_ITEMS_IDS = "INPUT_CHECKED_LIST_ITEMS_IDS";

    public static final String CALL_FILTER_OR_ADD_MARKS = "CALL_FILTER_OR_ADD_MARKS";

    private long[] mInputMarks;

    //true - если Активити вызвана для фильтрования фото по меткам на карте
    //или добавления меток для нового фото.
    private boolean isCallToFilterOrAddMarksToPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        openDatabase();
        getDataFromIntent();
        initToolbar();
        initMarksRecyclerFragment();
        initFAB();
    }

    private void getDataFromIntent() {
        Intent responseIntent = getIntent();
        if (responseIntent != null && responseIntent.getExtras() != null){
            mUserId = responseIntent.getIntExtra(Constants.USER_ID, Constants.DEFAULT_USER_ID_VALUE);
            mInputMarks = responseIntent.getLongArrayExtra(INPUT_CHECKED_LIST_ITEMS_IDS);
            isCallToFilterOrAddMarksToPhoto = responseIntent.getBooleanExtra(CALL_FILTER_OR_ADD_MARKS, false);
        }
    }

    //--------------------------Toolbar----------------------------//

    private void initToolbar() {
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbar.setTitle(getString(R.string.title_activity_marks_multi_choice));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (isCallToFilterOrAddMarksToPhoto) {
            toolbar.inflateMenu(R.menu.toolbar_marks_clear_filter);
            toolbar.setOnMenuItemClickListener(mToolbarMenuListener);
        }
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
        toolbar.setNavigationOnClickListener(mNavigationListener);
    }

    private Toolbar.OnMenuItemClickListener mToolbarMenuListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.item_filter_remove:
                    //Возвращаю ПУСТОЙ массив обратно в вызвавшую Активити - таким
                    //образом я сбрасываю фильтр фото на карте.
                    Intent backIntent = new Intent();
//                        backIntent.putExtra(OUTPUT_CHECKED_LIST_ITEMS_IDS, (long[])null);
                    setResult(RESULT_OK, backIntent);
                    finish();
                    return true;
            }
            return false;
        }
    };

    private View.OnClickListener mNavigationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    //-----------------------------FAB----------------------------------//

    public void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildNewMarkDialog(v);
            }
        });
    }

    private FloatingActionButton.OnVisibilityChangedListener mFABVisibilityListener =
            new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onShown(FloatingActionButton fab) {
                    fab.setVisibility(View.VISIBLE);
                    super.onShown(fab);
                }

                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    fab.setVisibility(View.INVISIBLE);
                }
            };

    @Override
    public void hideFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide(mFABVisibilityListener);
    }

    @Override
    public void showFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.show(mFABVisibilityListener);
    }

    //----------------------------ActionMode----------------------------//

    @Override
    public void changeCollapsedToolbarLayoutBackground(boolean isStartActionMode){
        final View overlay = findViewById(R.id.collapsing_toolbar_background);
        if (isStartActionMode){
            overlay.setVisibility(View.VISIBLE);
        }else {
            AlphaAnimation animation = new AlphaAnimation(1, 0);
            animation.setStartOffset(200);
            animation.setDuration(150);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    overlay.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            overlay.startAnimation(animation);
        }
    }

    //-----------------------------Marks---------------------------------//

    private void initMarksRecyclerFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        MarksFragment fragment = new MarksFragment();
        //Передаю Id пользователя и массив изначально выбранных Id меток во фрагмент.
        Bundle args = new Bundle();
        args.putInt(Constants.USER_ID, mUserId);
        if (mInputMarks != null && mInputMarks.length > 0){
            args.putLongArray(INPUT_CHECKED_LIST_ITEMS_IDS, mInputMarks);
        }
        //Передаю тип вызова Активити.
        args.putBoolean(CALL_FILTER_OR_ADD_MARKS, isCallToFilterOrAddMarksToPhoto);

        fragment.setArguments(args);
        transaction.add(R.id.container_marks,
                fragment,
                MARKS_FRAGMENT_TAG);
        transaction.commit();
    }

    private MarksFragment findMarksListMultiFragmentByTag(){
        return (MarksFragment)getSupportFragmentManager()
                .findFragmentByTag(MARKS_FRAGMENT_TAG);
    }

    private void buildNewMarkDialog(final View fab){
        final View alertView = getLayoutInflater().inflate(R.layout.alert_new_mark, null);
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addMark(fab, alertView);
            }
        };

        AlertUtil.showAlertDialog(this, R.string.alertNewMarkTilte, R.string.alertNewMarkMessage,
                R.drawable.ic_bookmark, alertView, true, positiveListener, null);
    }

    private void addMark(View fab, View alertView) {
        EditText nameText = (EditText) alertView.findViewById(R.id.editTextNewMarkName);
        EditText descriptionText = (EditText) alertView.findViewById(R.id.editTextNewMarkDescription);

        //TODO: сделать проверки на входные параметры: пустая строка и т.д.
        String name = nameText.getText().toString();
        String description = descriptionText.getText().toString();

        Mark mark = new Mark(name, description, mUserId);
        db.insertMark(mark);

        MarksFragment fragment = findMarksListMultiFragmentByTag();
        fragment.notifyRecyclerView();

        showSnackbar(fab, R.string.mark_was_added);
    }

    //-------------------------------Photos------------------------------------//

    @Override
    public void showPhotosByMark(Mark mark) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        PhotosFragment fragment = new PhotosFragment();
        //Передаю Id пользователя и метку во фрагмент.
        Bundle args = new Bundle();
        args.putInt(Constants.USER_ID, mUserId);
        args.putLong(PhotosFragment.INPUT_MARK_ID, mark.getId());

        fragment.setArguments(args);
        transaction.replace(R.id.container_marks,
                fragment,
                PHOTOS_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();

        //Устанавливаю в заголовок Toolbar-а название последней выбранной метки.
        setCollapsedToolbarTitleData(mark.getId());

        hideFAB();
    }

    private void setCollapsedToolbarTitleData(long markId) {
        Mark mark = db.findMarkById(markId);

        TextView marksText = (TextView) findViewById(R.id.textViewMarksTitle);
        marksText.setText(getString(R.string.title_activity_marks_multi_choice));

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbar.setTitle(mark.getName());
    }

    private void backCollapsedToolbarTitleData(){
        TextView marksText = (TextView) findViewById(R.id.textViewMarksTitle);
        marksText.setText("");

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbar.setTitle(getString(R.string.title_activity_marks_multi_choice));
    }

    @Override
    public void showSelectedPhoto(UserMedia photo) {
        Intent intent = new Intent(this, SelectedPhotoActivity.class);

        intent.putExtra(Constants.USER_ID, mUserId);
        intent.putExtra(SelectedPhotoActivity.INPUT_MEDIA, photo);

        startActivityForResult(intent, Constants.SHOW_SELECTED_PHOTO_ACTIVITY);
    }

    //------------------------------------------------------------------------//

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            //Обновляю список меток на случай того, если удалил фото.
            MarksFragment marksFragment = findMarksListMultiFragmentByTag();
            marksFragment.notifyRecyclerView();

            //Убираю из заголовка название последней выбранной метки.
            backCollapsedToolbarTitleData();

            showFAB();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SHOW_SELECTED_PHOTO_ACTIVITY && resultCode == RESULT_OK){
            openDatabase();
            PhotosFragment fragment = (PhotosFragment)getSupportFragmentManager()
                    .findFragmentByTag(PHOTOS_FRAGMENT_TAG);
            fragment.notifyRecyclerView();
        }
    }

    @Override
    public Database getDatabase() {
        return db;
    }
}
