package com.webkitchen.eeg.analysis;


/**
 * Generates and notifies listeners of the latest raw EEG sample values for
 * a particular channel
 *
 * @author Amy Palke
 * @see IChannelSampleListener
 */
public interface IChannelSampleGenerator
{
    /**
     * Attach listener to receive notification/copies of all new raw EEG sample values
     * for the channel
     *
     * @param listener the observer who wants to receive sample values
     */
    public void addSampleListener(IChannelSampleListener listener);

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeSampleListener(IChannelSampleListener listener);

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllSampleListeners();
}
