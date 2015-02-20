package com.example.utilisateur.punchcard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ActivityHistory extends ListActivity {

    private AdapterHistory _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        ////// data from MainActivity nom et id de celle cliquer dans la liste
        int id = getIntent().getIntExtra("id", 0);
        String name = getIntent().getStringExtra("name");

        setTitle(name);

        //call historique dans bd avec le id de l'occupation
        ////////////////////////////////////////////////////////////////////

        _adapter = new AdapterHistory(this, id);

        setListAdapter(_adapter);


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
