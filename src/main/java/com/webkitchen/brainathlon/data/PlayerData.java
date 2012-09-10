package com.webkitchen.brainathlon.data;


/**
 * Contains the necessary data to add a new player to the game
 * - Created by NewPlayerUI and passed into Application to create
 * the actual Player object
 *
 * @author Amy Palke
 */
public class PlayerData
{
    private String firstName;
    private int[] channels;
    private Integer playerInstrument;

    /**
     * Create a new PlayerData object
     *
     * @param firstName        the players first name
     * @param channels         the players EEG channel(s)
     * @param playerInstrument the feedback instrument selection
     */
    public PlayerData(String firstName, int[] channels, Integer playerInstrument)
    {
        this.firstName = firstName;
        this.channels = channels;
        this.playerInstrument = playerInstrument;
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
        return playerInstrument;
    }

    public String toString()
    {
        String channelString = "channels=";
        for (int i = 0, length = channels.length; i < length; i++)
        {
            channelString = channelString + channels[i] + " ";
        }
        return "Data: name=" + firstName + ", " + channelString;
    }
}
