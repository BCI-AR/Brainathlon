package com.webkitchen.eeg.analysis;

import java.util.EventListener;

/**
 * Listens for new ratio values between two specific frequency ranges
 *
 * @author Amy Palke
 */
public interface IRatioListener extends EventListener
{
    public void receiveRatio(double ratio);
}
