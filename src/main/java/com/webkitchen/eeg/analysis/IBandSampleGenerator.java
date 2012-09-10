package com.webkitchen.eeg.analysis;

/**
 * Filters for a specific frequency band, and notifies listeners of the latest values
 *
 * @author Amy Palke
 * @see IBandSampleListener
 */
public interface IBandSampleGenerator
{
    /**
     * Attach listener to receive notification/copies of all new band values
     *
     * @param listener the observer who wants to receive band values
     */
    public void addBandListener(IBandSampleListener listener);

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeBandListener(IBandSampleListener listener);

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllBandListeners();
}
