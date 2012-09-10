package com.webkitchen.brainathlon.data;

import com.webkitchen.brainathlon.gameComponents.IPlayerMonitor;
import com.webkitchen.brainathlon.gameComponents.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * A collection of players and linked monitors that the courses package up and send
 * to the UI
 *
 * @author Amy Palke
 */
public class PlayerMonitorList <T extends IPlayerMonitor>
{
    // Array of players & filters, indexed on playerNumber -1
    private List<Data> playerData = new ArrayList<Data>(2);
    private int size = 0;

    public void addPlayerAndMonitor(Player player, T monitor)
    {
        playerData.add(getPlayerIndex(player.getPlayerNumber()), new Data(player, monitor));
        size++;
    }

    public Player getPlayer(int playerNumber)
    {
        return playerData.get(getPlayerIndex(playerNumber)).player;
    }

    public String getPlayerName(int playerNumber)
    {
        return playerData.get(getPlayerIndex(playerNumber)).player.getFirstName();
    }

    public T getPlayerMonitor(int playerNumber)
    {
        return playerData.get(getPlayerIndex(playerNumber)).monitor;
    }

    public int size()
    {
        return size;
    }

    private int getPlayerIndex(int playerNumber)
    {
        // Players are 1-based, and our array is 0-based
        return --playerNumber;
    }

    private class Data
    {
        Player player;
        T monitor;

        public Data(Player player, T monitor)
        {
            this.player = player;
            this.monitor = monitor;
        }
    }
}