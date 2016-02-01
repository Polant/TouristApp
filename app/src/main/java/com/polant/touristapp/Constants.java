package com.polant.touristapp;

/**
 * В данном классе находятся глобальные константы. Константы, которые привязанны к определенным
 * активити, фрагментам и другим классам здесь не находятся.
 */
public class Constants {
    public static final int DEFAULT_USER_ID_VALUE = 1; //Id пользователя по умолчанию.
    public static final String USER_ID = "USER_ID"; //Используется в extras намерений.

    public static final int DEFAULT_LOCATION_UPDATE_FREQUENCY = 10000;//частота обновлений местонахождения.
    public static final int DEFAULT_LOCATION_UPDATE_MIN_DISTANCE = 5;//минимальная разница координат местоположения (в метрах).

    public static final int DEFAULT_CAMERA_ZOOM_LEVEL = 15;

    public static final int TAKE_PHOTO = 0;//Передается в startActivityForResult() для получения фото.
    public static final int SHOW_SELECTED_PHOTO_ACTIVITY = 1;//Обработка нового фото.
    public static final int SHOW_MARKS_MULTI_CHOICE_ACTIVITY = 2;//MarksActivity как фильтр либо для нового фото.
    public static final int SHOW_MARKS_ACTIVITY = 3;//MarksActivity как пункт меню NavigationDrawer.
    public static final int SHOW_SEARCH_ACTIVITY = 4;//SearchActivity
    public static final int SHOW_SETTINGS_ACTIVITY = 5;//SettingsActivity
    public static final int SHOW_SELECTED_PHOTO_ACTIVITY_FROM_INFO_WINDOW = 6;//После onInfoWindowClick.

    public static final String APP_LOG_TAG = "POLANT_LOGS"; //Используется в логах.
}
