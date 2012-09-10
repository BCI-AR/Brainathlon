package com.webkitchen.eeg.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Listens for samples in a given band, calculates the amplitude average for the samples,
 * and notifies listeners of the new average amplitude.
 * Objects can add themselves as listeners/observers to receive copies of
 * all new average amplitudes.
 *
 * @author Amy Palke
 */
public class BandMonitor implements IBandSampleListener, IAmplitudeGenerator
{
    // Our listener list must be synchronized since listeners will be added and deleted
    // by the main thread, but notification happens in the reader thread.
    private List<IAmplitudeListener> listeners = Collections.synchronizedList(new ArrayList<IAmplitudeListener>());
    private int sampleSize;
    private double[] samples;
    private int samplesAdded = 0;
    private int samplesAddedSinceNotification = 0;

    private double lastSentValue;
    private double tolerance;
    private int minNotificationInterval;

    /**
     * Creates a new monitor that will calculate amplitude values for a filtered signal
     *
     * @param sampleSize              the number of samples to use in our RMS calculation
     * @param tolerance               the amount of change in amplitude required before notifying listeners
     * @param minNotificationInterval the minimum interval for notifying listeners, even if amplitude
     *                                has not changed beyond tolerance amount
     */
    public BandMonitor(int sampleSize, double tolerance, int minNotificationInterval)
    {
        this.sampleSize = sampleSize;
        this.tolerance = tolerance;
        this.minNotificationInterval = minNotificationInterval;
        samples = new double[sampleSize];
    }

    /**
     * Processes the sample to calculate the signal amplitude, and notifies all listeners if
     * the difference between the new amplitude and last sent amplitude is greater than our
     * tolerance amount, or if the number of processed values has reached our minNotificationInterval
     *
     * @param bandSample the filtered sample value
     */
    public void receiveBand(double bandSample)
    {
        process(bandSample);
    }

    private void process(double sample)
    {
        // Don't start averaging until we have a full array of samples
        if (samplesAdded < sampleSize)
        {
            samples[samplesAdded++] = sample;
        }
        else
        {
            // scoot everything to right by 1 position
            System.arraycopy(samples, 0, samples, 1, sampleSize - 1);
            samples[0] = sample;

            double currentValue = AnalysisUtil.rms(samples);
            samplesAddedSinceNotification = (++samplesAddedSinceNotification % minNotificationInterval);
            // Notify listeners after every minNotificationInterval samples,
            // or if we've seen an increase or decrease
            if (samplesAddedSinceNotification == 0 ||
                currentValue < (lastSentValue - tolerance) ||
                currentValue > (lastSentValue + tolerance))
            {
                notifyListeners(currentValue);
            }
        }
    }

    /**
     * Send the new average to all of our listeners
     *
     * @param rms the latest average amplitude
     */
    private void notifyListeners(double rms)
    {
        lastSentValue = rms;
        // Note: This method is called by the eeg reader thread so we'll make an
        //  array copy to iterate to protect against concurrent modification errors
        //  if the listener list is changed by the main thread
        IAmplitudeListener[] listenerCopy = listeners.toArray(new IAmplitudeListener[0]);
        for (IAmplitudeListener listener : listenerCopy)
        {
            listener.receiveAmplitude(rms);
        }
        samplesAddedSinceNotification = 0;
    }

    /**
     * Attach listener to receive notification/copies of new amplitude levels
     *
     * @param listener the observer who wants to receive amplitude levels
     */
    public void addAmplitudeListener(IAmplitudeListener listener)
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
    public void removeAmplitudeListener(IAmplitudeListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllAmplitudeListeners()
    {
        listeners.clear();
    }
}
