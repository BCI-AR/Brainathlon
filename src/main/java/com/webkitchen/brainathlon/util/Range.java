package com.webkitchen.brainathlon.util;

/**
 * @author Amy Palke
 */
public class Range
{
    private int minValue;
    private int maxValue;

    public Range(int minValue, int maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public boolean contains(int value)
    {
        return (minValue <= value && value <= maxValue);
    }

    public int getMaxValue()
    {
        return maxValue;
    }

    public int getMinValue()
    {
        return minValue;
    }

    public int getDifference()
    {
        return maxValue - minValue;
    }

}
