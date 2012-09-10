package com.webkitchen.eeg.analysis.filterdesign;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Creates <code>IIRFilter</code>s designed according to the algorithm
 * and type provided in <code>FilterSpecification</code> objects.
 * <p/>
 * To create a new <code>IIRFilter</code>, first create a <code>FilterSpecification</code>
 * object which defines the filter specification.  Then pass the <code>FilterSpecification</code>
 * to the <code>FilterDesigner</code>'s <code>createFilter</code> method.
 * <p/>
 * This is based on Jim Peters' "Fidlib" digital filter designer code, which is
 * based, in part, on Tony Fisher's "mkfilter" package.
 * <p/>
 * Original source code and information on Jim Peters' Fidlib can be found at:<br>
 * <a href="http://uazu.net/fiview/">http://uazu.net/fiview/</a>
 * <p/>
 * Original source code and information on Tony Fisher's mkfilter can be found at:<br>
 * <a href="http://www-users.cs.york.ac.uk/~fisher/mkfilter/">http://www-users.cs.york.ac.uk/~fisher/mkfilter/</a>
 *
 * @author Amy Palke
 * @see FilterSpecification
 * @see FilterAlgorithm
 * @see FilterRange
 * @see IIRFilter
 */
public class FilterDesigner
{
    static final double INF = -1.0;


    /**
     * Returns a new <code>IIRFilter</code> designed according to the
     * specification provided in the <code>FilterSpecification</code> object
     *
     * @param spec the filter specifications
     * @return a new IIRFilter
     * @throws FilterDesignException if any error occurs during the filter design
     *                               - Errors will likely be due to invalid FilterSpecification settings
     */
    public IIRFilter createFilter(FilterSpecification spec)
    {
        double[] coefficients = createCoefficients(spec);
        return new IIRFilter(spec, coefficients);
    }

    /**
     * Returns an array of filter coefficients.  This can be used to create
     * <code>IIRFilter</code>s in an efficient manner.
     * Create an array of coefficients, then create multiple <code>IIRFilter</code>s
     * by passing in copies of the coefficients and the <code>FilterSpecification</code>
     *
     * @param spec the filter specifications
     * @return an array of coefficients for an <code>IIRFilter</code>
     * @throws FilterDesignException if any error occurs during the filter design
     *                               - Errors will likely be due to invalid FilterSpecification settings
     */
    public double[] createCoefficients(FilterSpecification spec)
    {
        FilterDesignScratchpad design = new FilterDesignScratchpad(spec);
        double[] coefficients = fid_design_coef(design);
        return coefficients;
    }

    /**
     * Design a filter and reduce it to a list of all the non-const
     * coefficients.
     * <p/>
     * Note that all 1-element FIRs and IIR first-coefficients are
     * merged into a single gain coefficient, which is returned
     * rather than being included in the coefficient list.  This is
     * to allow it to be merged with other gains within a stack of
     * filters.
     *
     * @param design the scratchpad object that contains our specification
     * @return an array of all the non-const coefficients
     * @throws FilterDesignException
     */
    private double[] fid_design_coef(FilterDesignScratchpad design)
    {
        int n_coef = design.getOrder() * 2;
        double[] coef = new double[n_coef + 1];

        // Generate the FidFilters according to our filter specifications
        List<FidFilter> filt = fid_design(design);

        int cnt = 0;
        double gain = 1.0;
        double[] iir = new double[1];
        double[] fir = new double[1];
        double iir_adj = 0;
        int coefIndex = 0;

        for (Iterator itr = filt.iterator(); itr.hasNext();)
        {
            FidFilter ff = (FidFilter) itr.next();
            if (ff.getTyp() == 'F' && ff.getVal().length == 1)
            {
                gain *= ff.getVal()[0];
                continue;
            }

            if (ff.getTyp() != 'I' && ff.getTyp() != 'F')
            {
                throw new FilterDesignException("fid_design_coef can't handle FidFilter type: " + ff.getTyp());
            }
            // Initialise to safe defaults
            int n_iir = 1;
            int n_fir = 1;
            int iir_cbm = ~0;
            int fir_cbm = ~0;

            // See if we have an IIR filter
            if (ff.getTyp() == 'I')
            {
                iir = ff.getVal();
                n_iir = ff.getVal().length;
                iir_cbm = ff.getCbm();
                iir_adj = 1.0 / ff.getVal()[0];
                // step to next FidFilter
                if (itr.hasNext())
                {
                    ff = (FidFilter) itr.next();
                }
                else
                {
                    ff = null;
                }
                gain *= iir_adj;
            }

            // See if we have an FIR filter
            if (ff != null && ff.getTyp() == 'F')
            {
                fir = ff.getVal();
                n_fir = ff.getVal().length;
                fir_cbm = ff.getCbm();
            }

            // Dump out all non-const coefficients in reverse order
            // max of fir or iir
            int len = n_fir > n_iir ? n_fir : n_iir;
            for (int a = len - 1; a >= 0; a--)
            {
                // Output IIR if present and non-const
                if (a < n_iir && a > 0 && ((iir_cbm & (1 << (a < 15 ? a : 15))) == 0))
                {
                    if (cnt++ < n_coef)
                    {
                        coef[coefIndex] = iir_adj * iir[a];
                        coefIndex++;
                    }
                }

                // Output FIR if present and non-const
                if (a < n_fir && ((fir_cbm & (1 << (a < 15 ? a : 15))) == 0))
                {
                    if (cnt++ < n_coef)
                    {
                        coef[coefIndex] = fir[a];
                        coefIndex++;
                    }
                }
            }
        }

        if (cnt != n_coef)
        {
            throw new FilterDesignException("fid_design_coef called with the wrong number of coefficients.\n "
                                            + "  Given " + n_coef + " expecting: "
                                            + cnt + " " + design);
        }
        // scoot everything to right by 1 position
        System.arraycopy(coef, 0, coef, 1, coef.length - 1);
        // set coef[0] to 1*gain
        coef[0] = gain;
        return coef;
    }


    /**
     * Creates a list of FidFilters
     *
     * @param design the scratchpad object that contains our specification and working numbers
     * @return a list of the FidFilters created
     * @throws FilterDesignException if the frequency is out of range for our sampling rate
     */
    private List<FidFilter> fid_design(FilterDesignScratchpad design)
    {
        // Adjust frequencies to range 0-0.5, and check them
        design.setFrequency0(design.getFrequency0() / design.getRate());
        if (design.getFrequency0() > 0.5)
        {
            throw new FilterDesignException("Frequency of " + design.getFrequency0() * design.getRate()
                                            + "out of range with sampling rate of: " + design.getRate());
        }
        design.setFrequency1(design.getFrequency1() / design.getRate());
        if (design.getFrequency1() > 0.5)
        {
            throw new FilterDesignException("Frequency of " + design.getFrequency1() * design.getRate()
                                            + "out of range with sampling rate of: " + design.getRate());
        }
        // Generate the filter
        List<FidFilter> ff = new ArrayList<FidFilter>();

        if (!design.isAutoAdjust())
        {
            ff = generateFidFilterList(design);
        }
        else
        {
// TODO: Make auto-adjust work for lowpass & highpass
            ff = auto_adjust_dual(design);
        }
//        else if (strstr(filter[sp.fi].fmt, "#R"))
//           rv= auto_adjust_dual(&sp, rate, f0, f1);
//        else
//           rv= auto_adjust_single(&sp, rate, f0);
        return ff;
    }

    /**
     * Handle the different 'back-ends' for Bessel, Butterworth and
     * Chebyshev filters, and the different ranges of bandpass, highpass,
     * lowpass and bandstop
     *
     * @param design our scratchpad with pole values and filter specs
     * @return list of generated FidFilters
     * @throws FilterDesignException
     */
    private List<FidFilter> generateFidFilterList(FilterDesignScratchpad design)
    {
        design.getAlgorithmType().generatePoles(design);
        FilterRange rangeType = design.getRangeType();
        rangeType.adjustRawPoles(design);
        s2z_bilinear(design);
        rangeType.setGainAndCbm(design);
        List<FidFilter> rv = z2fidfilter(design);
        double peakFrequency = rangeType.getPeakFrequency(design, rv, this);
        double response = fid_response(rv, peakFrequency);
        ((FidFilter) (rv.get(0))).getVal()[0] = 1.0 / response;

        return rv;
    }


//
//	Convert list of poles+zeros from S to Z using bilinear
//	transform
//
    private void s2z_bilinear(FilterDesignScratchpad design)
    {
        ComplexNumber[] pol = design.getPol();
        for (int a = 0, length = pol.length; a < length;)
        {
            // Calculate (2 + val) / (2 - val)
            if (pol[a].isReal())
            {
                if (pol[a].real() == -INF)
                {
                    pol[a] = new ComplexNumber(-1.0, 0);
                }
                else
                {
                    pol[a] = new ComplexNumber((2 + pol[a].real()) / (2 - pol[a].real()), 0);
                }
                a++;
            }
            else
            {
                ComplexNumber val = pol[a].copy();
                val = val.cneg();
                val = val.cadd(new ComplexNumber(2, 0));
                pol[a] = pol[a].cadd(new ComplexNumber(2, 0));
                pol[a] = pol[a].cdiv(val);
                a++;
            }
        }

        double[] zer = design.getZer();
        char[] zertyp = design.getZertyp();
        for (int a = 0, length = zer.length; a < length;)
        {
            // Calculate (2 + val) / (2 - val)
            if (zertyp[a] == 1)
            {
                if (zer[a] == -INF)
                {
                    zer[a] = -1.0;
                }
                else
                {
                    zer[a] = (2 + zer[a]) / (2 - zer[a]);
                }
                a++;
            }
// I'll need to add this if I add bandstop (zer is ComplexNumber[])
//            else
//            {
//                double val[2];
//                cass(val, zer + a);
//                cneg(val);
//                caddz(val, 2, 0);
//                caddz(zer + a, 2, 0);
//                cdiv(zer + a, val);
//                a += 2;
//            }
        }
    }


    /**
     * Generate a FidFilter for the current set of poles and zeros.
     * The given gain is inserted at the start of the FidFilter as a
     * one-coefficient FIR filter.  This is positioned to be easily
     * adjusted later to correct the filter gain.
     *
     * @param design the scratchpad object that contains our specification and working numbers
     * @return a list of the FidFilters created
     */
    private List<FidFilter> z2fidfilter(FilterDesignScratchpad design)
    {
        // Worst case: gain + 2-element IIR/FIR
        //   for each pole/zero
        int a;
        List<FidFilter> rv;
        FidFilter ff;
        rv = new ArrayList<FidFilter>();
        ff = new FidFilter('F', 1);
        ff.getVal()[0] = design.getGain();
        rv.add(ff);

        // Output as much as possible as 2x2 IIR/FIR filters
        for (a = 0; a < design.getN_pol() && a < design.getN_zer(); a++)
        {
            // Look for a pair of values for an IIR
            if (design.getPol()[a].isReal() && design.getPol()[a + 1].isReal())
            {
                // Two real values
                ff = new FidFilter('I', 3);
                ff.getVal()[0] = 1;
                ff.getVal()[1] = -(design.getPol()[a].real() + design.getPol()[a + 1].real());
                ff.getVal()[2] = design.getPol()[a].real() * design.getPol()[a + 1].real();
                rv.add(ff);
            }
            else
            {
                // A complex value and its conjugate pair
                ff = new FidFilter('I', 3);
                ff.getVal()[0] = 1;
                ff.getVal()[1] = -2 * design.getPol()[a].real();
                ff.getVal()[2] = design.getPol()[a].real() * design.getPol()[a].real() + design.getPol()[a].imaginary() * design.getPol()[a].imaginary();
                rv.add(ff);
            }
            // Look for a pair of values for an FIR
            int zerIndex = a * 2;
            if (design.getZertyp()[zerIndex] == 1 && design.getZertyp()[zerIndex + 1] == 1)
            {
                // Two real values
                // Skip if constant and 0/0
                if ((design.getCbm() != 0) || design.getZer()[zerIndex] != 0.0 || design.getZer()[zerIndex + 1] != 0.0)
                {
                    ff = new FidFilter('F', design.getCbm(), 3);
                    ff.getVal()[0] = 1;
                    ff.getVal()[1] = -(design.getZer()[zerIndex] + design.getZer()[zerIndex + 1]);
                    ff.getVal()[2] = design.getZer()[zerIndex] * design.getZer()[zerIndex + 1];
                    rv.add(ff);
                }
            }
            else if (design.getZertyp()[a] == 2)
            {
                // A complex value and its conjugate pair
                // Skip if constant and 0/0
                if ((design.getCbm() != 0) || design.getZer()[a] != 0.0 || design.getZer()[a + 1] != 0.0)
                {
                    ff = new FidFilter('F', design.getCbm(), 3);
                    ff.getVal()[0] = 1;
                    ff.getVal()[1] = -2 * design.getZer()[a];
                    ff.getVal()[2] = design.getZer()[a] * design.getZer()[a] + design.getZer()[a + 1] * design.getZer()[a + 1];
                    rv.add(ff);
                }
            }
            else
            {
                throw new FilterDesignException("Internal error -- bad zertyp[] values");
            }
        }

        // Clear up any remaining bits and pieces.  Should only be a 1x1
        // IIR/FIR.
        if (design.getN_pol() - a == 0 && design.getN_zer() - (a * 2) == 0)
        {
            ;
        }
        // Handle odd number of poles case
        else if (design.getN_pol() - a == 1 && design.getN_zer() - (a * 2) == 1)
        {
            if (design.getZertyp()[a] != 1)
            {
                throw new FilterDesignException("Internal error; bad poltyp or zertyp for final pole/zero");
            }
            ff = new FidFilter('F', 2);
            ff.getVal()[0] = 1;
            ff.getVal()[1] = -(design.getPol()[a].real());
            rv.add(ff);

            // Skip FIR if it is constant and zero
            if (design.getCbm() != 0 || design.getZer()[a] != 0.0)
            {
                ff = new FidFilter('F', design.getCbm(), 2);
                ff.getVal()[0] = 1;
                ff.getVal()[1] = -(design.getZer()[a]);
                rv.add(ff);
            }
        }
        else
        {
            throw new FilterDesignException("Internal error: unexpected poles/zeros at end of list");
        }
        return rv;
    }


//
//	Get the response of a filter at the given frequency (expressed
//	as a proportion of the sampling rate, 0->0.5).
    private double fid_response(List<FidFilter> filterList, double freq)
    {
        double theta = freq * 2 * Math.PI;
        ComplexNumber top = new ComplexNumber(1, 0);
        ComplexNumber bot = new ComplexNumber(1, 0);
        ComplexNumber zz = new ComplexNumber(Math.cos(theta), Math.sin(theta));

        for (Iterator i = filterList.iterator(); i.hasNext();)
        {
            FidFilter filt = (FidFilter) i.next();
            int cnt = filt.getVal().length;
            ComplexNumber resp = evaluate(filt.getVal(), cnt, zz);
            if (filt.getTyp() == 'I')
            {
                bot = bot.cmul(resp);
            }
            else if (filt.getTyp() == 'F')
            {
                top = top.cmul(resp);
            }
            else
            {
                throw new RuntimeException("Unknown filter type in fid_response():" + filt.getTyp());
            }
        }

        top = top.cdiv(bot);

        return top.hypot();
    }

//
//      Evaluate a complex polynomial given the coefficients.
//      rv[0]+i*rv[1] is the result, in[0]+i*in[1] is the input value.
//      Coefficients are real values.
//
    private ComplexNumber evaluate(double[] coef, int n_coef, ComplexNumber in)
    {
        ComplexNumber pz;        // Powers of Z
        int coefIndex = 0;

        // Handle first iteration by hand
        ComplexNumber rv = new ComplexNumber(coef[coefIndex++], 0);
        ComplexNumber c;

        if (--n_coef > 0)
        {
            // Handle second iteration by hand
            pz = in.copy();
            c = pz.cmulr(coef[coefIndex++]);
            rv = rv.cadd(c);
            n_coef--;

            // Loop for remainder
            while (n_coef > 0)
            {
                pz = pz.cmul(in);
                c = pz.cmulr(coef[coefIndex++]);
                rv = rv.cadd(c);
                n_coef--;
            }
        }
        return rv;
    }

//
//	Search for a peak between two given frequencies.  It is
//	assumed that the gradient goes upwards from 'f0' to the peak,
//	and then down again to 'f3'.  If there are any other curves,
//	this routine will get confused and will come up with some
//	frequency, although probably not the right one.
//
//	Returns the frequency of the peak.
//
    double search_peak(List<FidFilter> ff, double f0, double f3)
    {
        double f1, f2;
        double r1, r2;

        // Binary search, modified, taking two intermediate points.  Do 20
        // subdivisions, which should give 1/2^20 == 1e-6 accuracy compared
        // to original range.
        for (int a = 0; a < 20; a++)
        {
            f1 = 0.51 * f0 + 0.49 * f3;
            f2 = 0.49 * f0 + 0.51 * f3;
            if (f1 == f2) break;		// We're hitting FP limit
            r1 = fid_response(ff, f1);
            r2 = fid_response(ff, f2);
            if (r1 > r2)	// Peak is either to the left, or between f1/f2
            {
                f3 = f2;
            }
            else	 	// Peak is either to the right, or between f1/f2
            {
                f0 = f1;
            }
        }
        return (f0 + f3) * 0.5;
    }


    /**
     * Auto-adjust input frequencies to give response of 50% correct
     * to 6sf at the given frequency-points
     *
     * @param design
     * @return a list of FidFilters that have been adjusted
     * @throws FilterDesignException if the adjustments aren't converging after 1000 tries
     */
    private List<FidFilter> auto_adjust_dual(FilterDesignScratchpad design)
    {
        AutoAdjuster autoAdjuster = new AutoAdjuster();
        return autoAdjuster.adjust(design);
    }


    private class AutoAdjuster
    {
        double mid;
        double width;
        double errorAmount = 0.000000499;
        double originalFrequency0;
        double originalFrequency1;
        private double response0;
        private double response1;
        private double err0;
        private double err1;
        private double combinedErr;
        private List<FidFilter> rv;

        public List<FidFilter> adjust(FilterDesignScratchpad design)
        {
            mid = 0.5 * (design.getFrequency0() + design.getFrequency1());
            width = 0.5 * Math.abs(design.getFrequency1() - design.getFrequency0());
            errorAmount = 0.000000499;
            originalFrequency0 = design.getFrequency0();
            originalFrequency1 = design.getFrequency1();

            design.setFrequency0(mid - width);
            design.setFrequency1(mid + width);
            rv = generateFidFilterList(design);
            boolean bpass = (fid_response(rv, 0) < 0.5);
            double delta = width * 0.5;

            // Try delta changes until we get there
            for (int cnt = 0; true; cnt++, delta *= 0.51)
            {
                design.setFrequency0(mid - width);
                design.setFrequency1(mid + width);
                rv = generateFidFilterList(design);
                response0 = fid_response(rv, originalFrequency0);
                response1 = fid_response(rv, originalFrequency1);
                err0 = Math.abs(0.5 - response0);
                err1 = Math.abs(0.5 - response1);
                combinedErr = err0 + err1;

                double mid0 = mid;
                double width0 = width;
                double mid1 = mid + (((response0 > response1) == bpass) ? delta : -delta);
                double width1 = width + (((response0 + response1 < 1.0) == bpass) ? delta : -delta);

                if (mid0 - width1 > 0.0 && mid0 + width1 < 0.5)
                {
                    if (adjustAndTest(design, mid0, width1))
                    {
                        break;
                    }
                }
                if (mid1 - width0 > 0.0 && mid1 + width0 < 0.5)
                {
                    if (adjustAndTest(design, mid1, width0))
                    {
                        break;
                    }
                }
                if (mid1 - width1 > 0.0 && mid1 + width1 < 0.5)
                {
                    if (adjustAndTest(design, mid1, width1))
                    {
                        break;
                    }
                }
                if (cnt > 1000)
                {
                    throw new FilterDesignException("auto_adjust_dual -- design not converging");
                }
            }
            return rv;
        }

        private boolean adjustAndTest(FilterDesignScratchpad design, double testMid, double testWidth)
        {
            boolean isDesignWithinErrorAmount = false;
            design.setFrequency0(testMid - testWidth);
            design.setFrequency1(testMid + testWidth);
            rv = generateFidFilterList(design);
            response0 = fid_response(rv, originalFrequency0);
            response1 = fid_response(rv, originalFrequency1);
            err0 = Math.abs(0.5 - response0);
            err1 = Math.abs(0.5 - response1);
            if (err0 < errorAmount && err1 < errorAmount)
            {
                isDesignWithinErrorAmount = true;
            }
            else if ((err0 + err1) < combinedErr)
            {
                combinedErr = err0 + err1;
                this.mid = testMid;
                this.width = testWidth;
            }
            return isDesignWithinErrorAmount;
        }
    }
}