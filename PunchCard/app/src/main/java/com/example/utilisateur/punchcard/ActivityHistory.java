package com.example.utilisateur.punchcard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Date;


public class ActivityHistory extends ListActivity {

    private AdapterHistory _adapter;
    private int Occid = 0;
    OccupationHistory _temp;
    DataBaseHandler db = new DataBaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        ////// data from MainActivity nom et id de celle cliquer dans la liste
        Occid = getIntent().getIntExtra("id", 0);
        String name = getIntent().getStringExtra("name");

        setTitle(name);

        //call historique dans bd avec le id de l'occupation
        ////////////////////////////////////////////////////////////////////

        _adapter = new AdapterHistory(this, Occid);

        setListAdapter(_adapter);


    }

    public void onClickJob(View view)
    {
        Intent intent = new Intent("PunchCard.ActivityHistorySetting");

        int id = Integer.parseInt(
                ((TextView)view.findViewById(R.id.act_status)).getText().toString()
        );

        String name = ((TextView)view.findViewById(R.id.act_name)).getText().toString();
        _temp = db.getOccupationHistory(id);

        intent.putExtra("Occid", Occid);
        intent.putExtra("id", id);
        intent.putExtra("name", name);

        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // retour Modify
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {

                //TODO MODIFIER

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
