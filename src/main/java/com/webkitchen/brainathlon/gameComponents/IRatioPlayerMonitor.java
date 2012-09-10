package com.webkitchen.brainathlon.gameComponents;

import com.webkitchen.eeg.analysis.IDualBandSampleGenerator;
import com.webkitchen.eeg.analysis.IRatioGenerator;

/**
 * A combined interface used to create player monitors for the DualBandRatioCourse
 *
 * @author Amy Palke
 */
public interface IRatioPlayerMonitor
        extends IPlayerMonitor, IRatioGenerator, IDualBandSampleGenerator
{
}
