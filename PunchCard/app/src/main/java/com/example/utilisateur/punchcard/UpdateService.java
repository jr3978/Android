package com.example.utilisateur.punchcard;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Created by utilisateur on 2015-02-18.
 */
public class UpdateService  extends Service
{
    private static final String LOG = "utilisateur.android.widget.example";
    private static final String BUTTON_ONE_CLICKED = "com.yourpackage.BUTTON_ONE_CLICKED";
    private static final String BUTTON_TWO_CLICKED = "com.yourpackage.BUTTON_TWO_CLICKED";
    private static final String BUTTON_THREE_CLICKED = "com.yourpackage.BUTTON_THREE_CLICKED";
    private static final String BUTTON_FOUR_CLICKED = "com.yourpackage.BUTTON_FOUR_CLICKED";

    public void onStart(Intent intent, int startId)
    {
        Log.i(LOG, "Called");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widgetpunch);

        if(BUTTON_ONE_CLICKED.equals(intent.getAction()))
        {
            Log.i(LOG, "BUTTON ONE");

            remoteViews.setTextViewText(R.id.stackWidgetView,"testLeft");
            Bundle extras = intent.getExtras();
            int id = extras.getInt("test");
            appWidgetManager.updateAppWidget(id, remoteViews);
            return;
        }
        if(BUTTON_TWO_CLICKED.equals(intent.getAction()))
        {
            Log.i(LOG, "BUTTON TWO");

            remoteViews.setTextViewText(R.id.stackWidgetView,"testRight");
            Bundle extras = intent.getExtras();
            int id = extras.getInt("test");
            appWidgetManager.updateAppWidget(id, remoteViews);
            return;
        }
        if(BUTTON_THREE_CLICKED.equals(intent.getAction()))
        {
            Log.i(LOG, "BUTTON THREE");
            remoteViews.setTextViewText(R.id.buttonStart,"Stop");
            Bundle extras = intent.getExtras();
            int id = extras.getInt("test");
            appWidgetManager.updateAppWidget(id, remoteViews);
            return;
        }
        if(BUTTON_FOUR_CLICKED.equals(intent.getAction()))
        {
            Log.i(LOG, "BUTTON FOUR");
            remoteViews.setTextViewText(R.id.stackWidgetView,"ViewClicked");
            Bundle extras = intent.getExtras();
            int id = extras.getInt("test");
            appWidgetManager.updateAppWidget(id, remoteViews);
            return;
        }


        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        Log.d("ONSERVICE",String.valueOf(allWidgetIds[0]));
        ComponentName thisWidget = new ComponentName(getApplicationContext(), WidgetPunch.class);
        int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
        //  Log.w(LOG, "From Intent" + String.valueOf(allWidgetIds.length));
        //  Log.w(LOG, "Direct" + String.valueOf(allWidgetIds2.length));

        for (int widgetId : allWidgetIds)
        {
            // create some random data
            int number = (new Random().nextInt(100));


            Log.w("WidgetExample", String.valueOf(number));
            // Set the text
            remoteViews.setTextViewText(R.id.stackWidgetView, "Random: " + String.valueOf(number));

            Context context = getApplicationContext();

            Intent buttonOneIntent = new Intent(context, UpdateService.class);
            Intent buttonTwoIntent = new Intent(context, UpdateService.class);
            Intent buttonThreeIntent = new Intent(context, UpdateService.class);
            Intent buttonFourIntent = new Intent(context, UpdateService.class);

            // set action
            buttonOneIntent.setAction(BUTTON_ONE_CLICKED);
            buttonTwoIntent.setAction(BUTTON_TWO_CLICKED);
            buttonThreeIntent.setAction(BUTTON_THREE_CLICKED);
            buttonFourIntent.setAction(BUTTON_FOUR_CLICKED);
            Log.w("VLALEXTRA", String.valueOf(widgetId));
            // put widgetId
            buttonOneIntent.putExtra("test", widgetId);
            buttonTwoIntent.putExtra("test", widgetId);
            buttonThreeIntent.putExtra("test", widgetId);
            buttonFourIntent.putExtra("test", widgetId);

            // make these intents unique to avoid collisions
            buttonOneIntent.setData(Uri.withAppendedPath(Uri.parse("webcall_widget://buttonone/widgetid"), String.valueOf(widgetId)));
            buttonTwoIntent.setData(Uri.withAppendedPath(Uri.parse("webcall_widget://buttontwo/widgetid"), String.valueOf(widgetId)));
            buttonThreeIntent.setData(Uri.withAppendedPath(Uri.parse("webcall_widget://buttonthree/widgetid"), String.valueOf(widgetId)));
            buttonFourIntent.setData(Uri.withAppendedPath(Uri.parse("webcall_widget://buttonfour/widgetid"), String.valueOf(widgetId)));

            // pending intents
            PendingIntent buttonOnePendingIntent = PendingIntent.getService(context, 0, buttonOneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonTwoPendingIntent = PendingIntent.getService(context, 0, buttonTwoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonThreePendingIntent = PendingIntent.getService(context, 0, buttonThreeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonFourPendingIntent = PendingIntent.getService(context, 0, buttonFourIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // register onClickListeners to your buttons
            remoteViews.setOnClickPendingIntent(R.id.buttonLeft, buttonOnePendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.buttonRight, buttonTwoPendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.buttonStart, buttonThreePendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.stackWidgetView, buttonFourPendingIntent);
            // my changes - part 3 - end

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        stopSelf();
        super.onStart(intent, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}