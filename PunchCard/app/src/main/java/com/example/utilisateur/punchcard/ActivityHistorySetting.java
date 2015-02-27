package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mathew on 2015-02-20.
 */
public class ActivityHistorySetting extends Activity
{
    private OccupationHistory _history;
    DataBaseHandler db = new DataBaseHandler(this);
    String TotalHour;
    String Timein;
    String TimeOut;
    Occupation occ;
    int Occid;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historysettings);


        //id 0 si add
        //Occid 0 si modify
        int id = getIntent().getIntExtra("id", 0);
        Occid = getIntent().getIntExtra("Occid", 0);

        //Modify
        if(id != 0)
        {
            _history = db.getOccupationHistory(id);
        }
        else //add
        _history = new OccupationHistory();
        _history.setDateTimeIn(new Date()) ;
        //Add
        if(Occid != 0)
        {
            _history.setOccupationId(Occid);
        }

        String name = getIntent().getStringExtra("name");
        if (name == null){
            name = "-";
        }
        setTitle(name);
        occ = db.getOccupation(_history.getOccupationId());
        //Update total time
        updateTotal();
    }

    /**
     * Update le temps total affiché
     */
    private void updateTotal()
    {
        Date timein = _history.getDateTimeIn();
        Date timeout = _history.getDateTimeOut();
        Date out = new Date();
        //default
        if(timein == null)
        {
            TotalHour ="00:00";
        }
        else
        {
            if(timeout != null)
                out = timeout;
            //get dif
            long dif = (out.getTime() - timein.getTime());
            TotalHour = Tools.formatDifftoStringSec(dif);
        }


        Timein = (timein != null) ? Tools.formatCustomDateTime(timein) : "-";
        TimeOut = (timeout != null) ? Tools.formatCustomDateTime(timeout) : "-";

       TextView txtin = (TextView)findViewById(R.id.txtTimeinvalue);
        txtin.setText(Timein);

       TextView txtout = (TextView)findViewById(R.id.txtTimeoutvalue);
        txtout.setText(TimeOut);

        TextView txtTotal = (TextView)findViewById(R.id.txtTotalTimevalue);
        txtTotal.setText(TotalHour);

    }

    /**
     * Pop dialog pour changer le time out
     * @param view
     */
    public void TimeoutClick(View view)
    {
        //time out dialog
        final OccupationParameters params = db.getParametersByOccupationId(_history.getOccupationId());
        final OccupationParameters.RoundType rType = params.getRoundType();
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_logo);
        builder.setTitle("Set Time Out");
        builder.setView(R.layout.dialog_timein);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView ok = (TextView)alertDialog.findViewById(R.id.btn_Timeinsave);
        TextView cancel = (TextView)alertDialog.findViewById(R.id.btn_TimeinCancel);

        final DatePicker dp = (DatePicker)alertDialog.findViewById(R.id.datePicker);
        final TimePicker tp = (TimePicker)alertDialog.findViewById(R.id.timePicker);

        //set pickers value
        Date date = _history.getDateTimeOut();
        if(date == null)
        {
            date = new Date();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        dp.setMaxDate(new Date().getTime());
        dp.setMinDate(_history.getDateTimeIn().getTime());
        tp.setCurrentMinute(minutes);

        tp.setCurrentHour(hour);
        dp.updateDate(year,month,day);


            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //set date value with picker value
                    Date date = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
                    cal.set(Calendar.MONTH, dp.getMonth());
                    cal.set(Calendar.YEAR, dp.getYear());
                    cal.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
                    cal.set(Calendar.MINUTE, tp.getCurrentMinute());
                    date.setTime(cal.getTimeInMillis());

                    //cannot time out before time in
                    if(_history.getDateTimeIn().getTime() > date.getTime())
                    {
                        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(alertDialog.getContext() );
                        dlgAlert.setMessage("You cannot Time out before the Time in");
                        dlgAlert.setTitle("Error");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                    }
                    else {
                        //rounding
                        int minuteparam = params.getRoundMinuteValue();
                        int unroundedMinutes = cal.get(Calendar.MINUTE);
                        int mod = unroundedMinutes % minuteparam;
                        switch (rType)
                        {
                            case ROUND_DOWN:
                            {
                                cal.add(Calendar.MINUTE, -mod);
                                break;
                            }
                            case ROUND_NORMAL:
                            {
                                if(mod <= minuteparam /2)
                                    cal.add(Calendar.MINUTE,-mod);
                                else {
                                    int i2 = minuteparam - mod;
                                    cal.add(Calendar.MINUTE, i2);
                                }
                                break;
                            }
                            case ROUND_UP:
                            {
                                cal.add(Calendar.MINUTE, mod);
                                break;
                            }
                        }
                        cal.set(Calendar.SECOND,0);
                        _history.setDateTimeOut(cal.getTime());

                        occ.isIn(false);
                        alertDialog.dismiss();
                        updateTotal();
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

    }

    /**
     * Pop Dialog pour change le time in
     * @param view
     */
    public void timeinClick(View view)
    {
        final OccupationParameters params = db.getParametersByOccupationId(_history.getOccupationId());
        final OccupationParameters.RoundType rType = params.getRoundType();

            AlertDialog.Builder builder =  new AlertDialog.Builder(this);

            builder.setIcon(R.drawable.ic_logo);
            builder.setTitle("Set Time in");
            builder.setView(R.layout.dialog_timein);

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            TextView ok = (TextView)alertDialog.findViewById(R.id.btn_Timeinsave);
            TextView cancel = (TextView)alertDialog.findViewById(R.id.btn_TimeinCancel);
            final DatePicker dp = (DatePicker)alertDialog.findViewById(R.id.datePicker);
            final TimePicker tp = (TimePicker)alertDialog.findViewById(R.id.timePicker);

            //set picker time
            Date date = _history.getDateTimeIn();
            Calendar cal = Calendar.getInstance();
            if(date == null)
            {
                date = new Date();
            }
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minutes = cal.get(Calendar.MINUTE);
            tp.setCurrentMinute(minutes);
            tp.setCurrentHour(hour);
            dp.setMaxDate(new Date().getTime());
            dp.updateDate(year,month,day);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //set time in time with picker value
                    Date date = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
                    cal.set(Calendar.MONTH, dp.getMonth());
                    cal.set(Calendar.YEAR, dp.getYear());
                    cal.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
                    cal.set(Calendar.MINUTE, tp.getCurrentMinute());
                    date.setTime(cal.getTimeInMillis());

                    if(date.getTime() > new Date().getTime())
                    {
                        //if value if in the future, pop alert dialog
                        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(alertDialog.getContext() );
                        dlgAlert.setMessage("You cannot Time in in the future");
                        dlgAlert.setTitle("Error");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                    }
                    else
                    {
                        //round with setting value
                        int minuteparam = params.getRoundMinuteValue();
                        int unroundedMinutes = cal.get(Calendar.MINUTE);
                        int mod = unroundedMinutes % minuteparam;
                        switch (rType)
                        {
                            case ROUND_DOWN:
                            {
                                cal.add(Calendar.MINUTE, -mod);
                                break;
                            }
                            case ROUND_NORMAL:
                            {
                                if(mod <= minuteparam /2)
                                    cal.add(Calendar.MINUTE,-mod);
                                else {
                                    int i2 = minuteparam - mod;
                                    cal.add(Calendar.MINUTE, i2);
                                }
                                break;
                            }
                            case ROUND_UP:
                            {
                                cal.add(Calendar.MINUTE, mod);
                                break;
                            }
                        }
                        cal.set(Calendar.SECOND,0);
                        _history.setDateTimeIn(cal.getTime());
                        alertDialog.dismiss();
                        //new total
                        updateTotal();
                    }


                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

    }


    /**
     * Enregistre au click du bouton ok
     * @param view
     */
    public void onClickOk(View view)
    {
        //add
        if(Occid != 0) {
            db.addOccupationHistory(_history);
        }
        else //modify
        {
            if(_history.getDateTimeOut() == null || _history.getDateTimeIn() == null) {
                this.finish();
            }

            db.updateOccupationHistory(_history);
            db.updateOccupation(occ);
        }
        //close activity
        this.finish();
    }

    /**
     * Ferme au click de cancel
     * @param view
     */
    public void onClickCancel(View view)
    {
        if(Occid != 0)//if add, delete it
        {
            db.deleteOccupationHistory(_history);
        }
        //else just dont update
       this.finish();
    }
}
