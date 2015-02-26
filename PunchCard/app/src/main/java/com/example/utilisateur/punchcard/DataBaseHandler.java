package com.example.utilisateur.punchcard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



import java.lang.reflect.ParameterizedType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jrsao on 2/17/2015.
 * Classe effectuant des requetes sur la base de donnée
 */
public class DataBaseHandler extends SQLiteOpenHelper
{
    //region DATABASE CONST

    //--------- Database Version ---------
    private static final int DATABASE_VERSION = 1;

    //---------  Database Name ---------
    private static final String DATABASE_NAME = "punchCard";

    //---------  Tables ---------------
    private static final String TABLE_OCCUPATION = "Occupation";
    private static final String TABLE_PARAMETERS = "Parameters";
    private static final String TABLE_HISTORY = "History";

    //---------common colomns---------
    private static final String COL_ID = "Id";
    private static final String COL_OCC_ID = "IdOccupation";

    //---------Occupation columns---------
    private static final String COL_NAME = "Name";
    private static final String COL_STATUS = "Status";
    private static final String COL_SELECTED = "Selected";

    //---------Parameters columns---------
    private static final String COL_ROUND_TYPE = "RoundType";
    private static final String COL_ROUND_MIN_VALUE = "RoundMinuteValue";

    //---------History columns---------
    private static final String COL_DATE_IN = "DateTimeIn";
    private static final String COL_DATE_OUT = "DateTimeOut";
    private static final String COL_IS_PERIOD_END = "IsPeriodEnd";

    //endregion


    //region DATABASE BUILDER

    //--------- constructor ------------
    public DataBaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    //--------- SQLiteOpenHelper Methods -------------

    /**
     * Creer la bd et ajoute les tables Occupation, History et Parameters
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createOccupationTable =
                "CREATE TABLE " + TABLE_OCCUPATION + "( " +
                        COL_ID + " INTEGER PRIMARY KEY, " +
                        COL_NAME + " TEXT, " +
                        COL_STATUS + " INTEGER, " +
                        COL_SELECTED + " INTEGER" +
                        ");";

        String createHistoryTable =
                "CREATE TABLE " + TABLE_HISTORY + " ( " +
                        COL_ID + " INTEGER PRIMARY KEY, " +
                        COL_OCC_ID + " INTEGER, " +
                        COL_DATE_IN + " NUMERIC, " +
                        COL_DATE_OUT + " NUMERIC, " +
                        COL_IS_PERIOD_END + " NUMERIC," +
                        "FOREIGN KEY(" + COL_OCC_ID + ")" +
                        "REFERENCES " + TABLE_OCCUPATION + "(" + COL_ID + ")" +
                        " ON DELETE CASCADE " +
                        ");";

        String createParametersTable =
                "CREATE TABLE " + TABLE_PARAMETERS + " ( " +
                        //COL_ID + " INTEGER PRIMARY KEY, " +
                        COL_OCC_ID + " INTEGER PRIMARY KEY, " +
                        COL_ROUND_TYPE + " INTEGER, " +
                        COL_ROUND_MIN_VALUE + " INTEGER, " +
                        "FOREIGN KEY(" + COL_OCC_ID + ")" +
                        "REFERENCES " + TABLE_OCCUPATION + "(" + COL_ID + ")" +
                        " ON DELETE CASCADE " +
                        ");";

        db.execSQL(createOccupationTable);
        db.execSQL(createHistoryTable);
        db.execSQL(createParametersTable);
    }


    /**
     * Drop les tables si la BD est modifier et repasse par onCreate pour la recreer
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARAMETERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OCCUPATION);

        onCreate(db);
    }
    //endregion


    //region CRUD OCCUPATION TABLE

    /**
     * prend les données du cursor et crée un Occupation
     * @param cursor
     * @return
     */
    private Occupation cursorToOccupation(Cursor cursor)
    {
        Occupation occupation = new Occupation();
        occupation.setId(Integer.parseInt(cursor.getString(0)));
        occupation.setName(cursor.getString(1));
        if(Integer.parseInt(cursor.getString(2)) == 1)
            occupation.isIn(true);
        else
            occupation.isIn(false);

        if(Integer.parseInt(cursor.getString(3)) == 1)
            occupation.isSelected(true);
        else
            occupation.isSelected(false);


        return occupation;
    }


    /**
     * Obtient l'occupation avec le ID specifique
      * @param id
     * @return
     */
    public Occupation getOccupation(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OCCUPATION,
                new String[] { COL_ID ,COL_NAME, COL_STATUS, COL_SELECTED }, COL_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursorToOccupation(cursor);
    }


    /**
     * retourne la premiere l'occupation avec le nom specifie
     * @param name
     * @return
     */
    public Occupation getOccupation(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OCCUPATION,
                new String[] { COL_ID ,COL_NAME, COL_STATUS, COL_SELECTED }, COL_NAME + "=?",
                new String[] { String.valueOf(name) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

         return cursorToOccupation(cursor);
    }


    /**
     * Obtient toutes les occupations de la bd
     *
     * @return
     */
    public List<Occupation> getAllOccupations() {
        List<Occupation> listOcc = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_OCCUPATION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Occupation occupation = new Occupation();
                occupation.setId(Integer.parseInt(cursor.getString(0)));
                occupation.setName(cursor.getString(1));
                if(Integer.parseInt(cursor.getString(2)) == 1)
                occupation.isIn(true);
                else
                occupation.isIn(false);

                if(Integer.parseInt(cursor.getString(3)) == 1)
                    occupation.isSelected(true);
                else
                    occupation.isSelected(false);


                listOcc.add(occupation);
            }
            while (cursor.moveToNext());
        }

        return listOcc;
    }



    /**
     * Ajoute une occupation a la bd
     *
     * @param occ
     */
    public void addOccupation(Occupation occ) {
        if (occ == null) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, occ.getName());

        if(occ.isIn())
            values.put(COL_STATUS,1);
        else
            values.put(COL_STATUS,0);


        if(occ.isSelected())
            values.put(COL_SELECTED, 1);
        else
            values.put(COL_SELECTED, 0);

        db.insert(TABLE_OCCUPATION, null, values);

        db.close();
    }


    /**
     * Suppression de l'occupation de la bd
     *
     * @param occ
     */
    public void deleteOccupation(Occupation occ) {
        if (occ == null) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        if (!db.isReadOnly())
        {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        String id = new Integer(occ.getId()).toString();

        String query = "DELETE FROM " + TABLE_OCCUPATION +
                " WHERE " + COL_ID + " = " + id;

        db.delete(TABLE_OCCUPATION, COL_ID + " = " + id, null);
        db.close();
    }


    /**
     * Supprime toutes les occupations
     */
    public void deleteAllOccupations() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_OCCUPATION;

        db.delete(TABLE_OCCUPATION, null, null);
        db.close();
    }


    /**
     * Update l'occupation envoyer en parametre dans la BD
     * @param occ
     */
    public void updateOccupation(Occupation occ) {
        if (occ == null) {
            return;
        }

        String id = new Integer(occ.getId()).toString();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, occ.getName());
        values.put(COL_STATUS, occ.isIn());
        if(occ.isSelected()) {
            values.put(COL_SELECTED, 1);
        }
        else{
            values.put(COL_SELECTED, 0);
        }



        db.update(TABLE_OCCUPATION, values, COL_ID + " = " + id, null);
        db.close();
    }

    //endregion


    //region  CRUD PARAMETERS TABLE

    /**
     * Obtient les paramètres d'une occupation sous forme d'object OccupationParameters
     * @param occupationId id de l'occupation
     * @return
     */
    public OccupationParameters getParametersByOccupationId(int occupationId)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PARAMETERS,
                new String[]
                        {
                            COL_OCC_ID,
                            COL_ROUND_MIN_VALUE,
                            COL_ROUND_TYPE
                        },
                COL_OCC_ID + "=?",
                new String[] { String.valueOf(occupationId) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        OccupationParameters parameters = new OccupationParameters();

        parameters.setOccupationId(Integer.parseInt(cursor.getString(0)));
        parameters.setRoundMinuteValue(Integer.parseInt(cursor.getString(1)));
        parameters.setRoundType(
                OccupationParameters.RoundType.values()[Integer.parseInt(cursor.getString(2))]
        );

        return parameters;
    }


    /**
     * Obtient un dictionnaire de key value d'un parameters utiliser pour les requetes
     * @param parameters
     * @return
     */
    private ContentValues getParametersValue(OccupationParameters parameters)
    {
        ContentValues values = new ContentValues();

        values.put(COL_ROUND_TYPE, parameters.getRoundType().getValue());
        values.put(COL_OCC_ID, parameters.getOccupationId());
        values.put(COL_ROUND_MIN_VALUE, parameters.getRoundMinuteValue());

        return values;
    }


    /**
     * Ajouter un parameters a la BD
     * @param parameters
     */
    public void addParameters(OccupationParameters parameters)
    {
        if (parameters == null) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getParametersValue(parameters);

        db.insert(TABLE_PARAMETERS, null, values);
        db.close();
    }


    /**
     * Update le parameters passer en parametre dans la BD
     * @param parameters
     */
    public void updateParameters(OccupationParameters parameters)
    {
        if (parameters == null) {
            return;
        }

        String id = new Integer(parameters.getOccupationId()).toString();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getParametersValue(parameters);

        db.update(TABLE_PARAMETERS, values, COL_OCC_ID + " = " + id, null);

        db.close();
    }




    // endregion


    //region CRUD HISTORY TABLE

    /**
     * Retourne un object OccupationHistory avec le data du cursor passer en parametre
     * @param cursor
     * @return
     */
    private OccupationHistory cursorToOccupationHystory(Cursor cursor)
    {
        OccupationHistory history = new OccupationHistory();

        try
        {
            history.setId(Integer.parseInt(cursor.getString(0)));
            history.setOccupationId(Integer.parseInt(cursor.getString(1)));
            history.setDateTimeIn(parseDate(cursor.getString(2)));
            history.setDateTimeOut(parseDate(cursor.getString(3)));
            if(Integer.parseInt(cursor.getString(4)) == 1)
                history.isPeriodEnd(true);
            else
                history.isPeriodEnd(false);
        }
        catch (Exception e)
        {

        }

        return history;
    }


    /**
     * execute une requete SELECT sur la table History et retourne le resultat
     * @param rawQuery
     * @return
     */
    private List<OccupationHistory> executeRawQueryOnHistoryTable(String rawQuery)
    {
        List<OccupationHistory> listHistory = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(rawQuery, null);

        if (cursor.moveToFirst()) {
            do
            {
                listHistory.add(cursorToOccupationHystory(cursor));
            }
            while (cursor.moveToNext());
        }

        return listHistory;
    }


    /**
     * Obtient un historique specifique a son id
     * @param id
     * @return
     */
    public OccupationHistory getOccupationHistory(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY,
                new String[] { COL_ID , COL_OCC_ID, COL_DATE_IN, COL_DATE_OUT,COL_IS_PERIOD_END }, COL_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursorToOccupationHystory(cursor);
    }


    /**
     * Obtient tout l'historique d'un Occupation
     * @param occupationId
     * @return
     */
    public List<OccupationHistory> getOccupationHistoryFromOccId(int occupationId)
    {
        String query = "SELECT * FROM " + TABLE_HISTORY +
                       " WHERE " + COL_OCC_ID + " = " + occupationId;

        return executeRawQueryOnHistoryTable(query);
    }


    /**
     * Obtient tous les historiques de la bd
     * @return
     */
    public List<OccupationHistory> getAllOccupationHistory()
    {
        String query = "SELECT * FROM " + TABLE_HISTORY;

        return executeRawQueryOnHistoryTable(query);
    }


    /**
     * Obtient tous les historiques fin de periodes ou non
     * @param isEndPeriod true si retourne tous les fins de periods
     * @param occupationId id de l'occupation
     * @return list d'historiques
     */
    public List<OccupationHistory> getAllEndPeriod(boolean isEndPeriod, int occupationId)
    {
        String isEnfPeriodStr = isEndPeriod ? "1" : "0";

        String query = "SELECT * FROM " + TABLE_HISTORY +
                " WHERE " + COL_IS_PERIOD_END + " = " + isEnfPeriodStr +
                " AND " + COL_OCC_ID + " = " + occupationId;

        return executeRawQueryOnHistoryTable(query);
    }

    /**
     * Ajoute un historique à la BD
     * @param history
     */
    public void addOccupationHistory(OccupationHistory history)
    {
        if (history == null) {
            return;
        }
        SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        String in;
        String out;

        in = parserSDF.format(history.getDateTimeIn());


        values.put(COL_OCC_ID, history.getOccupationId());

        // ajoute la date OUT si elle n'est pas null.
        if (history.getDateTimeOut() != null) {
            out = parserSDF.format(history.getDateTimeOut());
            values.put(COL_DATE_OUT, out);
        }
        else
        {
            values.put(COL_DATE_OUT, "-");
        }

        // ajoute la date IN si elle n'est pas null.
        if (in != null) {
            values.put(COL_DATE_IN, in);
        }

        values.put(COL_IS_PERIOD_END,history.isPeriodEnd());

        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }


    /**
     * Delete l'historique passe en parametre
     * @param history
     */
    public void deleteOccupationHistory(OccupationHistory history)
    {
        if (history == null)
        {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        String id = new Integer(history.getId()).toString();

        db.delete(TABLE_HISTORY, COL_ID + " = " + id, null);
        db.close();
    }


    /**
     * Update l'historique dans la BD
     * @param history historique à updater
     */
    public void updateOccupationHistory(OccupationHistory history)
    {
        if (history == null) {
            return;
        }

        String id = new Integer(history.getId()).toString();

        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");

        ContentValues values = new ContentValues();
        values.put(COL_DATE_IN, parserSDF.format(history.getDateTimeIn()));
        values.put(COL_DATE_OUT, parserSDF.format(history.getDateTimeOut()));

        values.put(COL_OCC_ID, history.getOccupationId());
        values.put(COL_ID, history.getId());

        if(history.isPeriodEnd())
            values.put(COL_IS_PERIOD_END,true);
        else
            values.put(COL_IS_PERIOD_END,false);

        db.update(TABLE_HISTORY, values, COL_ID + " = " + id, null);

        db.close();
    }


    /**
     * Supprime toutes les historiques
     */
    public void deleteAllOccupationHistory()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_HISTORY;

        db.delete(TABLE_HISTORY, null, null);
        db.close();
    }


    /**
     * Delete tous les historiques d'une occupation
     * @param occupationId id de l'occupation
     */
    public void deleteHistoryFromOccId(int occupationId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_HISTORY, COL_OCC_ID + " = " + occupationId, null);
        db.close();
    }

    //endregion


    //region HELPER
    /**
     * Permet de parser une date selon le String format specifique enregistrer dans la BD
     * @param strDate
     * @return
     */
    private Date parseDate(String strDate)
    {
        if(strDate.equals("-"))
            return null;
        SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
        Date date = null;
        try
        {
            date = parserSDF.parse(strDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return date;
    }
    //endregion
}