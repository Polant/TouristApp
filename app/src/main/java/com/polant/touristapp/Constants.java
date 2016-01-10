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
}
