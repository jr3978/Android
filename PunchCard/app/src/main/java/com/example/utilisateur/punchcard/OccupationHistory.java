package com.example.utilisateur.punchcard;

import java.util.Date;

/**
 * Created by jrsao on 2/17/2015.
 *
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

    /**
     * id de l'historique
     * @return
     */
    public int getId()
    {
        return _id;
    }

    /**
     * id de l'occupation
     * @return
     */
    public int getOccupationId()
    {
        return _occupationId;
    }


    /**
     * Date punchIn
     * @return
     */
    public Date getDateTimeIn()
    {
        return  _dateTimeIn;
    }


    /**
     * Date punchOut
     * @return
     */
    public Date getDateTimeOut()
    {
        return _dateTimeOut;
    }


    /**
     * est une fin de période
     * @return
     */
    public  boolean isPeriodEnd(){ return _isPeriodEnd;}


    //------ setter ---------

    /**
     * id de l'historique
     * @param id
     */
    public void setId(int id)
    {
        _id = id;
    }


    /**
     * id de l'occupation
     * @param occupationId
     */
    public void setOccupationId(int occupationId)
    {
        _occupationId = occupationId;
    }


    /**
     * Date punchIn
     * @param dateTimeIn
     */
    public void setDateTimeIn(Date dateTimeIn)
    {
        _dateTimeIn = dateTimeIn;
    }


    /**
     * Date punch out
     * @param dateTimeOut
     */
    public void setDateTimeOut(Date dateTimeOut)
    {
        _dateTimeOut = dateTimeOut;
    }


    /**
     * est une fin de période
     * @param isEnd
     */
    public void isPeriodEnd(boolean isEnd)
    {
        _isPeriodEnd = isEnd;
    }

}
