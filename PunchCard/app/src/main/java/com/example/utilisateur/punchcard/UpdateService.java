package com.example.utilisateur.punchcard;

import android.app.AlarmManager;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by utilisateur on 2015-02-18.
 */
public class UpdateService  extends Service
{
    private static final String LOG = "utilisateur.android.widget.example";
    private static final String BUTTON_LEFT_CLICKED = "punchapp.BUTTON_LEFT_CLICKED";
    private static final String BUTTON_RIGHT_CLICKED = "punchapp.BUTTON_RIGHT_CLICKED";
    private static final String BUTTON_START_CLICKED = "punchapp.BUTTON_START_CLICKED";
    private static final String BUTTON_VIEW_CLICKED = "punchapp.BUTTON_VIEW_CLICKED";
    private static final String ALARM_TICK = "punchapp.ALARM_TICK";
    private AlarmManager alarmMgr;

    public void onStart(Intent intent, int startId)
    {


        Log.i(LOG, "Called");

        if(intent == null)
            return;

        DataBaseHandler db = new DataBaseHandler(this);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widgetpunch);
        List<Occupation> lstjob;
        lstjob = db.getAllOccupations();

        //region ButtonLeft
        if(BUTTON_LEFT_CLICKED.equals(intent.getAction()))
        {
            Log.i(LOG, "BUTTON LEFT");
            int jobcount = lstjob.size();
            boolean bfound = false;

            for(int i = 0 ; i < jobcount ; i++)
            {
                if(lstjob.get(i).isIn())
                    return;
            }

            for(int i = 0; i < jobcount ; i++)
            {
                if(lstjob.get(i).isSelected())
                {
                    bfound = true;
                    lstjob.get(i).isSelected(false);
                    db.updateOccupation(lstjob.get(i));
                    if(i != 0)
                    {
                        Log.i(LOG, lstjob.get(i-1).getName());
                        remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(i - 1).getName());
                        lstjob.get(i-1).isSelected(true);
                        db.updateOccupation(lstjob.get(i-1));
                        db.close();
                        break;
                    }
                    else
                    {
                        Log.i(LOG, lstjob.get(jobcount -1).getName());
                        remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(jobcount - 1).getName());
                        lstjob.get(jobcount -1).isSelected(true);
                        db.updateOccupation(lstjob.get(jobcount -1));
                        db.close();
                        break;

                    }


                }


            }
            if(!bfound && jobcount != 0)
            {
                remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(0).getName());
                lstjob.get(0).isSelected(true);
                db.updateOccupation(lstjob.get(0));
            }
            Bundle extras = intent.getExtras();
            int id = extras.getInt("widgetId");
            appWidgetManager.updateAppWidget(id, remoteViews);
            return;
        }
        //endregion
        //region ButtonRight
        if(BUTTON_RIGHT_CLICKED.equals(intent.getAction()))
        {
            Log.i(LOG, "BUTTON RIGHT");
            int jobcount = lstjob.size();
            boolean bfound = false;

            for(int i = 0 ; i < jobcount ; i++)
            {
                if(lstjob.get(i).isIn())
                    return;
            }

            for(int i = 0; i < jobcount ; i++)
            {
                if(lstjob.get(i).isSelected())
                {
                    bfound = true;
                    lstjob.get(i).isSelected(false);
                    db.updateOccupation(lstjob.get(i));
                    if(i != (jobcount-1))
                    {
                        Log.i(LOG, lstjob.get(i+1).getName());
                        remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(i + 1).getName());
                        lstjob.get(i+1).isSelected(true);
                        db.updateOccupation(lstjob.get(i+1));
                        db.close();

                        break;
                    }
                    else
                    {
                        Log.i(LOG, lstjob.get(0).getName());
                        remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(0).getName());
                        lstjob.get(0).isSelected(true);
                        db.updateOccupation(lstjob.get(0));
                        db.close();

                        break;

                    }


                }


            }
            if(!bfound && jobcount != 0)
            {
                remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(0).getName());
                lstjob.get(0).isSelected(true);
                db.updateOccupation(lstjob.get(0));
                db.getOccupationHistoryFromOccId(lstjob.get(0).getId());
            }
            Bundle extras = intent.getExtras();
            int id = extras.getInt("widgetId");
            appWidgetManager.updateAppWidget(id, remoteViews);
            return;
        }
//endregion

        //region ButtonStart
        if(BUTTON_START_CLICKED.equals(intent.getAction()))
        {
            Log.i(LOG, "BUTTON Start");
            int jobcount = lstjob.size();
            boolean OccIn = false;
            List<OccupationHistory> lstHisto = new ArrayList<>();
            for(int i = 0 ; i < jobcount ; i++)
            {
                //Find a activity already in
                if(lstjob.get(i).isIn())
                {
                    lstHisto = db.getOccupationHistoryFromOccId(lstjob.get(i).getId());
                    int HistoCount = lstHisto.size();

                    for(int i2 = 0; i2 < HistoCount ; i2++)
                    {
                        if(lstHisto.get(i2).getDateTimeOut() == null)
                        {
                            OccupationHistory h = lstHisto.get(i2);
                            lstHisto.get(i2).setDateTimeOut(new Date());
                            db.updateOccupationHistory(lstHisto.get(i2));
                            remoteViews.setTextViewText(R.id.buttonStart,"Start");

                            OccIn = true;

                            lstjob.get(i).isIn(false);
                            db.updateOccupation(lstjob.get(i));
                            db.close();
                            break;
                        }
                    }
                }
            }

            if(!OccIn)
            {
                for(int i = 0 ; i < jobcount ; i++)
                {
                    if(lstjob.get(i).isSelected())
                    {
                        remoteViews.setTextViewText(R.id.buttonStart,"Stop");
                        OccupationHistory Histo = new OccupationHistory();
                        Histo.setOccupationId(lstjob.get(i).getId());
                        Histo.setDateTimeOut(null);
                        Histo.setDateTimeIn(new Date());
                        db.addOccupationHistory(Histo);
                        lstjob.get(i).isIn(true);
                        db.updateOccupation(lstjob.get(i));
                        db.close();
                        break;
                    }
                }
            }

            Bundle extras = intent.getExtras();
            int id = extras.getInt("widgetId");
            appWidgetManager.updateAppWidget(id, remoteViews);
            return;
        }
        //endregion

        if(BUTTON_VIEW_CLICKED.equals(intent.getAction()))
        {
            Log.d("ONSERVICE","VIEWCLICKED");
            Intent i = new Intent();
            i.setClass(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return;
        }

        if(ALARM_TICK.equals(intent.getAction()))
        {
            Log.d("ONSERVICE","ALARMTICK");
            Random r = new Random();
            int i1 = r.nextInt(100);
            String s = Integer.toString(i1);
            remoteViews.setTextViewText(R.id.txtTime,s);
            Bundle extras = intent.getExtras();
            int id = extras.getInt("widgetId");
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
            int jobcount = lstjob.size();
            boolean bfound = false;
            for(int i = 0; i < jobcount ; i++)
            {
                if (lstjob.get(i).isSelected())
                {
                    remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(i).getName());
                    bfound = true;
                    break;
                }
            }
            if(!bfound && jobcount != 0)
            {
                remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(0).getName());
                lstjob.get(0).isSelected(true);
                db.updateOccupation(lstjob.get(0));
            }


            Context context = getApplicationContext();

            Intent buttonOneIntent = new Intent(context, UpdateService.class);
            Intent buttonTwoIntent = new Intent(context, UpdateService.class);
            Intent buttonThreeIntent = new Intent(context, UpdateService.class);
            Intent buttonFourIntent = new Intent(context, UpdateService.class);
            Intent tickIntent = new Intent(context,UpdateService.class);

            // set action
            buttonOneIntent.setAction(BUTTON_LEFT_CLICKED);
            buttonTwoIntent.setAction(BUTTON_RIGHT_CLICKED);
            buttonThreeIntent.setAction(BUTTON_START_CLICKED);
            buttonFourIntent.setAction(BUTTON_VIEW_CLICKED);
            tickIntent.setAction(ALARM_TICK);

            // put widgetId
            buttonOneIntent.putExtra("widgetId", widgetId);
            buttonTwoIntent.putExtra("widgetId", widgetId);
            buttonThreeIntent.putExtra("widgetId", widgetId);
            buttonFourIntent.putExtra("widgetId", widgetId);
            tickIntent.putExtra("widgetId",widgetId);

            // make these intents unique to avoid collisions
            buttonOneIntent.setData(Uri.withAppendedPath(Uri.parse("widget://buttonleft/widgetid"), String.valueOf(widgetId)));
            buttonTwoIntent.setData(Uri.withAppendedPath(Uri.parse("widget://buttonright/widgetid"), String.valueOf(widgetId)));
            buttonThreeIntent.setData(Uri.withAppendedPath(Uri.parse("widget://buttonstart/widgetid"), String.valueOf(widgetId)));
            buttonFourIntent.setData(Uri.withAppendedPath(Uri.parse("widget://buttonView/widgetid"), String.valueOf(widgetId)));
            tickIntent.setData(Uri.withAppendedPath(Uri.parse("widget://AlarmTick/widgetid"), String.valueOf(widgetId)));

            // pending intents
            PendingIntent buttonOnePendingIntent = PendingIntent.getService(context, 0, buttonOneIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonTwoPendingIntent = PendingIntent.getService(context, 0, buttonTwoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonThreePendingIntent = PendingIntent.getService(context, 0, buttonThreeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonFourPendingIntent = PendingIntent.getService(context, 0, buttonFourIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent alarmIntent = PendingIntent.getService(context, 0, tickIntent, PendingIntent.FLAG_UPDATE_CURRENT);



            // register onClickListeners to your buttons
            remoteViews.setOnClickPendingIntent(R.id.buttonLeft, buttonOnePendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.buttonRight, buttonTwoPendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.buttonStart, buttonThreePendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.layoutWidget, buttonFourPendingIntent);


            appWidgetManager.updateAppWidget(widgetId, remoteViews);

            alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                   AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
        }
        stopSelf();

        super.onStart(intent, startId);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}