package com.example.utilisateur.punchcard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Camera;
import android.util.Log;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jrsao on 2/17/2015.
 */
public class DataBaseHandler extends SQLiteOpenHelper {
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

    //---------Parameters columns---------
    private static final String COL_RESET_DAY = "ResetDay";
    private static final String COL_NB_DAY_BEFORE_RESET = "NbDayBeforeReset";
    private static final String COL_ROUND_TYPE = "RoundType";
    private static final String COL_ROUND_MIN_VALUE = "RoundMinuteValue";

    //---------History columns---------
    private static final String COL_DATE_IN = "DateTimeIn";
    private static final String COL_DATE_OUT = "DateTimeOut";

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
                        COL_STATUS + " NUMERIC" +
                        ");";

        String createHistoryTable =
                "CREATE TABLE " + TABLE_HISTORY + " ( " +
                        COL_ID + " INTEGER PRIMARY KEY, " +
                        COL_OCC_ID + " INTEGER, " +
                        COL_DATE_IN + " NUMERIC, " +
                        COL_DATE_OUT + " NUMERIC," +
                        "FOREIGN KEY(" + COL_OCC_ID + ")" +
                        "REFERENCES " + TABLE_OCCUPATION + "(" + COL_ID + ")" +
                        ");";

        String createParametersTable =
                "CREATE TABLE " + TABLE_PARAMETERS + " ( " +
                        COL_ID + " INTEGER PRIMARY KEY, " +
                        COL_OCC_ID + " INTEGER, " +
                        COL_RESET_DAY + " INTEGER, " +
                        COL_NB_DAY_BEFORE_RESET + " INTEGER," +
                        COL_ROUND_TYPE + " INTEGER" +
                        COL_ROUND_MIN_VALUE + " INTEGER" +
                        "FOREIGN KEY(" + COL_OCC_ID + ")" +
                        "REFERENCES " + TABLE_OCCUPATION + "(" + COL_ID + ")" +
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


    //region ************************** CRUD Occupation********************************

    /**
     * Obtient l'occupation avec le ID specifique
      * @param id
     * @return
     */
    public Occupation getOccupation(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OCCUPATION,
                new String[] { COL_ID ,COL_NAME, COL_STATUS }, COL_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Occupation occupation = new Occupation();
        occupation.setId(Integer.parseInt(cursor.getString(0)));
        occupation.setName(cursor.getString(1));
        occupation.isIn(Boolean.parseBoolean(cursor.getString(2)));


        return occupation;
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
                occupation.isIn(Boolean.parseBoolean(cursor.getString(2)));

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
        values.put(COL_STATUS, occ.isIn());

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

        db.update(TABLE_OCCUPATION, values, COL_ID + " = " + id, null);

        //------- TEST -------
        if (MainActivity.TEST) {
            Log.d("updateOccupation", "test");
        }
        //--------------------

        db.close();
    }

    //endregion

    //region 1************************** CRUD PARAMETERS ********************************


    /**
     * Obtient un dictionnaire de key value d'un parameters utiliser pour les requetes
     * @param parameters
     * @return
     */
    private ContentValues getParametersValue(OccupationParameters parameters)
    {
        ContentValues values = new ContentValues();

        values.put(COL_NB_DAY_BEFORE_RESET, parameters.getNbDayBeforeReset());
        values.put(COL_RESET_DAY, parameters.getResetDay().getValue());
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

        //------- TEST -------
        if (MainActivity.TEST) {
            Log.d("addParameters", "test");
        }
        //--------------------
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

        String id = new Integer(parameters.getId()).toString();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getParametersValue(parameters);

        db.update(TABLE_PARAMETERS, values, COL_ID + " = " + id, null);

        //------- TEST -------
        if (MainActivity.TEST) {
            Log.d("updateParameters", "test");
        }
        //--------------------

        db.close();
    }




    // endregion

    //region 2************************** CRUD HISTORY ********************************

    public void addHistory(OccupationHistory history)
    {
        if (history == null) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_DATE_IN, history.getDateTimeIn().toString());
        values.put(COL_DATE_OUT, history.get_dateTimeOut().toString());
        values.put(COL_OCC_ID, history.getOccupationId());

        db.insert(TABLE_HISTORY, null, values);
        db.close();

        //------- TEST -------
        if (MainActivity.TEST) {
            Log.d("addHistory", "test");
        }
        //--------------------
    }

    //endregion
}