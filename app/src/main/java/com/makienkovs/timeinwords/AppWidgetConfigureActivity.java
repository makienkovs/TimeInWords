package com.makienkovs.timeinwords;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AppWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.makienkovs.timeinwords.AppWidget";
    private static final String PREF_PREFIX_KEY_BGCOLOR = "appwidget_bgcolor_";
    private static final String PREF_PREFIX_KEY_COLOR = "appwidget_color_";
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 123;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static int bgColor;
    private static int color;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = AppWidgetConfigureActivity.this;
            savePref(context, mAppWidgetId);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    View.OnClickListener backColorChose = new View.OnClickListener() {
        public void onClick(final View v) {
            v.animate()
                    .setDuration(100)
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            v.animate().scaleX(1f).scaleY(1f).start();
                        }
                    })
                    .start();
            TextView example = findViewById(R.id.example);
            switch (v.getId()) {
                case R.id.holo_blue_bright:
                    example.setBackgroundResource(R.drawable.holo_blue_bright);
                    bgColor = 0;
                    break;
                case R.id.holo_blue_dark:
                    example.setBackgroundResource(R.drawable.holo_blue_dark);
                    bgColor = 1;
                    break;
                case R.id.holo_green_dark:
                    example.setBackgroundResource(R.drawable.holo_green_dark);
                    bgColor = 2;
                    break;
                case R.id.holo_orange_dark:
                    example.setBackgroundResource(R.drawable.holo_orange_dark);
                    bgColor = 3;
                    break;
                case R.id.holo_purple:
                    example.setBackgroundResource(R.drawable.holo_purple);
                    bgColor = 4;
                    break;
                case R.id.holo_red_dark:
                    example.setBackgroundResource(R.drawable.holo_red_dark);
                    bgColor = 5;
                    break;
            }
        }
    };

    View.OnClickListener colorChose = new View.OnClickListener() {
        public void onClick(final View v) {
            v.animate()
                    .setDuration(100)
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            v.animate().scaleX(1f).scaleY(1f).start();
                        }
                    })
                    .start();
            TextView example = findViewById(R.id.example);
            switch (v.getId()) {
                case R.id.white:
                    example.setTextColor(Color.parseColor("#ffffff"));
                    color = 0;
                    break;
                case R.id.gray:
                    example.setTextColor(Color.parseColor("#888888"));
                    color = 1;
                    break;
                case R.id.black:
                    example.setTextColor(Color.parseColor("#000000"));
                    color = 2;
                    break;
                case R.id.red:
                    example.setTextColor(Color.parseColor("#ff0000"));
                    color = 3;
                    break;
                case R.id.green:
                    example.setTextColor(Color.parseColor("#00ff00"));
                    color = 4;
                    break;
                case R.id.blue:
                    example.setTextColor(Color.parseColor("#0000ff"));
                    color = 5;
                    break;
            }
        }
    };

    public AppWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void savePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs.putInt(PREF_PREFIX_KEY_BGCOLOR + appWidgetId, bgColor);
        prefs.putInt(PREF_PREFIX_KEY_COLOR + appWidgetId, color);
        prefs.apply();
    }

    static int loadBgColor(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_PREFIX_KEY_BGCOLOR + appWidgetId, 0);
    }

    static int loadColor(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_PREFIX_KEY_COLOR + appWidgetId, 0);
    }

    static void deletePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs.remove(PREF_PREFIX_KEY_BGCOLOR + appWidgetId);
        prefs.remove(PREF_PREFIX_KEY_COLOR + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);
        setContentView(R.layout.app_widget_configure);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        AppWidget.registerReceiver(this);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
            constraintLayout.setBackground(wallpaperDrawable);
        }

        Button[] backColorButtons = new Button[]{
                findViewById(R.id.holo_blue_bright),
                findViewById(R.id.holo_blue_dark),
                findViewById(R.id.holo_green_dark),
                findViewById(R.id.holo_orange_dark),
                findViewById(R.id.holo_purple),
                findViewById(R.id.holo_red_dark)
        };

        for (Button b : backColorButtons) {
            b.setOnClickListener(backColorChose);
        }

        Button[] colorButtons = new Button[]{
                findViewById(R.id.white),
                findViewById(R.id.gray),
                findViewById(R.id.black),
                findViewById(R.id.red),
                findViewById(R.id.green),
                findViewById(R.id.blue)
        };

        for (Button b : colorButtons) {
            b.setOnClickListener(colorChose);
        }

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        color = loadColor(this, mAppWidgetId);
        bgColor = loadBgColor(this, mAppWidgetId);

        TextView example = findViewById(R.id.example);
        example.setText(AppWidget.getTimeString(this));

        switch (color) {
            case 0:
                example.setTextColor(Color.parseColor("#ffffff"));
                break;
            case 1:
                example.setTextColor(Color.parseColor("#888888"));
                break;
            case 2:
                example.setTextColor(Color.parseColor("#000000"));
                break;
            case 3:
                example.setTextColor(Color.parseColor("#ff0000"));
                break;
            case 4:
                example.setTextColor(Color.parseColor("#00ff00"));
                break;
            case 5:
                example.setTextColor(Color.parseColor("#0000ff"));
                break;
        }

        switch (bgColor) {
            case 0:
                example.setBackgroundResource(R.drawable.holo_blue_bright);
                break;
            case 1:
                example.setBackgroundResource(R.drawable.holo_blue_dark);
                break;
            case 2:
                example.setBackgroundResource(R.drawable.holo_green_dark);
                break;
            case 3:
                example.setBackgroundResource(R.drawable.holo_orange_dark);
                break;
            case 4:
                example.setBackgroundResource(R.drawable.holo_purple);
                break;
            case 5:
                example.setBackgroundResource(R.drawable.holo_red_dark);
                break;
        }

        //example.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
                constraintLayout.setBackground(wallpaperDrawable);
            }
        }
    }
}