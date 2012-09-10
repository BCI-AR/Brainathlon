package com.webkitchen.brainathlon.gameComponents;

import com.webkitchen.brainathlon.data.Configuration;
import com.webkitchen.eeg.analysis.IChannelSampleListener;
import com.webkitchen.eeg.analysis.filterdesign.FilterAlgorithm;
import com.webkitchen.eeg.analysis.filterdesign.FilterRange;
import com.webkitchen.eeg.analysis.filterdesign.FilterSpecification;
import com.webkitchen.eeg.analysis.filterdesign.IIRFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Listens for new raw samples, filters the raw data into the four standard
 * frequency bands of Beta, Alpha, Theta and Delta, then notifies listeners
 * of the latest values values packaged in a <code>Spectrum</code>.
 * Objects can add themselves as listeners/observers to receive copies of
 * all new <code>Spectrum</code>s.
 *
 * @author Amy Palke
 * @see ISpectrumGenerator
 * @see ISpectrumListener
 * @see Spectrum
 */
public class SpectrumFilter implements IChannelSampleListener, ISpectrumGenerator
{
    // Our listener list must be synchronized since listeners will be added and deleted
    // by the main thread, but notification happens in the reader thread.
    private List<ISpectrumListener> listeners = Collections.synchronizedList(new ArrayList<ISpectrumListener>());
    private IIRFilter betaFilter;
    private IIRFilter alphaFilter;
    private IIRFilter thetaFilter;
    private IIRFilter deltaFilter;

    /**
     * Creates a new filter that notifies listeners of sample values in the four
     * standard frequency bands of Beta, Alpha, Theta and Delta
     */
    public SpectrumFilter()
    {
        betaFilter = new IIRFilter(new BetaSpec(), BetaSpec.coefficients);
        alphaFilter = new IIRFilter(new AlphaSpec(), AlphaSpec.coefficients);
        thetaFilter = new IIRFilter(new ThetaSpec(), ThetaSpec.coefficients);
        deltaFilter = new IIRFilter(new DeltaSpec(), DeltaSpec.coefficients);
    }

    /**
     * Processes the sample to filter for our four frequency bands, and notifies
     * our listeners of the latest sample values
     *
     * @param rawSample the raw sample that we will process
     */
    public void receiveSample(double rawSample)
    {
        double betaVal = betaFilter.process(rawSample);
        double alphaVal = alphaFilter.process(rawSample);
        double thetaVal = thetaFilter.process(rawSample);
        double deltaVal = deltaFilter.process(rawSample);
        Spectrum spectrum = new Spectrum(betaVal, alphaVal, thetaVal, deltaVal);
        notifyListeners(spectrum);
    }

    /**
     * Send the new spectrum to all of our listeners
     *
     * @param spectrum the latest separated spectrum values
     */
    private void notifyListeners(Spectrum spectrum)
    {
        // Note: This method is called by the eeg reader thread so we'll make an
        //  array copy to iterate to protect against concurrent modification errors
        //  if the listener list is changed by the main thread
        ISpectrumListener[] listenerCopy = listeners.toArray(new ISpectrumListener[0]);
        for (ISpectrumListener listener : listenerCopy)
        {
            listener.receiveSpectrum(spectrum);
        }
    }

    /**
     * Attach listener to receive notification/copies of all new spectrums
     *
     * @param listener the observer who wants to receive spectrums
     */
    public void addSpectrumListener(ISpectrumListener listener)
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
    public void removeSpectrumListener(ISpectrumListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllSpectrumListeners()
    {
        listeners.clear();
    }


    private static class BetaSpec extends FilterSpecification
    {
        private static double[] coefficients = new double[]{
            3.9098846211500405E-5,
            0.8934292928232863,
            -1.579565308621543,
            0.9395802166479443,
            -1.8470518020719924,
            0.7579924069303536,
            -1.522745441226756,
            0.8284919149144723,
            -1.7192330130722595,
            0.7480058533788925,
            -1.5910928436300789
        };

        private BetaSpec()
        {
            setAlgorithmType(FilterAlgorithm.BUTTERWORTH);
            setRangeType(FilterRange.BANDPASS);
            setOrder(5);
            setRate(Configuration.getSampleRate());
            setFrequency0(12);
            setFrequency1(25);
            setAutoAdjust(true);
        }
    }


    private static class AlphaSpec extends FilterSpecification
    {
        private static double[] coefficients = new double[]{
            1.4328139213007392E-7,
            0.9687692182991368,
            -1.888780817710422,
            0.9776675530769263,
            -1.9373671043764888,
            0.9242662416208486,
            -1.8555126725577347,
            0.9383360044218676,
            -1.8933731982856097,
            0.9156801402563697,
            -1.8605106731373373
        };

        private AlphaSpec()
        {
            setAlgorithmType(FilterAlgorithm.BUTTERWORTH);
            setRangeType(FilterRange.BANDPASS);
            setOrder(5);
            setRate(Configuration.getSampleRate());
            setFrequency0(8);
            setFrequency1(12);
            setAutoAdjust(true);
        }
    }


    private static class ThetaSpec extends FilterSpecification
    {
        private static double[] coefficients = new double[]{
            1.4328120642830775E-7,
            0.9656306337607032,
            -1.9313270209105742,
            0.9808452753214321,
            -1.970336616161042,
            0.9188950445196632,
            -1.891827629526624,
            0.9438208751824131,
            -1.9310490739001676,
            0.9156801618050924,
            -1.897231115142515
        };

        private ThetaSpec()
        {
            setAlgorithmType(FilterAlgorithm.BUTTERWORTH);
            setRangeType(FilterRange.BANDPASS);
            setOrder(5);
            setRate(Configuration.getSampleRate());
            setFrequency0(4);
            setFrequency1(8);
            setAutoAdjust(true);
        }
    }


    private static class DeltaSpec extends FilterSpecification
    {
        private static double[] coefficients = new double[]{
            3.517261310962851E-8,
            0.9690291566054876,
            -1.9609915984969997,
            0.9907519645706122,
            -1.9900444393772285,
            0.927301687991401,
            -1.9217012054601756,
            0.9691853246005308,
            -1.9682016937989486,
            0.9361082206161236,
            -1.933775039373902
        };

        private DeltaSpec()
        {
            setAlgorithmType(FilterAlgorithm.BUTTERWORTH);
            setRangeType(FilterRange.BANDPASS);
            setOrder(5);
            setRate(Configuration.getSampleRate());
            setFrequency0(1);
            setFrequency1(4);
            setAutoAdjust(true);
        }
    }


}

