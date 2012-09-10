package com.webkitchen.eeg.analysis.filterdesign;


/**
 * Helper class which holds the specification information, poles, and zeros
 *
 * @author Amy Palke
 * @see FilterDesigner
 */
class FilterDesignScratchpad
{
    private FilterAlgorithm algorithmType;
    private FilterRange rangeType;
    private int rate;
    private double frequency0;
    private double frequency1;
    private boolean autoAdjust;
    private int order;

    private int n_pol;		        // Number of poles
    private ComplexNumber[] pol;	// Pole values
    private int n_zer;		        // Number of zeros
    private double[] zer; // Zero values
    private char[] zertyp;
    private double gain;
    private int cbm;


    FilterDesignScratchpad(FilterSpecification spec)
    {
        this.algorithmType = spec.getAlgorithmType();
        this.rangeType = spec.getRangeType();
        this.rate = spec.getRate();
        this.frequency0 = spec.getFrequency0();
        this.frequency1 = spec.getFrequency1();
        this.autoAdjust = spec.isAutoAdjust();
        this.order = spec.getOrder();
    }

    FilterAlgorithm getAlgorithmType()
    {
        return algorithmType;
    }

    void setAlgorithmType(FilterAlgorithm algorithmType)
    {
        this.algorithmType = algorithmType;
    }

    FilterRange getRangeType()
    {
        return rangeType;
    }

    void setRangeType(FilterRange rangeType)
    {
        this.rangeType = rangeType;
    }

    int getRate()
    {
        return rate;
    }

    void setRate(int rate)
    {
        this.rate = rate;
    }

    double getFrequency0()
    {
        return frequency0;
    }

    void setFrequency0(double frequency0)
    {
        this.frequency0 = frequency0;
    }

    double getFrequency1()
    {
        return frequency1;
    }

    void setFrequency1(double frequency1)
    {
        this.frequency1 = frequency1;
    }

    boolean isAutoAdjust()
    {
        return autoAdjust;
    }

    void setAutoAdjust(boolean autoAdjust)
    {
        this.autoAdjust = autoAdjust;
    }

    int getOrder()
    {
        return order;
    }

    void setOrder(int order)
    {
        this.order = order;
    }

    int getN_pol()
    {
        return n_pol;
    }

    void setN_pol(int n_pol)
    {
        this.n_pol = n_pol;
    }

    ComplexNumber[] getPol()
    {
        return pol;
    }

    void setPol(ComplexNumber[] pol)
    {
        this.pol = pol;
    }

    int getN_zer()
    {
        return n_zer;
    }

    void setN_zer(int n_zer)
    {
        this.n_zer = n_zer;
    }

    double[] getZer()
    {
        return zer;
    }

    void setZer(double[] zer)
    {
        this.zer = zer;
    }

    char[] getZertyp()
    {
        return zertyp;
    }

    void setZertyp(char[] zertyp)
    {
        this.zertyp = zertyp;
    }

    double getGain()
    {
        return gain;
    }

    void setGain(double gain)
    {
        this.gain = gain;
    }

    int getCbm()
    {
        return cbm;
    }

    void setCbm(int cbm)
    {
        this.cbm = cbm;
    }
}
