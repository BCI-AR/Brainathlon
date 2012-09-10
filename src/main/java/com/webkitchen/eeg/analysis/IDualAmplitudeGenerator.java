package com.webkitchen.eeg.analysis;

/**
 * Generates wave amplitude values for specific frequency bands, and notifies its
 * listeners of the latest amplitude value
 *
 * @author Amy Palke
 * @see IDualAmplitudeListener
 */
public interface IDualAmplitudeGenerator
{
    /**
     * Attach listener to receive notification/copies of new amplitude levels
     * for two different frequency bands
     *
     * @param listener the observer who wants to receive amplitude levels
     */
    public void addAmplitudeListener(IDualAmplitudeListener listener);

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeAmplitudeListener(IDualAmplitudeListener listener);

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllAmplitudeListeners();
}
