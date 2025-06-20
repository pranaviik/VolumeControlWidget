package com.example.volume_control;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.RemoteViews;



/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link volume_control_widgetConfigureActivity volume_contol_widgetConfigureActivity}
 */
public class volume_control_widget extends AppWidgetProvider {

    private static final String set_vol = "com.example.volume_control.SET_VOLUME";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = volume_control_widgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.volume_control_widget);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVol= audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVol= audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int activeDot= (int)Math.round((currentVol*10.0)/maxVol);

        for(int i=1;i<=10;i++) {
            int dotID = context.getResources().getIdentifier("dot_" + i, "id", context.getPackageName());

            views.setImageViewResource(dotID, i <= activeDot ? R.drawable.dot_on : R.drawable.dot_off);
            Intent intent = new Intent(context, volume_control_widget.class);
            intent.setAction(set_vol);
            intent.putExtra("level", i);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            views.setOnClickPendingIntent(dotID, pendingIntent);
        }

// Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (set_vol.equals(intent.getAction())) {
            int level = intent.getIntExtra("level", -1);
            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            if (level != -1 && widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int newVolume = (int) Math.ceil(level * max / 10.0);

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);

                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                updateAppWidget(context, manager, widgetId);
            }
        }
    }
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            volume_control_widgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}