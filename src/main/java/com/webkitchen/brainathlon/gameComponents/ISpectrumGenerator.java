package com.webkitchen.brainathlon.gameComponents;


/**
 * Filters raw data into the four standard frequency bands of
 * Beta, Alpha, Theta and Delta, then notifies listeners
 * of the latest values values packaged in a <code>Spectrum</code>.
 * Objects can add themselves as listeners/observers to receive copies of
 * all new <code>Spectrum</code>s.
 *
 * @author Amy Palke
 * @see ISpectrumListener
 * @see Spectrum
 */
public interface ISpectrumGenerator
{
    /**
     * Attach listener to receive notification/copies of all new spectrums
     *
     * @param listener the observer who wants to receive spectrums
     */
    public void addSpectrumListener(ISpectrumListener listener);

    /**
     * Remove listener from our notification list
     *
     * @param listener the observer to remove
     */
    public void removeSpectrumListener(ISpectrumListener listener);

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllSpectrumListeners();
}
