package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.data.FinalScore;
import com.webkitchen.brainathlon.gameComponents.Player;
import com.webkitchen.brainathlon.util.MathUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Displays the final game score dialog
 *
 * @author Amy Palke
 */
public class FinalScoreUI extends JFrame
{
    private JDialog dialog;
    private List<FinalScore> scores;
    private List<Player> players;

    public FinalScoreUI(List<Player> players, List<FinalScore> scores)
    {
        this.players = players;
        this.scores = scores;
    }

    public void createAndShowGUI()
    {
        dialog = new JDialog(this, "Game Over!!", true);
        Container dialogPane = dialog.getContentPane();
        JPanel mainPanel = createPanel();
        dialogPane.add(mainPanel, BorderLayout.CENTER);

        /* setLocationRelativeTo() must be called after pack() because
        placement is based on size.  Since this frame isn't visible,
        setLocationRelativeTo() will cause the dialog to be centered
        on screen. */
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true); // blocks until dialog is disposed
    }

    private JPanel createPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        JLabel title = createTitleLabel("Final Scores");
        panel.add(title, BorderLayout.NORTH);

        JPanel midPane;
        if (players.size() == 2)
        {
            midPane = createTwoPlayerPanel();
        }
        else
        {
            midPane = createOnePlayerPanel();
        }

        panel.add(midPane, BorderLayout.CENTER);

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

    private JPanel createTwoPlayerPanel()
    {
        JPanel midPane = new JPanel();
        midPane.setLayout(new BoxLayout(midPane, BoxLayout.Y_AXIS));
        JPanel listPane = new JPanel();
        listPane.setLayout(new SpringLayout());
        listPane.add(createRowTitleLabel("Event"));
        listPane.add(createRowTitleLabel("Winner"));
        listPane.add(createRowTitleLabel(players.get(0).getFirstName() + "'s Score"));
        listPane.add(createRowTitleLabel(players.get(1).getFirstName() + "'s Score"));
        listPane.add(createRowTitleLabel("Time"));
        for (FinalScore finalScore : scores)
        {
            JLabel course = createDataLabel(finalScore.getCourseTitle());
            listPane.add(course);
            JLabel winner = createDataLabel(finalScore.getWinnerName());
            listPane.add(winner);
            JLabel score1 = createDataLabel(String.valueOf(finalScore.getPlayerInfo().get(0).getScore()));
            listPane.add(score1);
            JLabel score2 = createDataLabel(String.valueOf(finalScore.getPlayerInfo().get(1).getScore()));
            listPane.add(score2);
            JLabel time = createDataLabel(getTimeText(finalScore.getTime()));
            listPane.add(time);
        }
        //Lay out the panel.
        int rows = scores.size() + 1; // score rows + title row
        SpringUtilities.makeCompactGrid(listPane,
                                        rows, 5, //rows, cols
                                        6, 6, //initX, initY
                                        40, 6); //xPad, yPad
        midPane.add(listPane);
        return midPane;
    }

    private JPanel createOnePlayerPanel()
    {
        JPanel midPane = new JPanel();
        midPane.setLayout(new BoxLayout(midPane, BoxLayout.Y_AXIS));
        JPanel listPane = new JPanel();
        listPane.setLayout(new SpringLayout());
        listPane.add(createRowTitleLabel("Event"));
        listPane.add(createRowTitleLabel("Score"));
        listPane.add(createRowTitleLabel("Time"));
        for (FinalScore finalScore : scores)
        {
            JLabel course = createDataLabel(finalScore.getCourseTitle());
            listPane.add(course);
            JLabel score = createDataLabel(String.valueOf(finalScore.getPlayerInfo().get(0).getScore()));
            listPane.add(score);
            JLabel time = createDataLabel(getTimeText(finalScore.getTime()));
            listPane.add(time);
        }
        //Lay out the panel.
        int rows = scores.size() + 1; // score rows + title row
        SpringUtilities.makeCompactGrid(listPane,
                                        rows, 3, //rows, cols
                                        6, 6, //initX, initY
                                        40, 6); //xPad, yPad
        midPane.add(listPane);
        return midPane;
    }

    private JLabel createTitleLabel(String text)
    {
        JLabel caption = new JLabel(text, SwingConstants.CENTER);
        caption.setForeground(Color.red);
        caption.setFont(new Font("TimesRoman", Font.ITALIC, 24));
        return caption;
    }

    private JLabel createRowTitleLabel(String text)
    {
        JLabel caption = new JLabel(text, SwingConstants.CENTER);
        caption.setFont(new Font("TimesRoman", Font.PLAIN, 24));
        return caption;
    }

    private JLabel createDataLabel(String text)
    {
        JLabel caption = new JLabel(text, SwingConstants.CENTER);
        caption.setFont(new Font("TimesRoman", Font.ITALIC, 18));
        return caption;
    }

    private String getTimeText(int time)
    {
        int minutes = time / 60;
        int seconds = time % 60;
        String timeText = String.valueOf(minutes) + ":" + MathUtil.padInt(seconds, 2);
        return timeText;
    }
}
