package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.RemoteController;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends ListActivity {

    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * A simple array adapter that creates a list of cheeses.
     */
    private class MyAdapter extends BaseAdapter
    {
        private List<Occupation> _occupations;
        private Context _context;

        public MyAdapter(Context context)
        {
            _context = context;
            DataBaseHandler db = new DataBaseHandler(context);

            _occupations = db.getAllOccupations();
        }

        @Override
        public int getCount()
        {
            return _occupations.size();
        }

        @Override
        public Occupation getItem(int position)
        {
            return _occupations.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            //DataBaseHandler db = new DataBaseHandler(_context);
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container)
        {
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
            }

            if (convertView == null)
            {
                Log.d(TAG, "getView()");
            }


            ((TextView) convertView.findViewById(R.id.act_name))
                    .setText(getItem(position).getName());

            ((TextView) convertView.findViewById(R.id.act_status))
                    .setText(new Integer(getItem(position).getId()).toString());

            return convertView;
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////


    //### true en mode test #####
    public static final boolean TEST = false;
    //###########################

    private final String TAG = "jeanrene";
    private MyAdapter _adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _adapter = new MyAdapter(this);

        setListAdapter(_adapter);

        //--------- Test ---------
        TestBd();
        //------------------------
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // event click sur une job
    public void onClickJob(View view)
    {
        startActivity(new Intent("PunchCard.History"));
    }


    // event click sur une job
    public void onClickAdd(View view)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add);
        dialog.setTitle("Add job");

        // button SAVE
        Button btnSave = (Button)dialog.findViewById(R.id.btn_add_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                addOccupation(dialog);
                dialog.dismiss();
                _adapter.notifyDataSetChanged();
                setListAdapter(_adapter);
            }
        });

        // button SET PARAMETERS
        Button btnParameters = (Button)dialog.findViewById(R.id.btn_add_set_parameters);
        btnParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent("PunchCard.History"));
            }
        });

        dialog.show();
    }


    private void addOccupation(Dialog dialog)
    {
        DataBaseHandler db = new DataBaseHandler(this);

        EditText txtBox = (EditText)dialog.findViewById(R.id.edit_name);

        Occupation occupation = new Occupation();
        occupation.setName(txtBox.getText().toString());
        occupation.isIn(false);
        occupation.isSelected(false);

        db.addOccupation(occupation);
    }


    private void TestBd()
    {
        if (!TEST)
            return;

        DataBaseHandler db = new DataBaseHandler(this);
        /*
        Log.d("JRRRR" , db.getOccupation(7).getName());


        db.deleteAllOccupations();
        return;
*/
        /*
        List<Occupation> occ = db.getAllOccupations();

        String size  = new Integer(occ.size()).toString();
        Log.d("Total Occupation: ", size);

        int c  = 0;
        for(Occupation occu : occ)
        {
            Log.d("Occupation: " + c, occu.getName());
            c++;
        }*/
    }
}