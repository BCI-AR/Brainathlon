package com.webkitchen.brainathlon.ui.elements;

import javax.swing.*;
import java.awt.*;


/**
 * @author Amy Palke
 */
public class TitledPanel extends JPanel
{
    /**
     * Creates a new <code>JPanel</code> with a titled border and
     * a BorderLayout
     */
    public TitledPanel(String title)
    {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),
                                                     BorderFactory.createEmptyBorder(10, 10, 5, 10)));
    }
}
