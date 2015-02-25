package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;


public class MainActivity extends ListActivity implements IListViewContainer
{
    private AdapterOccupation _adapter;
    private OccupationParameters _tempParam = new OccupationParameters();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _adapter = new AdapterOccupation(this, this);

        setListAdapter(_adapter);
    }


    private void initListView()
    {
        View v = findViewById(R.id.list_item_job);

        if (v == null)
        {
            int stoip = 0;
        }
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

  //. @Override
  //. public boolean onCreateOptionsMenu(Menu menu)
  //. {
  //.     getMenuInflater().inflate(R.menu.menu_main, menu);
  //.     return true;
  //. }


  //. @Override
  //. public boolean onOptionsItemSelected(MenuItem item)
  //. {
  //.     return super.onOptionsItemSelected(item);
  //. }



    // event click sur une job de la liste
    public void onClickJob(View view)
    {
        Intent intent = new Intent("PunchCard.ActivityHistory");

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_add);
        builder.setTitle("Add job");

        final AlertDialog alertDialog = builder.create();
        final Activity act = this;

        alertDialog.show();

        final Button btnSave = (Button)alertDialog.findViewById(R.id.btn_add_save);


        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText txtBox = (EditText)alertDialog.findViewById(R.id.edit_name);



                if(txtBox.getText().toString().length() > 20) {
                    final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(alertDialog.getContext());
                    dlgAlert.setMessage("Name too long");
                    dlgAlert.setTitle("Error");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    return;
                }
                Occupation occupation = new Occupation();
                occupation.setName(txtBox.getText().toString());
                occupation.isIn(false);
                occupation.isSelected(false);

                occupation = addOccupation(occupation, _tempParam);
                refreshListView();

                alertDialog.dismiss();
            }
        });
    }


    private Occupation addOccupation(Occupation occupation, OccupationParameters parameters)
    {
        DataBaseHandler db = new DataBaseHandler(this);
        db.addOccupation(occupation);

        Occupation occ = db.getOccupation(occupation.getName());
        parameters.setOccupationId(occ.getId());

        db.addParameters(parameters);

        return occ;
    }

    @Override
    public void refreshListView()
    {
        _adapter = new AdapterOccupation(this, this);

        _adapter.notifyDataSetInvalidated();
        setListAdapter(_adapter);
    }
}