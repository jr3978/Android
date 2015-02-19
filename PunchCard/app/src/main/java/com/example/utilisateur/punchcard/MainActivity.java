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

import java.util.Date;
import java.util.List;


public class MainActivity extends ListActivity {


    //### true en mode test #####
    public static final boolean TEST = true;
    //###########################

    private AdapterOccupation _adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _adapter = new AdapterOccupation(this);

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


    // event click sur une job de la liste
    public void onClickJob(View view)
    {
        Intent intent = new Intent("PunchCard.History");

        int id = Integer.parseInt(
                ((TextView)view.findViewById(R.id.act_status)).getText().toString()
        );

        String name = ((TextView)view.findViewById(R.id.act_name)).getText().toString();

        intent.putExtra("id", id);
        intent.putExtra("name", name);

        startActivityForResult(intent, 1);
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
                startActivity(new Intent("PunchCard.Parameters"));
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


    /**
     * Methode pour appeler les tests
     */
    private void TestBd()
    {
        if (!TEST)
            return;

       // DataBaseTest.allOccupation(this);
       // DataBaseTest.clearOccupation(this);
       // DataBaseTest.allHistoryFromOccupation(this);
       // DataBaseTest.clearHistoryTable(this);
       // DataBaseTest.addRandomHistory(this);
    }
}