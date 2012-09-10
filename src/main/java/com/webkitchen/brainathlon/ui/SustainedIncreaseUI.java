package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.data.PlayerMonitorList;
import com.webkitchen.eeg.analysis.IAmplitudeListener;
import com.webkitchen.eeg.analysis.IBandSampleListener;
import com.webkitchen.brainathlon.gameComponents.*;
import com.webkitchen.brainathlon.gameControl.SustainedIncreaseCourse;
import com.webkitchen.brainathlon.ui.elements.RawSignalPanel;
import com.webkitchen.brainathlon.ui.elements.TitledPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Displays the UI for the SustainedIncreaseCourse
 *
 * @author Amy Palke
 * @see SustainedIncreaseCourse
 */
public class SustainedIncreaseUI extends AbstractCourseUI
{
    private SustainedIncreaseCourse course;

    public SustainedIncreaseUI(SustainedIncreaseCourse course)
    {
        super(course);
        this.course = course;
    }

    protected void buildView()
    {
        // Create the player feedback panel(s), and wire them to the monitors
        List<JPanel> playerPanels = new ArrayList<JPanel>();

        PlayerMonitorList<ISustainedBandPlayerMonitor> playerMonitorList = course.getPlayerMonitorList();
        int channelIndex = 0;
        for (int i = 0, numPlayers = playerMonitorList.size(); i < numPlayers; i++)
        {
            int playerNumber = i + 1; // players are 1-based, not 0-based
            Player player = playerMonitorList.getPlayer(playerNumber);
            ISustainedBandPlayerMonitor monitor = playerMonitorList.getPlayerMonitor(playerNumber);

            // Set up the game board
            PlayerGameboardPanel board = new PlayerGameboardPanel();
            monitor.addCountdownListener(board);
            monitor.addRewardListener(board);
            board.soundChannel = channelIndex++;
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
            implements IRewardListener, ICountdownListener
    {
        private int preferredPanelWidth = 300;
        private int preferredPanelHeight = 500;
        private final Color lowColor = Color.RED;
        private final Color highColor = Color.GREEN;
        private Color ballColor = lowColor;
        private static final int BALL_SIZE = 40;
        private Point ballLocation = new Point(preferredPanelWidth / 2, preferredPanelHeight / 2);
        private int countdown = -1;
        private boolean inCountdown;

        // For audio feedback
        private int soundChannel;
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
            // If player just moved in target, update the screen
            if (inTarget && !inCountdown)
            {
                doAboveTarget();
            }
            // If player just moved out of target, update the screen
            else if (inCountdown && !inTarget)
            {
                doBelowTarget();
            }
        }

        public void receiveCountdown(Integer countdown)
        {
            this.countdown = countdown;
            playSound(MidiFeedback.SoundType.INCREASE, soundChannel, midiInstrument, MidiFeedback.Volume.MEDIUM);
            repaint();
        }

        private void doBelowTarget()
        {
            inCountdown = false;
            ballColor = lowColor;
            countdown = -1;
            playSound(MidiFeedback.SoundType.DECREASE, soundChannel, midiInstrument, MidiFeedback.Volume.MEDIUM);
            repaint();
        }

        private void doAboveTarget()
        {
            inCountdown = true;
            ballColor = highColor;
            playSound(MidiFeedback.SoundType.REWARD, soundChannel, midiInstrument, MidiFeedback.Volume.MEDIUM);
            repaint();
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
            graphics.fill3DRect((ballLocation.x - BALL_SIZE / 2),
                                ballLocation.y - BALL_SIZE / 2, BALL_SIZE, BALL_SIZE, true);

            if (inCountdown && countdown >= 0)
            {
                // Set color for score
                graphics.setColor(Color.WHITE);
                // Draw the score
                graphics.setFont(Font.decode("Arial-BOLD-14"));
                int xLoc = (countdown < 10) ? ballLocation.x - 4 : ballLocation.x - 8;
                graphics.drawString(String.valueOf(countdown), xLoc, ballLocation.y + 4);
            }
        }
    }
}
