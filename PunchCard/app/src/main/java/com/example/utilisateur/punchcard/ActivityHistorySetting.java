package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by utilisateur on 2015-02-20.
 */
public class ActivityHistorySetting extends Activity
{
    private OccupationHistory _history;
    DataBaseHandler db = new DataBaseHandler(this);
    int TotalHour = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historysettings);




        //id 0 si add
        //Occid 0 si modify
        int id = getIntent().getIntExtra("id", 0);
        int Occid = getIntent().getIntExtra("Occid", 0);

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
        TotalHour = 10;
        UpdateTotal();
    }

    private void UpdateTotal()
    {
        ListView lv = (ListView)findViewById(R.id.list);
        String[] values = new String[]
                {
                        "Time In", "Time out", "Total Time"
                };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.parameters_list_item, R.id.paramters_item, values);
        adapter.add(Integer.toString(TotalHour));
        adapter.notifyDataSetInvalidated();
        lv.setAdapter(adapter);

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
            builder.setView(R.layout.custom_dialog_nb_week);

            final AlertDialog alertDialog = builder.create();


            alertDialog.show();
        }

        if(value.equals("Time out"))
        {

        }


    }
}
