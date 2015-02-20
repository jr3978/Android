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




    //------ constructor ------
    public OccupationHistory()
    {

    }

    public OccupationHistory(int occupationId, Date dateTimeIn, Date dateTimeOut)
    {
        _occupationId = occupationId;
        _dateTimeIn = dateTimeIn;
        _dateTimeOut = _dateTimeOut;
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

}
