package com.webkitchen.brainathlon.gameControl;

import com.webkitchen.brainathlon.data.Configuration;
import com.webkitchen.brainathlon.data.PlayerMonitorList;
import com.webkitchen.eeg.analysis.BandFilter;
import com.webkitchen.eeg.analysis.BandMonitor;
import com.webkitchen.eeg.analysis.filterdesign.FilterDesigner;
import com.webkitchen.brainathlon.gameComponents.ICountdownListener;
import com.webkitchen.brainathlon.gameComponents.ISustainedBandPlayerMonitor;
import com.webkitchen.brainathlon.gameComponents.Player;
import com.webkitchen.brainathlon.ui.AbstractCourseUI;
import com.webkitchen.brainathlon.ui.SustainedIncreaseUI;
import com.webkitchen.brainathlon.util.Range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Mini-game that encourages sustained brainwave activity in a specific frequency band,
 * as specified in our SustainedIncreaseConfiguration.xml file.  Handles all logic for
 * this course, times and scores the game play, and provides feedback for the players.
 *
 * @author Amy Palke
 */
public class SustainedIncreaseCourse extends AbstractBandCourse
{
    private SustainedIncreaseConfiguration config;

    /**
     * Creates a new course
     *
     * @param players the list of players
     */
    public SustainedIncreaseCourse(List<Player> players)
    {
        super(players);
        loadConfiguration();
        courseTitle = config.filterSpec.getDescription() + " Sustained Increase Course";
        announceCourse = true;
        scoreRange = new Range(0, config.timeGoal);
        timedCourse = true;
        timeLimit = config.timeLimitMinutes * 60;  // in seconds
        playerMonitorList = new PlayerMonitorList<ISustainedBandPlayerMonitor>();
    }

    protected void attachMonitors()
    {
        for (Player player : players)
        {
            ISustainedBandPlayerMonitor playerMonitor = new SustainedIncreaseMonitor(player);
            playerMonitorList.addPlayerAndMonitor(player, playerMonitor);
        }
    }

    public PlayerMonitorList<ISustainedBandPlayerMonitor> getPlayerMonitorList()
    {
        // We know that superclass playerMonitorList holds ISustainedBandPlayerMonitor
        //  because we added them, so can ignore "unchecked assignment" warning
        return playerMonitorList;
    }

    protected AbstractCourseUI getCourseUI()
    {
        return new SustainedIncreaseUI(this);
    }

    protected void tick()
    {
        for (Player player : players)
        {
            SustainedIncreaseMonitor playerMonitor = (SustainedIncreaseMonitor) playerMonitorList.getPlayerMonitor(player.getPlayerNumber());
            if (playerMonitor != null)
            {
                playerMonitor.tick();
            }
        }
    }

    private void loadConfiguration()
    {
        config = Configuration.getSustainedIncreaseConfiguration();
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
    private class SustainedIncreaseMonitor extends AbstractBandPlayerMonitor
            implements ISustainedBandPlayerMonitor
    {
        private BandMonitor filterMonitor;
        private BandMonitor amplitudeLogMonitor;
        private boolean inCountdown;
        private int targetAmplitude;
        private int timeGoal;    // Seconds sustained above targetAmplitude
        private int timeAboveTarget = 0; // increases when player sustains level above target
        private List<ICountdownListener> countdownListeners = Collections.synchronizedList(new ArrayList<ICountdownListener>());;

        /**
         * Creates a filter that will monitor player activity, score it,
         * and notify its listeners of the player's score
         *
         * @param player the player to monitor
         */
        public SustainedIncreaseMonitor(Player player)
        {
            super(player);
            targetAmplitude = config.targetAmplitude;
            timeGoal = config.timeGoal;
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
                if (amplitude >= targetAmplitude)
                {
                    if (!inCountdown)
                    {
                        startCountDown();
                    }
                }
                else
                {
                    if (inCountdown)
                    {
                        stopCountDown();
                    }
                }
                notifyAmplitudeListeners(amplitude);
            }
        }

        private void startCountDown()
        {
            inCountdown = true;
            timeAboveTarget = 0;
            notifyRewardListeners(true);
        }

        private void stopCountDown()
        {
            inCountdown = false;
            timeAboveTarget = 0;
            notifyRewardListeners(false);
        }

        public void tick()
        {
            if (inCountdown && isActive)
            {
                // Send countdown of goal to 0
                notifyCountdownListeners(timeGoal - timeAboveTarget);
                // Score holds highest score/time above target
                score = Math.max(score, timeAboveTarget);
                notifyScoreListeners(score);
                if (timeAboveTarget >= timeGoal)
                {
                    playerWon(player);
                }
                timeAboveTarget++;
            }
        }

        /**
         * Attach listener to receive notification of the player's countdown time
         * to goal at sustained target level
         *
         * @param listener the observer who wants to receive the player's countdown time
         */
        public void addCountdownListener(ICountdownListener listener)
        {
            // Add the listener if he isn't already in our list
            if (!countdownListeners.contains(listener))
            {
                countdownListeners.add(listener);
            }
        }

        /**
         * Remove listener from our notification list
         *
         * @param listener the observer to remove
         */
        public void removeCountdownListener(ICountdownListener listener)
        {
            countdownListeners.remove(listener);
        }

        /**
         * Remove all listeners from our notification list
         */
        public void removeAllCountdownListeners()
        {
            countdownListeners.clear();
        }

        /**
         * Notify listeners of the last countdown we received
         */
        protected void notifyCountdownListeners(int countdown)
        {
            // Note: This method is called by the eeg reader thread so we'll make an
            //  array copy to iterate to protect against concurrent modification errors
            //  if the listener list is changed by the main thread
            ICountdownListener[] listenerCopy = countdownListeners.toArray(new ICountdownListener[0]);
            for (ICountdownListener listener : listenerCopy)
            {
                listener.receiveCountdown(countdown);
            }
        }
    }
}
