package com.webkitchen.eeg.analysis.filterdesign;


/**
 * Contains the filter specification information required for
 * the <code>FilterDesigner</code> to create an <code>IIRFilter</code>.
 *
 * @author Amy Palke
 * @see FilterAlgorithm
 * @see FilterRange
 * @see FilterDesigner
 * @see IIRFilter
 */
public class FilterSpecification
{
    private String description;
    private FilterAlgorithm algorithmType;
    private FilterRange rangeType;
    private int rate;
    private double frequency0;
    private double frequency1;
    private boolean autoAdjust;
    private int order;
    // A spec can be marked as non-editable to freeze it
    private boolean editable = true;

    /**
     * Sets the editable state of this FilterSpecification
     *
     * @param editable true if this FilterSpecification is editable, false if not
     */
    public void setEditable(boolean editable)
    {
        if (editable)
        {
            this.editable = editable;
        }
    }

    /**
     * Gets the editable state of this FilterSpecification
     *
     * @return true if this FilterSpecification is editable, false if not
     */
    public boolean isEditable()
    {
        return editable;
    }

    /**
     * Gets the text description of the filter
     *
     * @return the text description of the filter
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the text description of the filter
     *
     * @param description the text description of the filter
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Gets the algorithm/function type used to design the filter
     *
     * @return the algorithm/function type used to design the filter
     */
    public FilterAlgorithm getAlgorithmType()
    {
        return algorithmType;
    }

    /**
     * Sets the algorithm/function type to use when designing the filter
     *
     * @param algorithmType the algorithm/function type to use
     */
    public void setAlgorithmType(FilterAlgorithm algorithmType)
    {
        if (editable)
        {
            this.algorithmType = algorithmType;
        }
    }

    /**
     * Gets the filter range type used to design the filter
     *
     * @return the filter range type
     */
    public FilterRange getRangeType()
    {
        return rangeType;
    }

    /**
     * Sets the filter range type to use when designing the filter
     *
     * @param rangeType the filter range type
     */
    public void setRangeType(FilterRange rangeType)
    {
        if (editable)
        {
            this.rangeType = rangeType;
        }
    }

    /**
     * Gets the sample rate for the filter
     *
     * @return the sample rate
     */
    public int getRate()
    {
        return rate;
    }

    /**
     * Sets the sample rate for the filter
     *
     * @param rate the sample rate
     */
    public void setRate(int rate)
    {
        if (editable)
        {
            this.rate = rate;
        }
    }

    /**
     * Gets the low end of our frequency range
     *
     * @return the low end of our frequency range
     */
    public double getFrequency0()
    {
        return frequency0;
    }

    /**
     * Sets the low end of our frequency range
     *
     * @param frequency0 the low end of our frequency range
     */
    public void setFrequency0(double frequency0)
    {
        if (editable)
        {
            this.frequency0 = frequency0;
        }
    }

    /**
     * Gets the high end of our frequency range
     *
     * @return the high end of our frequency range
     */
    public double getFrequency1()
    {
        return frequency1;
    }

    /**
     * Sets the high end of our frequency range
     *
     * @param frequency1 the high end of our frequency range
     */
    public void setFrequency1(double frequency1)
    {
        if (editable)
        {
            this.frequency1 = frequency1;
        }
    }

    /**
     * Gets the auto-adjust attribute used to design the filter.  If this is true,
     * the <code>FilterDesigner</code> will attempt to adjust input frequencies to
     * give response of 50% correct to 6sf at the given frequency-points
     *
     * @return true if the filter will be auto-adjusted, false if not
     */
    public boolean isAutoAdjust()
    {
        return autoAdjust;
    }

    /**
     * Sets the auto-adjust attribute used to design the filter.  Setting this to
     * true will instruct the <code>FilterDesigner</code> to attempt to adjust
     * input frequencies to give response of 50% correct to 6sf at the given frequency-points
     *
     * @param autoAdjust true if the filter should be auto-adjusted, false if not
     */
    public void setAutoAdjust(boolean autoAdjust)
    {
        if (editable)
        {
            this.autoAdjust = autoAdjust;
        }
    }

    /**
     * Gets the order of the filter - the number of previous input or output values required
     * to calculate an output
     *
     * @return the order of the filter
     */
    public int getOrder()
    {
        return order;
    }

    /**
     * Sets the order of the filter - the number of previous input or output values required
     * to calculate an output
     *
     * @param order the order of the filter
     */
    public void setOrder(int order)
    {
        if (editable)
        {
            this.order = order;
        }
    }

    /**
     * Returns an exact copy of this FilterSpecification
     *
     * @return an exact copy of this FilterSpecification
     */
    public FilterSpecification copy()
    {
        FilterSpecification theCopy = new FilterSpecification();
        theCopy.algorithmType = algorithmType;
        theCopy.autoAdjust = autoAdjust;
        theCopy.frequency0 = frequency0;
        theCopy.frequency1 = frequency1;
        theCopy.order = order;
        theCopy.rangeType = rangeType;
        theCopy.rate = rate;
        return theCopy;
    }
}
