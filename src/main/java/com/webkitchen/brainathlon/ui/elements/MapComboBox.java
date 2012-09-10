package com.webkitchen.brainathlon.ui.elements;

import javax.swing.*;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Amy Palke
 */
public class MapComboBox extends JComboBox
{
    private Map map;

    /**
     * Creates a <code>JComboBox</code> with a default data model containing no items.
     * Call setMap to pass in the items to display.
     *
     * @see #setMap
     */
    public MapComboBox()
    {
    }

    /**
     * Creates a <code>JComboBox</code> with a default data model containing the
     * set of keys in the map.  By default the first item in the data model becomes
     * selected.
     *
     * @param map the keys to display, and associated values
     * @see javax.swing.DefaultComboBoxModel
     */
    public MapComboBox(Map map)
    {
        setMap(map);
    }

    /**
     * Sets the map that contains the set of keys to display and their associated
     * values to be returned by getSelectedValue()
     *
     * @param map the keys to display, and associated values
     */
    public void setMap(Map map)
    {
        this.map = map;
        Object[] keyArray = map.keySet().toArray();
        Arrays.sort(keyArray);
        setModel(new DefaultComboBoxModel(keyArray));
    }

    /**
     * Returns the value mapped to the selected key.
     *
     * @return the value mapped to current selected Object
     * @see #getSelectedItem
     */
    public Object getSelectedValue()
    {
        Object selectedItem = super.getSelectedItem();
        return map.get(selectedItem);
    }
}
