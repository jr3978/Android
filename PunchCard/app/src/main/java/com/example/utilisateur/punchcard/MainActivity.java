package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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


    /*
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
    */
}
