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


    //### true en mode test #####
    public static final boolean TEST = true;
    //###########################

    private AdapterOccupation _adapter;
    private OccupationParameters _tempParam = new OccupationParameters();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _adapter = new AdapterOccupation(this, this);

        setListAdapter(_adapter);


        //initListView();
        //--------- Test ---------
        TestBd();
        //------------------------

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
        // devrait etre un fragment au lieu d'un alert Dialog

        // ????????????
        //
        // add new Occupation
        // add new Parameters avec OccId
        // quand click sur btn save fait update sur Occupation
        //
        // ????????????

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_add);
        builder.setTitle("Add job");

        final AlertDialog alertDialog = builder.create();
        final Activity act = this;

        alertDialog.show();

        // button SAVE
        Button btnSave = (Button)alertDialog.findViewById(R.id.btn_add_save);


        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText txtBox = (EditText)alertDialog.findViewById(R.id.edit_name);

                Occupation occupation = new Occupation();
                occupation.setName(txtBox.getText().toString());
                occupation.isIn(false);
                occupation.isSelected(false);

                addOccupation(occupation, _tempParam);
                refreshListView();
                alertDialog.dismiss();
            }
        });



        // button SET PARAMETERS
        Button btnParameters = (Button)alertDialog.findViewById(R.id.btn_add_set_parameters);
        btnParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent("PunchCard.Parameters");

                intent.putExtra(OccupationParameters.Parameters.NB_WEEK_RESET.getValue(),
                        _tempParam.getNbDayBeforeReset()
                );
                intent.putExtra(OccupationParameters.Parameters.RESET_DAY.getValue(),
                        _tempParam.getResetDay().getValue()
                );
                intent.putExtra(OccupationParameters.Parameters.ROUND_MINUTE.getValue(),
                        _tempParam.getRoundMinuteValue()
                );
                intent.putExtra(OccupationParameters.Parameters.ROUND_TYPE.getValue(),
                        _tempParam.getRoundType()
                );

                startActivityForResult(intent, 3);
                //startActivity(intent);
            }
        });


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // retour ActivityParameters
        if (requestCode == 3)
        {
            if (resultCode == RESULT_OK)
            {
                int test = data.getIntExtra(
                        OccupationParameters.Parameters.NB_WEEK_RESET.getValue(),
                        0
                );

                _tempParam.setNbDayBeforeReset(test);
            }
        }
    }


    private void addOccupation(Occupation occupation, OccupationParameters parameters)
    {
        DataBaseHandler db = new DataBaseHandler(this);
        db.addOccupation(occupation);

        Occupation occ = db.getOccupation(occupation.getName());
        parameters.setOccupationId(occ.getId());

        db.addParameters(parameters);
    }


    /**
     * Methode pour appeler les tests
     */
    private void TestBd()
    {
        if (!TEST)
            return;

       // DataBaseTest.allHistory(this);
        DataBaseTest.getFirstParameter(this);
       // DataBaseTest.allOccupation(this);
       // DataBaseTest.clearOccupation(this);
       // DataBaseTest.allHistoryFromOccupation(this);
       // DataBaseTest.clearHistoryTable(this);
       // DataBaseTest.addRandomHistory(this);
    }

    @Override
    public void refreshListView()
    {
        _adapter = new AdapterOccupation(this, this);

        _adapter.notifyDataSetInvalidated();
        setListAdapter(_adapter);
    }
}