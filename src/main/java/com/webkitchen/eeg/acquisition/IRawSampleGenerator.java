package com.webkitchen.eeg.acquisition;


/**
 * Generates the raw EEG samples for a specific channel or channels.  Objects can add
 * themselves as listeners/observers to receive copies of all new samples on a
 * specific channel or set of channels.  Listeners must implement the <code>IRawSampleListener</code>
 * interface.
 *
 * @author Amy Palke
 * @see IRawSampleListener
 * @see RawSample
 */
public interface IRawSampleGenerator
{
    /**
     * Attach listener to receive notification/copies of <code>RawSample</code>s containing
     * specified channel values
     *
     * @param listener the observer who wants to receive <code>RawSample</code>s
     * @param channels the channels to listen to
     */
    public void addSampleListener(IRawSampleListener listener, int[] channels);

    /**
     * Remove listener from our notification list.  If the listener is listening to
     * multiple channels, it will be removed from all of them.
     *
     * @param listener the observer to remove
     */
    public void removeSampleListener(IRawSampleListener listener);

    /**
     * Remove all listeners from our notification list
     */
    public void removeAllSampleListeners();
}
