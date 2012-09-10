package com.webkitchen.brainathlon.gameComponents;

/**
 * Interface for generating player scores
 *
 * @author Amy Palke
 */
public interface IScoreGenerator
{
    /**
     * Attach listener to receive notification of the player's current score
     *
     * @param listener the observer who wants to receive the player's score
     */
    public void addScoreListener(IScoreListener listener);

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeScoreListener(IScoreListener listener);

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllScoreListeners();
}
