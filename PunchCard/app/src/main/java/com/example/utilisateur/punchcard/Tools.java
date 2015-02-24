package com.example.utilisateur.punchcard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jrsao on 2/23/2015.
 */
public class Tools
{
    /**
     * Formatte une date en format long Canada francais
     * @param date date a formatter
     * @return String dateFormatter
     */
    public static String formatDateCanada(Date date)
    {
        if (date == null)
        {
            return "Current Period";
        }

        String result = DateFormat
                .getDateInstance(DateFormat.LONG, Locale.CANADA)
                .format(date);

        return result;
    }


    /**
     * Formatte le temps d'une date en format 12:08 PM
     * @param date date a formatter
     * @return string du temps
     */
    public static String formatCustomDateTime(Date date)
    {
        if (date == null)
        {
            return "-";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE,  MMM d, yyyy    h:mm a");
        return sdf.format(date);
    }
}
