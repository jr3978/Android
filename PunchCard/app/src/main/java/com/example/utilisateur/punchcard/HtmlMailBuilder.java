package com.example.utilisateur.punchcard;

import android.content.Context;

import java.util.Date;
import java.util.TreeSet;

/**
 * Created by utilisateur on 2015-02-25.
 */
public class HtmlMailBuilder {

    private String _text = "";

    public  String get_text(){return _text;}

    public HtmlMailBuilder(Context _context, int id, String name)
    {
        DataBaseHandler db = new DataBaseHandler(_context);

        TreeSet<OccupationHistory> sorted = new TreeSet<>(new ComparatorOccupationHistory());
        sorted.addAll(db.getOccupationHistoryFromOccId(id));

        _text += "Here are all the histories for the activity named " + name;

        for(OccupationHistory histo:sorted)
        {
            _text += "\n\nTime in: \t" + Tools.formatCustomDateTime(histo.getDateTimeIn()) + "\nTime out: \t" +
                    Tools.formatCustomDateTime(histo.getDateTimeOut()) + "\nTotal Time: \t";
            Date out = new Date();
            Date in = new Date();
            if(histo.getDateTimeIn() != null)
                in= histo.getDateTimeIn();

            if(histo.getDateTimeOut() != null)
                out = histo.getDateTimeOut();

              _text +=      Tools.formatDifftoString(out.getTime() - in.getTime()) + "\n";
        }

    }
}
