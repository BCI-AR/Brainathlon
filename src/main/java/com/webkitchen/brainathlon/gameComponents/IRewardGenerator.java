package com.webkitchen.brainathlon.gameComponents;

/**
 * Interface for generating reward events
 *
 * @author Amy Palke
 */
public interface IRewardGenerator
{
    /**
     * Attach listener to receive notification when player deserves reward notification
     *
     * @param listener the observer who wants to receive the player's reward notification
     */
    public void addRewardListener(IRewardListener listener);

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeRewardListener(IRewardListener listener);

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllRewardListeners();
}
