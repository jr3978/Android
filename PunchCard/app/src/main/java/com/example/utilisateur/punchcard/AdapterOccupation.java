package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.TreeSet;

/**
 * Created by jrsao on 2/18/2015.
 * Classe adapteur pour listView des occupations
 */
public class AdapterOccupation extends BaseAdapter {
    private List<Occupation> _occupations;
    private Context _context;
    private Activity _activity;
    private DataBaseHandler _db;

    /**
     * Constructeur de l'adapteur de listView
     * @param activity activity parent
     */
    public AdapterOccupation(Activity activity) {
        _activity = activity;
        _context = _activity.getApplicationContext();
        _db = new DataBaseHandler(_context);

        _occupations = _db.getAllOccupations();
        _db.close();
    }


    /**
     * Obtient le nombre d'item de la listView
     * @return
     */
    @Override
    public int getCount() {
        return _occupations.size();
    }


    /**
     * Obtient l'item de la listView a la position specifier
     * @param position index de l'item
     * @return item (Occupation)
     */
    @Override
    public Occupation getItem(int position) {
        return _occupations.get(position);
    }


    /**
     * Obtient le Id de l'item specifier
     * @param position index de l'item
     * @return id de l'item
     */
    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }


    /**
     * Donne une view pour chaque element de la liste
     * @param position position de l'item de la liste
     * @param convertView view de l'item
     * @param container container parent
     * @return view de l'item
     */
    @Override
    public View getView(int position, View convertView, ViewGroup container) {

        if (convertView == null) {
            convertView = _activity.getLayoutInflater().inflate(R.layout.list_item, container, false);
        }

        TreeSet<OccupationHistory> historyTreeSet = new TreeSet<>(new ComparatorOccupationHistory());
        historyTreeSet.addAll(_db.getOccupationHistoryFromOccId(getItem(position).getId()));

        long diff = 0;

        for (OccupationHistory history : historyTreeSet) {
            if (history.getDateTimeOut() != null) {
                if (history.isPeriodEnd()) {
                    break;
                }
                else {
                    diff += history.getDateTimeOut().getTime() - history.getDateTimeIn().getTime();
                }
            }
        }

        String timeTxt = Tools.formatDifftoString(diff);

        ((TextView) convertView.findViewById(R.id.time))
                .setText(timeTxt);

        ((TextView) convertView.findViewById(R.id.act_name))
                .setText(getItem(position).getName());

        ((TextView) convertView.findViewById(R.id.act_status))
                .setText(String.valueOf(getItem(position).getId()));

        return convertView;
    }

}
