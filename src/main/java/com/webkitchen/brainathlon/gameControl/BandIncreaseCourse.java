package com.webkitchen.brainathlon.gameControl;

import com.webkitchen.brainathlon.data.Configuration;
import com.webkitchen.brainathlon.data.PlayerMonitorList;
import com.webkitchen.eeg.analysis.BandFilter;
import com.webkitchen.eeg.analysis.BandMonitor;
import com.webkitchen.eeg.analysis.filterdesign.FilterDesigner;
import com.webkitchen.brainathlon.gameComponents.IBandPlayerMonitor;
import com.webkitchen.brainathlon.gameComponents.Player;
import com.webkitchen.brainathlon.ui.AbstractCourseUI;
import com.webkitchen.brainathlon.ui.BandIncreaseUI;
import com.webkitchen.brainathlon.util.MathUtil;
import com.webkitchen.brainathlon.util.Range;

import java.util.List;


/**
 * Mini-game that encourages brainwave activity in a specific frequency band, as specified
 * in our BandIncreaseConfiguration.xml file.  Handles all logic for this course,
 * times and scores the game play, and provides feedback for the players.
 *
 * @author Amy Palke
 */
public class BandIncreaseCourse extends AbstractBandCourse
{
    private BandIncreaseConfiguration config;

    /**
     * Creates a new course
     *
     * @param players the list of players
     */
    public BandIncreaseCourse(List<Player> players)
    {
        super(players);
        loadConfiguration();
        courseTitle = config.filterSpec.getDescription() + " Increase Course";
        announceCourse = true;
        scoreRange = new Range(config.minScore, config.maxScore);
        timedCourse = true;
        timeLimit = config.timeLimitMinutes * 60;  // in seconds
        playerMonitorList = new PlayerMonitorList<IBandPlayerMonitor>();
    }

    protected void attachMonitors()
    {
        for (Player player : players)
        {
            IBandPlayerMonitor playerMonitor = new BandIncreasePlayerMonitor(player);
            playerMonitorList.addPlayerAndMonitor(player, playerMonitor);
        }
    }

    public PlayerMonitorList<IBandPlayerMonitor> getPlayerMonitorList()
    {
        // We know that superclass playerMonitorList holds IBandPlayerMonitors
        //  because we added them, so can ignore "unchecked assignment" warning
        return playerMonitorList;
    }

    protected AbstractCourseUI getCourseUI()
    {
        return new BandIncreaseUI(this);
    }

    private void loadConfiguration()
    {
        config = Configuration.getBandIncreaseConfiguration();
    }

    protected boolean needCoeffients()
    {
        return (config.filterCoefficients == null ||
                config.filterCoefficients.length == 0);
    }

    protected void createCoefficients(FilterDesigner designer)
    {
        config.filterCoefficients = designer.createCoefficients(config.filterSpec);
    }


    /**
     * Monitors and scores a given Player's brainwave activity in our target band
     */
    private class BandIncreasePlayerMonitor extends AbstractBandPlayerMonitor
    {
        private BandMonitor filterMonitor;
        private BandMonitor amplitudeLogMonitor;
        private int targetAmplitude;
        private double lastAmplitude = 0;
        private final double multiplier;
        private int scoreMinimum = 0;  // If you go above target, this increases

        /**
         * Creates a filter that will monitor player activity, score it,
         * and notify its listeners of the player's score
         *
         * @param player the player to monitor
         */
        public BandIncreasePlayerMonitor(Player player)
        {
            super(player);
            targetAmplitude = config.targetAmplitude;
            multiplier = config.scoreMultiplier;
        }

        /**
         * Attach filters to our Player - We will listen to the filters to monitor
         * and score the Player's brainwave amplitude in our target band
         */
        protected void attachFilters()
        {
            bandFilter = new BandFilter(config.filterSpec, config.filterCoefficients);
            player.addSampleListener(bandFilter);
            filterMonitor = new BandMonitor(config.sampleSize, config.tolerance, config.minNotificationInterval);
            bandFilter.addBandListener(filterMonitor);
            filterMonitor.addAmplitudeListener(this);
            amplitudeLogMonitor = new BandMonitor(Configuration.getSampleRate(), 600, Configuration.getSampleRate());
            bandFilter.addBandListener(amplitudeLogMonitor);
            AmplitudeLogger amplitudeLogger = new AmplitudeLogger();
            amplitudeLogMonitor.addAmplitudeListener(amplitudeLogger);
        }

        /**
         * Detach the listeners we set up
         */
        protected void cleanup()
        {
            super.cleanup();
            player.removeSampleListener(bandFilter);
            bandFilter.removeAllBandListeners();
            filterMonitor.removeAllAmplitudeListeners();
            amplitudeLogMonitor.removeAllAmplitudeListeners();
        }

        /**
         * Receive the new amplitude average, and use it to calculate the new score
         *
         * @param amplitude the latest amplitude
         */
        public void receiveAmplitude(double amplitude)
        {
            if (isActive)
            {
                // set lastAmplitude to the first amplitude value
                if (lastAmplitude == 0)
                {
                    lastAmplitude = amplitude;
                }
                else
                {
                    calculateScore(amplitude);
                    lastAmplitude = amplitude;
                    notifyScoreListeners(score);
                    if (score >= scoreRange.getMaxValue())
                    {
                        playerWon(player);
                    }
                    if (amplitude >= targetAmplitude)
                    {
                        notifyRewardListeners(true);
                    }
                }
                notifyAmplitudeListeners(amplitude);
            }
        }

        private void calculateScore(double amplitude)
        {
            // If we've held steady above the target, increase score slightly
            if (MathUtil.nearlyEqual(amplitude, lastAmplitude, 1) && amplitude > targetAmplitude)
            {
                score = (int) (score + multiplier);
            }
            else
            {
                double delta = amplitude - lastAmplitude;
                score = score + (int) (delta * multiplier);
            }

            // If we are above target, increase the minimum
            if (amplitude > targetAmplitude)
            {
                scoreMinimum += multiplier;
            }

            // Don't let the score drop below the current scoreMinimum
            if (score <= scoreMinimum)
            {
                score = scoreMinimum;
            }
            // And don't let it go over the max
            else if (score >= scoreRange.getMaxValue())
            {
                score = scoreRange.getMaxValue();
            }
        }
    }
}
