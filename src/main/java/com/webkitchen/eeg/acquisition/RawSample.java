package com.webkitchen.eeg.acquisition;


/**
 * Contains the raw EEG data sample(s) for a specific channel or set of channels.
 * <code>RawSample</code>s are immutable; their data cannot be changed after they have been created.
 *
 * @author Amy Palke
 * @see IRawSampleGenerator
 * @see IRawSampleListener
 */
public final class RawSample
{
    private final int packetNumber;
    private final int[] channelNumbers;
    private final int[] samples;

    /**
     * Creates a new <code>RawSample</code> containing packet sequence number, an array of channel number,
     * and array of channel samples
     *
     * @param packetNumber   the packet sequence number
     * @param channelNumbers the channel numbers
     * @param samples        the raw samples for these channels
     */
    RawSample(int packetNumber, int[] channelNumbers, int[] samples)
    {
        this.channelNumbers = channelNumbers;
        this.packetNumber = packetNumber;
        this.samples = samples;
    }

    /**
     * Returns the packet sequence number for the sample
     *
     * @return the packet sequence number
     */
    public int getPacketNumber()
    {
        return packetNumber;
    }

    /**
     * Returns an array of the channel numbers associated with the samples.
     * They are associated by array position - samples[0] comes from channels[0].
     *
     * @return the channel numbers
     */
    public int[] getChannelNumbers()
    {
        return channelNumbers;
    }

    /**
     * Returns and array of the raw samples associated with the channels.
     * They are associated by array position - samples[0] comes from channels[0].
     *
     * @return the raw samples for the channels
     */
    public int[] getSamples()
    {
        return samples;
    }

}
