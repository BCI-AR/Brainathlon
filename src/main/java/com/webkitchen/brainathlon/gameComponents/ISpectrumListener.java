package com.webkitchen.brainathlon.gameComponents;

import java.util.EventListener;


/**
 * Listener interface for receiving filtered frequency spectrum values in
 * the four standard frequency bands of Beta, Alpha, Theta and Delta
 *
 * @author Amy Palke
 * @see ISpectrumGenerator
 * @see Spectrum
 */
public interface ISpectrumListener extends EventListener
{
    /**
     * Receive the latest filtered spectrum values in the four standard
     * frequency bands of Beta, Alpha, Theta and Delta
     *
     * @param spectrum the latest filtered spectrum
     */
    public void receiveSpectrum(Spectrum spectrum);
}
