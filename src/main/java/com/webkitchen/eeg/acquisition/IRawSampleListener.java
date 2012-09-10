package com.webkitchen.eeg.acquisition;

import java.util.EventListener;


/**
 * Listener interface for receiving raw EEG samples.  Classes interested in processing
 * raw EEG data must implement this interface and add themselvesas listeners
 * to the <code>IRawSampleGenerator</code> using its <code>addSampleListener</code> method.
 *
 * @author Amy Palke
 * @see IRawSampleGenerator
 * @see RawSample
 */
public interface IRawSampleListener extends EventListener
{
    /**
     * Receive the latest raw EEG sample for a specific channel or set of
     * channels
     *
     * @param rawSample the latest raw EEG sample
     */
    public void receiveSample(RawSample rawSample);
}
