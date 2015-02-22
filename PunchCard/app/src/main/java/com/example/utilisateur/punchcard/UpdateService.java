package com.example.utilisateur.punchcard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    DataBaseHandler db = new DataBaseHandler(this);
    AppWidgetManager appWidgetManager;
    RemoteViews remoteViews;
    List<Occupation> lstjob;
    private AlarmManager alarmMgr;

    @Override
    public int onStartCommand(Intent intent,int flag, int startId)
    {
        appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widgetpunch);

        lstjob = db.getAllOccupations();
        Log.i(LOG, "Called");

        if(intent == null)
            return 1;



        //region ButtonLeft
        if(BUTTON_LEFT_CLICKED.equals(intent.getAction()))
        {
           LeftButtonClick(intent);
            return 1;
        }

        if(BUTTON_RIGHT_CLICKED.equals(intent.getAction()))
        {
            RightButtonClick(intent);
            return 1;
        }


        if(BUTTON_START_CLICKED.equals(intent.getAction()))
        {
           ButtonStartClick(lstjob,intent);
            return 1;
        }

        if(BUTTON_VIEW_CLICKED.equals(intent.getAction()))
        {
            WidgetClick();
            return 1;
        }

        if(ALARM_TICK.equals(intent.getAction()))
        {
           AlarmTick(intent);
            return 1;
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
            InitialUpdate(intent);

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

        super.onStartCommand(intent, flag,startId);
        return 0;
    }


    private void AlarmTick(Intent intent)
    {
        Log.d("ONSERVICE","ALARMTICK");

        Log.i(LOG, "BUTTON Start");
        int jobcount = lstjob.size();
        List<OccupationHistory> lstHisto = new ArrayList<>();
        boolean OccIn = false;

        for(int i = 0 ; i < jobcount ; i++)
        {
            //Find a activity already in
            if(lstjob.get(i).isIn())
            {
                lstHisto = db.getOccupationHistoryFromOccId(lstjob.get(i).getId());
                int HistoCount = lstHisto.size();
                for(int i2 = 0; i2 < HistoCount ; i2++)
                {
                    if (lstHisto.get(i2).getDateTimeOut() == null)
                    {
                        OccIn = true;
                        Date current = new Date();
                        Log.i(LOG, "DATE TICK : " + current);
                        Date datein = lstHisto.get(i2).getDateTimeIn();
                        Log.i(LOG, "DATEIN TICK : " + datein);
                        long dif = (current.getTime() - datein.getTime());
                        Log.i(LOG, "DIFF TICK : " + dif);
                        long diffHours = dif / (60 * 60 * 1000);
                        long diffMinutes = dif / (60 * 1000) % 60;
                        String txtheure = new String();
                        if(diffHours < 10)
                            txtheure += "0";
                        txtheure += Long.toString(diffHours);
                        txtheure += ":";
                        if(diffMinutes < 10)
                            txtheure += "0";
                        txtheure += Long.toString(diffMinutes);
                        remoteViews.setTextViewText(R.id.txtTime,txtheure);

                        Log.i(LOG, "ALARM TICK " + txtheure);

                    }
                }

            }
        }

        if(!OccIn)
        {
            remoteViews.setTextViewText(R.id.txtTime,"00:00");
        }


        Bundle extras = intent.getExtras();
        int id = extras.getInt("widgetId");
        appWidgetManager.partiallyUpdateAppWidget(id, remoteViews);
    }

    private void LeftButtonClick(Intent intent)
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
        appWidgetManager.partiallyUpdateAppWidget(id, remoteViews);
    }

    private void RightButtonClick(Intent intent)
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
        appWidgetManager.partiallyUpdateAppWidget(id, remoteViews);
    }

    private void ButtonStartClick(List<Occupation> lstjob, Intent intent)
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
                Log.i(LOG, "BUTTON Start ISIN");
                lstHisto = db.getOccupationHistoryFromOccId(lstjob.get(i).getId());
                int HistoCount = lstHisto.size();
                OccupationParameters params = db.getParametersByOccupationId(lstjob.get(i).getId());
                OccupationParameters.RoundType rType = params.getRoundType();
                int minutes = params.getRoundMinuteValue();

                Date whateverDateYouWant = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(whateverDateYouWant);
                int unroundedMinutes = calendar.get(Calendar.MINUTE);
                int mod = unroundedMinutes % minutes;
                switch (rType)
                {
                    case ROUND_DOWN:
                    {
                        calendar.add(Calendar.MINUTE, -mod);
                    }
                    case ROUND_NORMAL:
                    {
                        calendar.add(Calendar.MINUTE, mod <= minutes/2 ? -mod : (minutes-mod));
                    }
                    case ROUND_UP:
                    {
                        calendar.add(Calendar.MINUTE, mod);
                    }
                }


                for(int i2 = 0; i2 < HistoCount ; i2++)
                {
                    if(lstHisto.get(i2).getDateTimeOut() == null)
                    {
                        Log.i(LOG, "DATEOUT STOP : " + calendar.getTime());
                        lstHisto.get(i2).setDateTimeOut(calendar.getTime());
                        db.updateOccupationHistory(lstHisto.get(i2));
                        remoteViews.setTextViewText(R.id.buttonStart,"Start");
                        remoteViews.setTextViewText(R.id.txtTime,"00:00");


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
            Log.i(LOG, "BUTTON Start NOT IN");
            for(int i = 0 ; i < jobcount ; i++)
            {
                if(lstjob.get(i).isSelected())
                {
                    OccupationParameters params = db.getParametersByOccupationId(lstjob.get(i).getId());
                    OccupationParameters.RoundType rType = params.getRoundType();
                    int minutes = params.getRoundMinuteValue();


                    //Round date with params
                    Date whateverDateYouWant = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(whateverDateYouWant);
                    int unroundedMinutes = calendar.get(Calendar.MINUTE);
                    int mod = unroundedMinutes % minutes;
                    switch (rType)
                    {
                        case ROUND_DOWN:
                        {
                            calendar.add(Calendar.MINUTE, -mod);
                        }
                        case ROUND_NORMAL:
                        {
                            calendar.add(Calendar.MINUTE, mod <= minutes/2 ? -mod : (minutes-mod));
                        }
                        case ROUND_UP:
                        {
                            calendar.add(Calendar.MINUTE, mod);
                        }
                    }

                    remoteViews.setTextViewText(R.id.txtTime,"00:00");
                    remoteViews.setTextViewText(R.id.buttonStart,"Stop");
                    OccupationHistory Histo = new OccupationHistory();
                    Histo.setOccupationId(lstjob.get(i).getId());
                    Histo.setDateTimeOut(null);
                        Log.i(LOG, "DATEIN START : " + calendar.getTime());
                    Histo.setDateTimeIn(calendar.getTime());
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
        appWidgetManager.partiallyUpdateAppWidget(id, remoteViews);
        return;
    }

    private void WidgetClick()
    {
        Log.d("ONSERVICE","VIEWCLICKED");
        Intent i = new Intent();
        i.setClass(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void InitialUpdate(Intent intent)
    {
        Log.i(LOG, "InitialUpdate");
        int jobcount = lstjob.size();
        boolean OccIn = false;
        for(int i = 0 ; i < jobcount ; i++)
        {
            //Find a activity already in
            if(lstjob.get(i).isIn())
            {

                        remoteViews.setTextViewText(R.id.buttonStart,"Stop");
                        remoteViews.setTextViewText(R.id.stackWidgetView,lstjob.get(i).getName());
                        OccIn = true;
                        break;


            }
        }

        if(!OccIn)
        {
            for(int i = 0 ; i < jobcount ; i++)
            {
                if(lstjob.get(i).isSelected())
                {
                    remoteViews.setTextViewText(R.id.buttonStart,"Start");
                    remoteViews.setTextViewText(R.id.stackWidgetView,lstjob.get(i).getName());
                    break;
                }
            }
        }

        Bundle extras = intent.getExtras();
        int id = extras.getInt("widgetId");
        appWidgetManager.partiallyUpdateAppWidget(id, remoteViews);
        return;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {

        Log.i(LOG, "OnConfig change");
        Log.i(LOG, Integer.toString(newConfig.orientation));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}