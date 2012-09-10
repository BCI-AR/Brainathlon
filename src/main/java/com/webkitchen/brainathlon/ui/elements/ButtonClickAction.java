package com.webkitchen.brainathlon.ui.elements;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * @author Amy Palke
 */
public class ButtonClickAction extends AbstractAction
{
    private AbstractButton button;

    /**
     * Creates a swing Action that will click this button
     *
     * @param button the button to click
     */
    public ButtonClickAction(AbstractButton button)
    {
        this.button = button;
    }

    /**
     * Click the button
     */
    public void actionPerformed(ActionEvent e)
    {
        button.doClick();
    }
}
