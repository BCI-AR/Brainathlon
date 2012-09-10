package com.webkitchen.eeg.analysis;

import java.util.EventListener;


/**
 * Listener interface for receiving filtered frequency band values
 *
 * @author Amy Palke
 * @see IBandSampleGenerator
 */
public interface IBandSampleListener extends EventListener
{
    /**
     * Receive and process the latest filtered frequency band value
     *
     * @param sampleValue filtered frequency band value
     */
    public void receiveBand(double sampleValue);
}
