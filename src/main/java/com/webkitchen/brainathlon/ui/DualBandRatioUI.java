package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.data.PlayerMonitorList;
import com.webkitchen.eeg.analysis.IDualBandSampleListener;
import com.webkitchen.eeg.analysis.IRatioListener;
import com.webkitchen.brainathlon.gameComponents.*;
import com.webkitchen.brainathlon.gameControl.DualBandRatioCourse;
import com.webkitchen.brainathlon.ui.elements.RawSignalPanel;
import com.webkitchen.brainathlon.ui.elements.TitledPanel;
import com.webkitchen.brainathlon.util.MathUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays the UI for the DualBandRatioCourse
 *
 * @author Amy Palke
 * @see DualBandRatioCourse
 */
public class DualBandRatioUI extends AbstractCourseUI
{
    private DualBandRatioCourse course;

    public DualBandRatioUI(DualBandRatioCourse course)
    {
        super(course);
        this.course = course;
    }

    protected void buildView()
    {
        // Create the player feedback panel(s), and wire them to the monitors
        List<JPanel> playerPanels = new ArrayList<JPanel>();

        PlayerMonitorList<IRatioPlayerMonitor> playerMonitorList = course.getPlayerMonitorList();
        int channelIndex = 0;
        for (int i = 0, numPlayers = playerMonitorList.size(); i < numPlayers; i++)
        {
            int playerNumber = i + 1; // players are 1-based, not 0-based
            Player player = playerMonitorList.getPlayer(playerNumber);
            IRatioPlayerMonitor monitor = playerMonitorList.getPlayerMonitor(playerNumber);

            // Set up the game board
            PlayerGameboardPanel board = new PlayerGameboardPanel();
            monitor.addScoreListener(board);
            monitor.addRewardListener(board);
            board.soundChannel = channelIndex++;
            board.midiInstrument = player.getInstrument();

            // Set up the signal display
            PlayerInfoPanel info = new PlayerInfoPanel();
            monitor.addDualBandListener(info);
            monitor.addScoreListener(info);
            monitor.addRatioListener(info);

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
            implements IScoreListener, IRatioListener, IDualBandSampleListener
    {
        private JLabel scoreDisplay = new JLabel("0");
        private JLabel ratioDisplay = new JLabel("");
        private RawSignalPanel bandOneWaveDisplay;
        private RawSignalPanel bandTwoWaveDisplay;
        private int preferredPanelHeight = 100;
        private int preferredPanelWidth = 300;
        private int scale = 1;

        PlayerInfoPanel()
        {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            bandOneWaveDisplay = new RawSignalPanel(preferredPanelHeight, scale);
            bandOneWaveDisplay.setPreferredSize(new Dimension(preferredPanelWidth, preferredPanelHeight));
            bandTwoWaveDisplay = new RawSignalPanel(preferredPanelHeight, scale);
            bandTwoWaveDisplay.setPreferredSize(new Dimension(preferredPanelWidth, preferredPanelHeight));

            JPanel scorePanel = new JPanel(new GridLayout(2, 2));
            JLabel scoreLabel = new JLabel("Score: ");
            scoreLabel.setHorizontalAlignment(JLabel.RIGHT);
            JLabel ratioLabel = new JLabel("Ratio of " + course.getBandOneDescription() + "/" + course.getBandTwoDescription() + ": ");
            ratioLabel.setHorizontalAlignment(JLabel.RIGHT);
            scorePanel.add(scoreLabel);
            scorePanel.add(scoreDisplay);
            scorePanel.add(ratioLabel);
            scorePanel.add(ratioDisplay);

            add(bandOneWaveDisplay);
            add(bandTwoWaveDisplay);
            add(scorePanel);
        }

        public void receiveScore(Integer score)
        {
            scoreDisplay.setText(score.toString());
        }

        public void receiveRatio(double ratio)
        {
            // Ignore infinity readings
            if (ratio != Double.POSITIVE_INFINITY && ratio != Double.NEGATIVE_INFINITY)
            {
                ratioDisplay.setText(String.valueOf(MathUtil.round(ratio, 2)));
            }
        }


        public void receiveBand(double sampleOneValue, double sampleTwoValue)
        {
            bandOneWaveDisplay.setCurrentValue(sampleOneValue);
            bandTwoWaveDisplay.setCurrentValue(sampleTwoValue);
        }
    }

    private class PlayerGameboardPanel extends JPanel
            implements IScoreListener, IRewardListener
    {
        private int preferredWidth = 400;
        private int preferredHeight = 350;
        private final Color ballColor = Color.RED;
        private static final int BALL_SIZE = 40;
        private Point[] balanceLine;
        private Point ballLocation;
        private int score = -1;

        // For audio feedback
        private int soundChannel;
        private int midiInstrument;


        /**
         * Creates a new <code>SimpleSpectrumDisplayPanel</code>
         */
        public PlayerGameboardPanel()
        {
            setPreferredSize(new Dimension(preferredWidth, preferredHeight));
            int centerY = preferredHeight / 2;
            // Start with line flat in the center of the screen
            balanceLine = new Point[]
            {
                new Point(0, centerY),
                new Point(preferredWidth, centerY)
            };
            // Start with ball in the center on top of the line
            ballLocation = new Point(preferredWidth / 2, centerY - (BALL_SIZE / 2));
        }

        public void receiveReward(boolean inTarget)
        {
            if (inTarget)
            {
                playSound(MidiFeedback.SoundType.REWARD, soundChannel, midiInstrument, MidiFeedback.Volume.SOFT);
            }
        }

        public void receiveScore(Integer score)
        {
            if (this.score > -1)
            {
                int lastScore = this.score;
                this.score = score;

                if (score > lastScore)
                {
                    playSound(MidiFeedback.SoundType.INCREASE, soundChannel, midiInstrument, MidiFeedback.Volume.SOFT);
                }
                else if (score < lastScore)
                {
                    playSound(MidiFeedback.SoundType.DECREASE, soundChannel, midiInstrument, MidiFeedback.Volume.SOFT);
                }

                int difference = (score - lastScore) * 4;
                int height = getHeight();
                int width = getWidth();
                int midline = height / 2;
                Point start = balanceLine[0];
                Point end = balanceLine[1];

                // Tilt the balance line to reflect the direction the score is moving
                start.y = midline - difference;
                end.y = midline + difference;
                end.x = width;
                ballLocation.x = scaleScore();
                // y = mx + b, values inverted here since 0 is top of screen
                float slope = ((float) (start.y - end.y)) / ((float) (end.x - start.x));
                ballLocation.y = (int) (slope * (width - ballLocation.x) + end.y - (BALL_SIZE / 2));

                repaint();
            }
            else
            {
                this.score = score; // first score
            }
        }

        /**
         * Scale the score to fit correctly on screen. A score of 0 puts you at the left, and 100
         * puts you at the right
         *
         * @return the scaled score
         */
        private int scaleScore()
        {
            int width = this.getWidth() - (2 * BALL_SIZE);
            int scaledValue = (((width * score)
                                / course.getScoreRange().getDifference())
                               + BALL_SIZE);
            return scaledValue;
        }

        /**
         * Called by repaint(), repaints the current state of our graph
         */
        public void paintComponent(Graphics graphics)
        {
            super.paintComponent(graphics);
            drawLine(graphics);
            drawBall(graphics);
        }

        private void drawLine(Graphics graphics)
        {
            Point start = balanceLine[0];
            Point end = balanceLine[1];
            graphics.drawLine(start.x, start.y, end.x, end.y);
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
