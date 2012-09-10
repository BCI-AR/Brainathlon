package com.webkitchen.eeg.analysis.filterdesign;


/**
 * Processes digital signal input to filter for specific frequencies.
 * Frequencies, filter type and filtering algorithm are all specified in our
 * <code>FilterSpecification</code>.
 *
 * @author Amy Palke
 * @see FilterSpecification
 */
public class IIRFilter
{
    // coef array is package-accessible for testing - could make private if TestFilterDesigner is removed
    double[] coef;
    private double[] buf;
    private int bufferSize;
    private int order;
    private boolean oddOrder;

    /**
     * Creates a new IIRFilter that be used to filter raw input.
     * For valid results, the coefficients should have been designed by the
     * <code>FilterSpecification</code>,  but we don't check validity here,
     * that is the responsibility of the caller.
     * The <code>FilterSpecification</code>, and coefficients are used to filter for our
     * specific frequencies.
     *
     * @param spec the specification that defines our filter's properties
     * @param coef the coefficients that will be used to process input
     */
    public IIRFilter(FilterSpecification spec, double[] coef)
    {
        this.order = spec.getOrder();
        this.bufferSize = order * 2;
        this.buf = new double[bufferSize];
        this.coef = coef;
        this.oddOrder = (order % 2) == 0 ? false : true;
    }

    /**
     * Processes the raw digital signal input to filter for our specific frequencies
     *
     * @param val the raw sample value
     * @return the filtered value
     */
    public double process(double val)
    {
        double tmp, fir, iir;
        tmp = buf[0]; // save current [0] as arraycopy below overwrites it
        // scoot everything to left by 1 position
        System.arraycopy(buf, 1, buf, 0, bufferSize - 1);

        val *= coef[0];
        for (int i = 1, length = buf.length, midpoint = order; i <= length; i += 2)
        {
            iir = val - (coef[i] * tmp) - (coef[i + 1] * buf[i - 1]);
            if (i < midpoint)
            {
                fir = -buf[i - 1] - buf[i - 1] + tmp + iir;
            }
            else if (i == midpoint && oddOrder)
            {
                fir = -tmp + iir;
            }
            else
            {
                fir = buf[i - 1] + buf[i - 1] + tmp + iir;
            }
            tmp = buf[i];
            buf[i] = iir;
            val = fir;
        }
        return val;
    }
}
