package com.webkitchen.eeg.analysis;

import java.util.EventListener;


/**
 * Listener interface for receiving two filtered frequency band values
 *
 * @author Amy Palke
 * @see IDualBandSampleGenerator
 */
public interface IDualBandSampleListener extends EventListener
{
    /**
     * Receive and process the latest filtered frequency band values
     *
     * @param sampleOneValue one filtered frequency band value
     * @param sampleTwoValue the other filtered frequency band value
     */
    public void receiveBand(double sampleOneValue, double sampleTwoValue);
}
