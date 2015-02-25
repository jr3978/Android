package com.example.utilisateur.punchcard;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Mathew on 2015-02-13.
 */
public class WidgetPunch  extends AppWidgetProvider
{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds)
    {
        // Get all widget ids
        ComponentName thisWidget = new ComponentName(context,
                WidgetPunch.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(),
                UpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Update the widgets via the service
        context.startService(intent);
    }

}

