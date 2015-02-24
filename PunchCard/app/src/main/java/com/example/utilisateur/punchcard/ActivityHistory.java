package com.example.utilisateur.punchcard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by jrsao on 2/23/2015.
 *
 * Class activity des historiques d'un occupation specifique
 */
public class ActivityHistory extends Activity
{
    private int _occupationid = 0;
    private ExpandableListAdapter _listAdapter;
    private ExpandableListView _expandableListView;
    private List<String> _listDataHeader;
    private HashMap<String, List<OccupationHistory>> _listDataChild;
    private DataBaseHandler _db;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        _db = new DataBaseHandler(this);
        _occupationid = getIntent().getIntExtra("id", 0);

        String name = getIntent().getStringExtra("name");

        setTitle(name);

        initExpandableList();
    }


    /**
     * Initialise la liste extensible et l'adapter
     */
    private void initExpandableList()
    {
        _expandableListView = (ExpandableListView)findViewById(R.id.list_expandable_history);

        setListData();

        _listAdapter = new ExpandableListAdapter(this, _listDataHeader, _listDataChild);

        _expandableListView.setAdapter(_listAdapter);

        _expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                //  String name = ((TextView)v.findViewById(R.id.act_name)).getText().toString();

                Intent intent = new Intent("PunchCard.ActivityHistorySetting");

                int extraId = (int)id;
                intent.putExtra("Occid", 0);
                intent.putExtra("id", extraId);
                //   intent.putExtra("name", name);

                startActivityForResult(intent, 1);
                return true;
            }
        });

        _expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                ExpandableListView listView = (ExpandableListView) parent;
                long pos = listView.getExpandableListPosition(position);

                // get type and correct positions
                int itemType = ExpandableListView.getPackedPositionType(pos);
                int groupPos = ExpandableListView.getPackedPositionGroup(pos);
                int childPos = ExpandableListView.getPackedPositionChild(pos);

                // if child is long-clicked
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
                {
                    showPopupMenu(view, (int) _listAdapter.getChildId(groupPos, childPos), true);
                }
                return true;
            }
        });}

    private void showPopupMenu(final View convertView, final int historyId, final boolean set)
    {

        PopupMenu popupMenu = new PopupMenu(this, convertView);
        popupMenu.inflate(R.menu.popup_menu_history);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.popup_history_item_delete:
                        return true;

                    case R.id.popup_history_item_set:
                        // TODO rafraichir la liste apres un set
                        OccupationHistory occupationHistory = _db.getOccupationHistory(historyId);
                        occupationHistory.isPeriodEnd(true);
                        _db.updateOccupationHistory(occupationHistory);
                        return true;
                }

                return false;
            }
        });

        popupMenu.show();
    }

    /**
     * Ajoute les tous les historiques relier a l'occupation a la liste extensible
     */
    private void setListData()
    {
        _listDataChild = new HashMap<>();
        _listDataHeader = new ArrayList<>();

        DataBaseHandler db = new DataBaseHandler(this);

        List<OccupationHistory> occss = db.getOccupationHistoryFromOccId(_occupationid);
        TreeSet<OccupationHistory> sorted = new TreeSet<>(new ComparatorOccupationHistory());
        sorted.addAll(db.getOccupationHistoryFromOccId(_occupationid));


        List<OccupationHistory> tempList = new ArrayList<>();

        for(OccupationHistory history : sorted)
        {

            tempList.add(history);

            if (history.isPeriodEnd())
            {
                Date endPoint = history.getDateTimeIn();

                _listDataHeader.add(Tools.formatDateCanada(endPoint));
                _listDataChild.put(_listDataHeader.get(_listDataHeader.size() -1), tempList);
                tempList.clear();
            }

        }

        _listDataHeader.add("Current period");
        _listDataChild.put(_listDataHeader.get(_listDataHeader.size() -1), tempList);




/*


        _listDataHeader = new ArrayList<>();
        _listDataChild = new HashMap<>();
        List<OccupationHistory> headers = new ArrayList<>();

        headers.addAll(db.getAllEndPeriod(true, _occupationid));
        OccupationHistory currentPeriod = new OccupationHistory(0, null, null);
        headers.add(currentPeriod);


        // TODO alogrithme qui fonctionne comme il faut avec la requete getAllOccupationInPeriod

        OccupationHistory lastParent = null;
        int index = 0;

        // ajoute les enfants aux parents
        if (headers != null)
        {
            for (OccupationHistory parent : headers)
            {
                Date periodStart = (lastParent != null) ? lastParent.getDateTimeIn() : null;
                Date periodEnd = (parent != null) ? parent.getDateTimeIn() : null;

                Date parentStart = parent.getDateTimeIn();
                String startStr = (parentStart != null) ? Tools.formatDateCanada(parentStart): "-";
                String header = "Period " + startStr;

                _listDataHeader.add(header);

                List<OccupationHistory> children = db.getAllOccupationInPeriod(
                        periodStart, periodEnd, _occupationid);

                if (children != null)
                {
                    String item = _listDataHeader.get(index);
                    if (item == null)
                    {
                        int st = 0;
                    }
                    _listDataChild.put(_listDataHeader.get(index), children);
                }
                else
                {
                    children = new ArrayList<>();
                    _listDataChild.put(header, children);
                }

                index++;
                lastParent = parent;
            }
        }
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

  // @Override
  // public boolean onCreateOptionsMenu(Menu menu) {
  //     getMenuInflater().inflate(R.menu.menu_history, menu);
  //     return true;
  // }

  // @Override
  // public boolean onOptionsItemSelected(MenuItem item) {
  //     int id = item.getItemId();

  //     if (id == R.id.action_settings) {
  //         return true;
  //     }

  //     return super.onOptionsItemSelected(item);
  // }

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
