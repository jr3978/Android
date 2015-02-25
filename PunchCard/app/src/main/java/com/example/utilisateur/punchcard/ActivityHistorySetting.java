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

        //Add
        if(Occid != 0)
        {
            _history.setOccupationId(Occid);
            _history.setDateTimeIn(new Date());
            _history.setDateTimeOut(new Date());
        }

        String name = getIntent().getStringExtra("name");
        if (name == null){
            name = "-";
        }
        setTitle(name);

        //Update total time
        UpdateTotal();
    }

    //Update Total time
    private void UpdateTotal()
    {
        Date timein = _history.getDateTimeIn();
        Date timeout = _history.getDateTimeOut();
        if(timein == null || timeout == null)
        {
            TotalHour ="00:00";
        }
        else {
            long dif = (timeout.getTime() - timein.getTime());
            TotalHour = ToShortDateString(dif);
        }


        Timein = Tools.formatCustomDateTime(timein);
        TimeOut = Tools.formatCustomDateTime(timeout);

        ListView lv = (ListView)findViewById(R.id.lstHistorySet);
        String[] values = new String[]
                {
                        "Time In:        " + Timein, "Time out:      " + TimeOut, "Total Time : " + TotalHour
                };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.parameters_list_item, R.id.paramters_item, values);
        adapter.notifyDataSetInvalidated();
        lv.setAdapter(adapter);

    }

    public static int safeLongToInt(long l) {
        return (int) Math.max(Math.min(Integer.MAX_VALUE, l), Integer.MIN_VALUE);
    }

    public void onClickItemParameter(View view)
    {
        TextView textView = (TextView)view.findViewById(R.id.paramters_item);
        String value = textView.getText().toString();
        String name = getIntent().getStringExtra("name");
        final OccupationParameters params = db.getParametersByOccupationId(_history.getOccupationId());
        final OccupationParameters.RoundType rType = params.getRoundType();

        if(value.equals("Time In:        " + Timein))
        {
            AlertDialog.Builder builder =  new AlertDialog.Builder(this);

            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("Set Time in");
            builder.setView(R.layout.dialog_timein);

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            TextView ok = (TextView)alertDialog.findViewById(R.id.btn_Timeinsave);
            TextView cancel = (TextView)alertDialog.findViewById(R.id.btn_TimeinCancel);
            final DatePicker dp = (DatePicker)alertDialog.findViewById(R.id.datePicker);
            final TimePicker tp = (TimePicker)alertDialog.findViewById(R.id.timePicker);

            Date date = _history.getDateTimeIn();
            Calendar cal = Calendar.getInstance();
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
                        UpdateTotal();
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

        if(value.equals("Time out:      " + TimeOut))
        {
            AlertDialog.Builder builder =  new AlertDialog.Builder(this);

            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("Set Time Out");
            builder.setView(R.layout.dialog_timein);

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            TextView ok = (TextView)alertDialog.findViewById(R.id.btn_Timeinsave);
            TextView cancel = (TextView)alertDialog.findViewById(R.id.btn_TimeinCancel);

            final DatePicker dp = (DatePicker)alertDialog.findViewById(R.id.datePicker);
            final TimePicker tp = (TimePicker)alertDialog.findViewById(R.id.timePicker);

            Date date = _history.getDateTimeOut();
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

                    Date date = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
                    cal.set(Calendar.MONTH, dp.getMonth());
                    cal.set(Calendar.YEAR, dp.getYear());
                    cal.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
                    cal.set(Calendar.MINUTE, tp.getCurrentMinute());
                    date.setTime(cal.getTimeInMillis());

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
                        alertDialog.dismiss();
                        UpdateTotal();
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


        }

    public void onClickOk(View view)
    {
        if(Occid != 0) {
            db.addOccupationHistory(_history);
        }
        else
        {
            db.updateOccupationHistory(_history);
        }



        this.finish();
    }

    public void onClickCancel(View view)
    {
        if(Occid != 0)
        {
            db.deleteOccupationHistory(_history);
        }

       this.finish();
    }

    private String ToShortDateString(long milli)
    {
        String ToString;

        long diffHours = milli / (60 * 60 * 1000);
        long diffMinutes = milli / (60 * 1000) % 60;
        ToString = "";
        if (diffHours < 10)
            ToString += "0";
        ToString += Long.toString(diffHours);
        ToString += ":";
        if (diffMinutes < 10)
            ToString += "0";

        ToString += Long.toString(diffMinutes);
        return ToString;
    }
}
