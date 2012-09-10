package com.webkitchen.brainathlon.util;

/**
 * @author Amy Palke
 */
public class MathUtil
{
    /**
     * Don't let anyone instantiate this class.
     */
    private MathUtil()
    {
    }

    /**
     * Round a double value to a specified number of decimal
     * places.
     *
     * @param val    the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return val rounded to places decimal places.
     */
    public static double round(double val, int places)
    {
        long factor = (long) Math.pow(10, places);

        // Shift the decimal the correct number of places
        // to the right.
        val = val * factor;

        // Round to the nearest integer.
        long tmp = Math.round(val);

        // Shift the decimal the correct number of places
        // back to the left.
        return (double) tmp / factor;
    }

    public static String padInt(int theInt, int length)
    {
        String s = Integer.toString(theInt);
        if (s.length() < length)
        {
            s = "0000000000".substring(0, length - s.length()) + s;
        }
        return s;
    }

    /**
     * Returns true if abs(d1 - d2) <= delta
     *
     * @param d1    a value to compare
     * @param d2    another value to compare
     * @param delta the tolerated delta
     * @return true if the difference between d1 and d2 is less than delta
     */
    public static boolean nearlyEqual(double d1, double d2, double delta)
    {
        return (Math.abs(d1 - d2) <= delta) ? true : false;
    }
}
