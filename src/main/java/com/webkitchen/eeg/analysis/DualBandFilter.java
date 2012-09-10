package com.webkitchen.eeg.analysis;

import com.webkitchen.eeg.analysis.filterdesign.FilterDesigner;
import com.webkitchen.eeg.analysis.filterdesign.FilterSpecification;
import com.webkitchen.eeg.analysis.filterdesign.IIRFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Listens for new raw sample values, then filters the raw data into two frequency bands
 * as described by our two filter specifications, and notifies
 * listeners of the filtered values.
 * Objects can add themselves as listeners/observers to receive copies of
 * all new filtered samples.
 *
 * @author Amy Palke
 * @see IDualBandSampleListener
 */
public class DualBandFilter implements IChannelSampleListener, IDualBandSampleGenerator
{
    // Our listener list must be synchronized since listeners will be added and deleted
    // by the main thread, but notification happens in the reader thread.
    private List<IDualBandSampleListener> listeners = Collections.synchronizedList(new ArrayList<IDualBandSampleListener>());
    private IIRFilter filter1;
    private IIRFilter filter2;

    /**
     * Creates a new <code>DualBandFilter</code> that builds two <code>IIRFilter</code>s to match
     * the <code>FilterSpecification</code> parameters, and generates band sample values for
     * the specified filter types
     *
     * @param spec1 the specification we will use to design our first <code>IIRFilter</code>
     * @param spec2 the specification we will use to design our second <code>IIRFilter</code>
     */
    public DualBandFilter(FilterSpecification spec1, FilterSpecification spec2)
    {
        FilterDesigner designer = new FilterDesigner();
        filter1 = designer.createFilter(spec1);
        filter2 = designer.createFilter(spec2);
    }

    /**
     * Creates a new <code>DualBandFilter</code> that uses the <code>IIRFilter</code>
     * parameters to generate band sample values
     *
     * @param filter1 our first <code>IIRFilter</code>
     * @param filter2 our second <code>IIRFilter</code>
     */
    public DualBandFilter(IIRFilter filter1, IIRFilter filter2)
    {
        this.filter1 = filter1;
        this.filter2 = filter2;
    }

    /**
     * Processes the sample to filter for our two frequency bands, and notifies
     * our listeners of the latest band sample values
     *
     * @param rawSample the raw sample that we will process
     */
    public void receiveSample(double rawSample)
    {
        double bandSampleValue1 = filter1.process(rawSample);
        double bandSampleValue2 = filter2.process(rawSample);
        notifyListeners(bandSampleValue1, bandSampleValue2);
    }

    /**
     * Send the new samples to all of our listeners
     *
     * @param sample1 the latest sample for band #1
     * @param sample2 the latest sample for band #2
     */
    private void notifyListeners(double sample1, double sample2)
    {
        // Note: This method is called by the eeg reader thread so we'll make an
        //  array copy to iterate to protect against concurrent modification errors
        //  if the listener list is changed by the main thread
        IDualBandSampleListener[] listenerCopy = listeners.toArray(new IDualBandSampleListener[0]);
        for (IDualBandSampleListener listener : listenerCopy)
        {
            listener.receiveBand(sample1, sample2);
        }
    }

    /**
     * Attach listener to receive notification/copies of all new band values
     *
     * @param listener the observer who wants to receive band values
     */
    public void addDualBandListener(IDualBandSampleListener listener)
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
    public void removeDualBandListener(IDualBandSampleListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllDualBandListeners()
    {
        listeners.clear();
    }
}

