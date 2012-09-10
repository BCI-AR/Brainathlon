package com.webkitchen.brainathlon.gameComponents;

import java.util.EventListener;

/**
 * Listener interface for receiving countdown counts
 *
 * @author Amy Palke
 */
public interface ICountdownListener extends EventListener
{
    public void receiveCountdown(Integer countdown);
}
