package com.webkitchen.eeg.analysis;

import java.util.EventListener;


/**
 * Listens for new wave amplitude values in a specific frequency range
 *
 * @author Amy Palke
 * @see IAmplitudeGenerator
 */
public interface IAmplitudeListener extends EventListener
{
    /**
     * Receive and process the latest amplitude reading
     *
     * @param amplitude the latest amplitude reading
     */
    public void receiveAmplitude(double amplitude);
}
