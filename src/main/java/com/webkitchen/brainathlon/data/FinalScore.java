package com.webkitchen.brainathlon.data;

import com.webkitchen.brainathlon.gameComponents.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the final score for a course in the game
 *
 * @author Amy Palke
 */
public class FinalScore
{
    private final Player winner;
    private final int time;
    private final String courseTitle;
    private List<PlayerInfo> players = new ArrayList<PlayerInfo>();

    /**
     * Creates a final score
     *
     * @param courseTitle the course title
     * @param time        the time (in seconds) elapsed during the course
     * @param winner      the player who won the course
     */
    public FinalScore(String courseTitle, int time, Player winner)
    {
        this.courseTitle = courseTitle;
        this.time = time;
        this.winner = winner;
    }

    public void addPlayerInfo(Player player, int score)
    {
        players.add(new PlayerInfo(player, score));
    }

    public List<PlayerInfo> getPlayerInfo()
    {
        return players;
    }

    public String getCourseTitle()
    {
        return courseTitle;
    }

    public int getTime()
    {
        return time;
    }

    public Player getWinner()
    {
        return winner;
    }

    public int getPlayerCount()
    {
        return players.size();
    }

    public String getWinnerName()
    {
        String text;
        if (winner != null)
        {
            text = winner.getFirstName();
        }
        else
        {
            if (players.size() == 1)
            {
                text = "Timed out";
            }
            else
            {
                text = "Tie game";
            }
        }
        return text;
    }

    public String getWinnerText()
    {
        String text;
        if (winner != null)
        {
            text = "The winner is " + winner.getFirstName() + "!";
        }
        else
        {
            if (players.size() == 1)
            {
                text = "Time's up!";
            }
            else
            {
                text = "Tie game!";
            }
        }
        return text;
    }


    public class PlayerInfo
    {
        private final Player player;
        private final int score;

        public PlayerInfo(Player player, int score)
        {
            this.player = player;
            this.score = score;
        }

        public Player getPlayer()
        {
            return player;
        }

        public int getScore()
        {
            return score;
        }
    }
}
