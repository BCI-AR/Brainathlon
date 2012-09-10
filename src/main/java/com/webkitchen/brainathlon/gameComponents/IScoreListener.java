package com.webkitchen.brainathlon.gameComponents;

import java.util.EventListener;

/**
 * Listener interface for receiving scores
 *
 * @author Amy Palke
 */
public interface IScoreListener extends EventListener
{
    public void receiveScore(Integer score);
}
