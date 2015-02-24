package com.example.utilisateur.punchcard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ActivityHistory extends Activity
{
    private int _occupationid = 0;
    private ExpandableListAdapter _listAdapter;
    private ExpandableListView _expandableListView;
    private List<OccupationHistory> _listDataHeader;
    private HashMap<OccupationHistory, List<OccupationHistory>> _listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        ////// data from MainActivity nom et id de celle cliquer dans la liste
        _occupationid = getIntent().getIntExtra("id", 0);
        String name = getIntent().getStringExtra("name");

        setTitle(name);

        //call historique dans bd avec le id de l'occupation
        ////////////////////////////////////////////////////////////////////

        _expandableListView = (ExpandableListView)findViewById(R.id.list_expandable_history);

        setListData();

        _listAdapter = new ExpandableListAdapter(this, _listDataHeader, _listDataChild);

        _expandableListView.setAdapter(_listAdapter);

        _expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
            {
                //feedback toast
                return false;
            }
        });

        _expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
        {
            @Override
            public void onGroupExpand(int groupPosition)
            {

            }
        });


        /*
        _adapter = new AdapterHistory(this, Occid);

        setListAdapter(_adapter);*/


    }

    private void setListData()
    {
        DataBaseHandler db = new DataBaseHandler(this);

        // ajoute les parents
        _listDataHeader = new ArrayList<>();
        _listDataHeader.add(new OccupationHistory(_occupationid, null, null));
        _listDataHeader.addAll(db.getAllEndPeriod(true, _occupationid));

        _listDataChild = new HashMap<>();

        OccupationHistory lastParent = null;
        int index = 0;

        // ajoute les enfants aux parents
        if (_listDataHeader != null)
        {
            for (OccupationHistory parent : _listDataHeader)
            {
                Date start = (lastParent != null) ? lastParent.getDateTimeIn() : null;
                Date end = (parent != null) ? parent.getDateTimeIn() : null;

                List<OccupationHistory> children = db.getAllOccupationInPeriod(
                        start, end, _occupationid);

                if (children != null)
                {
                    _listDataChild.put(_listDataHeader.get(index), children);
                }

                index++;
                lastParent = parent;
            }
        }


        /*
        TEST POUR VOIR DE QUOI LA LISTE A L'AIR

        OccupationHistory occ = new OccupationHistory(1, new Date(), new Date(2015,03,02));
        OccupationHistory occ1 = new OccupationHistory(1, new Date(2000,05,05), new Date(2015,03,02));
        OccupationHistory occ2 = new OccupationHistory(1, new Date(2000,1,05), new Date(2015,03,02));
        OccupationHistory occ3 = new OccupationHistory(1, new Date(), new Date(2015,03,02));
        OccupationHistory occ4 = new OccupationHistory(1, new Date(2002,05,05), new Date(2015,03,02));
        OccupationHistory occ5 = new OccupationHistory(1, new Date(), new Date(2015,03,02));
        OccupationHistory occ6 = new OccupationHistory(1, new Date(2012,12,1), new Date(2015,03,02));
        OccupationHistory occ7 = new OccupationHistory(1, new Date(2000,05,05), new Date(2015,03,02));
        OccupationHistory occ8 = new OccupationHistory(1, new Date(), new Date(2015,03,02));
        OccupationHistory occ9 = new OccupationHistory(1, new Date(), new Date(2015,03,02));
        OccupationHistory occ10 = new OccupationHistory(1, new Date(), new Date(2015,03,02));

       // _listDataHeader = db.getAllEndPeriod(true);

        _listDataHeader.add(occ);
        _listDataHeader.add(occ5);
        _listDataHeader.add(occ8);

        List<OccupationHistory> child = new ArrayList<>();
        child.add(occ1);
        child.add(occ2);
        child.add(occ3);
        child.add(occ4);

        List<OccupationHistory> child2 = new ArrayList<>();
        child2.add(occ6);
        child2.add(occ7);


        List<OccupationHistory> child3 = new ArrayList<>();
        child3.add(occ9);
        child3.add(occ10);

        _listDataChild.put(_listDataHeader.get(0), child);
        _listDataChild.put(_listDataHeader.get(1), child2);
        _listDataChild.put(_listDataHeader.get(2), child3);
        */
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        /*
        _listAdapter = new AdapterHistory(this, Occid);
        _adapter.notifyDataSetInvalidated();
        setListAdapter(_adapter);*/

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


    public void onClickJob(View view)
    {
        Intent intent = new Intent("PunchCard.ActivityHistorySetting");

        int id = Integer.parseInt(
                ((TextView)view.findViewById(R.id.act_status)).getText().toString()
        );

        String name = ((TextView)view.findViewById(R.id.act_name)).getText().toString();

        intent.putExtra("Occid", 0);
        intent.putExtra("id", id);
        intent.putExtra("name", name);

        startActivityForResult(intent, 1);
    }

    public void onClickAdd(View view)
    {
        Intent intent = new Intent("PunchCard.ActivityHistorySetting");


        String name = "New History";


        intent.putExtra("Occid", _occupationid);
        intent.putExtra("id", 0);
        intent.putExtra("name", name);

        startActivityForResult(intent,2);
    }
}
