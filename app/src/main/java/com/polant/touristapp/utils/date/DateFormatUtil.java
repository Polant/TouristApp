package com.polant.touristapp.utils.date;

import android.content.Context;

import com.polant.touristapp.R;

import java.text.DateFormatSymbols;

/**
 * Created by Антон on 01.02.2016.
 */
public class DateFormatUtil {

    public static DateFormatSymbols getDateFormatSymbols(final Context context){
        return new DateFormatSymbols(){
            @Override
            public String[] getMonths() {
                return context.getResources().getStringArray(R.array.date_format_symbols);
            }
        };
    }

}
