package com.webkitchen.eeg.analysis;

import com.webkitchen.eeg.analysis.filterdesign.FilterDesigner;
import com.webkitchen.eeg.analysis.filterdesign.FilterSpecification;
import com.webkitchen.eeg.analysis.filterdesign.IIRFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Listens for new raw sample values, then filters the raw data into
 * the specified frequency band described by our FilterSpecification, and notifies
 * listeners of the filtered value.
 * Objects can add themselves as listeners/observers to receive copies of
 * all new filtered samples.
 *
 * @author Amy Palke
 * @see IBandSampleListener
 */
public class BandFilter implements IChannelSampleListener, IBandSampleGenerator
{
    // Our listener list must be synchronized since listeners will be added and deleted
    // by the main thread, but notification happens in the reader thread.
    private List<IBandSampleListener> listeners = Collections.synchronizedList(new ArrayList<IBandSampleListener>());
    private IIRFilter filter;

    /**
     * Creates a new <code>BandFilter</code> that builds an <code>IIRFilter</code> to match
     * the <code>FilterSpecification</code> parameter, and generates band sample values for
     * the specified filter type
     *
     * @param spec the specification we will use to design our <code>IIRFilter</code>
     */
    public BandFilter(FilterSpecification spec)
    {
        FilterDesigner designer = new FilterDesigner();
        filter = designer.createFilter(spec);
    }

    /**
     * Creates a new <code>BandFilter</code> that uses the filterCoefficients parameter
     * to generate band sample values.
     * For valid results, the coefficients should have been designed by the
     * <code>FilterSpecification</code>,  but we don't check validity here,
     * that is the responsibility of the caller.
     *
     * @param spec               the specification we will use for our <code>IIRFilter</code>
     * @param filterCoefficients the coefficients we will use to process raw samples
     */
    public BandFilter(FilterSpecification spec, double[] filterCoefficients)
    {
        filter = new IIRFilter(spec, filterCoefficients);
    }

    /**
     * Processes the sample to filter for our band, and notifies our listeners of the
     * latest band sample value
     *
     * @param rawSample the raw sample that we will process
     */
    public void receiveSample(double rawSample)
    {
        double bandSampleValue = filter.process(rawSample);
        notifyListeners(bandSampleValue);
    }

    /**
     * Send the new sample to all of our listeners
     *
     * @param sample the latest sample
     */
    private void notifyListeners(double sample)
    {
        // Note: This method is called by the eeg reader thread so we'll make an
        //  array copy to iterate to protect against concurrent modification errors
        //  if the listener list is changed by the main thread
        IBandSampleListener[] listenerCopy = listeners.toArray(new IBandSampleListener[0]);
        for (IBandSampleListener listener : listenerCopy)
        {
            listener.receiveBand(sample);
        }
    }

    /**
     * Attach listener to receive notification/copies of all new band values
     *
     * @param listener the observer who wants to receive band values
     */
    public void addBandListener(IBandSampleListener listener)
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
    public void removeBandListener(IBandSampleListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllBandListeners()
    {
        listeners.clear();
    }
}

