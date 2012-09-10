package com.webkitchen.eeg.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Listens for samples in two given bands, calculates the amplitude averages for each and the
 * ratio between the two, then notifies listeners of the ratio.
 * Objects can add themselves as listeners/observers to receive copies of
 * all new amplitude ratios.
 *
 * @author Amy Palke
 */
public class RatioMonitor implements IDualBandSampleListener, IRatioGenerator, IDualAmplitudeGenerator
{
    // Our listener lists must be synchronized since listeners will be added and deleted
    // by the main thread, but notification happens in the reader thread.
    private List<IRatioListener> ratioListeners = Collections.synchronizedList(new ArrayList<IRatioListener>());
    private List<IDualAmplitudeListener> amplitudeListeners = Collections.synchronizedList(new ArrayList<IDualAmplitudeListener>());
    private int sampleSize;
    private int samplesAdded = 0;
    private int samplesAddedSinceNotification = 0;

    private double lastSentValue;
    private double tolerance;
    private int minNotificationInterval;

    private AmplitudeMonitor monitor1;
    private AmplitudeMonitor monitor2;

    /**
     * Creates a new monitor that will calculate the ratio between the amplitude values
     * of two filtered signals
     *
     * @param sampleSize              the number of samples to use in our RMS calculation
     * @param tolerance               the amount of change in ratio required before notifying listeners
     * @param minNotificationInterval the minimum interval for notifying listeners, even if amplitude
     *                                has not changed beyond tolerance amount
     */
    public RatioMonitor(int sampleSize, double tolerance, int minNotificationInterval)
    {
        this.sampleSize = sampleSize;
        this.tolerance = tolerance;
        this.minNotificationInterval = minNotificationInterval;
        monitor1 = new AmplitudeMonitor(sampleSize);
        monitor2 = new AmplitudeMonitor(sampleSize);
    }

    /**
     * Processes the samples to calculate the current amplitude ratio, and notifies all listeners if
     * the difference between the new ratio and last sent ratio is greater than our
     * tolerance amount, or if the number of processed values has reached our minNotificationInterval
     *
     * @param sample1 one filtered sample
     * @param sample2 the other filtered sample
     */
    public void receiveBand(double sample1, double sample2)
    {
        monitor1.process(sample1);
        monitor2.process(sample2);

        // Don't send notification until we have a full array of samples
        if (samplesAdded < sampleSize)
        {
            samplesAdded++;
        }
        else
        {
            double currentRatio = monitor1.currentValue / monitor2.currentValue;
            samplesAddedSinceNotification = (++samplesAddedSinceNotification % minNotificationInterval);
            // Notify ratio listeners after every minNotificationInterval samples,
            // or if we've seen an increase or decrease
            if (samplesAddedSinceNotification == 0 ||
                currentRatio < (lastSentValue - tolerance) ||
                currentRatio > (lastSentValue + tolerance))
            {
                notifyRatioListeners(currentRatio);
            }
            // Notify amplitude listeners after minNotificationInterval
            if (samplesAddedSinceNotification == 0)
            {
                notifyAmplitudeListeners(monitor1.currentValue, monitor2.currentValue);
            }
        }
    }

    private void notifyRatioListeners(double currentRatio)
    {
        lastSentValue = currentRatio;
        // Note: This method is called by the eeg reader thread so we'll make an
        //  array copy to iterate to protect against concurrent modification errors
        //  if the listener list is changed by the main thread
        IRatioListener[] listenerCopy = ratioListeners.toArray(new IRatioListener[0]);
        for (IRatioListener listener : listenerCopy)
        {
            listener.receiveRatio(currentRatio);
        }
        samplesAddedSinceNotification = 0;
    }

    /**
     * Attach listener to receive notification of the player's ratio of 2 wave bands
     *
     * @param listener the observer who wants to receive the player's ratio of 2 wave bands
     */
    public void addRatioListener(IRatioListener listener)
    {
        // Add the listener if he isn't already in our list
        if (!ratioListeners.contains(listener))
        {
            ratioListeners.add(listener);
        }
    }

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeRatioListener(IRatioListener listener)
    {
        ratioListeners.remove(listener);
    }

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllRatioListeners()
    {
        ratioListeners.clear();
    }

    private void notifyAmplitudeListeners(double amplitudeOne, double amplitudeTwo)
    {
        // Note: This method is called by the eeg reader thread so we'll make an
        //  array copy to iterate to protect against concurrent modification errors
        //  if the listener list is changed by the main thread
        IDualAmplitudeListener[] listenerCopy = amplitudeListeners.toArray(new IDualAmplitudeListener[0]);
        for (IDualAmplitudeListener listener : listenerCopy)
        {
            listener.receiveAmplitude(amplitudeOne, amplitudeTwo);
        }
    }

    /**
     * Attach listener to receive notification/copies of new amplitude levels
     * for two different frequency bands
     *
     * @param listener the observer who wants to receive amplitude levels
     */
    public void addAmplitudeListener(IDualAmplitudeListener listener)
    {
        // Add the listener if he isn't already in our list
        if (!amplitudeListeners.contains(listener))
        {
            amplitudeListeners.add(listener);
        }
    }

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeAmplitudeListener(IDualAmplitudeListener listener)
    {
        amplitudeListeners.remove(listener);
    }

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllAmplitudeListeners()
    {
        amplitudeListeners.clear();
    }

    private class AmplitudeMonitor
    {
        private double[] samples;
        private double currentValue;

        public AmplitudeMonitor(int sampleSize)
        {
            samples = new double[sampleSize];
        }

        private void process(double sample)
        {
            // scoot everything to right by 1 position
            System.arraycopy(samples, 0, samples, 1, sampleSize - 1);
            samples[0] = sample;

            currentValue = AnalysisUtil.rms(samples);
        }

    }
}
