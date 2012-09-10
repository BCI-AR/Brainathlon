package com.webkitchen.brainathlon.gameComponents;

import com.webkitchen.eeg.analysis.IAmplitudeGenerator;
import com.webkitchen.eeg.analysis.IBandSampleGenerator;

/**
 * A combined interface used to create player monitors for the BandIncreaseCourse
 *
 * @author Amy Palke
 */
public interface IBandPlayerMonitor
        extends IPlayerMonitor, IBandSampleGenerator, IAmplitudeGenerator
{
}
