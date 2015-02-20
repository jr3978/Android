package com.example.utilisateur.punchcard;

import java.util.PriorityQueue;

/**
 * Created by jrsao on 2/17/2015.
 */
public class OccupationParameters
{
    public enum Parameters {
        NB_WEEK_RESET("How many week before reset ?"),
        RESET_DAY("Set reset day"),
        ROUND_TYPE("Round type (up, down, normal)"),
        ROUND_MINUTE("Round for every x minutes");

        private String _value;

        Parameters(String value) {
            _value = value;
        }

        public String getValue() {
            return _value;
        }

        public static Parameters fromString(String text) {
            if (text != null) {
                for (Parameters b : Parameters.values()) {
                    if (text.equalsIgnoreCase(b.getValue())) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    // Round Type
    public enum RoundType
    {
        ROUND_DOWN (0),
        ROUND_NORMAL (1),
        ROUND_UP (2);

        private int _value;

        RoundType(int v)
        {
            _value = v;
        }

        public int getValue()
        {
            return _value;
        }
    }

    // DayOfWeek
    public enum DayOfWeek
    {
        SUNDAY (0),
        MONDAY (1),
        TUESDAY (2),
        WEDNESDAY (3),
        THURSDAY (4),
        FRIDAY (5),
        SATURDAY (6);

        private int _value;

        DayOfWeek(int v)
        {
            _value = v;
        }

        public int getValue()
        {
            return _value;
        }
    }

    //----- private field -----
    private int _id;
    private int _occupationId;
    private DayOfWeek _resetDay;
    private int _nbDayBeforeReset;
    private int _roundMinuteValue;
    private RoundType _roundType;


    //----- constructor ----
    public OccupationParameters()
    {
        _resetDay = DayOfWeek.SUNDAY;
        _nbDayBeforeReset = 7;
        _roundMinuteValue = 1;
        _roundType = RoundType.ROUND_NORMAL;
    }

    public  OccupationParameters(int occupationId ,DayOfWeek resetDay, int nbDayBeforeReset,
                                 int roundMinuteValue, RoundType roundType)
    {
        _occupationId = occupationId;
        _resetDay = resetDay;
        _nbDayBeforeReset = nbDayBeforeReset;
        _roundMinuteValue = roundMinuteValue;
        _roundType = roundType;
    }


    //-------- getter --------------
    public int getId()
    {
        return _id;
    }

    public int getOccupationId()
    {
        return _occupationId;
    }

    public DayOfWeek getResetDay()
    {
        return _resetDay;
    }

    public int getNbDayBeforeReset()
    {
        return _nbDayBeforeReset;
    }

    public int getRoundMinuteValue()
    {
        return _roundMinuteValue;
    }

    public RoundType getRoundType()
    {
        return _roundType;
    }


    //------ setter ----------
    public void setId(int id)
    {
        _id = id;
    }

    public void setOccupationId(int occupationId)
    {
        _occupationId = occupationId;
    }

    public void setResetDay(DayOfWeek resetDay)
    {
        _resetDay = resetDay;
    }

    public void setNbDayBeforeReset(int nbDayBeforeReset)
    {
        _nbDayBeforeReset = nbDayBeforeReset;
    }

    public void setRoundMinuteValue(int roundMinuteValue)
    {
        _roundMinuteValue = roundMinuteValue;
    }

    public void setRoundType(RoundType roundType)
    {
        _roundType = roundType;
    }



}
