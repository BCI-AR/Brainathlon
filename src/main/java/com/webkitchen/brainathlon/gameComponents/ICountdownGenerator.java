package com.webkitchen.brainathlon.gameComponents;

/**
 * Handles counting down time intervals and notifying listeners of the current count
 *
 * @author Amy Palke
 */
public interface ICountdownGenerator
{
    /**
     * Attach listener to receive notification of the player's countdown time
     * to goal at sustained target level
     *
     * @param listener the observer who wants to receive the player's countdown time
     */
    public void addCountdownListener(ICountdownListener listener);

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeCountdownListener(ICountdownListener listener);

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllCountdownListeners();
}
