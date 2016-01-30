package com.polant.touristapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.polant.touristapp.Constants;
import com.polant.touristapp.utils.alert.AlertUtil;
import com.polant.touristapp.utils.image.ImageUtils;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.model.MarkRecord;
import com.polant.touristapp.model.UserMedia;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SelectedPhotoActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_selected_photo;

    //используются как ключи в полученном намерении.
    public static final String IMAGE_EXTERNAL_PATH = "IMAGE_EXTERNAL_PATH";
    public static final String IMAGE_LOCATION = "IMAGE_LOCATION";
    public static final String INPUT_MEDIA = "INPUT_MEDIA";

    private Database db;
    private int userId;
    private Location location;
    private String imagePath;
    private boolean exportToGalleryFlag = false;  //TODO: когда сделаю настройки приложения, то сделать проверку этого флага.

    private long[] marksIds;    //Массив Id выбранных меток.

    private UserMedia mInputMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        getDataFromIntent();
        initToolbar();
    }

    private void openDatabase() {
        //База открывается и закрывается в onStart() и onStop().
        if (db != null) {
            if (db.isClosed()) {
                db = new Database(this);
                db.open();
            }
        }else{
            db = new Database(this);
            db.open();
        }
    }

    private void getDataFromIntent() {
        Intent responseIntent = getIntent();
        if (responseIntent != null && responseIntent.getExtras() != null){
            imagePath = responseIntent.getStringExtra(IMAGE_EXTERNAL_PATH);
            userId = responseIntent.getIntExtra(Constants.USER_ID, Constants.DEFAULT_USER_ID_VALUE);
            location = (Location)responseIntent.getExtras().get(IMAGE_LOCATION);

            //Если фото уже есть в базе и надо его посмотреть/отредактировать.
            mInputMedia = (UserMedia)responseIntent.getExtras().get(INPUT_MEDIA);
            if (mInputMedia != null){
                EditText nameText = (EditText) findViewById(R.id.editTextPhotoName);
                EditText descriptionText = (EditText) findViewById(R.id.editTextPhotoDescription);

                nameText.setText(mInputMedia.getName());
                descriptionText.setText(mInputMedia.getDescription());

                openDatabase();
                if (mInputMedia != null) {
                    setMarksIdsByPhoto(mInputMedia);
                    Log.d(Constants.APP_LOG_TAG, "setMArks");
                }
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_selected_photo);

        toolbar.inflateMenu(R.menu.toolbar_selected_photo_activity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO: реализовать обработчик выбора пункта меню toolbar-а.
                int id = item.getItemId();
                switch (id) {
                    case R.id.item_check_confirm:
                        if (mInputMedia == null) {
                            savePhoto();
                        }
                        else{
                            updatePhoto();
                        }
                        return true;
                    case R.id.item_bookmark:
                        //Выбор меток для фото.
                        Intent intent = new Intent(SelectedPhotoActivity.this, MarksActivity.class);
                        intent.putExtra(Constants.USER_ID, userId);
                        intent.putExtra(MarksActivity.INPUT_CHECKED_LIST_ITEMS_IDS, marksIds);
                        intent.putExtra(MarksActivity.CALL_FILTER_OR_ADD_MARKS, true);
                        startActivityForResult(intent, Constants.SHOW_MARKS_MULTI_CHOICE_ACTIVITY);
                        return true;
                    case R.id.item_map_marker:
                        return true;
                    case R.id.item_export_to_gallery:
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        /*Инициализирую ImageView полученным фото после того как весь интерфейс загрузился,
          чтоб можно было получить данные о размере ImageView. Если делать это в onCreate(), то
          методы imageView.getWidth() и imageView.getHeight() всегда возвращают значение 0.*/
        initImageView();
    }

    private void initImageView() {
        ImageView imageView = (ImageView) findViewById(R.id.imageViewSelectedPhoto);

        //Изменяю размер фото чтоб оно поместилось в ImageView.
        Bitmap bitmap = ImageUtils.createBitmap(
                mInputMedia == null ? imagePath : mInputMedia.getMediaExternalPath(),
                imageView.getWidth(), imageView.getHeight());
        imageView.setImageBitmap(bitmap);
    }

    //---------------------------Обновление фото-------------------------------//

    private void updatePhoto() {
        if (marksIds == null) { //Фото должно иметь хотя бы 1 метку.
            AlertUtil.showAlertDialog(this, R.string.alertSelectedPhotoHasNotMarksTitle,
                    R.string.alertSelectedPhotoHasNotMarksMessage, R.drawable.warning_orange,
                    null, true, getString(R.string.alertResultPositive), null, null, null);
            return;
        }
        //TODO: проверить на корректность введенных данных (пустые строки и т.д.).
        //Получаю название фото и его описание.
        EditText nameText = (EditText) findViewById(R.id.editTextPhotoName);
        EditText descriptionText = (EditText) findViewById(R.id.editTextPhotoDescription);
        mInputMedia.setName(nameText.getText().toString());
        mInputMedia.setDescription(descriptionText.getText().toString());

        //Получаю новый путь к фото.
        String renamedPath = generateNewPhotoName(mInputMedia.getMediaExternalPath(),
                mInputMedia.getId(), mInputMedia.getName());
        renamePhoto(mInputMedia.getMediaExternalPath(), renamedPath);

        mInputMedia.setMediaExternalPath(renamedPath);
        db.updateMedia(mInputMedia);

        //Сначала надо удалить старые записи.
        db.deleteMarkRecordsByMediaId(mInputMedia.getId());

        for (long marksId : marksIds) {
            db.insertMarkRecord(new MarkRecord(mInputMedia.getId(), (int) marksId));
        }
        exportInGallery(renamedPath);

        setResult(RESULT_OK);
        finish();
    }

    //Вызываю, когда откроется база.
    private void setMarksIdsByPhoto(UserMedia photo){
        List<Long> marks = db.findMarksIdsByMediaId(photo.getId());
        marksIds = new long[marks.size()];
        for (int i = 0; i < marks.size(); i++) {
            marksIds[i] = marks.get(i);
        }
    }

    //----------------------------Сохранение фото------------------------------//

    private void savePhoto() {
        try {
            if (marksIds == null){ //Фото должно иметь хотя бы 1 метку.
                AlertUtil.showAlertDialog(this, R.string.alertSelectedPhotoHasNotMarksTitle,
                        R.string.alertSelectedPhotoHasNotMarksMessage, R.drawable.warning_orange,
                        null, true, getString(R.string.alertResultPositive), null, null, null);
                return;
            }
            //TODO: проверить на корректность введенных данных (пустые строки и т.д.).
            //Получаю название фото и его описание.
            EditText nameText = (EditText) findViewById(R.id.editTextPhotoName);
            EditText descriptionText = (EditText) findViewById(R.id.editTextPhotoDescription);
            String name = nameText.getText().toString();
            String description = descriptionText.getText().toString();
            //Геолокация
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            //Дата и время.
            ExifInterface metadata = new ExifInterface(imagePath);
            String date = metadata.getAttribute(ExifInterface.TAG_DATETIME);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:mm:dd hh:mm:ss", Locale.getDefault());
            Date createdDate = sdf.parse(date);

            UserMedia newMedia = new UserMedia(name, description, userId,
                    latitude, longitude, imagePath,
                    exportToGalleryFlag ? UserMedia.IN_GALLERY : UserMedia.NOT_IN_GALLERY,
                    createdDate.getTime());

            //Сохраняю в БД и получаю Id новой записи.
            int mediaId = db.insertMedia(newMedia);

            //Получаю новое название фото.
            String renamedPath = generateNewPhotoName(imagePath, mediaId, name);
            //Переименовываю файл.
            renamePhoto(imagePath, renamedPath);

            //Устанавливаю обхекту UserMedia полученный Id и новый путь к изображению, а затем обновляю базу.
            newMedia.setId(mediaId);
            newMedia.setMediaExternalPath(renamedPath);
            db.updateMedia(newMedia);

            //Добавление записи о медиа и ее метках в промежуточную сущность БД.
            for (long marksId : marksIds) {
                db.insertMarkRecord(new MarkRecord(mediaId, (int)marksId));
            }
            //Экспорт в галерею (проверка на то нужно ли экспортировать внутри метода).
            exportInGallery(renamedPath);

            setResult(RESULT_OK);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        finish();
    }

    private String generateNewPhotoName(String imagePath, int mediaId, String name) {
        return new StringBuilder(imagePath)
                .delete(imagePath.lastIndexOf('/') + 1, imagePath.length())
                .append(mediaId)
                .append('_')
                .append(name)
                .append(".jpg")
                .toString();
    }

    private void renamePhoto(String currentName, String newName) {
        File photo = new File(currentName);
        photo.renameTo(new File(newName));
    }

    private void exportInGallery(String path) {
        //TODO: реализовать экспорт в галарею.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.SHOW_MARKS_MULTI_CHOICE_ACTIVITY && resultCode == RESULT_OK){
            if (data != null) {
                //Просто сохраняю массив Id выбранных из списка меток.
                marksIds = data.getLongArrayExtra(MarksActivity.OUTPUT_CHECKED_LIST_ITEMS_IDS);
            }
        }
    }

    //----------------------Открытие и закрытие базы-----------------------------//

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
