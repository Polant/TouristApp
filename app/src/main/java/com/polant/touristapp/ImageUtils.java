package com.polant.touristapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

/**
 * Created by Антон on 10.01.2016.
 */
public class ImageUtils {

    public static Bitmap createBitmap(String imagePath, int width, int height) {
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
}
