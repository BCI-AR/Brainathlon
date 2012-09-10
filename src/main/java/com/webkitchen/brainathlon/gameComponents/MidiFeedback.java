package com.webkitchen.brainathlon.gameComponents;

import com.webkitchen.brainathlon.data.Configuration;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles setup of Midi and playback of sounds
 *
 * @author Amy Palke
 */
public class MidiFeedback
{
    private static final MidiFeedback ourInstance = new MidiFeedback();
    private boolean isLoaded;
    private Synthesizer synth;
    private MidiChannel[] midiChannels;
    private Instrument[] allInstruments;
    private Map<String, Integer> instrumentOptions;
    private int targetInstrumentName = 0;
    private int targetInstrumentIndex = 1;
    private String[][] targetInstruments =
            new String[][]{{"Crystal", "98"},
                           {"Atmosphere", "99"},
                           {"Soundtrack", "97"},
                           {"Goblins", "101"},
                           {"Marimba", "12"},
                           {"Moon Jelly", "273"},
                           {"Hi Temple Gong", "347"}};

    public enum SoundType
    {
        INCREASE(60), DECREASE(50), REWARD(70);

        private final int note;

        SoundType(int note)
        {
            this.note = note;
        }

        public int getNote()
        {
            return note;
        }
    };

    public enum Volume
    {
        SOFT(30), MEDIUM(50), LOUD(100);

        private final int velocity;

        Volume(int velocity)
        {
            this.velocity = velocity;
        }
    };

    /**
     * Private constructor - access instance through getInstance() factory method
     */
    private MidiFeedback()
    {
        // private to ensure singleton status
    }

    /**
     * Factory method for return the single instance of MidiFeedback
     *
     * @return the single instance of MidiFeedback
     */
    public static MidiFeedback getInstance()
    {
        return ourInstance;
    }

    /**
     * Attempts to load Midi components
     *
     * @throws MidiUnavailableException if we are unable to load any components
     */
    public void loadMidi() throws MidiUnavailableException
    {

        if (!isLoaded)
        {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            midiChannels = synth.getChannels();
            allInstruments = synth.getDefaultSoundbank().getInstruments();
            loadInstrumentOptions();

            isLoaded = true;
        }
    }

    private void loadInstrumentOptions() throws MidiUnavailableException
    {
        instrumentOptions = new HashMap<String, Integer>();
        // Add our target instruments, if available
        for (String[] instrumentData : targetInstruments)
        {
            Integer index = new Integer(instrumentData[targetInstrumentIndex]);
            if (allInstruments[index].getName().equals(instrumentData[targetInstrumentName]))
            {
                instrumentOptions.put(instrumentData[targetInstrumentName], index);
            }
        }
        // If the targets weren't in our predicted locations, search for them
        if (instrumentOptions.size() == 0)
        {
            for (String[] instrumentData : targetInstruments)
            {
                String name = instrumentData[targetInstrumentName];
                for (int i = 0, length = allInstruments.length; i < length; i++)
                {
                    if (allInstruments[i].getName().equals(name))
                    {
                        instrumentOptions.put(name, new Integer(i));
                        break;
                    }
                }
            }
        }
        // Add some random Instruments, if none of our targets are available
        if (instrumentOptions.size() == 0)
        {
            int midiInstSize = allInstruments.length;
            int targetSize = targetInstruments.length;
            for (int i = 0; i < targetSize && i < midiInstSize; i++)
            {
                instrumentOptions.put(allInstruments[i].getName(), new Integer(i));
            }
        }
        // If no instruments are available, throw an error
        if (instrumentOptions.size() == 0)
        {
            throw new MidiUnavailableException("Can't load instruments");
        }
    }

    public boolean isLoaded()
    {
        return isLoaded;
    }


    /**
     * Returns the target instrument options
     *
     * @return a Map of instrument names and numbers
     */
    public Map<String, Integer> getInstrumentOptions()
    {
        return instrumentOptions;
    }

    /**
     * Plays a feedback sound at medium volume
     *
     * @param type       the type of feedback
     * @param channel    the channel to use
     * @param instrument the Midi instrument number
     */
    public void playSound(SoundType type, int channel, int instrument)
    {
        playSound(type, channel, instrument, Volume.MEDIUM);
    }

    /**
     * Plays a feedback sound
     *
     * @param type       the type of feedback
     * @param channel    the channel to use
     * @param instrument the Midi instrument number
     * @param volume     the sound level
     */
    public void playSound(SoundType type, int channel, int instrument, Volume volume)
    {
        synth.loadInstrument(allInstruments[instrument]);
        midiChannels[channel].programChange(instrument);

        midiChannels[channel].noteOn(type.note, volume.velocity);
        midiChannels[channel].noteOff(type.note, volume.velocity);
    }

    /**
     * Play the Midi song specified in the configuration file
     *
     * @see Configuration
     */
    public void playCourseOverSong()
    {
        try
        {
            // Create full filename
            String file = Configuration.getCourseOverSongFile();

            // Access the file
            Sequence sequence = MidiSystem.getSequence(new File(file));

            // Create a sequencer for the sequence
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(sequence);

            // Start playing
            sequencer.start();
        }
                // If we can't play the song, just print out the error and continue
        catch (InvalidMidiDataException ignore)
        {
            ignore.printStackTrace();
        }
        catch (IOException ignore)
        {
            ignore.printStackTrace();
        }
        catch (MidiUnavailableException ignore)
        {
            ignore.printStackTrace();
        }

    }

    /**
     * This can be used to print out all available Midi instruments
     */
    private void printInstruments()
    {
        for (int i = 0, length = allInstruments.length; i < length; i++)
        {
            System.out.println("instr[" + i + "] = " + allInstruments[i].getName());
        }
    }
}
