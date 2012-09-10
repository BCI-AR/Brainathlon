package com.webkitchen.brainathlon.ui.elements;

import javax.swing.*;
import java.awt.event.KeyEvent;


/**
 * @author Amy Palke
 */
public class KeyHandler
{
    private static final String CLICKED = "clicked";

    public static void addEnterKeyListener(AbstractButton button)
    {
        button.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), CLICKED);
        button.getActionMap().put(CLICKED, new ButtonClickAction(button));
    }

    public static void addKeyListener(AbstractButton button, int key)
    {
        button.getInputMap().put(KeyStroke.getKeyStroke(key, 0, false), CLICKED);
        button.getActionMap().put(CLICKED, new ButtonClickAction(button));
    }

    private KeyHandler()
    {
        // Don't allow instantiation
    }
}
