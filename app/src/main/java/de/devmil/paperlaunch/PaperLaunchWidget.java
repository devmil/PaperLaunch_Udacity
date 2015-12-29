package de.devmil.paperlaunch;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import de.devmil.paperlaunch.service.LauncherOverlayService;
import de.devmil.paperlaunch.service.ServiceState;

/**
 * Implementation of App Widget functionality.
 */
public class PaperLaunchWidget extends AppWidgetProvider {

    public static class WidgetData {
        private boolean mIsRunning;

        public WidgetData(boolean isRunning) {
            mIsRunning = isRunning;
        }
        public boolean isRunning() {
            return mIsRunning;
        }
    }

    public static void updateAppWidgets(Context context, boolean launcherIsRunning) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        updateAppWidgets(context, appWidgetManager, new WidgetData(launcherIsRunning));
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                        WidgetData widgetData) {
        RemoteViews views = getWidgetUpdates(context, widgetData);

        int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, PaperLaunchWidget.class));

        for(int appWidgetId : widgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private static RemoteViews getWidgetUpdates(Context context, WidgetData widgetData) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.paper_launch_widget);
        views.setImageViewResource(
                R.id.appwidget_runningindicator,
                widgetData.isRunning()
                        ? R.mipmap.ic_pause_black_24dp
                        : R.mipmap.ic_play_arrow_black_24dp);

        Intent controlIntent = LauncherOverlayService.createControlIntent(context, !widgetData.isRunning());
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                0,
                controlIntent,
                0);

        views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);

        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ServiceState serviceState = new ServiceState(context);
        WidgetData data = new WidgetData(serviceState.getIsActive());

        RemoteViews views = getWidgetUpdates(context, data);

        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

