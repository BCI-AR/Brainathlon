package com.webkitchen.eeg.analysis;


/**
 * Generates wave amplitude ratios for two specific frequency bands, and notifies its
 * listeners of the latest ratio value
 *
 * @author Amy Palke
 */
public interface IRatioGenerator
{
    /**
     * Attach listener to receive notification of the player's ratio of 2 wave bands
     *
     * @param listener the observer who wants to receive the player's ratio of 2 wave bands
     */
    public void addRatioListener(IRatioListener listener);

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeRatioListener(IRatioListener listener);

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllRatioListeners();
}
