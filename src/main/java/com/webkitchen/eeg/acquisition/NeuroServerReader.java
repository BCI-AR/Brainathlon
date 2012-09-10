package com.webkitchen.eeg.acquisition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Observable reader that communicates with NeuroServer to retrieve EDF packets.  Objects can
 * add themselves as listeners/observers to receive copies of all new packets retrieved.
 *
 * @author Amy Palke
 * @see NeuroServerConnection
 * @see DebuggingConnection
 * @see IPacketListener
 * @see Packet
 */
class NeuroServerReader extends Thread
{
    private INeuroServerConnection connection;
    // Our listener list must be synchronized since listeners will be added and deleted
    // by the main thread, but notification happens in the reader thread.
    private List<IPacketListener> listeners = Collections.synchronizedList(new ArrayList<IPacketListener>());
    private int sleepDuration = 10;
    private boolean reading;

    private int physicalMin = -512;   // physical max = 512
    private int digitalMin = 0;       // digital max = 1023
    private int signalDifference = physicalMin - digitalMin;

    /**
     * Beginning reading any incoming data packets
     *
     * @param debugMode true to read from a file for debugging, false to read from the EEG device
     * @throws IOException if we are unable to connect to the EEG device
     */
    public void startReading(boolean debugMode) throws IOException
    {
        // Read from static file when in debug mode, from EEG machine when not in debugMode
        if (debugMode)
        {
            connection = DebuggingConnection.getInstance();
        }
        else
        {
            connection = NeuroServerConnection.getInstance();
        }

        connection.connect();
        connection.startWatch();
        reading = true;
    }

    /**
     * Stop reading data packets
     */
    public void stopReading()
    {
        reading = false;
    }

    /**
     * Check for packets, and send them along to our listeners
     */
    public void run()
    {
        // Loop while in reading mode, checking for packets
        while (reading)
        {
            // Read all lines in the buffer
            while (connection.hasNextLine() && reading)
            {
                notifyPacketListeners();
            }

            try
            {
                if (reading)
                {
                    Thread.sleep(sleepDuration);
                }
            }
            catch (InterruptedException ignore)
            {
                // ignore interruptions
            }
        }
        connection.close();
    }

    /**
     * Attach listener to receive notification/copies of all new Packets
     *
     * @param listener the observer who wants to receive Packets
     */
    public void addPacketListener(IPacketListener listener)
    {
        // Add the listener if he isn't already in our list
        if (!listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removePacketListener(IPacketListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Remove all listener from our notification list
     */
    public void removeAllPacketListeners()
    {
        listeners.clear();
    }

    /**
     * Package the latest data into a <code>Packet</code> object, and send it to all listeners
     */
    private void notifyPacketListeners()
    {
        if (reading)
        {
            try
            {
                Packet packet = getNext();
                // Note: This method is called by the eeg reader thread so we'll make an
                //  array copy to iterate to protect against concurrent modification errors
                //  if the listener list is changed by the main thread
                IPacketListener[] listenerCopy = listeners.toArray(new IPacketListener[0]);
                for (IPacketListener listener : listenerCopy)
                {
                    listener.receivePacket(packet);
                }
            }
            catch (IOException e)
            {
                System.out.println("Unable to read the next packet");
                e.printStackTrace();
            }
        }
    }

    /**
     * Read the data from our connection and package it into a <code>Packet</code> object
     *
     * @return the next raw data packet
     * @throws IOException if we are unable to connect to the EEG device
     */
    private Packet getNext() throws IOException
    {
        String data = connection.getNextLine();
        StringTokenizer tokenizer = new StringTokenizer(data);
        // skip over first 2 tokens "!" & "0"
        tokenizer.nextToken();
        tokenizer.nextToken();
        int packetNumber = Integer.parseInt(tokenizer.nextToken());
        int channelCount = Integer.parseInt(tokenizer.nextToken());
        int samples[] = new int[channelCount];
        // add each rawSample to our array of samples
        for (int i = 0; i < channelCount; i++)
        {
            // Account for difference in physical and digital signal
            int sample = Integer.parseInt(tokenizer.nextToken()) + signalDifference;
            samples[i] = sample;
        }
        return new Packet(packetNumber, channelCount, samples);
    }

}
