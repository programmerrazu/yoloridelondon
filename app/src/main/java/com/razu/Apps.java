package com.razu;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public class Apps extends Application {

    private static Apps instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Apps getInstance() {
        if (instance == null)
            instance = new Apps();
        return instance;
    }

    public static void redirect(Context context, final Class<? extends Activity> activityToOpen) {
        Intent intent = new Intent(context, activityToOpen);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ((Activity) context).startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
        ((Activity) context).finish();
    }

    public static void snackBarMsg(String msg, View view, Context context) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}