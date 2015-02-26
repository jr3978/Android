package com.example.utilisateur.punchcard;

import java.util.Date;

/**
 * Created by jrsao on 2/17/2015.
 */
public class OccupationHistory
{
    //------ private field -----
    private int _id;
    private int _occupationId;
    private Date _dateTimeIn;
    private Date _dateTimeOut;
    private boolean _isPeriodEnd;



    //------ constructor ------
    public OccupationHistory()
    {

    }

    //------ getter --------
    public int getId()
    {
        return _id;
    }

    public int getOccupationId()
    {
        return _occupationId;
    }

    public Date getDateTimeIn()
    {
        return  _dateTimeIn;
    }

    public Date getDateTimeOut()
    {
        return _dateTimeOut;
    }

    public  boolean isPeriodEnd(){ return _isPeriodEnd;}


    //------ setter ---------
    public void setId(int id)
    {
        _id = id;
    }

    public void setOccupationId(int occupationId)
    {
        _occupationId = occupationId;
    }

    public void setDateTimeIn(Date dateTimeIn)
    {
        _dateTimeIn = dateTimeIn;
    }

    public void setDateTimeOut(Date dateTimeOut)
    {
        _dateTimeOut = dateTimeOut;
    }

    public void isPeriodEnd(boolean isEnd)
    {
        _isPeriodEnd = isEnd;
    }

}
