package com.webkitchen.eeg.analysis;

/**
 * Filters for two specific frequency bands, and notifies listeners of the latest values
 *
 * @author Amy Palke
 * @see IDualBandSampleListener
 */
public interface IDualBandSampleGenerator
{
    /**
     * Attach listener to receive notification/copies of all new band values
     *
     * @param listener the observer who wants to receive band values
     */
    public void addDualBandListener(IDualBandSampleListener listener);

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeDualBandListener(IDualBandSampleListener listener);

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllDualBandListeners();
}
