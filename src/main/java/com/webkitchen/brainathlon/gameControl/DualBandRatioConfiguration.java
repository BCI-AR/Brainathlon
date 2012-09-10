package com.webkitchen.brainathlon.gameControl;

import com.webkitchen.eeg.analysis.filterdesign.FilterSpecification;

/**
 * Contains the configuration for the DualBandRatioCourse as specified in the XML configuration file
 *
 * @author Amy Palke
 * @see com.webkitchen.brainathlon.data.Configuration
 */
public final class DualBandRatioConfiguration
{
    public final int sampleSize;  // Number of samples averaged to compute amplitudes
    public final double tolerance;  // Ignore increase or decrease of amount smaller than this
    public final int minNotificationInterval; // Get an update after this minimum number samples

    public final int timeLimitMinutes;
    public final int minScore;
    public final int maxScore;
    public final double targetRatio;  // Ratio of bandOne to bandTwo
    public final int aboveTargetMultiplier;
    public final int belowTargetMultiplier;
    public final int maxScoreChange;

    public final FilterSpecification bandOneSpec;
    public double[] bandOneCoefficients;

    public DualBandRatioConfiguration(int aboveTargetMultiplier, FilterSpecification bandOneSpec, FilterSpecification bandTwoSpec, int belowTargetMultiplier, int maxScore, int maxScoreChange, int minNotificationInterval, int minScore, int sampleSize, double targetRatio, int timeLimitMinutes, double tolerance)
    {
        this.aboveTargetMultiplier = aboveTargetMultiplier;
        this.bandOneSpec = bandOneSpec;
        this.bandTwoSpec = bandTwoSpec;
        this.belowTargetMultiplier = belowTargetMultiplier;
        this.maxScore = maxScore;
        this.maxScoreChange = maxScoreChange;
        this.minNotificationInterval = minNotificationInterval;
        this.minScore = minScore;
        this.sampleSize = sampleSize;
        this.targetRatio = targetRatio;
        this.timeLimitMinutes = timeLimitMinutes;
        this.tolerance = tolerance;
    }

    public final FilterSpecification bandTwoSpec;
    public double[] bandTwoCoefficients;

}
