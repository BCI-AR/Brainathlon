package com.webkitchen.brainathlon.gameControl;

import com.webkitchen.brainathlon.data.PlayerMonitorList;
import com.webkitchen.eeg.analysis.filterdesign.FilterDesigner;
import com.webkitchen.brainathlon.gameComponents.ISpectrumListener;
import com.webkitchen.brainathlon.gameComponents.ISpectrumPlayerMonitor;
import com.webkitchen.brainathlon.gameComponents.Player;
import com.webkitchen.brainathlon.gameComponents.SpectrumFilter;
import com.webkitchen.brainathlon.ui.AbstractCourseUI;
import com.webkitchen.brainathlon.ui.SimpleDisplayUI;

import java.util.List;


/**
 * Simply displays brainwave activity in the four standard frequency bands of
 * Beta, Alpha, Theta and Delta
 *
 * @author Amy Palke
 */
public class SimpleDisplayCourse extends AbstractCourse
{

    public SimpleDisplayCourse(List<Player> players)
    {
        super(players);
        courseTitle = "Simple Display Course";
        announceCourse = false;
        timedCourse = false;  // no time limit
        playerMonitorList = new PlayerMonitorList<ISpectrumPlayerMonitor>();
    }

    protected AbstractCourseUI getCourseUI()
    {
        SimpleDisplayUI ui = new SimpleDisplayUI(this);
        return ui;
    }

    public PlayerMonitorList<ISpectrumPlayerMonitor> getPlayerMonitorList()
    {
        // We know that superclass playerMonitorList holds ISpectrumPlayerMonitors
        //  because we added them, so can ignore "unchecked assignment" warning
        return playerMonitorList;
    }

    protected void attachMonitors()
    {
        for (Player player : players)
        {
            ISpectrumPlayerMonitor monitor = new SimpleDisplayPlayerMonitor(player);
            playerMonitorList.addPlayerAndMonitor(player, monitor);
        }
    }

    protected boolean needCoeffients()
    {
        // Our filter has built-in coefficients
        return false;
    }

    protected void createCoefficients(FilterDesigner designer)
    {
        // No need to do anything, our filter has built-in coefficients
    }

    /**
     * Monitors a given Player's brain wave activity
     */
    private class SimpleDisplayPlayerMonitor extends AbstractPlayerMonitor
            implements ISpectrumPlayerMonitor
    {
        SpectrumFilter filter;

        /**
         * Creates a filter that will monitor player activity,
         * and notify its listeners of the player's spectrum values
         *
         * @param player the player to monitor
         */
        public SimpleDisplayPlayerMonitor(Player player)
        {
            super(player);
        }

        protected void attachFilters()
        {
            filter = new SpectrumFilter();
            player.addSampleListener(filter);
        }

        protected void cleanup()
        {
            super.cleanup();
            player.removeSampleListener(filter);
        }

        /**
         * Attach listener to receive notification/copies of all new spectrums
         *
         * @param listener the observer who wants to receive spectrums
         */
        public void addSpectrumListener(ISpectrumListener listener)
        {
            filter.addSpectrumListener(listener);
        }

        /**
         * Remove listener from our notification list
         *
         * @param listener the observer to remove
         */
        public void removeSpectrumListener(ISpectrumListener listener)
        {
            filter.removeSpectrumListener(listener);
        }

        /**
         * Remove all listeners from our notification list
         */
        public void removeAllSpectrumListeners()
        {
            filter.removeAllSpectrumListeners();
        }
    }
}
