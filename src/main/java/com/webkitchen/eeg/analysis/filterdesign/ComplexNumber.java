package com.webkitchen.eeg.analysis.filterdesign;


/**
 * This class is a partial implementation of complex numbers.  It works fine for our
 * filter designing, but may not be fit for other purposes.
 *
 * @author Amy Palke
 */
class ComplexNumber
{
    private double realPart;
    private double imaginaryPart;

    /**
     * Constructs a new <code>ComplexNumber</code> with real and imaginary variables
     *
     * @param real      the real part
     * @param imaginary the imaginary part
     */
    public ComplexNumber(double real, double imaginary)
    {
        this.realPart = real;
        this.imaginaryPart = imaginary;
    }

    /**
     * Returns the real part of the complex number
     */
    public double real()
    {
        return realPart;
    }

    /**
     * Returns the imaginary part of the complex number
     */
    public double imaginary()
    {
        return imaginaryPart;
    }

    /**
     * Creates and returns a copy of this object
     *
     * @return an exact copy of this <code>ComplexNumber</code>
     */
    public ComplexNumber copy()
    {
        return new ComplexNumber(this.realPart, this.imaginaryPart);
    }

    /**
     * Returns <code>true</code> if the imaginary part is zero, <code>false</code> otherwise
     *
     * @return <code>true</code> if the imaginary part is zero, <code>false</code> otherwise
     */
    public boolean isReal()
    {
        return (imaginaryPart == 0.0) ? true : false;
    }

    /**
     * Compares this <code>ComplexNumber</code> with the specified <code>Object</code> for equality
     *
     * @param o The <code>Object</code> to compare with this <code>ComplexNumber</code>
     * @return <code>true</code> if and only if the specified <code>Object</code> is a
     *         <code>ComplexNumber</code> whose value is numerically equal to
     *         this <code>ComplexNumber</code>
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ComplexNumber)) return false;

        final ComplexNumber complexNumber = (ComplexNumber) o;

        if (imaginaryPart != complexNumber.imaginaryPart) return false;
        if (realPart != complexNumber.realPart) return false;

        return true;
    }

    /**
     * Returns the hash code for this <code>ComplexNumber</code>
     *
     * @return hash code for this <code>ComplexNumber</code>
     */
    public int hashCode()
    {
        int result;
        long temp;
        temp = Double.doubleToLongBits(realPart);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(imaginaryPart);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Returns a string representation of this <code>ComplexNumber</code>
     */
    public String toString()
    {
        return "{" + realPart + "," + imaginaryPart + "}";
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is (<code>a</code> * <code>b</code>)
     *
     * @param a A complex number
     * @param b Another complex number
     * @return The product of <code>a</code> and <code>b</code>
     */
    public static ComplexNumber cmul(ComplexNumber a, ComplexNumber b)
    {
        double r = a.realPart * b.realPart - a.imaginaryPart * b.imaginaryPart;
        double i = a.realPart * b.imaginaryPart + a.imaginaryPart * b.realPart;
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is (<code>this</code> * <code>b</code>)
     *
     * @param b Another complex number
     * @return The product of <code>this</code> and <code>b</code>
     */
    public ComplexNumber cmul(ComplexNumber b)
    {
        return ComplexNumber.cmul(this, b);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is <code>a</code> squared
     *
     * @param a A complex number
     * @return The result of <code>a</code> squared
     */
    public static ComplexNumber csqu(ComplexNumber a)
    {
        double r = a.realPart * a.realPart - a.imaginaryPart * a.imaginaryPart;
        double i = 2 * a.realPart * a.imaginaryPart;
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is <code>this</code> squared
     *
     * @return The result of <code>this</code> squared
     */
    public ComplexNumber csqu()
    {
        return ComplexNumber.csqu(this);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is (<code>a</code> * <code>b</code>)
     *
     * @param a A complex number
     * @param b A real number
     * @return The product of <code>a</code> and <code>b</code>
     */
    public static ComplexNumber cmulr(ComplexNumber a, double b)
    {
        double r = a.realPart * b;
        double i = a.imaginaryPart * b;
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is (<code>this</code> * <code>b</code>)
     *
     * @param b A real number
     * @return The product of <code>this</code> and <code>b</code>
     */
    public ComplexNumber cmulr(double b)
    {
        return ComplexNumber.cmulr(this, b);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is the complex conjugate of <code>a</code>
     *
     * @param a A complex number
     * @return The complex conjugate of <code>a</code>
     */
    public static ComplexNumber cconj(ComplexNumber a)
    {
        double r = a.realPart;
        double i = -a.imaginaryPart;
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is the complex conjugate of this <code>ComplexNumber</code>
     *
     * @return The complex conjugate of <code>this</code>
     */
    public ComplexNumber cconj()
    {
        return ComplexNumber.cconj(this);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is (<code>a</code> / <code>b</code>)
     *
     * @param a A complex number
     * @param b Another complex number
     * @return The quotient of <code>a</code> over <code>b</code>
     */
    public static ComplexNumber cdiv(ComplexNumber a, ComplexNumber b)
    {
        double fact = 1.0 / (b.realPart * b.realPart + b.imaginaryPart * b.imaginaryPart);
        double r = a.realPart * b.realPart + a.imaginaryPart * b.imaginaryPart;
        r *= fact;
        double i = -a.realPart * b.imaginaryPart + a.imaginaryPart * b.realPart;
        i *= fact;
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is (<code>this</code> / <code>b</code>)
     *
     * @param b Another complex number
     * @return The quotient of <code>this</code> over <code>b</code>
     */
    public ComplexNumber cdiv(ComplexNumber b)
    {
        return ComplexNumber.cdiv(this, b);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is the reciprocal of <code>a</code>
     *
     * @param a A complex number
     * @return The reciprocal of <code>a</code>
     */
    public static ComplexNumber crecip(ComplexNumber a)
    {
        double fact = 1.0 / (a.realPart * a.realPart + a.imaginaryPart * a.imaginaryPart);
        double r = a.realPart * fact;
        double i = a.imaginaryPart * -fact;
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is the reciprocal of this <code>ComplexNumber</code>
     *
     * @return The reciprocal of <code>this</code>
     */
    public ComplexNumber crecip()
    {
        return ComplexNumber.crecip(this);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is (<code>a</code> + <code>b</code>)
     *
     * @param a A complex number
     * @param b Another complex number
     * @return The sum of <code>a</code> and <code>b</code>
     */
    public static ComplexNumber cadd(ComplexNumber a, ComplexNumber b)
    {
        double r = a.realPart + b.realPart;
        double i = a.imaginaryPart + b.imaginaryPart;
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is (<code>this</code> + <code>b</code>)
     *
     * @param b Another complex number
     * @return The sum of <code>this</code> and <code>b</code>
     */
    public ComplexNumber cadd(ComplexNumber b)
    {
        return ComplexNumber.cadd(this, b);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is (<code>a</code> - <code>b</code>)
     *
     * @param a A complex number
     * @param b Another complex number
     * @return The difference of <code>a</code> minus <code>b</code>
     */
    public static ComplexNumber csub(ComplexNumber a, ComplexNumber b)
    {
        double r = a.realPart - b.realPart;
        double i = a.imaginaryPart - b.imaginaryPart;
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is (<code>this</code> - <code>b</code>)
     *
     * @param b Another complex number
     * @return The difference of <code>this</code> minus <code>b</code>
     */
    public ComplexNumber csub(ComplexNumber b)
    {
        return ComplexNumber.csub(this, b);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is the negation of <code>a</code>
     *
     * @param a A complex number
     * @return The negation of <code>a</code>
     */
    public static ComplexNumber cneg(ComplexNumber a)
    {
        double r = -a.realPart;
        double i = -a.imaginaryPart;
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is the negation of this <code>ComplexNumber</code>
     *
     * @return The negation of <code>this</code>
     */
    public ComplexNumber cneg()
    {
        return ComplexNumber.cneg(this);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is <code>a</code> squared
     *
     * @param a A complex number
     * @return The result of <code>a</code> squared
     */
    public static ComplexNumber csqrt(ComplexNumber a)
    {
        //	Complex square root: aa= aa^0.5
        double mag = Math.hypot(a.realPart, a.imaginaryPart);
        double r = my_sqrt((mag + a.realPart) * 0.5);
        double i = my_sqrt((mag - a.realPart) * 0.5);
        if (a.imaginaryPart < 0.0) i = -i;
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is <code>this</code> squared
     *
     * @return The result of <code>this</code> squared
     */
    public ComplexNumber csqrt()
    {
        return ComplexNumber.csqrt(this);
    }

    private static double my_sqrt(double aa)
    {
        return aa <= 0.0 ? 0.0 : Math.sqrt(aa);
    }

    /**
     * Returns sqrt(<i>realPart</i><sup>2</sup>&nbsp;+<i>imaginaryPart</i><sup>2</sup>)
     *
     * @return sqrt(<i>realPart</i><sup>2</sup>&nbsp;+<i>imaginaryPart</i><sup>2</sup>)
     */
    public double hypot()
    {
        return Math.hypot(this.realPart, this.imaginaryPart);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is the complex imaginary exponent, <i>e</i><sup>i.theta</sup>
     *
     * @param theta A real number
     * @return <i>e</i><sup>i.theta</sup>
     */
    public static ComplexNumber cexpj(double theta)
    {
        //	Complex imaginary exponent: aa= e^i.theta
        double r = Math.cos(theta);
        double i = Math.sin(theta);
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is <i>e</i><sup>a</sup>
     *
     * @param a A complex number
     * @return <i>e</i><sup>a</sup>
     */
    public static ComplexNumber cexp(ComplexNumber a)
    {
        //	Complex exponent: aa= e^aa
        double mag = Math.exp(a.realPart);
        double r = mag * Math.cos(a.imaginaryPart);
        double i = mag * Math.sin(a.imaginaryPart);
        return new ComplexNumber(r, i);
    }

    /**
     * Returns a <code>ComplexNumber</code> whose value is <i>e</i><sup>this</sup>
     *
     * @return <i>e</i><sup>this</sup>
     */
    public ComplexNumber cexp()
    {
        return ComplexNumber.cexp(this);
    }
}
