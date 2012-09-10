package com.webkitchen.brainathlon.gameControl;

import com.webkitchen.brainathlon.data.Configuration;
import com.webkitchen.brainathlon.data.FinalScore;
import com.webkitchen.brainathlon.data.PlayerMonitorList;
import com.webkitchen.eeg.analysis.filterdesign.FilterDesigner;
import com.webkitchen.brainathlon.gameComponents.IPlayerMonitor;
import com.webkitchen.brainathlon.gameComponents.IRewardListener;
import com.webkitchen.brainathlon.gameComponents.IScoreListener;
import com.webkitchen.brainathlon.gameComponents.Player;
import com.webkitchen.brainathlon.ui.AbstractCourseUI;
import com.webkitchen.brainathlon.ui.CourseOverUI;
import com.webkitchen.brainathlon.ui.PleaseWaitForFilterSetupUI;
import com.webkitchen.brainathlon.ui.StartCourseUI;
import com.webkitchen.brainathlon.ui.StartCourseUI;
import com.webkitchen.brainathlon.util.Range;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Contains the generic logic for all Brainathlon courses
 *
 * @author Amy Palke
 */
public abstract class AbstractCourse implements WindowListener
{
    protected List<Player> players;
    protected Player winner;

    protected boolean running = false;   // returns control to the game controller
    protected boolean courseOver = false;  // used internally after game has been won

    private long gameStartTime;
    private static long TICK_INTERVAL = 1000L; // update timer every second
    protected boolean timedCourse;
    protected boolean announceCourse;
    protected int timeInCourse;  // in seconds
    protected int timeLimit;     // in seconds

    protected PlayerMonitorList playerMonitorList;
    protected CourseOverUI courseOverUI;
    protected AbstractCourseUI courseUI;

    protected String courseTitle;
    protected Range scoreRange;
    private FinalScore finalScore;


    public AbstractCourse(List<Player> players)
    {
        this.players = players;
    }

    public abstract PlayerMonitorList getPlayerMonitorList();

    protected abstract void attachMonitors();

    protected abstract AbstractCourseUI getCourseUI();

    protected abstract boolean needCoeffients();

    protected abstract void createCoefficients(FilterDesigner designer);

    /**
     * Designs IIR filter coefficients if they weren't specified in the configuration file
     */
    protected void designFilters()
    {
        if (needCoeffients())
        {
            final PleaseWaitForFilterSetupUI pleaseWaitUI = new PleaseWaitForFilterSetupUI();
            displayWaitWindow(pleaseWaitUI);

            // Create the filters
            FilterDesigner designer = new FilterDesigner();
            createCoefficients(designer);

            dismissWaitWindow(pleaseWaitUI);
        }
    }

    /**
     * Start the game, and do not return until the game is finished
     */
    public void start()
    {
        designFilters();
        // Display the dialog announcing our course
        if (announceCourse)
        {
            displayStartDialog();
        }
        attachMonitors();

        courseUI = getCourseUI();
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    courseUI.createAndShowGUI();
                }
            });

            attachAsListener(courseUI);


            running = true;
            playCourse();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        finally
        {
            // Make sure the filters are detached
            cleanupMonitors();
        }
    }

    private void displayStartDialog()
    {
        final StartCourseUI startDialog = new StartCourseUI(this.courseTitle);

        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    startDialog.createAndShowGUI();
                }
            });
        }
        catch (InterruptedException ignore)
        {
        }
        catch (InvocationTargetException ignore)
        {
        }
    }

    public Range getScoreRange()
    {
        return scoreRange;
    }

    public FinalScore getFinalScore()
    {
        return finalScore;
    }

    public Player getWinner()
    {
        return winner;
    }

    public int getFinishTime()
    {
        return timeInCourse;
    }

    /**
     * Activate the monitors, and update the timer
     */
    private void playCourse()
    {
        activateMonitors();
        startTimer();
        while (running)
        {
            sleepBriefly();
            updateTime();
            courseUI.setTimeDisplay(timeInCourse);
            if (courseOver)
            {
                updateFinalScore();
                doCourseOver();
            }
        }
    }

    private void updateFinalScore()
    {
        finalScore = new FinalScore(courseTitle, timeInCourse, winner);
        for (int i = 1, numPlayers = playerMonitorList.size(); i <= numPlayers; i++)
        {
            Player player = playerMonitorList.getPlayer(i);
            int score = playerMonitorList.getPlayerMonitor(i).getFinalScore();
            finalScore.addPlayerInfo(player, score);
        }
    }

    /**
     * Clean up, and display the "course over" UI
     */
    private void doCourseOver()
    {
        cleanupMonitors();
        courseOverUI = new CourseOverUI(finalScore);
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    courseOverUI.createAndShowGUI();
                }
            });
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }

        running = false;
        courseOverUI.dispose();
        courseUI.dispose();
    }

    private void endCourseUpdates()
    {
        courseOver = true;
        deactivateMonitors();
    }

    /**
     * Determine who had the highest score
     */
    private void determineWinner()
    {
        assert(players.size() == 2);

        Player player1 = playerMonitorList.getPlayer(1);
        int player1Score = playerMonitorList.getPlayerMonitor(1).getFinalScore();
        Player player2 = playerMonitorList.getPlayer(2);
        int player2Score = playerMonitorList.getPlayerMonitor(2).getFinalScore();

        if (player1Score == player2Score)
        {
            winner = null; // tie game
        }
        else
        {
            winner = (player1Score > player2Score) ? player1 : player2;
        }
    }

    private void startTimer()
    {
        gameStartTime = System.currentTimeMillis();
    }

    private void sleepBriefly()
    {
        try
        {
            Thread.sleep(TICK_INTERVAL);
        }
        catch (InterruptedException e)
        {
        }
    }

    private void updateTime()
    {
        int timeNow = (int) ((System.currentTimeMillis() - gameStartTime) / 1000L);  // ms --> secs
        // Don't allow our official time to go over the limit, in case we oversleep
        timeInCourse = (timeNow > timeLimit) ? timeLimit : timeNow;

        // Allow subclasses to access time ticks
        tick();

        // Check if our time is up
        if (timedCourse && timeInCourse >= timeLimit)
        {
            doTimeIsUp();
        }
    }

    /**
     * Called after timeInCourse is updated, subclasses should override to add
     * timing-based game functionality
     */
    protected void tick()
    {
        // subclasses can override to do something on time ticks
    }

    /**
     * End the UI updates, and determine the winner (if it's a 2 player game)
     */
    private void doTimeIsUp()
    {
        endCourseUpdates();
        if (players.size() == 2)
        {
            determineWinner();
        }
    }

    /**
     * Subclasses should all use this to listen for window events
     * that we need to handle
     *
     * @param window the Window to listen to
     */
    protected void attachAsListener(Window window)
    {
        window.addWindowListener(this);
        window.addKeyListener(new QuitKeyListener());
    }

    /**
     * Set the winner
     *
     * @param player the player who won
     */
    protected void playerWon(Player player)
    {
        endCourseUpdates();
        this.winner = player;
    }

    protected static void dumpCoefficients(String name, double[] coef)
    {
        System.out.println();
        System.out.println();
        System.out.println("private double[] " + name + "Coefficients = new double[]{");
        for (int i = 0, length = coef.length; i < length; i++)
        {
            System.out.println(coef[i] + ",");
        }
        System.out.println();
        System.out.println();
    }

    private void displayWaitWindow(final PleaseWaitForFilterSetupUI pleaseWaitUI)
    {
        try
        {
            // Display the wait window
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    pleaseWaitUI.createAndShowGUI();
                }
            });
        }
        catch (InterruptedException ignore)
        {
            // Just print a stack trace and continue on
            ignore.printStackTrace();
        }
        catch (InvocationTargetException ignore)
        {
            // Just print a stack trace and continue on
            ignore.printStackTrace();
        }
    }

    private void dismissWaitWindow(final PleaseWaitForFilterSetupUI pleaseWaitUI)
    {
        try
        {
            // Dismiss the wait window
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    pleaseWaitUI.disposeOfGUI();
                }
            });
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }

    private void activateMonitors()
    {
        for (Player player : players)
        {
            AbstractPlayerMonitor playerMonitor = (AbstractPlayerMonitor) playerMonitorList.getPlayerMonitor(player.getPlayerNumber());
            if (playerMonitor != null)
            {
                playerMonitor.setActive(true);
            }
        }
    }

    private void deactivateMonitors()
    {
        for (Player player : players)
        {
            AbstractPlayerMonitor playerMonitor = (AbstractPlayerMonitor) playerMonitorList.getPlayerMonitor(player.getPlayerNumber());
            if (playerMonitor != null)
            {
                playerMonitor.setActive(false);
            }
        }
    }

    private void cleanupMonitors()
    {
        // Have the monitors cleanup - detach any filters created
        for (Player player : players)
        {
            AbstractPlayerMonitor playerMonitor = (AbstractPlayerMonitor) playerMonitorList.getPlayerMonitor(player.getPlayerNumber());
            if (playerMonitor != null)
            {
                playerMonitor.cleanup();
            }
        }
    }


    // ----------------- window listener methods -------------

    public void windowClosing(WindowEvent e)
    {
        stop();
    }

    public void windowOpened(WindowEvent e)
    {
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowDeactivated(WindowEvent e)
    {
    }


    // ----------------------------------------------------


    // ------------- game life cycle methods ------------
    // called by the window listener methods


    public void stop()
            // called when the JFrame is closing
    {
        running = false;
        cleanupMonitors();
    }

    public String getTitle()
    {
        return courseTitle;
    }

    private class QuitKeyListener extends KeyAdapter
    {
        // listen for esc, q, end, ctrl-c on the canvas to
        // allow a convenient exit from the full screen configuration
        public void keyPressed(KeyEvent e)
        {
            int keyCode = e.getKeyCode();
            if ((keyCode == KeyEvent.VK_ESCAPE) ||
                (keyCode == KeyEvent.VK_END) ||
                ((keyCode == KeyEvent.VK_C) && e.isControlDown()))
            {
                stop();
            }
        }
    }

    // ----------------------------------------------

    /**
     * AbstractPlayerMonitor monitors and scores a given Player's brainwave activity
     */
    protected abstract class AbstractPlayerMonitor implements IPlayerMonitor
    {
        protected Player player;
        // Our listener lists must be synchronized since listeners will be added and deleted
        // by the main thread, but notification happens in the reader thread.
        protected List<IScoreListener> scoreListeners = Collections.synchronizedList(new ArrayList<IScoreListener>());
        protected List<IRewardListener> rewardListeners = Collections.synchronizedList(new ArrayList<IRewardListener>());
        protected boolean isActive;
        protected int score = 0;
        protected BufferedWriter log;


        /**
         * Creates a AbstractPlayerMonitor that will monitor player activity, score it,
         * and notify its listeners of the player's score
         *
         * @param player the player to monitor
         */
        AbstractPlayerMonitor(Player player)
        {
            this.player = player;
            try
            {
                Date today = new Date();
                String todayNumeric = DateFormat.getDateInstance(DateFormat.SHORT).format(today);
                todayNumeric = todayNumeric.replace('/', '.');
                String todayDescription = DateFormat.getDateInstance(DateFormat.FULL).format(today);
                String fileName = player.getFirstName() + "." + todayNumeric + "." + today.getTime() + ".log";
                log = new BufferedWriter(new FileWriter(Configuration.getUserLogFileDirectory() + fileName));
                log.write(player.getFirstName() + " - " + todayDescription + " - " + courseTitle);
                log.newLine();
            }
            catch (IOException ignore)
            {
                System.out.println("Unable to create log file");
                ignore.printStackTrace();
            }
            attachFilters();
        }

        /**
         * Create the listener relationships required for the data pipeline
         */
        protected abstract void attachFilters();

        /**
         * Close log file.  Subclasses should override to add any clean-up code that
         * they need, but they must call super.cleanup().
         */
        protected void cleanup()
        {
            if (log != null)
            {
                synchronized (log)
                {
                    try
                    {
                        log.flush();
                    }
                    catch (IOException ignore)
                    {
                        System.out.println("Unable to flush log file");
                        ignore.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            log.close();
                        }
                        catch (IOException ignore)
                        {
                            System.out.println("Unable to close log file");
                            ignore.printStackTrace();
                        }
                    }
                    log = null;
                }
            }
        }


        /**
         * Returns true if this monitor is currently active
         * (sending event notifications to its listeners)
         *
         * @return true if this monitor is currently active
         */
        public boolean isActive()
        {
            return isActive;
        }

        /**
         * Sets this monitor to be active or inactive (when active, the monitor sends
         * event notifications to its listeners, when inactive it does not)
         *
         * @param active
         */
        public void setActive(boolean active)
        {
            isActive = active;
        }

        /**
         * Returns our player's score
         *
         * @return the current score for our player
         */
        public int getFinalScore()
        {
            return score;
        }

        /**
         * Attach listener to receive notification of the player's current score
         *
         * @param listener the observer who wants to receive the player's score
         */
        public void addScoreListener(IScoreListener listener)
        {
            // Add the listener if he isn't already in our list
            if (!scoreListeners.contains(listener))
            {
                scoreListeners.add(listener);
            }
        }

        /**
         * Remove listener from our notification list
         *
         * @param listener the observer to remove
         */
        public void removeScoreListener(IScoreListener listener)
        {
            scoreListeners.remove(listener);
        }

        /**
         * Remove all listeners from our notification list
         */
        public void removeAllScoreListeners()
        {
            scoreListeners.clear();
        }

        /**
         * Notify listeners of the current score
         *
         * @param score the current score
         */
        protected void notifyScoreListeners(Integer score)
        {
            // Note: This method is called by the eeg reader thread so we'll make an
            //  array copy to iterate to protect against concurrent modification errors
            //  if the listener list is changed by the main thread
            IScoreListener[] listenerCopy = scoreListeners.toArray(new IScoreListener[0]);
            for (IScoreListener listener : listenerCopy)
            {
                listener.receiveScore(score);
            }
        }

        /**
         * Attach listener to receive notification when player deserves reward notification
         *
         * @param listener the observer who wants to receive the player's reward notification
         */
        public void addRewardListener(IRewardListener listener)
        {
            // Add the listener if he isn't already in our list
            if (!rewardListeners.contains(listener))
            {
                rewardListeners.add(listener);
            }
        }

        /**
         * Remove listener from our notification list
         *
         * @param listener the observer to remove
         */
        public void removeRewardListener(IRewardListener listener)
        {
            rewardListeners.remove(listener);
        }

        /**
         * Remove all listeners from our notification list
         */
        public void removeAllRewardListeners()
        {
            rewardListeners.clear();
        }

        /**
         * Notify listeners of the last amplitude we received
         */
        protected void notifyRewardListeners(boolean inTarget)
        {
            // Note: This method is called by the eeg reader thread so we'll make an
            //  array copy to iterate to protect against concurrent modification errors
            //  if the listener list is changed by the main thread
            IRewardListener[] listenerCopy = rewardListeners.toArray(new IRewardListener[0]);
            for (IRewardListener listener : listenerCopy)
            {
                listener.receiveReward(inTarget);
            }
        }
    }

}
