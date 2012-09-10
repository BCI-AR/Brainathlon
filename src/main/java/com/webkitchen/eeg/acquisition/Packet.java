package com.webkitchen.eeg.acquisition;


/**
 * Represents one packet of EDF data, containing samples for all active channels.
 * Packets are immutable; their data cannot be changed after they have been created.
 *
 * @author Amy Palke
 * @see NeuroServerReader
 * @see IPacketListener
 */
final class Packet
{
    private final int packetNumber;
    private final int channelCount;
    private final int[] samples;

    /**
     * Creates a new <code>Packet</code> containing packet sequence number, channel count, and
     * array of channel samples
     *
     * @param packetNumber the packet sequence number
     * @param channelCount the number of active channels
     * @param samples      an array of samples for each active channel
     */
    Packet(int packetNumber, int channelCount, int[] samples)
    {
        this.packetNumber = packetNumber;
        this.channelCount = channelCount;
        this.samples = samples;
    }

    /**
     * Returns the packet sequence number
     *
     * @return the packet sequence number
     */
    int getPacketNumber()
    {
        return packetNumber;
    }

    /**
     * Returns the number of active channels
     *
     * @return the number of active channels
     */
    int getChannelCount()
    {
        return channelCount;
    }

    /**
     * Returns an array of samples for each active channel.
     * The samples are in channel number order - samples[0] represents channel #1,
     * samples[1] represents channel #2, etc.
     *
     * @return an array of samples for each active channel
     */
    int[] getSamples()
    {
        return samples;
    }
}
