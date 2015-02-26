package com.example.utilisateur.punchcard;

import android.app.Activity;
import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jrsao on 2/23/2015.
 */
public class Tools {
    /**
     * Formatte une date en format long Canada francais
     *
     * @param date date a formatter
     * @return String dateFormatter
     */
    public static String formatDateCanada(Date date, Context context)
    {
        if (date == null) {
            return context.getString(R.string.current_period);
        }

        String result = DateFormat
                .getDateInstance(DateFormat.LONG, Locale.CANADA)
                .format(date);

        return result;
    }

    /**
     * Formatte diff en long de temps vers string hh:mm
     *
     * @param diff a formatter
     * @return string du temps
     */
    public static String formatDifftoString(long diff) {
        long diffHours = diff / (60 * 60 * 1000);
        long diffMinutes = diff / (60 * 1000) % 60;
        String txtheure = new String();
        if (diffHours < 10)
            txtheure += "0";
        txtheure += Long.toString(diffHours);
        txtheure += ":";
        if (diffMinutes < 10)
            txtheure += "0";
        txtheure += Long.toString(diffMinutes);

        return txtheure;
    }


    /**
     * Formatte diff en long de temps vers string hh:mm:ss
     *
     * @param diff a formatter
     * @return string du temps
     */
    public static String formatDifftoStringSec(long diff) {
        long diffHours = diff / (60 * 60 * 1000);
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffSecond =  diff / 1000 % 60;
        String txtheure = new String();
        if (diffHours < 10)
            txtheure += "0";
        txtheure += Long.toString(diffHours);
        txtheure += ":";
        if (diffMinutes < 10)
            txtheure += "0";
        txtheure += Long.toString(diffMinutes);
        txtheure += ":";
        if(diffSecond < 10)
            txtheure += "0";

        txtheure += diffSecond;


        return txtheure;
    }

    /**
     * Formatte le temps d'une date en format 12:08 PM
     *
     * @param date date a formatter
     * @return string du temps
     */
    public static String formatCustomDateTime(Date date) {
        if (date == null) {
            return "-";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE,  MMM d, yyyy    h:mm a");
        return sdf.format(date);
    }
}
