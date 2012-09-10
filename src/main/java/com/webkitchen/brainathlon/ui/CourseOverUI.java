package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.data.FinalScore;
import com.webkitchen.brainathlon.gameComponents.MidiFeedback;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays the "course over" dialog
 *
 * @author Amy Palke
 */
public class CourseOverUI extends JFrame
{
    private JDialog dialog;
    private String courseTitle;
    private FinalScore finalScore;
    private MidiFeedback midiFeedback;
    private boolean soundEnabled;


    public CourseOverUI(FinalScore finalScore)
    {
        this.courseTitle = finalScore.getCourseTitle();
        this.finalScore = finalScore;
        loadMidi();
    }

    public void createAndShowGUI()
    {
        dialog = new JDialog(this, courseTitle + " Over!!", true);
        Container dialogPane = dialog.getContentPane();
        JPanel mainPanel = createPanel();
        dialogPane.add(mainPanel, BorderLayout.CENTER);

        /* setLocationRelativeTo() must be called after pack() because
        placement is based on size.  Since this frame isn't visible,
        setLocationRelativeTo() will cause the dialog to be centered
        on screen. */
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        playSong();
        dialog.setVisible(true); // blocks until dialog is disposed
    }

    private JPanel createPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        JLabel caption = new JLabel(finalScore.getWinnerText(), SwingConstants.CENTER);
        caption.setForeground(Color.red);
        caption.setFont(new Font("TimesRoman", Font.ITALIC, 24));
        panel.add(caption, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        panel.add(okButton, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Close the dialog
                dialog.removeAll();
                dialog.dispose();
            }
        });
        return panel;
    }

    private void loadMidi()
    {
        midiFeedback = MidiFeedback.getInstance();
        try
        {
            midiFeedback.loadMidi();
            soundEnabled = true;
        }
        catch (MidiUnavailableException e)
        {
            soundEnabled = false;
        }
    }

    private void playSong()
    {
        if (soundEnabled)
        {
            midiFeedback.playCourseOverSong();
        }
    }
}
