package com.webkitchen.brainathlon.ui;

import com.webkitchen.brainathlon.data.PlayerData;
import com.webkitchen.brainathlon.gameComponents.MidiFeedback;

import javax.sound.midi.MidiUnavailableException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handles the logic for adding a new player
 *
 * @author Amy Palke
 * @see IAddPlayerView
 * @see AddPlayerUI
 */
public class AddPlayerPresenter
{
    public enum Mode
    {
        ADD("Add"), EDIT("Edit");
        private String description;

        private Mode(String description)
        {
            this.description = description;
        }
    };

    private MidiFeedback midiController;
    private boolean soundEnabled;
    private Mode mode;
    private String description;

    private String playerName;
    private Integer playerChannel;
    private Integer playerInstrument;

    private IAddPlayerView view;


    public AddPlayerPresenter(Mode mode, IAddPlayerView view)
    {
        this.mode = mode;
        this.view = view;
        description = mode.description + " Player";

        initMidi();
    }

    public void displayView() throws BrainathlonUIException
    {
        view.buildView(description);
        if (soundEnabled)
        {
            view.setInstrumentMap(midiController.getInstrumentOptions());
        }
        addButtonListener();
        addInstrumentListener();
        view.packAndShow(); // blocks until dialog is disposed
    }

    public boolean hasPlayerData()
    {
        return (playerName != null && playerChannel != null);
    }

    public PlayerData getPlayerData()
    {
        PlayerData newPlayer = null;
        if (hasPlayerData())
        {
            newPlayer = new PlayerData(playerName, new int[]{playerChannel.intValue()}, playerInstrument);
        }
        return newPlayer;
    }

    private void addButtonListener()
    {
        view.addButtonListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                playerName = view.getPlayerName();
                playerChannel = view.getChannel();
                if (soundEnabled)
                {
                    playerInstrument = view.getInstrument();
                }
                view.closeView();
            }
        });
    }

    private void addInstrumentListener()
    {
        if (soundEnabled)
        {
            view.addInstrumentListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    Integer selectedValue = view.getInstrument();
                    midiController.playSound(MidiFeedback.SoundType.INCREASE, 0, selectedValue, MidiFeedback.Volume.LOUD);
                }
            });
        }

    }

    private void initMidi()
    {
        midiController = MidiFeedback.getInstance();
        try
        {
            midiController.loadMidi();
            soundEnabled = true;
        }
        catch (MidiUnavailableException e)
        {
            soundEnabled = false;
        }
    }
}
