package com.example.utilisateur.punchcard;

/**
 * Created by jrsao on 2/17/2015.
 */
public class Occupation
{
    //------ private field ----
    private int _id;
    private String _name;
    private Boolean _isIn;
    private Boolean _isSelected;


    //------ constructor -----
    public Occupation()
    {

    }

    public Occupation(String name, boolean isIn)
    {
        _name = name;
        _isIn = isIn;
        _isSelected = false;
    }


    //---- getter -------
    public int getId()
    {
        return _id;
    }

    public String getName()
    {
        return _name;
    }

    public Boolean isIn()
    {
        return _isIn;
    }

    public Boolean isSelected() {return _isSelected;}

    //-------- setter -----
    public void setId(int id)
    {
        _id = id;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public void isIn(boolean isIn)
    {
        _isIn = isIn;
    }

    public void isSelected(boolean isSelected)
    {
        _isSelected = isSelected;
    }


}
