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
 * Created by utilisateur on 2015-02-20.
 */
public class ActivityHistorySetting extends Activity
{
    private OccupationHistory _history;
    DataBaseHandler db = new DataBaseHandler(this);
    String TotalHour;
    DatePicker datepick;
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

        if(id != 0)
        {
            _history = db.getOccupationHistory(id);
        }
        else
        _history = new OccupationHistory();

        if(Occid != 0)
        {
            _history.setOccupationId(Occid);


        }

        String name = getIntent().getStringExtra("name");
        setTitle(name);

        UpdateTotal();

    }

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
            long diffHours = dif / (60 * 60 * 1000);
            long diffMinutes = dif / (60 * 1000) % 60;
            TotalHour = "";
            if (diffHours < 10)
                TotalHour += "0";
            TotalHour += Long.toString(diffHours);
            TotalHour += ":";
            if (diffMinutes < 10)
                TotalHour += "0";

            TotalHour += Long.toString(diffMinutes);
        }




        ListView lv = (ListView)findViewById(R.id.lstHistorySet);
        String[] values = new String[]
                {
                        "Time In", "Time out", "Total Time", TotalHour
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

        if(value.equals("Time In"))
        {
            AlertDialog.Builder builder =  new AlertDialog.Builder(this);

            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("Set Time in");
            builder.setView(R.layout.dialog_timein);

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Button ok = (Button)alertDialog.findViewById(R.id.btn_Timeinsave);
            Button cancel = (Button)alertDialog.findViewById(R.id.btn_TimeinCancel);
            final DatePicker dp = (DatePicker)alertDialog.findViewById(R.id.datePicker);
            final TimePicker tp = (TimePicker)alertDialog.findViewById(R.id.timePicker);

            Date date = _history.getDateTimeIn();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR);
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
                        _history.setDateTimeIn(date);
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

        if(value.equals("Time out"))
        {
            AlertDialog.Builder builder =  new AlertDialog.Builder(this);

            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("Set Time Out");
            builder.setView(R.layout.dialog_timein);

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Button ok = (Button)alertDialog.findViewById(R.id.btn_Timeinsave);
            Button cancel = (Button)alertDialog.findViewById(R.id.btn_TimeinCancel);

            final DatePicker dp = (DatePicker)alertDialog.findViewById(R.id.datePicker);
            final TimePicker tp = (TimePicker)alertDialog.findViewById(R.id.timePicker);

            Date date = _history.getDateTimeOut();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR);
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
                        _history.setDateTimeOut(date);
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
}
