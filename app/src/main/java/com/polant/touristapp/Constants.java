package com.polant.touristapp;

/**
 * В данном классе находятся глобальные константы. Константы, которые привязанны к определенным
 * активити, фрагментам и другим классам здесь не находятся.
 */
public class Constants {
    public static final int DEFAULT_USER_ID_VALUE = 1; //Id пользователя по умолчанию.
    public static final String USER_ID = "USER_ID"; //Используется в extras намерений.

    public static final int LOCATION_UPDATE_FREQUENCY = 10000;//частота обновлений местонахождения.
    public static final int LOCATION_UPDATE_MIN_DISTANCE = 5;//минимальная разница координат местоположения (в метрах).

    public static final int DEFAULT_CAMERA_ZOOM_LEVEL = 15;

    //Передается в startActivityForResult() для получения фото.
    public static final int TAKE_PHOTO = 0;
    public static final int SHOW_SELECTED_PHOTO_ACTIVITY = 1;
    public static final int SHOW_MARKS_MULTI_CHOICE_ACTIVITY = 2;
    public static final int SHOW_MARKS_ACTIVITY = 3;

    public static final String APP_LOG_TAG = "POLANT_LOGS"; //Используется в логах.
}
