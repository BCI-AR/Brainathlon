package com.webkitchen.brainathlon;

import com.webkitchen.brainathlon.data.Configuration;
import com.webkitchen.brainathlon.data.PlayerData;
import com.webkitchen.eeg.acquisition.EEGAcquisitionController;
import com.webkitchen.brainathlon.gameComponents.Player;
import com.webkitchen.brainathlon.gameControl.GameController;
import com.webkitchen.brainathlon.gameControl.SimpleDisplayCourse;
import com.webkitchen.brainathlon.ui.*;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Application begins program execution and manages application state
 *
 * @author Amy Palke
 * @see GameController
 */
public class Application
{
    private ApplicationState state;
    private GameData gameData;
    private EEGAcquisitionController eegAcquisitionController;

    private Application()
    {
        eegAcquisitionController = EEGAcquisitionController.getInstance();
        state = new InitialState();
    }

    /**
     * Runs the Brainathlon application, a neurofeedback game which allows
     * 1 to 2 players to compete head-to-head to determine who really is the
     * master of their own mind!
     *
     * @param args ignored
     */
    public static void main(String[] args)
    {
        Application app = new Application();
        app.run();
    }

    private void run()
    {
        JFrame.setDefaultLookAndFeelDecorated(true);

        while (state != null)
        {
            state = state.process(this);
        }

        quit();
    }

    private void quit()
    {
        // Save the current configuration
        try
        {
            Configuration.save();
        }
        catch (IOException e)
        {
            displayError("Unable to save configuration files");
            e.printStackTrace();
        }
        finally
        {
            // Clean up and quit
            eegAcquisitionController.stopReading();
            eegAcquisitionController = null;
            System.exit(0);
        }
    }


    private void setTargetNumberOfPlayers(int numberOfPlayers)
    {
        gameData = new GameData(numberOfPlayers);
    }

    private boolean needsNewPlayer()
    {
        return gameData.needsPlayer();
    }

    private void addPlayer(PlayerData playerData)
    {
        gameData.addPlayer(playerData);
    }

    private void setupEEGReader() throws IOException
    {
        eegAcquisitionController.startReading(Configuration.getDebugMode());
    }

    private void attachPlayerFilters()
    {
        // Add players as rawSample listeners, listening to their channels
        for (Iterator itr = gameData.players.iterator(); itr.hasNext();)
        {
            Player player = (Player) itr.next();
            int[] channels = player.getChannels();
            eegAcquisitionController.getChannelSampleGenerator().addSampleListener(player, channels);
        }
    }

    private void loadConfiguration()
    {
        Configuration.load();
    }


    /**
     * Holds game information that Application is collecting in order to create a new Game
     */
    class GameData
    {
        private List<Player> players;
        private int numberOfPlayers;
        private int playersAdded = 0;

        GameData(int numberOfPlayers)
        {
            this.numberOfPlayers = numberOfPlayers;
            this.players = new ArrayList<Player>();
        }

        void addPlayer(PlayerData playerData)
        {
            if (needsPlayer())
            {
                Player newPlayer = new Player(++playersAdded, playerData);
                players.add(newPlayer);
            }
            else
            {
                throw new RuntimeException("Attempting to add too many players!");
            }
        }

        boolean needsPlayer()
        {
            return (players.size() < numberOfPlayers);
        }
    }


    /**
     * Handles state-specific behavior for Application
     */
    abstract class ApplicationState
    {
        abstract ApplicationState process(Application app);
    }

    /**
     * InitialState happens when the Application is first started.  It handles
     * setting up the eeg reader
     */
    class InitialState extends ApplicationState
    {

        /**
         * InitialState sets up the EEG reader
         *
         * @param app our associated Application
         * @return the next logical state, either AddPlayerState or null
         */
        ApplicationState process(Application app)
        {
            ApplicationState nextState = null;
            try
            {
                app.loadConfiguration();
                app.setupEEGReader();
                nextState = new NewGameState();
            }
            catch (IOException e)
            {
                displayError("Unable to connect to EEG reader. (Check that Neuroserver is running)");
                // we'll return nextState = null, the quit state
            }
            return nextState;
        }
    }

    /**
     * NewGameState happens when Application has no current game, and handles setting up game type so that players can
     * be added
     */
    class NewGameState extends ApplicationState
    {
        private SelectNewGameUI newGameUI = new SelectNewGameUI();
        private SelectNewGameUI.Selection selection;

        /**
         * NewGameState displays a "New Game" dialog, where users choose game type, or opt to quit the application. If a
         * new game type is selected, the next state is AddPlayerState. If "quit" is selected, the next state is the
         * quit state (null).
         *
         * @param app our associated Application
         * @return the next logical state, either AddPlayerState or null
         */
        ApplicationState process(Application app)
        {
            ApplicationState nextState = null;


            /* Schedule a job for the event-dispatching thread:
             creating and showing the "New Game" GUI.
             Wait for it to finish, so we can get selected number of players */
            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        selection = newGameUI.createAndShowGUI();
                    }
                });

                if (selection != SelectNewGameUI.Selection.QUIT)
                {
                    app.setTargetNumberOfPlayers(selection.getNumberPlayers());
                    nextState = new AddPlayerState();
                }
            }
            catch (InterruptedException e)
            {
                nextState = this; // try again
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
                // we'll return nextState = null, the quit state
            }
            return nextState;
        }
    }


    /**
     * AddPlayerState happens when game type has been selected, but all players have not yet been added to the game
     */
    class AddPlayerState extends ApplicationState
    {
        /**
         * AddPlayerState displays the "Add PlayerData" dialog, where users enter player information. As players are
         * added through the dialog, they are added as PlayerData objects to the game. Once all players have been added,
         * the game is ready to begin.
         *
         * @param app our associated Application
         * @return the next logical state, either FilterSetupState or null
         */
        ApplicationState process(Application app)
        {
            ApplicationState nextState = null;

            while (app.needsNewPlayer())
            {
                PlayerData playerData = newPlayerForm();
                // If we were returned a null object, go to quit state (null)
                if (playerData == null)
                {
                    break;
                }
                else
                {
                    app.addPlayer(playerData);
                }
            }
            // If all players have been added, we are ready to start the game
            if (!app.needsNewPlayer())
            {
                // Create the filters
                app.attachPlayerFilters();
                nextState = new DisplayEEGBandsState();
            }

            return nextState;
        }

        /**
         * Displays the AddPlayerUI, and collects a PlayerData object
         *
         * @return the newly added PlayerData
         */
        private PlayerData newPlayerForm()
        {
            PlayerData playerData = null;
            AddPlayerUI view = new AddPlayerUI();
            AddPlayerPresenter presenter = new AddPlayerPresenter(AddPlayerPresenter.Mode.ADD, view);
            try
            {
                presenter.displayView();
                playerData = presenter.getPlayerData();
            }
            catch (BrainathlonUIException e)
            {
                e.printStackTrace();
            }
            return playerData;
        }
    }

    /**
     * DisplayEEGBandsState happens when new players have been added - it displays the EEG bands
     * so the players can make sure their electrodes are attached well, and the EEG device is
     * working properly
     */
    class DisplayEEGBandsState extends ApplicationState
    {
        /**
         * DisplayEEGBandsState displays the EEG band UI
         *
         * @param app our associated Application
         * @return the next logical state, either GamePlayState or null
         */
        ApplicationState process(Application app)
        {
            final SimpleDisplayCourse display = new SimpleDisplayCourse(gameData.players);
            display.start();  // blocks until window is closed
            return new GamePlayState();
        }
    }

    /**
     * GamePlayState tells Application to create a new Game that is ready to be started, and hands control over to the
     * Game
     */
    class GamePlayState extends ApplicationState
    {
        /**
         * GamePlayState displays the "Begin Game" screen, where users can select to begin playing the game or opt to
         * quit the application
         *
         * @param app our associated Application
         * @return the next logical state, either GameInProgressState or null
         */
        ApplicationState process(Application app)
        {
            GameController game = new GameController(gameData.players);
            game.PlayGame(); // blocks until game is over

            return new GameOverState();
        }
    }


    /**
     * GameInProgressState happens when the game ends, and the user has the option to have a rematch (same players),
     * play a new game (new players), or quit the application
     */
    class GameOverState extends ApplicationState
    {
        private GameOverUI gameOverUI = new GameOverUI();
        private GameOverUI.Selection selection;

        /**
         * GameOverState displays the "Game Over" dialog, where user has the option to have a rematch (same players),
         * play a new game (new players), or quit the application
         *
         * @param app our associated Application
         * @return the next logical state, either GamePlayState, NewGameState or null
         */
        ApplicationState process(Application app)
        {
            ApplicationState nextState = null;

            /* Schedule a job for the event-dispatching thread:
             creating and showing the "Game Over" GUI.
             Wait for it to finish, so we can get the selection */
            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        selection = gameOverUI.createAndShowGUI();
                    }
                });

                if (selection == GameOverUI.Selection.REMATCH)
                {
                    nextState = new GamePlayState();
                }
                else if (selection == GameOverUI.Selection.NEWGAME)
                {
                    nextState = new NewGameState();
                }
                // else we'll return nextState = null, the quit state
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                // we'll return nextState = null, the quit state
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
                // we'll return nextState = null, the quit state
            }
            return nextState;
        }
    }

    private void displayError(String errorMessage)
    {
        final ErrorMessageUI errorUI = new ErrorMessageUI(errorMessage);
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    errorUI.createAndShowGUI();
                }
            });
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }
}
