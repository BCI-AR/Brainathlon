package com.webkitchen.brainathlon.gameControl;

import com.webkitchen.brainathlon.data.Configuration;
import com.webkitchen.brainathlon.data.PlayerMonitorList;
import com.webkitchen.eeg.analysis.*;
import com.webkitchen.eeg.analysis.filterdesign.FilterDesigner;
import com.webkitchen.eeg.analysis.filterdesign.IIRFilter;
import com.webkitchen.brainathlon.gameComponents.IRatioPlayerMonitor;
import com.webkitchen.brainathlon.gameComponents.Player;
import com.webkitchen.brainathlon.ui.AbstractCourseUI;
import com.webkitchen.brainathlon.ui.DualBandRatioUI;
import com.webkitchen.brainathlon.util.MathUtil;
import com.webkitchen.brainathlon.util.Range;

import java.io.IOException;
import java.util.List;


/**
 * Mini-game that encourages brainwave activity in a specific ratio between two
 * frequency bands, as specified in our DualBandRatioConfiguration.xml file.
 * Handles all logic for this course, times and scores the game play, and provides
 * feedback for the players.
 *
 * @author Amy Palke
 */
public class DualBandRatioCourse extends AbstractCourse
{
    private DualBandRatioConfiguration config;

    /**
     * Creates a new course
     *
     * @param players the list of players
     */
    public DualBandRatioCourse(List<Player> players)
    {
        super(players);
        loadConfiguration();
        courseTitle = getBandOneDescription() + "-" + getBandTwoDescription() + " Ratio Course";
        announceCourse = true;
        scoreRange = new Range(config.minScore, config.maxScore);
        timedCourse = true;
        timeLimit = config.timeLimitMinutes * 60;  // in seconds
        playerMonitorList = new PlayerMonitorList<IRatioPlayerMonitor>();
    }

    protected void attachMonitors()
    {
        for (Player player : players)
        {
            IRatioPlayerMonitor playerMonitor = new DualBandRatioMonitor(player);
            playerMonitorList.addPlayerAndMonitor(player, playerMonitor);
        }
    }

    public PlayerMonitorList<IRatioPlayerMonitor> getPlayerMonitorList()
    {
        // We know that superclass playerMonitorList holds ISustainedBandPlayerMonitor
        //  because we added them, so can ignore "unchecked assignment" warning
        return playerMonitorList;
    }

    protected AbstractCourseUI getCourseUI()
    {
        return new DualBandRatioUI(this);
    }

    public String getBandOneDescription()
    {
        return config.bandOneSpec.getDescription();
    }

    public String getBandTwoDescription()
    {
        return config.bandTwoSpec.getDescription();
    }

    private void loadConfiguration()
    {
        config = Configuration.getDualBandRatioConfiguration();
    }

    protected boolean needCoeffients()
    {
        return (config.bandOneCoefficients == null ||
                config.bandTwoCoefficients == null ||
                config.bandOneCoefficients.length == 0 ||
                config.bandTwoCoefficients.length == 0);
    }

    protected void createCoefficients(FilterDesigner designer)
    {
        config.bandOneCoefficients = designer.createCoefficients(config.bandOneSpec);
        config.bandTwoCoefficients = designer.createCoefficients(config.bandTwoSpec);
    }

    /**
     * Monitors and scores a given Player's brainwave activity in our target bands
     */
    private class DualBandRatioMonitor extends AbstractPlayerMonitor
            implements IRatioListener, IRatioPlayerMonitor
    {
        private DualBandFilter filter;
        private RatioMonitor filterMonitor;
        private RatioMonitor amplitudeMonitor;
        private double targetRatio;
        private final int maxScoreChange;
        private final int aboveTargetMultiplier;
        private final int belowTargetMultiplier;
        private int currentScore; // "score" stores high score, this stores current

        /**
         * Creates a filter that will monitor player activity, score it,
         * and notify its listeners of the player's current score
         *
         * @param player the player to monitor
         */
        public DualBandRatioMonitor(Player player)
        {
            super(player);
            score = ((config.maxScore - config.minScore) / 2); // start ball in middle
            currentScore = score;
            targetRatio = config.targetRatio;
            maxScoreChange = config.maxScoreChange;
            belowTargetMultiplier = config.belowTargetMultiplier;
            aboveTargetMultiplier = config.aboveTargetMultiplier;
        }

        /**
         * Attach filters to our Player - We will listen to the filters to monitor
         * and score the Player's brainwave amplitude in our target bands
         */
        protected void attachFilters()
        {
            filter = new DualBandFilter(new IIRFilter(config.bandOneSpec, config.bandOneCoefficients),
                                        new IIRFilter(config.bandTwoSpec, config.bandTwoCoefficients));
            player.addSampleListener(filter);
            filterMonitor = new RatioMonitor(config.sampleSize, config.tolerance, config.minNotificationInterval);
            filter.addDualBandListener(filterMonitor);
            filterMonitor.addRatioListener(this);
            amplitudeMonitor = new RatioMonitor(Configuration.getSampleRate(), 600, Configuration.getSampleRate());
            filter.addDualBandListener(amplitudeMonitor);
            DualAmplitudeLogger amplitudeLogger = new DualAmplitudeLogger();
            amplitudeMonitor.addAmplitudeListener(amplitudeLogger);
        }

        /**
         * Detach the listeners we set up
         */
        protected void cleanup()
        {
            super.cleanup();
            player.removeSampleListener(filter);
            filter.removeAllDualBandListeners();
            filterMonitor.removeAllRatioListeners();
            amplitudeMonitor.removeAllRatioListeners();
        }

        public void receiveRatio(double ratio)
        {
            if (isActive)
            {
                // Ignore infinity & NAN readings
                if (ratioIsValid(ratio))
                {
                    calculateScore(ratio);
                    notifyScoreListeners(currentScore);
                    if (currentScore > score)
                    {
                        score = currentScore;
                    }
                    if (score >= scoreRange.getMaxValue())
                    {
                        playerWon(player);
                    }
                }
            }
        }

        private boolean ratioIsValid(double ratio)
        {
            return (0 < ratio && ratio < 100);
        }

        private void calculateScore(double ratio)
        {
            if (ratio >= targetRatio)
            {
                double scoreIncrease = Math.min(((ratio - targetRatio) * aboveTargetMultiplier), maxScoreChange);
                currentScore = (int) (currentScore + scoreIncrease);
            }
            else
            {
                double scoreDecrease = Math.min(((targetRatio - ratio) * belowTargetMultiplier), maxScoreChange);
                currentScore = (int) (currentScore - scoreDecrease);
            }
            // Don't let the currentScore drop below the min
            currentScore = (currentScore < scoreRange.getMinValue()) ? scoreRange.getMinValue() : currentScore;
            // And don't let it go over the max
            currentScore = (currentScore > scoreRange.getMaxValue()) ? scoreRange.getMaxValue() : currentScore;
        }


        /**
         * Attach listener to our RatioMonitor,
         * to receive notification of the player's ratio of 2 wave bands
         *
         * @param listener the observer who wants to receive the player's ratio of 2 wave bands
         */
        public void addRatioListener(IRatioListener listener)
        {
            filterMonitor.addRatioListener(listener);
        }

        /**
         * Remove listener from our RatioMonitor's notification list
         *
         * @param listener the observer to remove
         */
        public void removeRatioListener(IRatioListener listener)
        {
            filterMonitor.removeRatioListener(listener);
        }

        /**
         * Remove all listeners from our RatioMonitor's notification list
         */
        public void removeAllRatioListeners()
        {
            filterMonitor.removeAllRatioListeners();
        }

        /**
         * Attach listener to our DualBandFilter
         *
         * @param listener the observer who wants to receive band values
         */
        public void addDualBandListener(IDualBandSampleListener listener)
        {
            filter.addDualBandListener(listener);
        }

        /**
         * Remove listener from our DualBandFilter's notification list
         *
         * @param listener the observer to remove
         */
        public void removeDualBandListener(IDualBandSampleListener listener)
        {
            filter.removeDualBandListener(listener);
        }

        /**
         * Remove all listeners from our DualBandFilter's notification list
         */
        public void removeAllDualBandListeners()
        {
            filter.removeAllDualBandListeners();
        }

        class DualAmplitudeLogger implements IDualAmplitudeListener
        {
            /**
             * Receive and process the latest amplitude reading
             *
             * @param amplitudeOne the latest amplitude reading for one band
             * @param amplitudeTwo the latest amplitude reading for the other band
             */
            public void receiveAmplitude(double amplitudeOne, double amplitudeTwo)
            {
                if (log != null)
                {
                    synchronized (log)
                    {
                        try
                        {
                            log.write(String.valueOf(MathUtil.round(amplitudeOne, 2))
                                      + "\t"
                                      + String.valueOf(MathUtil.round(amplitudeTwo, 2)));
                            log.newLine();
                        }
                        catch (IOException ignore)
                        {
                            System.out.println("Unable to log user's amplitude");
                            ignore.printStackTrace();
                        }
                    }
                }
            }
        }

    }
}
