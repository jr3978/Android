package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

    public AdapterHistory(Activity activity, int occupationId)
    {
        _occupationId = occupationId;
        _activity = activity;
        _context = _activity.getApplicationContext();

        DataBaseHandler db = new DataBaseHandler(_context);

        _history = db.getOccupationHistoryFromOccId(_occupationId);
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

        ((TextView) convertView.findViewById(R.id.act_name))
                .setText(getItem(position).getDateTimeIn().toString());

        ((TextView) convertView.findViewById(R.id.act_status))
                .setText(new Integer(getItem(position).getId()).toString());

        return convertView;
    }
}
