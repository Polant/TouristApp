package com.polant.touristapp.utils.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.polant.touristapp.R;

/**
 * Created by Антон on 28.01.2016.
 */
public class AlertUtil {

    public static void showAlertDialog(Context context, int titleRes, int messageRes, int iconRes, View view,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener negativeListener){

        AlertDialog.Builder builder = buildSimpleAlert(context, titleRes, messageRes, view,
                positiveListener, negativeListener);

        builder.setIcon(iconRes);

        AlertDialog result = builder.create();
        result.show();
    }

    public static void showAlertDialog(Context context, int titleRes, int messageRes, View view,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener negativeListener){

        AlertDialog.Builder builder = buildSimpleAlert(context, titleRes, messageRes, view,
                positiveListener, negativeListener);

        AlertDialog result = builder.create();
        result.show();
    }

    private static AlertDialog.Builder buildSimpleAlert(Context context, int titleRes, int messageRes, View view,
                                                        DialogInterface.OnClickListener positiveListener,
                                                        DialogInterface.OnClickListener negativeListener){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleRes)
                .setMessage(messageRes)
                .setCancelable(true)
                .setView(view)
                .setPositiveButton(context.getString(R.string.alertResultPositive), positiveListener)
                .setNegativeButton(context.getString(R.string.alertResultNegative), negativeListener);

        return builder;
    }

}
