package com.webkitchen.eeg.analysis;

import java.util.EventListener;


/**
 * Listens for new wave amplitude values in a specific frequency range
 *
 * @author Amy Palke
 * @see IDualAmplitudeGenerator
 */
public interface IDualAmplitudeListener extends EventListener
{
    /**
     * Receive and process the latest amplitude readings
     *
     * @param amplitudeOne the latest amplitude reading for one band
     * @param amplitudeTwo the latest amplitude reading for the other band
     */
    public void receiveAmplitude(double amplitudeOne, double amplitudeTwo);
}
