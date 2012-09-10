package com.webkitchen.brainathlon.gameComponents;

import com.webkitchen.brainathlon.data.PlayerData;
import com.webkitchen.eeg.acquisition.IRawSampleListener;
import com.webkitchen.eeg.acquisition.RawSample;
import com.webkitchen.eeg.analysis.IChannelSampleGenerator;
import com.webkitchen.eeg.analysis.IChannelSampleListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A person playing the game
 *
 * @author Amy Palke
 */
public class Player implements IRawSampleListener, IChannelSampleGenerator
{
    private int playerNumber;
    private String firstName;
    private int[] channels;
    private Integer instrument;
    // Our listener list must be synchronized since listeners will be added and deleted
    // by the main thread, but notification happens in the reader thread.
    private List<IChannelSampleListener> listeners = Collections.synchronizedList(new ArrayList<IChannelSampleListener>());

    /**
     * Creates a new player with the given number, name, channels and midi instrument selection
     *
     * @param playerNumber 1 or 2
     * @param playerData   contains the player's name, channels and midi instrument selection
     */
    public Player(int playerNumber, PlayerData playerData)
    {
        this(playerNumber, playerData.getFirstName(), playerData.getChannels(), playerData.getInstrument());
    }

    /**
     * Creates a new player with the given number, name, channels and midi instrument selection
     *
     * @param playerNumber 1 or 2
     * @param firstName    the player's first name
     * @param channels     the channels that the player is hooked up to
     * @param instrument   the midi instrument number that the player selected
     */
    public Player(int playerNumber, String firstName, int[] channels, Integer instrument)
    {
        this.playerNumber = playerNumber;
        this.firstName = firstName;
        this.channels = channels;
        this.instrument = instrument;
    }

    public int getPlayerNumber()
    {
        return playerNumber;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public int[] getChannels()
    {
        return channels;
    }

    public Integer getInstrument()
    {
        return instrument;
    }

    public String toString()
    {
        String channelString = "channels=";
        for (int i = 0, length = channels.length; i < length; i++)
        {
            channelString = channelString + channels[i] + " ";
        }
        return "Player #" + playerNumber + ": name=" + firstName + ", " + channelString;
    }

    /**
     * Receive the raw sample, and pass it's value along to our listeners
     *
     * @param rawSample our latest EEG raw sample
     */
    public void receiveSample(RawSample rawSample)
    {
        notifyListeners(rawSample.getSamples()[0]);
    }

    /**
     * Send the latest raw sample to our listeners
     *
     * @param rawSample our latest EEG raw sample
     */
    private void notifyListeners(double rawSample)
    {
        // Note: This method is called by the eeg reader thread so we'll make an
        //  array copy to iterate to protect against concurrent modification errors
        //  if the listener list is changed by the main thread
        IChannelSampleListener[] listenerCopy = listeners.toArray(new IChannelSampleListener[0]);
        for (IChannelSampleListener listener : listenerCopy)
        {
            listener.receiveSample(rawSample);
        }
    }

    /**
     * Attach listener to receive notification/copies of all new Samples
     *
     * @param listener the observer who wants to receive Samples
     */
    public void addSampleListener(IChannelSampleListener listener)
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
    public void removeSampleListener(IChannelSampleListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllSampleListeners()
    {
        listeners.clear();
    }
}
