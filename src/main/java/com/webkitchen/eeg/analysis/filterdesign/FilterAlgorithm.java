package com.webkitchen.eeg.analysis.filterdesign;


/**
 * Specifies the algorithm/function used to design a filter, such as Butterworth, Bessel,
 * or Chebyshev.  Provides specific pole generation strategies for the <code>FilterDesigner</code>
 * to create an <code>IIRFilter</code>.  Specified in <code>FilterSpecification</code>.
 *
 * @author Amy Palke
 * @see FilterSpecification
 * @see FilterDesigner
 * @see IIRFilter
 */
public abstract class FilterAlgorithm
{
    /**
     * The Butterworth filter generation algorithm
     */
    public static final FilterAlgorithm BUTTERWORTH = new Butterworth();
    /**
     * The Bessel filter generation algorithm
     */
    public static final FilterAlgorithm BESSEL = new Bessel();
    /**
     * The Chebyshev filter generation algorithm
     */
    public static final FilterAlgorithm CHEBYSHEV = new Chebyshev();
    private final String name;

    FilterAlgorithm(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return this.name;
    }

    abstract void generatePoles(FilterDesignScratchpad design);


    private static class Butterworth extends FilterAlgorithm
    {
        private Butterworth()
        {
            super("Butterworth");
        }

        /**
         * Generate Butterworth poles for the given order.  These are
         * regularly-spaced points on the unit circle to the left of the
         * real==0 line.
         */
        void generatePoles(FilterDesignScratchpad design)
        {
            int order = design.getOrder();
            // we'll add order/2 ComplexNumbers now, and more later according to filter type
            ComplexNumber[] pol = new ComplexNumber[order];
            int a;
            for (a = 0; a < (order / 2); a++)
            {
                pol[a] = ComplexNumber.cexpj(Math.PI - (order - (a * 2) - 1) * 0.5 * Math.PI / order);
            }
            // Handle odd order
            if (a < order)
            {
                pol[a] = new ComplexNumber(-1.0, 0); // a real number
            }
            design.setN_pol(order);
            design.setPol(pol);
        }
    }


    private static class Bessel extends FilterAlgorithm
    {
        private Bessel()
        {
            super("Bessel");
        }

        void generatePoles(FilterDesignScratchpad design)
        {
            throw new UnsupportedOperationException("Sorry!  That's not implemented yet...");
        }
    }


    private static class Chebyshev extends FilterAlgorithm
    {
        private Chebyshev()
        {
            super("Chebyshev");
        }

        void generatePoles(FilterDesignScratchpad design)
        {
            throw new UnsupportedOperationException("Sorry!  That's not implemented yet...");
        }
    }

}
