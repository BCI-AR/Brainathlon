package com.webkitchen.brainathlon.gameControl;

import com.webkitchen.eeg.analysis.BandFilter;
import com.webkitchen.eeg.analysis.IAmplitudeListener;
import com.webkitchen.eeg.analysis.IBandSampleListener;
import com.webkitchen.brainathlon.gameComponents.IBandPlayerMonitor;
import com.webkitchen.brainathlon.gameComponents.Player;
import com.webkitchen.brainathlon.util.MathUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains the generic logic for courses that encourage activity in one frequency band
 *
 * @author Amy Palke
 */
public abstract class AbstractBandCourse extends AbstractCourse
{
    public AbstractBandCourse(List<Player> players)
    {
        super(players);
    }

    /**
     * AbstractPlayerMonitor monitors and scores a given Player's brainwave activity
     */
    protected abstract class AbstractBandPlayerMonitor extends AbstractPlayerMonitor
            implements IBandPlayerMonitor, IAmplitudeListener
    {
        protected BandFilter bandFilter;
        // Our listener lists must be synchronized since listeners will be added and deleted
        // by the main thread, but notification happens in the reader thread.
        protected List<IAmplitudeListener> amplitudeListeners = Collections.synchronizedList(new ArrayList<IAmplitudeListener>());

        /**
         * Creates a AbstractPlayerMonitor that will monitor player activity, score it,
         * and notify its listeners of the player's score
         *
         * @param player the player to monitor
         */
        public AbstractBandPlayerMonitor(Player player)
        {
            super(player);
        }

        /**
         * Attach listener to receive notification/copies of the player's current amplitude
         * in the target band
         *
         * @param listener the observer who wants to receive player amplitude
         */
        public void addAmplitudeListener(IAmplitudeListener listener)
        {
            // Add the listener if he isn't already in our list
            if (!amplitudeListeners.contains(listener))
            {
                amplitudeListeners.add(listener);
            }
        }

        /**
         * Remove listener from our notification list
         *
         * @param listener the observer to remove
         */
        public void removeAmplitudeListener(IAmplitudeListener listener)
        {
            amplitudeListeners.remove(listener);
        }

        /**
         * Remove all listeners from our notification list
         */
        public void removeAllAmplitudeListeners()
        {
            amplitudeListeners.clear();
        }

        /**
         * Notify listeners of the last amplitude we received
         */
        protected void notifyAmplitudeListeners(double amplitude)
        {
            // Note: This method is called by the eeg reader thread so we'll make an
            //  array copy to iterate to protect against concurrent modification errors
            //  if the listener list is changed by the main thread
            IAmplitudeListener[] listenerCopy = amplitudeListeners.toArray(new IAmplitudeListener[0]);
            for (IAmplitudeListener listener : listenerCopy)
            {
                listener.receiveAmplitude(amplitude);
            }
        }

        /**
         * Attach listener to our BandFilter,
         * to receive notification/copies of all new band values
         *
         * @param listener the observer who wants to receive band values
         */
        public void addBandListener(IBandSampleListener listener)
        {
            bandFilter.addBandListener(listener);
        }

        /**
         * Remove listener from our BandFilter's notification list
         *
         * @param listener the observer to remove
         */
        public void removeBandListener(IBandSampleListener listener)
        {
            bandFilter.removeBandListener(listener);
        }

        /**
         * Remove all listeners from our BandFilter's notification list
         */
        public void removeAllBandListeners()
        {
            bandFilter.removeAllBandListeners();
        }


        protected class AmplitudeLogger implements IAmplitudeListener
        {
            /**
             * Receive and process the latest amplitude reading
             *
             * @param amplitude the latest amplitude reading
             */
            public void receiveAmplitude(double amplitude)
            {
                if (log != null)
                {
                    synchronized (log)
                    {
                        try
                        {
                            log.write(String.valueOf(MathUtil.round(amplitude, 2)));
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
