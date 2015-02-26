package com.example.utilisateur.punchcard;

import android.content.Context;

import java.util.PriorityQueue;

/**
 * Created by jrsao on 2/17/2015.
 */
public class OccupationParameters
{

    /**
     * Type d'arrondissement
     * à la baisse, normal, à la hausse
     */
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
    // primary key
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

    /**
     * id de l'occupation
     * @return
     */
    public int getOccupationId()
    {
        return _occupationId;
    }


    /**
     * Valeur d'arrondissement en minute
     * @return
     */
    public int getRoundMinuteValue()
    {
        return _roundMinuteValue;
    }


    /**
     * type d'arrondissement
     * @return
     */
    public RoundType getRoundType()
    {
        return _roundType;
    }


    //------ setter ----------

    /**
     * Id de l'occupation
     * @param occupationId
     */
    public void setOccupationId(int occupationId)
    {
        _occupationId = occupationId;
    }


    /**
     * Valeur d'arrondissement en minute
     * @param roundMinuteValue
     */
    public void setRoundMinuteValue(int roundMinuteValue)
    {
        _roundMinuteValue = roundMinuteValue;
    }


    /**
     * type d'arrondissement
     * @param roundType
     */
    public void setRoundType(RoundType roundType)
    {
        _roundType = roundType;
    }



}
