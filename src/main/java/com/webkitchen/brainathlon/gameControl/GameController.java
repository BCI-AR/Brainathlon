package com.webkitchen.brainathlon.gameControl;

import com.webkitchen.brainathlon.data.Configuration;
import com.webkitchen.brainathlon.data.FinalScore;
import com.webkitchen.brainathlon.gameComponents.Player;
import com.webkitchen.brainathlon.ui.FinalScoreUI;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Controls the game, running each of our 3 courses in succession
 *
 * @author Amy Palke
 * @see BandIncreaseCourse
 * @see SustainedIncreaseCourse
 * @see DualBandRatioCourse
 */
public class GameController
{
    private List<Player> players;
    private GameState state = new GameInProgressState();
    private List<AbstractCourse> courses;
    private List<FinalScore> scores = new ArrayList<FinalScore>();
    private BufferedWriter log;
    private String fileName = "FinalScores.log";


    public GameController(List<Player> players)
    {
        this.players = players;
        initCourses();
    }

    private void initCourses()
    {
        courses = new ArrayList<AbstractCourse>();
        courses.add(new BandIncreaseCourse(players));
        courses.add(new SustainedIncreaseCourse(players));
        courses.add(new DualBandRatioCourse(players));
    }

    /**
     * Starts the game in motion - each of our 3 courses will be played in succession
     */
    public void PlayGame()
    {
        while (state != null)
        {
            state = state.process(this);
        }
    }


    /**
     * Handles state-specific behavior for GameController
     */
    abstract class GameState
    {
        abstract GameState process(GameController game);
    }

    /**
     * GameInProgressState handles logic for displaying the individual game
     * courses in order
     */
    class GameInProgressState extends GameState
    {
        /**
         * GameInProgressState displays the game courses
         *
         * @param game our associated GameController
         * @return the next logical state, either GameOverState or null
         */
        GameState process(GameController game)
        {
            for (AbstractCourse course : game.courses)
            {
                course.start();  // blocks until game is over
                FinalScore finalScore = course.getFinalScore();
                if (finalScore != null)
                {
                    scores.add(course.getFinalScore());
                }
            }

            return new GameOverState();
        }
    }


    /**
     * GameInProgressState displays the final score for all Game courses
     */
    class GameOverState extends GameState
    {
        /**
         * GameOverState displays the final score for all Game courses
         *
         * @param game our associated GameController
         * @return the next logical state, null to end game
         */
        GameState process(GameController game)
        {
            GameState nextState = null;
            final FinalScoreUI finalScores = new FinalScoreUI(players, scores);

            try
            {
                logFinalScores();
            }
            catch (IOException ignore)
            {
                System.out.println("Unable to log scores");
                ignore.printStackTrace();
            }

            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        finalScores.createAndShowGUI();
                    }
                });
            }
            catch (InterruptedException ignore)
            {
                // we'll return nextState = null, the quit state
            }
            catch (InvocationTargetException ignore)
            {
                // we'll return nextState = null, the quit state
            }
            return nextState;
        }

        private void logFinalScores() throws IOException
        {
            boolean append = true;
            log = new BufferedWriter(new FileWriter(Configuration.getUserLogFileDirectory() + fileName, append));

            // Add the date and current time
            Date today = new Date();
            String todayDate = DateFormat.getDateInstance(DateFormat.FULL).format(today);
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aaa");
            String todayTime = formatter.format(today);
            log.write(todayDate + " - " + todayTime);
            log.newLine();

            // Add the scores
            for (FinalScore score : scores)
            {
                log.write(score.getCourseTitle() + "\t" + score.getWinnerName() + "\t" + score.getTime());
                log.newLine();
                for (FinalScore.PlayerInfo playerInfo : score.getPlayerInfo())
                {
                    log.write(playerInfo.getPlayer() + "\t" + playerInfo.getScore());
                    log.newLine();
                }
            }
            log.flush();
            log.close();
        }
    }

}

