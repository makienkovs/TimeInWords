package com.makienkovs.timeinwords;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Locale;

public class AppWidget extends AppWidgetProvider {

    private static final String TAG = "AppWidgetTAG";
    private static boolean isDisabled = false;

    private static class Receiver extends BroadcastReceiver {

        private Receiver() {
        }

        private static Receiver instance;

        public static Receiver getInstance() {
            if (instance == null)
                instance = new Receiver();
            return instance;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Receiver receive a broadcast " + intent.getAction());
            Intent send = new Intent();
            send.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            context.sendBroadcast(send);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d(TAG, "updateAppWidget " + appWidgetId);

        int color = AppWidgetConfigureActivity.loadColor(context, appWidgetId);
        int bgColor = AppWidgetConfigureActivity.loadBgColor(context, appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.appwidget_text, getTimeString(context));

        switch (color) {
            case 0:
                views.setTextColor(R.id.appwidget_text, Color.parseColor("#ffffff"));
                break;
            case 1:
                views.setTextColor(R.id.appwidget_text, Color.parseColor("#888888"));
                break;
            case 2:
                views.setTextColor(R.id.appwidget_text, Color.parseColor("#000000"));
                break;
            case 3:
                views.setTextColor(R.id.appwidget_text, Color.parseColor("#ff0000"));
                break;
            case 4:
                views.setTextColor(R.id.appwidget_text, Color.parseColor("#00ff00"));
                break;
            case 5:
                views.setTextColor(R.id.appwidget_text, Color.parseColor("#0000ff"));
                break;
        }

        switch (bgColor) {
            case 0:
                views.setInt(R.id.layout, "setBackgroundResource", R.drawable.holo_blue_bright);
                break;
            case 1:
                views.setInt(R.id.layout, "setBackgroundResource", R.drawable.holo_blue_dark);
                break;
            case 2:
                views.setInt(R.id.layout, "setBackgroundResource", R.drawable.holo_green_dark);
                break;
            case 3:
                views.setInt(R.id.layout, "setBackgroundResource", R.drawable.holo_orange_dark);
                break;
            case 4:
                views.setInt(R.id.layout, "setBackgroundResource", R.drawable.holo_purple);
                break;
            case 5:
                views.setInt(R.id.layout, "setBackgroundResource", R.drawable.holo_red_dark);
                break;
        }

//      Конфигурационный экран
        Intent configIntent = new Intent(context, AppWidgetConfigureActivity.class);
        configIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId,
                configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_text, pIntent);

//      Обновление виджета
//        Intent updateIntent = new Intent(context, AppWidget.class);
//        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
//                new int[]{appWidgetId});
//        PendingIntent pIntent = PendingIntent.getBroadcast(context, appWidgetId, updateIntent, 0);
//        views.setOnClickPendingIntent(R.id.appwidget_text, pIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "onUpdate");
        registerReceiver(context);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "onDeleted");
        for (int appWidgetId : appWidgetIds) {
            AppWidgetConfigureActivity.deletePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "onEnabled");
        isDisabled = false;
    }

    public static void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        context.getApplicationContext().registerReceiver(Receiver.getInstance(), intentFilter);
        Log.d(TAG, "registerReceiver");
    }

    private void unregisteredReceiver(final Context context) {
        try {
            context.getApplicationContext().unregisterReceiver(Receiver.getInstance());
            Log.d(TAG, "unregisterReceiver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "onDisabled");
        unregisteredReceiver(context);
        isDisabled = true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive " + intent.getAction());
        final AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context.getPackageName(), AppWidget.class.getName()));
        if (ids.length == 0 || isDisabled)
            return;
        onUpdate(context, manager, ids);
    }

    static String getTimeString(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        String[] arrMonth = resources.getStringArray(R.array.arrMonth);
        String[] arrDays = resources.getStringArray(R.array.arrDays);
        String[] arrDaysOfWeek = resources.getStringArray(R.array.arrDaysOfWeek);
        String[] arrMinutes = resources.getStringArray(R.array.arrMinutes);
        String[] arrHours1 = resources.getStringArray(R.array.arrHours1);
        String[] arrHours2 = resources.getStringArray(R.array.arrHours2);
        String[] undefined = resources.getStringArray(R.array.undefined);
        String lang = Locale.getDefault().getLanguage();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String time;

//        Log.d(TAG, "Hour " + hour + " minute " + minute);

        if (lang.equals("ru")) {
            if (hour == 0 && minute == 0) {
                time = undefined[2];
            } else if (hour == 12 && minute == 0) {
                time = undefined[3];
            } else if (minute == 0) {
                time = undefined[4] + arrHours2[hour - 1];
            } else if (minute == 35 || minute == 40 || minute == 45) {
                time = arrMinutes[minute] + " " + arrHours2[hour];
            } else if (minute <= 45) {
                time = arrMinutes[minute] + " " + arrHours1[hour];
            } else {
                time = arrMinutes[minute] + " " + arrHours2[hour];
            }
        } else {
            if (minute == 0) {
                time = undefined[4] + arrHours2[hour];
            } else if (minute < 35) {
                time = arrMinutes[minute] + " " + arrHours1[hour];
            } else {
                time = arrMinutes[minute] + " " + arrHours1[hour + 1];
            }
        }

        return undefined[0] +
                arrDays[day] +
                " " +
                arrMonth[month] +
                " " +
                year +
                undefined[1] +
                ", " +
                arrDaysOfWeek[dayOfWeek - 1] +
                ", " +
                time +
                ".";
    }
}