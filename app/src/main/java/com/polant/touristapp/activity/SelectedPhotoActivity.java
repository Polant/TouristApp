package com.polant.touristapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.polant.touristapp.ImageUtils;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;
import com.polant.touristapp.model.MarkRecord;
import com.polant.touristapp.model.UserMedia;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SelectedPhotoActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_selected_photo;

    //используются как ключи в полученном намерении.
    public static final String IMAGE_EXTERNAL_PATH = "IMAGE_EXTERNAL_PATH";
    public static final String IMAGE_LOCATION = "IMAGE_LOCATION";

    private Database db;
    private int userId;
    private Location location;
    private String imagePath;
    private boolean exportToGalleryFlag = false;  //TODO: когда сделаю настройки приложения, то сделать проверку этого флага.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        getDataFromIntent();
        initToolbar();
    }

    //База открывается и закрывается в onStart() и onStop().
    private void openDatabase() {
        db = new Database(this);
        db.open();
    }

    private void getDataFromIntent() {
        Intent responseIntent = getIntent();
        if (responseIntent != null && responseIntent.getExtras() != null){
            imagePath = responseIntent.getStringExtra(IMAGE_EXTERNAL_PATH);
            userId = responseIntent.getIntExtra(Constants.USER_ID, 1);
            location = (Location)responseIntent.getExtras().get(IMAGE_LOCATION);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        /*Инициализирую ImageView полученным фото после того как весь интерфейс загрузился,
          чтоб можно было получить данные о размере ImageView. Если делать это в onCreate(), то
          методы imageView.getWidth() и imageView.getHeight() всегда возвращают значение 0.*/
        initImageView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        openDatabase();
    }

    private void initImageView() {
        ImageView imageView = (ImageView) findViewById(R.id.imageViewSelectedPhoto);

        //Изменяю размер фото чтоб оно поместилось в ImageView.
        Bitmap bitmap = ImageUtils.createBitmap(imagePath, imageView.getWidth(), imageView.getHeight());
        imageView.setImageBitmap(bitmap);
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
                        savePhoto();
                        return true;
                    case R.id.item_bookmark:
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
                //TODO: реализовать обработчик навигации toolbar-а.
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    //----------------------------Сохранение фото------------------------------//

    private void savePhoto() {
        try {
            //Получаю название фото и его описание.
            EditText nameText = (EditText) findViewById(R.id.editTextPhotoName);
            EditText descriptionText = (EditText) findViewById(R.id.editTextPhotoDescription);
            String name = nameText.getText().toString();
            String description = descriptionText.getText().toString();
            //Геолокация
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            //Дата.
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
            //TODO: убрать -1 из аргументов конструктора, заменив проверкой на количество меток.
            db.insertMarkRecord(new MarkRecord(mediaId, -1));

            //Экспорт в галерею (проверка на то нужно ли экспортировать внутри метода.
            exportInGallery(renamedPath);

            setResult(RESULT_OK);
            //Log.d("MY_LOGS_CREATED_PHOTO", newMedia.toString());
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

    //------------------------------------------------------------//
}
