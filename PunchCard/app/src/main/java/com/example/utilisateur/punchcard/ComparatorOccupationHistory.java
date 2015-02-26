package com.example.utilisateur.punchcard;

import java.util.Comparator;

/**
 * Created by jrsao on 2/24/2015.
 * Comparateur de OccupationHistory, compare le DateTimeIn (Date)
 */
public class ComparatorOccupationHistory implements Comparator<OccupationHistory>
{
    @Override
    public int compare(OccupationHistory lhs, OccupationHistory rhs) {
        return rhs.getDateTimeIn().compareTo(lhs.getDateTimeIn());
    }
}
