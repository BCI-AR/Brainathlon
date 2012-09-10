package com.webkitchen.eeg.analysis;

/**
 * Provides utility methods for the analysis package
 *
 * @author Amy Palke
 */
class AnalysisUtil
{
    /**
     * Don't let anyone instantiate this class.
     */
    private AnalysisUtil()
    {
    }

    /**
     * Returns the rms (Root Mean Square),
     * which is Math.sqrt( Sum( data[i] * data[i] ) / data.length)
     *
     * @param data the data array
     * @return
     */
    public static double rms(double[] data)
    {
        int sum = 0;
        int size = data.length;
        for (int i = 0; i < size; i++)
        {
            sum += (data[i] * data[i]);
        }
        return Math.sqrt(sum / size);
    }
}
