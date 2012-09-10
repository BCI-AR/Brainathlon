package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.ui.elements.KeyHandler;
import com.webkitchen.brainathlon.ui.elements.TitledPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


/**
 * Displays the "game over" dialog
 *
 * @author Amy Palke
 */
public class GameOverUI extends JFrame
{
    public enum Selection
    {
        REMATCH, NEWGAME, QUIT, NO_SELECTION
    };
    private Selection selectedValue = Selection.NO_SELECTION;

    private JDialog dialog;
    // Provides mutually exclusive radio button selection
    private ButtonGroup group = new ButtonGroup();
    // Our radio buttons
    private JRadioButton rematch = new JRadioButton("Rematch (same players)");
    private JRadioButton newGame = new JRadioButton("New Game (new players)");
    private JRadioButton[] radioButtons = {rematch, newGame};
    // Our control buttons
    private JButton selectButton = new JButton("Select");
    private JButton quitButton = new JButton("Quit");
    private JButton[] controlButtons = {selectButton, quitButton};

    public Selection createAndShowGUI()
    {
        dialog = new JDialog(this, "Game Over", true);
        Container dialogPane = dialog.getContentPane();
        JPanel selectNextGamePanel = createSelectNextGamePanel();
        dialogPane.add(selectNextGamePanel, BorderLayout.CENTER);

        /* setLocationRelativeTo() must be called after pack() because
        placement is based on size.  Since this frame isn't visible,
        setLocationRelativeTo() will cause the dialog to be centered
        on screen. */
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true); // blocks until dialog is disposed

        return selectedValue;
    }

    private JPanel createSelectNextGamePanel()
    {
        JPanel mainPanel = new TitledPanel("Play again");

        // Add the radio buttons
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        for (int i = 0, length = radioButtons.length; i < length; i++)
        {
            radioButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            radioPanel.add(radioButtons[i]);
            group.add(radioButtons[i]);
        }
        mainPanel.add(radioPanel, BorderLayout.CENTER);
        addRadioButtonListeners();

        // Add the control buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 10));
        for (int i = 0, length = controlButtons.length; i < length; i++)
        {
            buttonPanel.add(controlButtons[i]);
        }
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        addControlButtonListeners();

        return mainPanel;
    }

    private void addRadioButtonListeners()
    {
        KeyHandler.addKeyListener(rematch, KeyEvent.VK_SPACE);
        rematch.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectedValue = Selection.REMATCH;
            }
        });

        KeyHandler.addKeyListener(newGame, KeyEvent.VK_SPACE);
        newGame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectedValue = Selection.NEWGAME;
            }
        });
    }

    private void addControlButtonListeners()
    {
        dialog.getRootPane().setDefaultButton(selectButton);
        selectButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Close the dialog if the user has made a selection
                if (selectedValue != Selection.NO_SELECTION)
                {
                    dialog.dispose();
                }
            }
        });

        KeyHandler.addEnterKeyListener(quitButton);
        quitButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectedValue = Selection.QUIT;
                dialog.dispose();
            }
        });
    }
}
