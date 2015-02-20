package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jrsao on 2/18/2015.
 */
public class AdapterOccupation extends BaseAdapter
{
    private List<Occupation> _occupations;

    private Context _context;
    private Activity _activity;

    public AdapterOccupation(Activity activity)
    {
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

        initItemListener(convertView);

        ((TextView) convertView.findViewById(R.id.act_name))
                .setText(getItem(position).getName());

        ((TextView) convertView.findViewById(R.id.act_status))
                .setText(new Integer(getItem(position).getId()).toString());

        return convertView;
    }

    /**
     * Abonne les items aux listener
     * @param convertView
     */
    private void initItemListener(View convertView)
    {
        View v = convertView.findViewById(R.id.list_item_job);

        v.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v) {
                //***********

                // devrait etre un fragment au lieu d'un AlertDialgo
                // voir exemple googleSheet

                //************

                AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                builder.setCancelable(true);
                builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // affiche un message d'avertissement voulez-vous vraiment...
                    }
                });

                // builder
                return false;
            }
        });

    }


}
