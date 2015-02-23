package com.example.utilisateur.punchcard;

import android.content.Context;

import java.util.PriorityQueue;

/**
 * Created by jrsao on 2/17/2015.
 */
public class OccupationParameters
{
    public enum Parameters
    {
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



    //----- private field -----
    private int _id;
    private int _occupationId;
    private int _roundMinuteValue;
    private RoundType _roundType;


    //----- constructor ----
    public OccupationParameters()
    {
        _roundMinuteValue = 1;
        _roundType = RoundType.ROUND_NORMAL;
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

    public void setRoundMinuteValue(int roundMinuteValue)
    {
        _roundMinuteValue = roundMinuteValue;
    }

    public void setRoundType(RoundType roundType)
    {
        _roundType = roundType;
    }



}
