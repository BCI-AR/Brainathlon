package com.webkitchen.eeg.acquisition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Listens for new Packets, then splits the multiple channels apart into individual
 * samples.  Objects can add themselves as listeners/observers to receive copies of
 * all new samples on a specific channel or set of channels
 * <P>
 * Note that any listener adds or removes may not be reflected in the current
 * notification round, since reads (notifications) happen in the eeg reader thread
 * and writes (adds and removes) happen in the main thread.
 *
 * @author Amy Palke
 * @see EEGAcquisitionController
 * @see IRawSampleListener
 * @see RawSample
 */
class Demultiplexer implements IPacketListener, IRawSampleGenerator
{
    // Contains all listeners, mapped by:
    //   key = channels that the listener is interested in
    //   value = List of listeners interested in the channels/key
    // Using ConcurrentHashMap since listeners will be added and deleted
    // by the main thread, but notification happens in the reader thread.
    private ConcurrentMap<int[], List<IRawSampleListener>> listeners = new ConcurrentHashMap<int[], List<IRawSampleListener>>();

    /**
     * Receive packets and send the individual channel samples to our
     * registered listeners.  Each listener will receive a RawSample containing
     * the channels they have registered for.
     *
     * @param packet the latest EDF packet received by the reader
     */
    public void receivePacket(Packet packet)
    {
        // Note: This method is called by the eeg reader thread, so any adds or removes by the
        //   main thread won't necessarily be reflected during our iteration.  ConcurrentHashMap
        //   promises that we'll get keySet elements at most once, and won't get
        //   ConcurrentModificationExceptions, so no need to synchronize our Map.

        // Loop through each key in our keySet, creating Samples for our listeners
        for (int[] key : listeners.keySet())
        {
            // Make an array copy to iterate to protect against ConcurrentModificationException
            // if the listener list is changed by the main thread
            IRawSampleListener[] channelListeners = listeners.get(key).toArray(new IRawSampleListener[0]);

            // create RawSample for the channels specified by key
            int[] samples = new int[key.length];
            for (int i = 0; i < key.length; i++)
            {
                int channelIndex = key[i] - 1; // channels are 1-based, not 0-based
                samples[i] = packet.getSamples()[channelIndex];
            }
            RawSample rawSample = new RawSample(packet.getPacketNumber(), key, samples);

            // send the RawSample to the listeners
            notifyListeners(channelListeners, rawSample);
        }
    }

    /**
     * Send the sample to each listener in the array
     *
     * @param channelListeners an array of listeners that we should send the sample to
     * @param rawSample        the sample to send to all listeners
     */
    private void notifyListeners(IRawSampleListener[] channelListeners, RawSample rawSample)
    {
        for (IRawSampleListener listener : channelListeners)
        {
            listener.receiveSample(rawSample);
        }
    }

    /**
     * Attach listener to receive notification/copies of <code>RawSample</code> containing
     * specified channel values
     *
     * @param listener the observer who wants to receive <code>RawSample</code>s
     * @param channels the channels to listen to
     */
    public void addSampleListener(IRawSampleListener listener, int[] channels)
    {
        int[] key = channels;
        List<IRawSampleListener> values = listeners.get(key);
        // Create the value list, if necessary
        if (values == null)
        {
            // Use a synchronized list, since reads and writes happen in different threads
            values = Collections.synchronizedList(new ArrayList<IRawSampleListener>());
            listeners.put(key, values);
        }
        values.add(listener);
    }

    /**
     * Remove listener from our notification list.  If the listener is listening to
     * multiple channels, it will be removed from all of them.
     *
     * @param listener the observer to remove
     */
    public void removeSampleListener(IRawSampleListener listener)
    {
        // Loop through our keySet, checking each listener/value list, and
        // removing the listener from all
        for (int[] key : listeners.keySet())
        {
            List<IRawSampleListener> listenerList = listeners.get(key);
            if (listenerList.contains(listener))
            {
                listenerList.remove(listener);
                // If the listener was the only value, remove the key as well
                if (listenerList.isEmpty())
                {
                    listeners.remove(key);
                }
            }
        }
    }

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllSampleListeners()
    {
        listeners.clear();
    }
}
