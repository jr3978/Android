package com.example.utilisateur.punchcard;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrsao on 2/23/2015.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter
{
    private Context _context;

    // header titles
    private List<String> _listDataheaders;

    // header titles, childs data
    private HashMap<String, List<OccupationHistory>> _listDataChilds;


    //Constructor
    /**
     * Constructor de ExpandableListAdapter
     * @param context contexte contenant la liste
     * @param listDataHeaders data de tous les headers
     * @param listDataChilds data de tous les childs des groups
     */
    public ExpandableListAdapter(Context context, List<String> listDataHeaders,
                  HashMap<String, List<OccupationHistory>> listDataChilds)
    {
        _context = context;
        _listDataChilds = listDataChilds;
        _listDataheaders = listDataHeaders;
    }


    // region Override method from BaseExpandableListAdapter

    /**
     * Retourne le nombre de parent (listHeader)
     * @return nombre de parent dans la liste
     */
    @Override
    public int getGroupCount()
    {
        return _listDataheaders.size();
    }


    /**
     * Retourne le nombre d'enfant (listChild)
     * @param groupPosition -position du parent dans la liste
     * @return -nombre d'enfant dans ce groupe
     */
    @Override
    public int getChildrenCount(int groupPosition)
    {
        int test = groupPosition;
        String te = _listDataheaders.get(groupPosition);

        return _listDataChilds.get(_listDataheaders.get(groupPosition)).size();
    }


    /**
     * Retourne le dataHeader de la position passer en parametre
     * @param groupPosition -position du parent dans la liste
     * @return object parent (occupationHistory)
     */
    @Override
    public String getGroup(int groupPosition)
    {
        return _listDataheaders.get(groupPosition);
    }


    /**
     * Retourne le dataChild du groupe et de sa position passe en parametre
     * @param groupPosition -position du parent dans la liste
     * @param childPosition -position de l'enfant dans le groupe parent
     * @return object enfant (OccupationHistory)
     */
    @Override
    public OccupationHistory getChild(int groupPosition, int childPosition)
    {
        return _listDataChilds.get(getGroup(groupPosition))
                .get(childPosition);
    }


    /**
     * Obtient le id de l'OccupationHistory parent a la postition passer en parametre
     * @param groupPosition -position du parent dans la liste
     * @return id de l'OccupationHistory
     */
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }


    /**
     * Obtient le id de l'OccupationHistory enfant a la postition passer en parametre
     * @param groupPosition -position du parent dans la liste
     * @param childPosition -position de l'enfant dans le groupe parent
     * @return id de l'OccupationHistory
     */
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return getChild(groupPosition, childPosition).getId();
    }


    /**
     * Les id des parents et des enfants sont stables, utilisent les memes que la base de donnee
     * @return vrai si les ID sont stables
     */
    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    /**
     * Obtient un object View qui est le parent ( un group )
     * @param groupPosition -position du parent dans la liste
     * @param isExpanded vrai si la vue est expensible
     * @param convertView view contenant le data
     * @param parent view contenant du parent
     * @return view parent
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        String headerTitle = getGroup(groupPosition);

        // list_group_history comme parent si pas preciser
        if (convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater)_context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.list_group_history, null);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.list_group_history_header);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(headerTitle);

        return convertView;
    }


    /**
     * Obtient un object view qui est l'enfant d'un group
     * @param groupPosition -position du parent dans la liste
     * @param childPosition -position de l'enfant dans le groupe parent
     * @param isLastChild vrai si est le dernier enfant
     * @param convertView view contenant le data
     * @param parent parent
     * @return view enfant
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = Tools.formatDateCanada(
                getChild(groupPosition, childPosition).getDateTimeIn(), _context);

        childText += ("   " + _context.getString(R.string.exp_list_childText));
        Date out;

        if (getChild(groupPosition, childPosition).getDateTimeOut() == null) {
            out = new Date();
        } else {
            out = getChild(groupPosition, childPosition).getDateTimeOut();
        }

        long diff = out.getTime() - getChild(groupPosition, childPosition).getDateTimeIn().getTime();
        childText += Tools.formatDifftoString(diff);


        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) _context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.list_item_history, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.list_group_history_item);
        textView.setText(childText);

        return convertView;
    }


    /**
     * Permet de savoir si l'enfant est selectionnable
     * @param groupPosition --position du parent dans la liste
     * @param childPosition -position de l'enfant dans le groupe parent
     * @return vrai si les elements enfants se peuvent etre selectionner
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    //endregion


}
