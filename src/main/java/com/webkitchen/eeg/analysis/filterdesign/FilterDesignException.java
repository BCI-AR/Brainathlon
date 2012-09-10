package com.webkitchen.eeg.analysis.filterdesign;


/**
 * Runtime exception thrown when the <code>FilterDesigner</code> is unable to create a new
 * <code>IIRFilter</code>, usually due to inconsistent settings in the
 * <code>FilterSpecification</code>.
 *
 * @author Amy Palke
 * @see FilterSpecification
 * @see FilterDesigner
 * @see IIRFilter
 */
public class FilterDesignException extends RuntimeException
{
    public FilterDesignException(String message)
    {
        super(message);
    }
}
