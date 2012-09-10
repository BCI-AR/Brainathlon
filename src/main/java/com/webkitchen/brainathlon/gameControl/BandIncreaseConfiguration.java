package com.webkitchen.brainathlon.gameControl;

import com.webkitchen.eeg.analysis.filterdesign.FilterSpecification;

/**
 * Contains the configuration for the BandIncreaseCourse as specified in the XML configuration file
 *
 * @author Amy Palke
 * @see com.webkitchen.brainathlon.data.Configuration
 */
public class BandIncreaseConfiguration
{
    public final int sampleSize;  // Number of samples averaged to compute amplitudes
    public final double tolerance;  // Ignore increase or decrease of amount smaller than this
    public final int minNotificationInterval; // Get an update after this minimum number samples

    public final int targetAmplitude;
    public final int timeLimitMinutes;
    public final int minScore;
    public final int maxScore;
    public final int scoreMultiplier;

    public final FilterSpecification filterSpec;
    public double[] filterCoefficients;

    public BandIncreaseConfiguration(FilterSpecification filterSpec, int maxScore, int minNotificationInterval, int minScore, int sampleSize, int scoreMultiplier, int targetAmplitude, int timeLimitMinutes, double tolerance)
    {
        this.filterSpec = filterSpec;
        this.maxScore = maxScore;
        this.minNotificationInterval = minNotificationInterval;
        this.minScore = minScore;
        this.sampleSize = sampleSize;
        this.scoreMultiplier = scoreMultiplier;
        this.targetAmplitude = targetAmplitude;
        this.timeLimitMinutes = timeLimitMinutes;
        this.tolerance = tolerance;
    }
}
