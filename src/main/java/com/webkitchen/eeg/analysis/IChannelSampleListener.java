package com.webkitchen.eeg.analysis;

import java.util.EventListener;


/**
 * Listener interface for receiving raw EEG samples for a particular channel.
 * Classes interested in processing a channel's raw EEG data must implement this interface
 * and add themselves as listeners to the <code>IChannelSampleGenerator</code> using its
 * <code>addSampleListener</code> method.
 *
 * @author Amy Palke
 * @see IChannelSampleGenerator
 */
public interface IChannelSampleListener extends EventListener
{
    /**
     * Receive the latest raw EEG sample for a specific channel
     *
     * @param rawSample the latest raw EEG sample
     */
    public void receiveSample(double rawSample);
}
