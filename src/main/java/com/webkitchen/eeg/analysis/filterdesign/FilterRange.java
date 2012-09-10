package com.webkitchen.eeg.analysis.filterdesign;

import java.util.List;


/**
 * Specifies the range type used to design a filter, such as Bandpass, Lowpass, Highpass
 * or Bandstop.  Provides specific raw pole adjustment strategies for the <code>FilterDesigner</code>
 * to create an <code>IIRFilter</code>.  Specified in <code>FilterSpecification</code>.
 *
 * @author Amy Palke
 * @see FilterSpecification
 * @see FilterDesigner
 * @see IIRFilter
 */
public abstract class FilterRange
{
    /**
     * Bandpass filter -  passes all frequencies in a given range and
     * suppresses all frequencies not within the range
     */
    public static final FilterRange BANDPASS = new Bandpass();
    /**
     * Lowpass filter - passes all frequencies below a specified frequency
     */
    public static final FilterRange LOWPASS = new Lowpass();
    /**
     * Highpass filter - passes all frequencies above a specified frequency
     */
    public static final FilterRange HIGHPASS = new Highpass();
    /**
     * Bandstop filter - suppresses a given range of frequencies,
     * transmitting only those above and below that band
     */
    public static final FilterRange BANDSTOP = new Bandstop();
    private static final double TWOPI = (2 * Math.PI);
    private final String name;

    FilterRange(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return this.name;
    }

    /**
     * Pre-warp a frequency
     *
     * @param val the original frequency
     * @return the pre-warped frequency
     */
    private static double prewarp(double val)
    {
        return Math.tan(val * Math.PI) / Math.PI;
    }


    abstract void adjustRawPoles(FilterDesignScratchpad design);

    abstract void setGainAndCbm(FilterDesignScratchpad design);

    /**
     * Overall filter gain is adjusted to give the peak at 1.0.  This
     * is easy for all types except for band-pass, where a search is
     * required to find the precise peak.  This is much slower than
     * the other types.
     *
     * @param design our scratchpad with pole values and filter specs
     * @param list   our list of FidFilters generated
     * @return the peak frequency at 1.0
     */
    abstract double getPeakFrequency(FilterDesignScratchpad design, List<FidFilter> list, FilterDesigner designer);


    private static class Bandpass extends FilterRange
    {
        private Bandpass()
        {
            super("Bandpass");
        }

        /**
         * Adjust raw poles to BP filter.  The number of poles is doubled.
         */
        void adjustRawPoles(FilterDesignScratchpad design)
        {
            double freq1 = prewarp(design.getFrequency0());
            double freq2 = prewarp(design.getFrequency1());

            ComplexNumber[] pol = design.getPol();
            int polLength = pol.length;
            double w0 = TWOPI * Math.sqrt(freq1 * freq2);
            double bw = 0.5 * TWOPI * (freq2 - freq1);

            // Run through the list backwards, expanding as we go
            // Add (n_pol % 2) to round up if n_pol is odd
            for (int a = (polLength / 2) + (polLength % 2), b = polLength; a > 0;)
            {
                // if pole is a real number
                if (pol[a - 1].isReal())
                {
                    double hba;
                    a--;
                    b--;
                    hba = pol[a].real() * bw;
                    pol[b] = new ComplexNumber(1.0 - (w0 / hba) * (w0 / hba), 0.0);
                    pol[b] = pol[b].csqrt();
                    pol[b] = pol[b].cadd(new ComplexNumber(1.0, 0.0));
                    pol[b] = pol[b].cmulr(hba);
                }
                else
                {
                    ComplexNumber hba;
                    a--;
                    b -= 2; // hop down 2 spots so we can add 2 poles
                    hba = pol[a].copy();
                    hba = hba.cmulr(bw);
                    pol[b] = hba.copy();
                    pol[b] = pol[b].crecip();
                    pol[b] = pol[b].cmulr(w0);
                    pol[b] = pol[b].csqu();
                    pol[b] = pol[b].cneg();
                    pol[b] = pol[b].cadd(new ComplexNumber(1.0, 0.0));
                    pol[b] = pol[b].csqrt();
                    pol[b] = pol[b].cmul(hba);
                    pol[b + 1] = pol[b].copy();
                    pol[b + 1] = pol[b + 1].cneg();
                    pol[b] = pol[b].cadd(hba);
                    pol[b + 1] = pol[b + 1].cadd(hba);
                }
            }

            // Add zeros
            int zerLength = polLength * 2;
            design.setN_zer(zerLength);
            double[] zer = new double[zerLength];
            char[] zertyp = new char[zerLength];
            for (int a = 0; a < zerLength; a++)
            {
                zertyp[a] = 1;
                zer[a] = (a < zerLength / 2) ? 0.0 : -FilterDesigner.INF;
            }
            design.setZer(zer);
            design.setZertyp(zertyp);
        }

        void setGainAndCbm(FilterDesignScratchpad design)
        {
            design.setGain(1.0);
            design.setCbm(~0);    // FIR is constant
        }

        double getPeakFrequency(FilterDesignScratchpad design, List<FidFilter> list, FilterDesigner designer)
        {
            return designer.search_peak(list, design.getFrequency0(), design.getFrequency1());
        }
    }


    private static class Lowpass extends FilterRange
    {
        private Lowpass()
        {
            super("Lowpass");
        }

        void adjustRawPoles(FilterDesignScratchpad design)
        {
            throw new UnsupportedOperationException("Sorry!  That's not implemented yet...");
        }

        void setGainAndCbm(FilterDesignScratchpad design)
        {
            design.setGain(1.0);
            design.setCbm(~0);    // FIR is constant
        }

        double getPeakFrequency(FilterDesignScratchpad design, List<FidFilter> list, FilterDesigner designer)
        {
            return 0.0;
        }
    }


    private static class Highpass extends FilterRange
    {
        private Highpass()
        {
            super("Highpass");
        }

        void adjustRawPoles(FilterDesignScratchpad design)
        {
            throw new UnsupportedOperationException("Sorry!  That's not implemented yet...");
        }

        void setGainAndCbm(FilterDesignScratchpad design)
        {
            design.setGain(1.0);
            design.setCbm(~0);    // FIR is constant
        }

        double getPeakFrequency(FilterDesignScratchpad design, List<FidFilter> list, FilterDesigner designer)
        {
            return 0.5;
        }
    }


    private static class Bandstop extends FilterRange
    {
        private Bandstop()
        {
            super("Bandstop");
        }

        void adjustRawPoles(FilterDesignScratchpad design)
        {
            throw new UnsupportedOperationException("Sorry!  That's not implemented yet...");
        }

        void setGainAndCbm(FilterDesignScratchpad design)
        {
            design.setGain(1.0);
            design.setCbm(5);  // FIR second coefficient is *non-const* for bandstop
        }

        double getPeakFrequency(FilterDesignScratchpad design, List<FidFilter> list, FilterDesigner designer)
        {
            return 0.0;
        }
    }
}
