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
                                       boolean cancelable, String positiveText, String negativeText,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener negativeListener){

        AlertDialog.Builder builder = buildSimpleAlert(context, titleRes, messageRes, view,
                cancelable, positiveText, negativeText,
                positiveListener, negativeListener);

        builder.setIcon(iconRes);

        AlertDialog result = builder.create();
        result.show();
    }

    public static void showAlertDialog(Context context, int titleRes, int messageRes, int iconRes, View view,
                                       boolean cancelable,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener negativeListener){

        showAlertDialog(context, titleRes, messageRes, iconRes, view, cancelable,
                context.getString(R.string.alertResultPositive), context.getString(R.string.alertResultNegative),
                positiveListener, negativeListener);
    }

    //Без иконки.
    public static void showAlertDialog(Context context, int titleRes, int messageRes, View view,
                                       boolean cancelable,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener negativeListener){

        AlertDialog.Builder builder = buildSimpleAlert(context, titleRes, messageRes, view,
                cancelable,
                context.getString(R.string.alertResultPositive), context.getString(R.string.alertResultNegative),
                positiveListener, negativeListener);

        AlertDialog result = builder.create();
        result.show();
    }

    private static AlertDialog.Builder buildSimpleAlert(Context context, int titleRes, int messageRes, View view,
                                                        boolean cancelable, String positiveText, String negativeText,
                                                        DialogInterface.OnClickListener positiveListener,
                                                        DialogInterface.OnClickListener negativeListener){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleRes)
                .setMessage(messageRes)
                .setCancelable(cancelable)
                .setView(view)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener);

        return builder;
    }

}
