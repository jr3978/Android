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

    //---- getter -------

    /**
     * id de l'occupation
     * @return
     */
    public int getId()
    {
        return _id;
    }


    /**
     * nom de l'occupation
     * @return
     */
    public String getName()
    {
        return _name;
    }


    /**
     * est puncher in
     * @return
     */
    public Boolean isIn()
    {
        return _isIn;
    }


    /**
     * est affiché sur le widget
     * @return
     */
    public Boolean isSelected() {return _isSelected;}

    //-------- setter -----


    /**
     * id de l'occupation
     * @param id
     */
    public void setId(int id)
    {
        _id = id;
    }

    /**
     * nom de l'occupation
     * @param name
     */
    public void setName(String name)
    {
        _name = name;
    }


    /**
     * est puncher In
     * @param isIn
     */
    public void isIn(boolean isIn)
    {
        _isIn = isIn;
    }


    /**
     * est sélectionner par le widget
     * @param isSelected
     */
    public void isSelected(boolean isSelected)
    {
        _isSelected = isSelected;
    }


}
