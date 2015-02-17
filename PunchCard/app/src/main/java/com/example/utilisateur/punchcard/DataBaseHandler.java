package com.example.utilisateur.punchcard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jrsao on 2/17/2015.
 */
public class DataBaseHandler extends SQLiteOpenHelper
{
    //--------- Database Version ---------
    private static final int DATABASE_VERSION = 1;

    //---------  Database Name ---------
    private static final String DATABASE_NAME = "punchCard";

    //---------  Tables ---------------
    private String TABLE_OCCUPATION = "Occupation";
    private String TABLE_PARAMETERS = "Parameters";
    private String TABLE_HISTORY = "History";


    //--------- constructor ------------
    public DataBaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    //--------- SQLiteOpenHelper Methods -------------
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createOccupationTable =
                "CREATE TABLE " + TABLE_OCCUPATION + "( " +
                        "Id     INTEGER PRIMARY KEY, " +
                        "Name   TEXT, " +
                        "Status NUMERIC" +
                        ");";

        String createHistoryTable =
                "CREATE TABLE " + TABLE_HISTORY + " ( " +
                        "Id            INTEGER PRIMARY KEY, " +
                        "OccupationId  INTEGER, " +
                        "DateTimeIn    NUMERIC, " +
                        "DateTimeOut   NUMERIC," +
                        "FOREIGN KEY(OccupationId) REFERENCES(Occupation)" +
                        ");";

        String createParametersTable =
                "CREATE TABLE " + TABLE_PARAMETERS + " ( " +
                        "Id               INTEGER PRIMARY KEY, " +
                        "OccupationId     INTEGER, " +
                        "ResetDay         INTEGER, " +
                        "NbDayBeforReset  INTEGER," +
                        "RoundType        INTEGER" +
                        "RoundMinuteValue INTEGER" +
                        "FOREIGN KEY(OccupationId) REFERENCES(Occupation)" +
                        ");";

        db.execSQL(createOccupationTable);
        db.execSQL(createHistoryTable);
        db.execSQL(createParametersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARAMETERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OCCUPATION);

        // Create tables again
        onCreate(db);
        }
    }


    // CRUD Occupation


    // CRUD Parameters


    // CRUD History
