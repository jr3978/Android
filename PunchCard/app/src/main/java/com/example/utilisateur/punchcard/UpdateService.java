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
 * Created by Mathew on 2015-02-18.
 */
public class UpdateService  extends Service
{
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
        //Widget view
        remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widgetpunch);

        //all activity
        lstjob = db.getAllOccupations();

        //return if no intent
        if(intent == null)
            return 1;

        String s = intent.getAction();
        if("UPDATE".equals(intent.getAction()))
        {
            InitialUpdate(intent);
            return 1;
        }

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



        //Default code when service startedd ( no button click)

        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        ComponentName thisWidget = new ComponentName(getApplicationContext(), WidgetPunch.class);
        //update every widget
        for (int widgetId : allWidgetIds)
        {
            int jobcount = lstjob.size();
            boolean bfound = false;
            for(int i = 0; i < jobcount ; i++)
            {
                //Default text at selected activity (if widget deleted of service restart)
                if (lstjob.get(i).isSelected())
                {
                    remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(i).getName());
                    bfound = true;
                    break;
                }
            }
            //If none if found, pick a default one
            if(!bfound && jobcount != 0)
            {
                remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(0).getName());
                lstjob.get(0).isSelected(true);
                db.updateOccupation(lstjob.get(0));
            }
            //Update
            InitialUpdate(intent);

            Context context = getApplicationContext();

            //Create button and alarm intent
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



            // register onClickListeners to  buttons
            remoteViews.setOnClickPendingIntent(R.id.buttonLeft, buttonOnePendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.buttonRight, buttonTwoPendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.buttonStart, buttonThreePendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.layoutWidget, buttonFourPendingIntent);

            //update widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

            //set alarm
            alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                   AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
        }
        //stop service
        stopSelf();

        super.onStartCommand(intent, flag,startId);
        return 0;
    }

    /**
     * Change le text du temps lors du tick de l'alarm
     * @param intent
     */
    private void AlarmTick(Intent intent)
    {
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
                        //find time diff between now and punch in
                        Date current = new Date();
                        Date datein = lstHisto.get(i2).getDateTimeIn();
                        long dif = (current.getTime() - datein.getTime());
                        String txtheure = Tools.formatDifftoString(dif);
                        remoteViews.setTextViewText(R.id.txtTime,txtheure);
                    }
                }
            }
        }

        //If no activity in, default time value
        if(!OccIn)
        {
            remoteViews.setTextViewText(R.id.txtTime,"00:00");
        }

        db.close();
        Bundle extras = intent.getExtras();
        int id = extras.getInt("widgetId");
        //update only textview
        appWidgetManager.partiallyUpdateAppWidget(id, remoteViews);
    }

    /**
     * Change l'activity selectionner lors d'un click sur fleche gauche
     * @param intent
     */
    private void LeftButtonClick(Intent intent)
    {
        int jobcount = lstjob.size();
        boolean bfound = false;
        for(int i = 0 ; i < jobcount ; i++)
        {
            //wont change if an activity is punched in
            if(lstjob.get(i).isIn())
                return;
        }

        for(int i = 0; i < jobcount ; i++)
        {
            //check wich one is selected
            if(lstjob.get(i).isSelected())
            {
                bfound = true;
                //set to false and change selected value
                lstjob.get(i).isSelected(false);
                db.updateOccupation(lstjob.get(i));
                if(i != 0)
                {
                    remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(i - 1).getName());
                    lstjob.get(i-1).isSelected(true);
                    db.updateOccupation(lstjob.get(i-1));
                    db.close();
                    break;
                }
                else
                {
                    remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(jobcount - 1).getName());
                    lstjob.get(jobcount -1).isSelected(true);
                    db.updateOccupation(lstjob.get(jobcount -1));
                    db.close();
                    break;

                }
            }
        }
        //if none if found, set default value
        if(!bfound && jobcount != 0)
        {
            remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(0).getName());
            lstjob.get(0).isSelected(true);
            db.updateOccupation(lstjob.get(0));
        }
        db.close();
        Bundle extras = intent.getExtras();
        int id = extras.getInt("widgetId");
        //update only textview
        appWidgetManager.partiallyUpdateAppWidget(id, remoteViews);
    }

    /**
     * Change l'activity selectionné lors d'un click sur fleche droite
     * @param intent
     */
    private void RightButtonClick(Intent intent)
    {
        int jobcount = lstjob.size();
        boolean bfound = false;
        for(int i = 0 ; i < jobcount ; i++)
        {
            //wont change value is an activity is punched in
            if(lstjob.get(i).isIn())
                return;
        }

        for(int i = 0; i < jobcount ; i++)
        {
            //Find selected one and change value
            if(lstjob.get(i).isSelected())
            {
                bfound = true;
                lstjob.get(i).isSelected(false);
                db.updateOccupation(lstjob.get(i));
                if(i != (jobcount-1))
                {
                    remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(i + 1).getName());
                    lstjob.get(i+1).isSelected(true);
                    db.updateOccupation(lstjob.get(i+1));
                    db.close();

                    break;
                }
                else
                {
                    remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(0).getName());
                    lstjob.get(0).isSelected(true);
                    db.updateOccupation(lstjob.get(0));
                    db.close();

                    break;

                }


            }


        }
        //default
        if(!bfound && jobcount != 0)
        {
            remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(0).getName());
            lstjob.get(0).isSelected(true);
            db.updateOccupation(lstjob.get(0));
            db.getOccupationHistoryFromOccId(lstjob.get(0).getId());
        }
        db.close();
        Bundle extras = intent.getExtras();
        int id = extras.getInt("widgetId");
        appWidgetManager.partiallyUpdateAppWidget(id, remoteViews);
    }

    /**
     * Punch in ou punch out selon l'état, lors d'un click sur bouton start/stop
     * @param lstjob
     * @param intent
     */
    private void ButtonStartClick(List<Occupation> lstjob, Intent intent)
    {
        int jobcount = lstjob.size();
        boolean OccIn = false;
        List<OccupationHistory> lstHisto = new ArrayList<>();
        for(int i = 0 ; i < jobcount ; i++)
        {
            //Find a activity already in
            //punch out
            if(lstjob.get(i).isIn())
            {
                lstHisto = db.getOccupationHistoryFromOccId(lstjob.get(i).getId());
                int HistoCount = lstHisto.size();
                OccupationParameters params = db.getParametersByOccupationId(lstjob.get(i).getId());
                OccupationParameters.RoundType rType = params.getRoundType();
                int minutes = params.getRoundMinuteValue();

                //round time value with settings
                Date d = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);
                int unroundedMinutes = calendar.get(Calendar.MINUTE);
                int mod = unroundedMinutes % minutes;
                switch (rType)
                {
                    case ROUND_DOWN:
                    {
                        calendar.add(Calendar.MINUTE, -mod);
                        break;
                    }
                    case ROUND_NORMAL:
                    {
                        if(mod <= minutes /2)
                            calendar.add(Calendar.MINUTE,-mod);
                        else {
                            int i2 = minutes - mod;
                            calendar.add(Calendar.MINUTE, i2);
                        }
                        break;
                    }
                    case ROUND_UP:
                    {
                        calendar.add(Calendar.MINUTE, mod);
                        break;
                    }
                }
                for(int i2 = 0; i2 < HistoCount ; i2++)
                {
                    //set time out and change text to start
                    if(lstHisto.get(i2).getDateTimeOut() == null)
                    {
                     //   calendar.set(Calendar.SECOND,0);
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

        //punch in
        if(!OccIn)
        {
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
                            break;
                        }
                        case ROUND_NORMAL:
                        {
                            calendar.add(Calendar.MINUTE, mod <= minutes/2 ? -mod : (minutes-mod));
                            break;
                        }
                        case ROUND_UP:
                        {
                            calendar.add(Calendar.MINUTE, mod);
                            break;
                        }
                    }
                 //   calendar.set(Calendar.SECOND,0);
                    //change to stop
                    remoteViews.setTextViewText(R.id.txtTime,"00:00");
                    remoteViews.setTextViewText(R.id.buttonStart,"Stop");

                    //create new histo and punch in
                    OccupationHistory Histo = new OccupationHistory();
                    Histo.setOccupationId(lstjob.get(i).getId());
                    Histo.setDateTimeOut(null);
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
        db.close();
        int id = extras.getInt("widgetId");
        appWidgetManager.partiallyUpdateAppWidget(id, remoteViews);
        return;
    }

    /**
     * Click sur le widget ,lance l'activité
     */
    private void WidgetClick()
    {
        Intent i = new Intent();
        i.setClass(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    /**
     * Update le widget pour lui donné le bon état
     * @param intent
     */
    private void InitialUpdate(Intent intent)
    {
        int jobcount = lstjob.size();
        boolean OccIn = false;
        boolean isselec = false;
        for(int i = 0 ; i < jobcount ; i++)
        {
            //Find a activity already in
            if(lstjob.get(i).isIn())
            {
                Log.d("INITITAL UPDATE","IS IN");
                        remoteViews.setTextViewText(R.id.buttonStart,"Stop");
                        remoteViews.setTextViewText(R.id.stackWidgetView,lstjob.get(i).getName());
                        OccIn = true;
                        isselec = true;
                        break;
            }
        }

        if(!OccIn)
        {
            for(int i = 0 ; i < jobcount ; i++)
            {
                if(lstjob.get(i).isSelected())
                {
                    isselec = true;
                    remoteViews.setTextViewText(R.id.buttonStart,"Start");
                    remoteViews.setTextViewText(R.id.stackWidgetView,lstjob.get(i).getName());
                    break;
                }
            }
        }

        if(!isselec)
        {
            remoteViews.setTextViewText(R.id.buttonStart,"Start");
            if(lstjob.size() > 0) {
                remoteViews.setTextViewText(R.id.stackWidgetView, lstjob.get(0).getName());
            }
            else {
                remoteViews.setTextViewText(R.id.stackWidgetView, "No Activity");
            }


        }

        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetPunch.class));
        for(int id:ids)
        {
            appWidgetManager.partiallyUpdateAppWidget(id, remoteViews);
        }
        db.close();
        return;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}