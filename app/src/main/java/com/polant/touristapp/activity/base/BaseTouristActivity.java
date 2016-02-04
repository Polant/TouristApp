package com.polant.touristapp.activity.base;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.polant.touristapp.Constants;
import com.polant.touristapp.R;
import com.polant.touristapp.data.Database;

/**
 * Created by Антон on 02.02.2016.
 */
public class BaseTouristActivity extends AppCompatActivity {

    protected Database db;

    protected int mUserId = Constants.DEFAULT_USER_ID_VALUE;

    protected void openDatabase() {
        if (db != null) {
            if (db.isClosed()) {
                db = new Database(this);
                db.open();
            }
        }else{
            db = new Database(this);
            db.open();
        }
    }

    protected void showSnackbar(View view, int stringResource, int lengthTime) {
        Snackbar.make(view, stringResource, lengthTime)
                .setAction(R.string.snackbar_close_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        openDatabase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
    }
}
