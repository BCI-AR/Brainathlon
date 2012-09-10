package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.data.PlayerMonitorList;
import com.webkitchen.eeg.analysis.IAmplitudeListener;
import com.webkitchen.eeg.analysis.IBandSampleListener;
import com.webkitchen.brainathlon.gameComponents.*;
import com.webkitchen.brainathlon.gameControl.BandIncreaseCourse;
import com.webkitchen.brainathlon.ui.elements.RawSignalPanel;
import com.webkitchen.brainathlon.ui.elements.TitledPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Displays the UI for the BandIncreaseCourse
 *
 * @author Amy Palke
 * @see BandIncreaseCourse
 */
public class BandIncreaseUI extends AbstractCourseUI
{
    private BandIncreaseCourse course;

    public BandIncreaseUI(BandIncreaseCourse course)
    {
        super(course);
        this.course = course;
    }

    protected void buildView()
    {
        // Create the player feedback panel(s), and wire them to the monitors
        List<JPanel> playerPanels = new ArrayList<JPanel>();

        PlayerMonitorList<IBandPlayerMonitor> playerMonitorList = course.getPlayerMonitorList();
        int channelIndex = 0;
        for (int i = 0, numPlayers = playerMonitorList.size(); i < numPlayers; i++)
        {
            int playerNumber = i + 1; // players are 1-based, not 0-based
            Player player = playerMonitorList.getPlayer(playerNumber);
            IBandPlayerMonitor monitor = playerMonitorList.getPlayerMonitor(playerNumber);

            // Set up the game board
            PlayerGameboardPanel board = new PlayerGameboardPanel();
            monitor.addScoreListener(board);
            monitor.addRewardListener(board);
            board.increaseChannel = channelIndex++;
            board.decreaseChannel = channelIndex++;
            board.midiInstrument = player.getInstrument();

            // Set up the signal display
            PlayerInfoPanel info = new PlayerInfoPanel();
            monitor.addScoreListener(info);
            monitor.addAmplitudeListener(info);
            monitor.addBandListener(info);

            // Put them together on a panel, and add that to our list
            TitledPanel panel = new TitledPanel(player.getFirstName());
            panel.add(board, BorderLayout.CENTER);
            panel.add(info, BorderLayout.SOUTH);

            playerPanels.add(panel);
        }

        // Create a display panel with a grid that accommodates our player panel(s)
        int columns = playerPanels.size();
        JPanel displayPanel = new JPanel(new GridLayout(1, columns, 5, 5));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        for (JPanel panel : playerPanels)
        {
            displayPanel.add(panel);
        }

        // Create the top panel
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        timeDisplay = new JLabel("0:00");
        JLabel timerLabel = new JLabel("time: ");
        timerLabel.setHorizontalAlignment(JLabel.RIGHT);
        topPanel.add(timerLabel);
        topPanel.add(timeDisplay);

        // Create the main panel, and add all subpanels to it
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(displayPanel, BorderLayout.CENTER);

        getContentPane().add(mainPanel);
    }

    private class PlayerInfoPanel extends JPanel
            implements IScoreListener, IAmplitudeListener, IBandSampleListener
    {
        private JLabel scoreDisplay = new JLabel("0");
        private JLabel amplitudeDisplay = new JLabel("");
        private RawSignalPanel waveDisplay;

        PlayerInfoPanel()
        {
            this.setLayout(new BorderLayout(5, 5));
            this.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            waveDisplay = new RawSignalPanel(100, 1);
            waveDisplay.setPreferredSize(new Dimension(300, 100));

            JPanel scorePanel = new JPanel(new GridLayout(2, 2));
            JLabel scoreLabel = new JLabel("Current Score: ");
            scoreLabel.setHorizontalAlignment(JLabel.RIGHT);
            JLabel amplitudeLabel = new JLabel("Current Amplitude: ");
            amplitudeLabel.setHorizontalAlignment(JLabel.RIGHT);
            scorePanel.add(scoreLabel);
            scorePanel.add(scoreDisplay);
            scorePanel.add(amplitudeLabel);
            scorePanel.add(amplitudeDisplay);

            add(waveDisplay, BorderLayout.CENTER);
            add(scorePanel, BorderLayout.SOUTH);
        }

        public void receiveScore(Integer score)
        {
            scoreDisplay.setText(score.toString());
        }

        public void receiveAmplitude(double amplitude)
        {
            amplitudeDisplay.setText(String.valueOf(Math.round(amplitude)));
        }

        public void receiveBand(double bandSample)
        {
            waveDisplay.setCurrentValue(bandSample);
        }
    }

    private class PlayerGameboardPanel extends JPanel
            implements IScoreListener, IRewardListener
    {
        private int preferredPanelWidth = 300;
        private int preferredPanelHeight = 500;
        private final Color ballColor = Color.RED;
        private static final int BALL_SIZE = 40;
        private Point ballLocation = new Point(preferredPanelWidth / 2, preferredPanelHeight - (BALL_SIZE / 2));
        private int score;

        // For audio feedback
        private int increaseChannel;
        private int decreaseChannel;
        private int midiInstrument;


        /**
         * Creates a new <code>SimpleSpectrumDisplayPanel</code>
         */
        public PlayerGameboardPanel()
        {
            setPreferredSize(new Dimension(preferredPanelWidth, preferredPanelHeight));
        }

        public void receiveReward(boolean inTarget)
        {
            if (inTarget)
            {
                playSound(MidiFeedback.SoundType.REWARD, increaseChannel, midiInstrument, MidiFeedback.Volume.MEDIUM);
            }
        }

        public void receiveScore(Integer score)
        {
            int lastScore = this.score;
            this.score = score;

            if (score > lastScore)
            {
                doIncrease();
            }
            else if (score < lastScore)
            {
                doDecrease();
            }
            repaint();
        }

        private void doDecrease()
        {
            int loc = scaleScore();
            int bottomLoc = this.getHeight() - (BALL_SIZE / 2);
            // Don't let the ball go off screen
            if (loc > bottomLoc)
            {
                loc = bottomLoc;
            }
            ballLocation.y = loc;
            playSound(MidiFeedback.SoundType.DECREASE, decreaseChannel, midiInstrument, MidiFeedback.Volume.SOFT);
        }

        private void doIncrease()
        {
            int loc = scaleScore();
            int topLoc = BALL_SIZE / 2;
            // Don't let the ball go off screen
            if (loc < topLoc)
            {
                loc = topLoc;
            }
            ballLocation.y = loc;
            playSound(MidiFeedback.SoundType.INCREASE, increaseChannel, midiInstrument, MidiFeedback.Volume.SOFT);
        }

        /**
         * Scale the score to fit correctly on screen. A score of 0 puts you at the bottom of the screen, and 100 puts
         * you at the top
         *
         * @return the scaled score
         */
        private int scaleScore()
        {
            int height = this.getHeight();
            // First calculate the value inverted (big scores have big values)
            int scaledValue = (((height - BALL_SIZE) * score)
                               / course.getScoreRange().getDifference())
                              + (BALL_SIZE / 2);
            // Then invert (we want be scores to have small values, because ball travels up)
            scaledValue = height - scaledValue;
            return scaledValue;
        }

        /**
         * Called by repaint(), repaints the current state of our graph
         */
        public void paintComponent(Graphics graphics)
        {
            super.paintComponent(graphics);
            drawBall(graphics);
        }

        private void drawBall(Graphics graphics)
        {
            // Draw our ball
            graphics.setColor(ballColor);
            graphics.fillOval(ballLocation.x - BALL_SIZE / 2,
                              ballLocation.y - BALL_SIZE / 2, BALL_SIZE, BALL_SIZE);
        }

    }
}
