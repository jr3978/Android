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
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jrsao on 2/18/2015.
 */
public class AdapterOccupation extends BaseAdapter
{
    private List<Occupation> _occupations;

    private Context _context;
    private Activity _activity;
    private IListViewContainer _iListViewContainer;

    public AdapterOccupation(Activity activity, IListViewContainer iListViewContainer)
    {
        _iListViewContainer = iListViewContainer;
        _activity = activity;
        _context = _activity.getApplicationContext();
        DataBaseHandler db = new DataBaseHandler(_context);



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
            convertView = _activity.getLayoutInflater().inflate(R.layout.list_item, container, false);
        }

        initItemListener(convertView, position);

        ((TextView) convertView.findViewById(R.id.act_name))
                .setText(getItem(position).getName());

        ((TextView) convertView.findViewById(R.id.act_status))
                .setText(new Integer(getItem(position).getId()).toString());

       // List<OccupationHistory> _history = db.getOccupationHistoryFromOccId(getItem(position).getId());
        //long diff = 0;
       // for(OccupationHistory histo:_history)
       // {
            //TODO AFFICHER HEURE ICI
        //    diff += histo.getDateTimeOut().getTime() - histo.getDateTimeIn().getTime();
       // }

        return convertView;
    }

    /**
     * Abonne les items aux listener
     * @param convertView
     */
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
