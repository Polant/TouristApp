package com.polant.touristapp.utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Антон on 10.01.2016.
 */
public class ImageUtils {

    /*Изменяет размер фото, чтоб оно поместилось в ImageView
     или другой элемент UI заданного размера*/
    public static Bitmap createBitmap(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        //Установив данное поле true, я не получаю сам объект Bitmap,
        //а только получаю его размеры в выходных параметрах объекта options.
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
