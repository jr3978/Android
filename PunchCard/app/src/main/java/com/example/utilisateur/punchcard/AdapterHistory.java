package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jrsao on 2/18/2015.
 */
public class AdapterHistory extends BaseAdapter
{
    private List<OccupationHistory> _history;

    private Context _context;
    private Activity _activity;
    private int _occupationId;
    private IListViewContainer _iListViewContainer;
    DataBaseHandler db;

    public AdapterHistory(Activity activity, int occupationId)
    {
        _occupationId = occupationId;
        _activity = activity;
        _context = _activity.getApplicationContext();

        DataBaseHandler db = new DataBaseHandler(_context);
        _history = db.getOccupationHistoryFromOccId(_occupationId);
        db.close();
    }

    @Override
    public int getCount()
    {
        return _history.size();
    }

    @Override
    public OccupationHistory getItem(int position)
    {
        return _history.get(position);
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
            convertView = _activity.getLayoutInflater().inflate(R.layout.list_item, container, false);
        }

        initItemListener(convertView, position);
        ((TextView) convertView.findViewById(R.id.act_name))
                .setText(getItem(position).getDateTimeIn().toString());

        ((TextView) convertView.findViewById(R.id.act_status))
                .setText(new Integer(getItem(position).getId()).toString());

        DataBaseHandler db = new DataBaseHandler(_context);

        long diff = 0;

            diff += getItem(position).getDateTimeOut().getTime() - getItem(position).getDateTimeIn().getTime();

        String timetxt = "";
        long diffHours = diff / (60 * 60 * 1000);
        long diffMinutes = diff / (60 * 1000) % 60;
        timetxt = "";
        if (diffHours < 10)
            timetxt += "0";
        timetxt += Long.toString(diffHours);
        timetxt += ":";
        if (diffMinutes < 10)
            timetxt += "0";

        timetxt += Long.toString(diffMinutes);
        ((TextView) convertView.findViewById(R.id.time))
                .setText(timetxt);

        db.close();

        return convertView;
    }

    private void initItemListener(final View convertView, final int position)
    {
        View v = convertView.findViewById(R.id.list_item_job);

        v.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                showPopupMenu(convertView, position);

                return true;
            }
        });

    }

    private void showPopupMenu(final View convertView, final int position)
    {
        PopupMenu popupMenu = new PopupMenu(_activity, convertView);
        popupMenu.inflate(R.menu.popup_menu_occupation);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                final DataBaseHandler db = new DataBaseHandler(_context);
                final int id = getItem(position).getId();
                final Occupation occupation = db.getOccupation(id);

                switch (item.getItemId()) {
                    case R.id.item_delete_occupation:
                    {
                        showDeleteAlertDialog(occupation);
                    }
                    return true;

                    case R.id.item_send_mail:

                        return true;


                    case R.id.item_set_parameters:
                    {
                        Intent intent = new Intent("PunchCard.Parameters");

                        intent.putExtra("id",occupation.getId());
                        _activity.startActivityForResult(intent, 2);
                    }
                    return true;


                }

                return false;
            }
        });

        popupMenu.show();
    }

    AlertDialog alert;
    private void showDeleteAlertDialog(final Occupation occupation)
    {


        final DataBaseHandler db = new DataBaseHandler(_context);

        final AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.delete_advertise);


        //button ok
        builder.setPositiveButton(R.string.ok_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        String message = occupation.getName() +  " " +
                                _context.getString(R.string.occupation_deleted);

                        db.deleteOccupation(occupation);

                        _iListViewContainer.refreshListView();

                        Toast.makeText(
                                _context,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );

        //button cancel
        builder.setNegativeButton(R.string.cancel_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        alert.dismiss();
                    }
                }
        );


        final AlertDialog al = builder.create();
        alert = al;
        al.show();
    }
}
