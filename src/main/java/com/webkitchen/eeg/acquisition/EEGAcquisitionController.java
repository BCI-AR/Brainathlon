package com.webkitchen.eeg.acquisition;

import java.io.IOException;


/**
 * Controller that starts/stops reading EEG data, and provides access to the
 * <code>IRawSampleGenerator</code>.  Classes wanting to receive EEG data
 * from a specific channel or channels should register as a listener on the
 * channel(s) with the <code>IRawSampleGenerator</code>'s
 * <code>addSampleListener</code> method, and implement the
 * <code>IRawSampleListener</code> interface.
 *
 * @author Amy Palke
 * @see IRawSampleGenerator
 * @see IRawSampleListener
 * @see RawSample
 */
public class EEGAcquisitionController
{
    private static final EEGAcquisitionController INSTANCE = new EEGAcquisitionController();
    private static Demultiplexer demultiplexer;
    private static NeuroServerReader reader;
    private static boolean isActive;

    /**
     * Private constructor - access instance through getInstance() factory method
     */
    private EEGAcquisitionController()
    {
        // Create the demultiplexer
        demultiplexer = new Demultiplexer();
    }

    /**
     * Returns the single instance of EEGAcquisitionController
     *
     * @return the single instance of EEGAcquisitionController
     */
    public static EEGAcquisitionController getInstance()
    {
        return INSTANCE;
    }

    /**
     * Returns true if the control is currently reading, false otherwise
     *
     * @return true or false if filters are set up
     */
    public static boolean isActive()
    {
        return isActive;
    }

    /**
     * Begins the reading of EEG data, and notification of <code>IRawSampleGenerator</code>
     * listeners.  Objects that call <code>startReading</code> should always
     * call <code>stopReading</code> when they are done reading EEG data.
     *
     * @throws IOException if we are unable to connect to the EEG device
     */
    public void startReading(boolean debugMode) throws IOException
    {
        if (!isActive)
        {
            // Wire together the wave acquisition and analysis components
            reader = new NeuroServerReader();
            reader.addPacketListener(demultiplexer);

            // Start up the reader
            reader.startReading(debugMode);
            reader.start();
            isActive = true;
        }
    }

    /**
     * Ends the reading of EEG data, and notification of <code>IRawSampleGenerator</code>
     * listeners
     */
    public void stopReading()
    {
        if (isActive)
        {
            isActive = false;

            reader.stopReading();
            reader.removeAllPacketListeners();
            reader = null;

            demultiplexer.removeAllSampleListeners();
        }
    }

    /**
     * Returns the <code>IRawSampleGenerator</code> that acquires the
     * raw EEG samples.  Objects can add themselves as listeners in order
     * to receive the samples.
     *
     * @return the EEG reader that can notify listeners of new EEG samples
     * @see #startReading
     */
    public IRawSampleGenerator getChannelSampleGenerator()
    {
        return demultiplexer;
    }
}
