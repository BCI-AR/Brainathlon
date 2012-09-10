package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.ui.elements.TitledPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Amy Palke
 */
public class StartCourseUI extends JFrame
{
    private JDialog dialog;
    private boolean isReady;
    private String courseTitle;

    // Our control buttons
    private JButton startButton = new JButton("Start Course");

    public StartCourseUI(String courseTitle)
    {
        this.courseTitle = courseTitle;
    }

    public boolean createAndShowGUI()
    {
        dialog = new JDialog(this, courseTitle, true);
        Container dialogPane = dialog.getContentPane();
        JPanel startGamePanel = createStartGamePanel();
        dialogPane.add(startGamePanel, BorderLayout.CENTER);

        /* setLocationRelativeTo() must be called after pack() because
        placement is based on size.  Since this frame isn't visible,
        setLocationRelativeTo() will cause the dialog to be centered
        on screen. */
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true); // blocks until dialog is disposed

        return isReady;
    }

    private JPanel createStartGamePanel()
    {
        JPanel mainPanel = new TitledPanel(courseTitle);

        JLabel caption = new JLabel("Are you ready to start the " + courseTitle + "?", SwingConstants.CENTER);
        caption.setForeground(Color.red);
        caption.setFont(new Font("TimesRoman", Font.ITALIC, 24));
        mainPanel.add(caption, BorderLayout.CENTER);


        // Add the control buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 10));
        buttonPanel.add(startButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        addControlButtonListeners();

        return mainPanel;
    }

    private void addControlButtonListeners()
    {
        dialog.getRootPane().setDefaultButton(startButton);
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                isReady = true;
                dialog.dispose();
            }
        });
    }
}
