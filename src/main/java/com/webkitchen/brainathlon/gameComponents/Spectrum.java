package com.webkitchen.brainathlon.gameComponents;


/**
 * Contains the EEG data for one channel, broken the four standard
 * frequency bands of Beta, Alpha, Theta and Delta.
 * Spectrums are immutable; their data cannot be changed after they have been created.
 *
 * @author Amy Palke
 */
public final class Spectrum
{
    private final double beta;
    private final double alpha;
    private final double theta;
    private final double delta;

    /**
     * Creates a new Spectrum with the given values
     *
     * @param beta  the sample from the beta frequency range
     * @param alpha the sample from the alpha frequency range
     * @param theta the sample from the theta frequency range
     * @param delta the sample from the delta frequency range
     */
    public Spectrum(double beta, double alpha, double theta, double delta)
    {
        this.beta = beta;
        this.alpha = alpha;
        this.theta = theta;
        this.delta = delta;
    }

    /**
     * Returns the sample from the beta frequency range
     *
     * @return the sample from the beta frequency range
     */
    public double getBeta()
    {
        return beta;
    }

    /**
     * Returns the sample from the alpha frequency range
     *
     * @return the sample from the alpha frequency range
     */
    public double getAlpha()
    {
        return alpha;
    }

    /**
     * Returns the sample from the theta frequency range
     *
     * @return the sample from the theta frequency range
     */
    public double getTheta()
    {
        return theta;
    }

    /**
     * Returns the sample from the delta frequency range
     *
     * @return the sample from the delta frequency range
     */
    public double getDelta()
    {
        return delta;
    }
}
