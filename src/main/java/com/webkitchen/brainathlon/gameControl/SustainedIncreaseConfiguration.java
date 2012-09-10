package com.webkitchen.brainathlon.gameControl;

import com.webkitchen.eeg.analysis.filterdesign.FilterSpecification;

/**
 * Contains the configuration for the SustainedIncreaseCourse as specified in the XML configuration file
 *
 * @author Amy Palke
 * @see com.webkitchen.brainathlon.data.Configuration
 */
public class SustainedIncreaseConfiguration
{
    public final int sampleSize;  // Number of samples averaged to compute amplitudes
    public final double tolerance;  // Ignore increase or decrease of amount smaller than this
    public final int minNotificationInterval; // Get an update after this minimum number samples

    public final int targetAmplitude;
    public final int timeGoal;    // Seconds sustained above targetAmplitude
    public final int timeLimitMinutes;

    public final FilterSpecification filterSpec;
    public double[] filterCoefficients;

    public SustainedIncreaseConfiguration(FilterSpecification filterSpec, int minNotificationInterval, int sampleSize, int targetAmplitude, int timeGoal, int timeLimitMinutes, double tolerance)
    {
        this.filterSpec = filterSpec;
        this.minNotificationInterval = minNotificationInterval;
        this.sampleSize = sampleSize;
        this.targetAmplitude = targetAmplitude;
        this.timeGoal = timeGoal;
        this.timeLimitMinutes = timeLimitMinutes;
        this.tolerance = tolerance;
    }
}
