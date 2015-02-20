package com.example.utilisateur.punchcard;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jrsao on 2/18/2015.
 *
 * Classe pour tester les requetes sur la BD
 */
public class DataBaseTest
{
    public  static void getFirstParameter(Context context)
    {
        DataBaseHandler db = new DataBaseHandler(context);

        OccupationParameters parameters = db.getParametersByOccupationId(3);

        int stop = 0;
    }

    public static void allOccupation(Context context)
    {
        DataBaseHandler db = new DataBaseHandler(context);

        List<Occupation> occ = db.getAllOccupations();

        int stop = 0;
    }

    public static void clearOccupation(Context context)
    {
        DataBaseHandler db = new DataBaseHandler(context);

        db.deleteAllOccupations();
    }

    public static void allHistoryFromOccupation(Context context)
    {
        DataBaseHandler db = new DataBaseHandler(context);

        List<Occupation> occ = db.getAllOccupations();

        if (occ.size() > 0) {

           List<OccupationHistory> history  = db.getOccupationHistoryFromOccId(occ.get(0).getId());

            int stop = 0;
        }
    }

    public static void clearHistoryTable(Context context)
    {
        DataBaseHandler db = new DataBaseHandler(context);

        db.deleteAllOccupationHistory();
    }

    /**
     * BUG quand on entre une date null
     * @param context
     */
    public static void addRandomHistory(Context context) {
        DataBaseHandler db = new DataBaseHandler(context);
        Occupation o;

        List<Occupation> occ = db.getAllOccupations();

        if (occ.size() > 0) {
            o = occ.get(0);

            for (int i = 0; i < 10; i++) {
                OccupationHistory h = new OccupationHistory();
                h.setDateTimeOut(new Date());
                h.setDateTimeIn(new Date());
                        h.setOccupationId(o.getId());
                db.addOccupationHistory(h);
            }

        }
    }

}
