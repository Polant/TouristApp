package com.polant.touristapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;

public class SelectedPhotoActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_selected_photo;
    public static final String IMAGE_EXTERNAL_PATH = "IMAGE_EXTERNAL_PATH"; //используется как ключ в полученном намерении.

    private Database db;
    private int userId;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault);
        setContentView(LAYOUT);

        db = new Database(this);
        db.open();

        getDataFromIntent();

        initToolbar();
    }

    private void getDataFromIntent() {
        Intent responseIntent = getIntent();
        if (responseIntent != null && responseIntent.getExtras() != null){
            imagePath = responseIntent.getStringExtra(IMAGE_EXTERNAL_PATH);
            userId = responseIntent.getIntExtra(Constants.USER_ID, 1);
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

    private void initImageView() {
        ImageView imageView = (ImageView) findViewById(R.id.imageViewSelectedPhoto);
        imageView.setImageBitmap(createBitmap(imagePath, imageView.getWidth(), imageView.getHeight()));
    }

    //Изменяю размер фото чтоб оно поместилось в ImageView.
    private Bitmap createBitmap(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        //Установив данное поле true, я не получаю сам объект Bitmap, а только получаю его размеры.
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, options);

        int originalImageWidth = options.outWidth;
        int originalImageHeight = options.outHeight;

        //Определяю насколько нужно уменьшить изображение.
        int scaleFactor = Math.min(originalImageWidth / width, originalImageHeight / height);

        //Получаю объект Bitmap, который имеет размеры соответствующие ImageView.
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(imagePath, options);
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
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
