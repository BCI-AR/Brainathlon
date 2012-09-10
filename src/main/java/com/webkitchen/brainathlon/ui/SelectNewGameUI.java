package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.data.Configuration;
import com.webkitchen.brainathlon.ui.elements.KeyHandler;
import com.webkitchen.brainathlon.ui.elements.TitledPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


/**
 * Displays dialog that allows players to pick 1-player or 2-player game mode
 *
 * @author Amy Palke
 */
public class SelectNewGameUI extends JFrame
{
    public enum Selection
    {
        ONE_PLAYER(1), TWO_PLAYER(2), QUIT(0), NO_SELECTION(0);
        private int numberPlayers;

        private Selection(int numberPlayers)
        {
            this.numberPlayers = numberPlayers;
        }

        public int getNumberPlayers()
        {
            return numberPlayers;
        }
    };
    private Selection selectedValue = Selection.NO_SELECTION;

    private JDialog dialog;
    // Provides mutually exclusive radio button selection
    private ButtonGroup group = new ButtonGroup();
    // Our radio buttons
    private JRadioButton onePlayer = new JRadioButton("1 Player");
    private JRadioButton twoPlayer = new JRadioButton("2 Player");
    private JRadioButton[] radioButtons = {onePlayer, twoPlayer};
    // Our control buttons
    private JButton selectButton = new JButton("Select");
    private JButton quitButton = new JButton("Quit");
    private JButton[] controlButtons = {selectButton, quitButton};

    public Selection createAndShowGUI()
    {
        dialog = new JDialog(this, "New Game", true);
        Container dialogPane = dialog.getContentPane();
        JPanel selectModePanel = createSelectModePanel();
        dialogPane.add(selectModePanel, BorderLayout.CENTER);

        /* setLocationRelativeTo() must be called after pack() because
        placement is based on size.  Since this frame isn't visible,
        setLocationRelativeTo() will cause the dialog to be centered
        on screen. */
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true); // blocks until dialog is disposed

        return selectedValue;
    }


    private JPanel createSelectModePanel()
    {
        JPanel mainPanel = new TitledPanel("Select mode");

        if (Configuration.getDebugMode())
        {
            JLabel debugModeWarning = new JLabel("Running in debug mode");
            debugModeWarning.setForeground(Color.RED);
            mainPanel.add(debugModeWarning, BorderLayout.NORTH);
        }

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
        KeyHandler.addKeyListener(onePlayer, KeyEvent.VK_SPACE);
        onePlayer.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectedValue = Selection.ONE_PLAYER;
            }
        });

        KeyHandler.addKeyListener(twoPlayer, KeyEvent.VK_SPACE);
        twoPlayer.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectedValue = Selection.TWO_PLAYER;
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
                // Close the dialog only if the user has made a selection
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
