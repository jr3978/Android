package com.example.utilisateur.punchcard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
import android.widget.Toast;

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
    private int _occupationId = 0;
    private ExpandableListAdapter _listAdapter;
    private ExpandableListView _expandableListView;
    private List<String> _listDataHeader;
    private HashMap<String, List<OccupationHistory>> _listDataChild;
    private DataBaseHandler _db;


    /**
     * initialise l'activity quand elle est creer
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        _db = new DataBaseHandler(this);
        _occupationId = getIntent().getIntExtra("id", 0);
        String name = getIntent().getStringExtra("name");
        setTitle(name);

        //refresh liste
        initExpandableList();
    }


    /**
     * Refresh when a activity returns
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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

                Intent intent = new Intent("PunchCard.ActivityHistorySetting");
                int extraId = (int)id;
                //0 == modify
                intent.putExtra("Occid", 0);
                // id to modify
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


    /**
     * Affiche un menu popup
     * @param convertView ancre
     * @param historyId id de l'historique cliquer
     * @param set true si n'est pas un endPeriod
     */
    private void showPopupMenu(final View convertView, final int historyId, final boolean set)
    {
        final PopupMenu popupMenu = new PopupMenu(this, convertView);
        popupMenu.inflate(R.menu.popup_menu_history);

        OccupationHistory histo = _db.getOccupationHistory(historyId);

        //Change popup menu text depending of history bool isPeriodEnd
        if(histo.isPeriodEnd())
        {
            popupMenu.getMenu().getItem(0).setTitle(getResources().getString(R.string.popup_menu_unset_title));
        }

        //click on item
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.popup_history_item_delete:
                    {
                        //Confirm dialog
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityHistory.this);
                        builder.setIcon(R.drawable.ic_logo);
                        builder.setTitle(R.string.delete_advertisehisto);
                        //button ok, delete and refresh
                        builder.setPositiveButton(R.string.ok_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton)
                                    {
                                        OccupationHistory occupationHistory = _db.getOccupationHistory(historyId);
                                        _db.deleteOccupationHistory(occupationHistory);
                                        initExpandableList();
                                    }
                                }
                        );
                        //button cancel, dismiss dialog
                        builder.setNegativeButton(R.string.cancel_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }
                        );
                        final AlertDialog al = builder.create();
                        al.show();
                        return true;
                    }
                    case R.id.popup_history_item_set:

                        OccupationHistory occupationHistory = _db.getOccupationHistory(historyId);
                        //reverse isPeriodEnd
                        occupationHistory.isPeriodEnd(!occupationHistory.isPeriodEnd());
                       _db.updateOccupationHistory(occupationHistory);
                        //refresh list
                        initExpandableList();
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

        //sorted history
        TreeSet<OccupationHistory> sorted = new TreeSet<>(new ComparatorOccupationHistory());
        sorted.addAll(_db.getOccupationHistoryFromOccId(_occupationId));

        //sorted history for diff calculation
        List<OccupationHistory> tempListTotal = new ArrayList<>();

        //list of total time
        List<Long> lstDiff = new ArrayList<>();
        int inc = 0;
        long diff = 0;

        //Get all the total times for each period
        for(OccupationHistory history : sorted)
        {
            tempListTotal.add(history);

            if (history.isPeriodEnd())
            {
                tempListTotal.remove(history);
                for(OccupationHistory histo:tempListTotal)
                {
                    Date out;

                    if(histo.getDateTimeOut() == null)
                    {
                        out = new Date();
                    }
                    else
                    {
                        out =  histo.getDateTimeOut();
                    }
                        diff += out.getTime() - histo.getDateTimeIn().getTime();
                }
                lstDiff.add(inc,diff);
                diff = 0;
                tempListTotal.clear();
                tempListTotal.add(history);
                inc++;
            }
       }
        //Total time for current period
        for(OccupationHistory histo:tempListTotal)
        {
            Date out;

            if(histo.getDateTimeOut() == null)
            {
                out = new Date();
            }
            else
            {
                out =  histo.getDateTimeOut();
            }

            diff += out.getTime() - histo.getDateTimeIn().getTime();
        }
        lstDiff.add(inc,diff);

        //Set the header and child items for the expendable list
        List<OccupationHistory> tempList = new ArrayList<>();
        _listDataHeader.add(getResources().getString(R.string.current_period) + "  " +
                getResources().getString(R.string.total_time) + " "
                + Tools.formatDifftoString(lstDiff.get(_listDataHeader.size())));

        for(OccupationHistory history : sorted)
        {
            tempList.add(history);
            if (history.isPeriodEnd())
            {
                tempList.remove(history);
                Date endPoint = history.getDateTimeIn();
                ArrayList<OccupationHistory> lst = new ArrayList<OccupationHistory>();
                lst.addAll(tempList);
                _listDataChild.put(_listDataHeader.get(_listDataHeader.size()-1),lst);
                _listDataHeader.add(Tools.formatDateCanada(endPoint, this) + " " +
                        getResources().getString(R.string.total_time)
                        + Tools.formatDifftoString(lstDiff.get(_listDataHeader.size())));
                tempList.clear();
                tempList.add(history);
            }
        }
        _listDataChild.put(_listDataHeader.get(_listDataHeader.size()-1),tempList);
    }


    /**
     * Call activity to add new history with occid of current occupation
     * @param view ancre
     */
    public void onClickAdd(View view)
    {
        Intent intent = new Intent("PunchCard.ActivityHistorySetting");
        String name = getResources().getString(R.string.new_history);
        intent.putExtra("Occid", _occupationId);
        intent.putExtra("id", 0);
        intent.putExtra("name", name);
        startActivityForResult(intent,2);
    }
}
