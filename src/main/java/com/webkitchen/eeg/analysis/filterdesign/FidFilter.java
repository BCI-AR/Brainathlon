package com.webkitchen.eeg.analysis.filterdesign;


/**
 * Helper class used to hold values during filter design
 *
 * @author Amy Palke
 * @see FilterDesigner
 */
class FidFilter
{
    private char typ;		// Type of filter element 'I' IIR, 'F' FIR, or 0 for end of list
    private int cbm;		// Constant bitmap.  Bits 0..14, if set, indicate that val[0..14]
    //   is a constant across changes in frequency for this filter type
    //   Bit 15, if set, indicates that val[15..inf] are constant.
    private double[] val;

    FidFilter(char typ, int cbm, int len)
    {
        this.setTyp(typ);
        this.setCbm(cbm);
        this.setVal(new double[len]);
    }

    FidFilter(char typ, int len)
    {
        this(typ, 0, len);
    }

    FidFilter copy()
    {
        // Create our copy
        FidFilter copy = new FidFilter(this.getTyp(), this.getCbm(), this.getVal().length);
        // Give it a copy of our val array
        System.arraycopy(this.getVal(), 0, copy.getVal(), 0, this.getVal().length);
        return copy;
    }

    char getTyp()
    {
        return typ;
    }

    void setTyp(char typ)
    {
        this.typ = typ;
    }

    int getCbm()
    {
        return cbm;
    }

    void setCbm(int cbm)
    {
        this.cbm = cbm;
    }

    double[] getVal()
    {
        return val;
    }

    void setVal(double[] val)
    {
        this.val = val;
    }
}
