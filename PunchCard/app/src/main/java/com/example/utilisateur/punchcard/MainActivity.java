package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ListActivity {

    private final String TAG = "jeanrene";



    /**
     * A simple array adapter that creates a list of cheeses.
     */
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Job.JOBS.length;
        }

        @Override
        public String getItem(int position) {
            return Job.JOBS[position];
        }

        @Override
        public long getItemId(int position) {
            return Job.JOBS[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
            }

            if (convertView == null) {
                Log.d(TAG, "getView()");
            }


            ((TextView) convertView.findViewById(R.id.act_name))
                    .setText(getItem(position));

            ((TextView) convertView.findViewById(R.id.act_status))
                    .setText("5h 57min");

            return convertView;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListAdapter(new MyAdapter());
    }



    // event click sur une job
    public void onClickJob(View view) {
        startActivity(new Intent("PunchCard.History"));

    }

    // event click sur une job
    public void onClickAdd(View view) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add);
        dialog.setTitle("Add job");

        // button SAVE
        Button btnSave = (Button)dialog.findViewById(R.id.btn_add_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("PunchCard.History"));
            }
        });

        // button SET PARAMETERS
        Button btnParameters = (Button)dialog.findViewById(R.id.btn_add_set_parameters);
        btnParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("PunchCard.History"));
            }
        });

        dialog.show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
