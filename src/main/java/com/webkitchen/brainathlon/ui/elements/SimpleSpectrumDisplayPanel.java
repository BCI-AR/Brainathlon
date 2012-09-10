package com.webkitchen.brainathlon.ui.elements;

import com.webkitchen.eeg.analysis.BandMonitor;
import com.webkitchen.eeg.analysis.IAmplitudeListener;
import com.webkitchen.eeg.analysis.IChannelSampleListener;
import com.webkitchen.brainathlon.gameComponents.ISpectrumListener;
import com.webkitchen.brainathlon.gameComponents.Spectrum;
import com.webkitchen.brainathlon.ui.SpringUtilities;

import javax.swing.*;
import java.awt.*;


/**
 * Displays the raw signal along with filtered Beta, Alpha, Theta and Delta waves.
 *
 * @author Amy Palke
 */
public class SimpleSpectrumDisplayPanel extends TitledPanel
        implements ISpectrumListener, IChannelSampleListener
{
    private int preferredPanelWidth = 300;
    private int preferredPanelHeight = 120;
    private JLabel allWavesLabel = new JLabel("All");
    private JLabel betaLabel = new JLabel("Beta");
    private JLabel alphaLabel = new JLabel("Alpha");
    private JLabel thetaLabel = new JLabel("Theta");
    private JLabel deltaLabel = new JLabel("Delta");
    private RawSignalPanel allWavesValue = new RawSignalPanel(preferredPanelHeight, 1);
    private RawSignalPanel betaValue = new RawSignalPanel(preferredPanelHeight, 1);
    private RawSignalPanel alphaValue = new RawSignalPanel(preferredPanelHeight, 1);
    private RawSignalPanel thetaValue = new RawSignalPanel(preferredPanelHeight, 1);
    private RawSignalPanel deltaValue = new RawSignalPanel(preferredPanelHeight, 1);
    private BandMonitor betaMonitor = new BandMonitor(128, 2, 256);
    private BandMonitor alphaMonitor = new BandMonitor(128, 2, 256);
    private BandMonitor thetaMonitor = new BandMonitor(128, 2, 256);
    private BandMonitor deltaMonitor = new BandMonitor(128, 2, 256);
    private RMSLabel betaRMSLabel = new RMSLabel();
    private RMSLabel alphaRMSLabel = new RMSLabel();
    private RMSLabel thetaRMSLabel = new RMSLabel();
    private RMSLabel deltaRMSLabel = new RMSLabel();

    /**
     * Creates a new <code>SimpleSpectrumDisplayPanel</code>
     */
    public SimpleSpectrumDisplayPanel(String titleText)
    {
        super(titleText);

        setLayout(new SpringLayout());

        //Add the components
        add(allWavesLabel);
        allWavesValue.setPreferredSize(new Dimension(preferredPanelWidth, preferredPanelHeight));
        allWavesLabel.setLabelFor(allWavesValue);
        add(allWavesValue);

        JPanel betaPanel = new JPanel(new GridLayout(2, 1));
        betaPanel.add(betaLabel);
        betaPanel.add(betaRMSLabel);
        betaMonitor.addAmplitudeListener(betaRMSLabel);
        add(betaPanel);
        betaValue.setPreferredSize(new Dimension(preferredPanelWidth, preferredPanelHeight));
        betaLabel.setLabelFor(betaValue);
        add(betaValue);

        JPanel alphaPanel = new JPanel(new GridLayout(2, 1));
        alphaPanel.add(alphaLabel);
        alphaPanel.add(alphaRMSLabel);
        alphaMonitor.addAmplitudeListener(alphaRMSLabel);
        add(alphaPanel);
        alphaValue.setPreferredSize(new Dimension(preferredPanelWidth, preferredPanelHeight));
        alphaLabel.setLabelFor(alphaValue);
        add(alphaValue);

        JPanel thetaPanel = new JPanel(new GridLayout(2, 1));
        thetaPanel.add(thetaLabel);
        thetaPanel.add(thetaRMSLabel);
        thetaMonitor.addAmplitudeListener(thetaRMSLabel);
        add(thetaPanel);
        thetaValue.setPreferredSize(new Dimension(preferredPanelWidth, preferredPanelHeight));
        thetaLabel.setLabelFor(thetaValue);
        add(thetaValue);

        JPanel deltaPanel = new JPanel(new GridLayout(2, 1));
        deltaPanel.add(deltaLabel);
        deltaPanel.add(deltaRMSLabel);
        deltaMonitor.addAmplitudeListener(deltaRMSLabel);
        add(deltaPanel);
        deltaValue.setPreferredSize(new Dimension(preferredPanelWidth, preferredPanelHeight));
        deltaLabel.setLabelFor(deltaValue);
        add(deltaValue);

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(this,
                                        5, 2, //rows, cols
                                        6, 6, //initX, initY
                                        6, 6); //xPad, yPad
    }

    /**
     * Receive the Spectrum and update our values.
     * This method will be invoked from the reader thread, and invokeLater is used to
     * schedule the updates for executing in the UI event-dispatching thread.
     *
     * @param spectrum the new frequency band values
     */
    public void receiveSpectrum(final Spectrum spectrum)
    {
        // update the values
        betaValue.setCurrentValue(spectrum.getBeta());
        alphaValue.setCurrentValue(spectrum.getAlpha());
        thetaValue.setCurrentValue(spectrum.getTheta());
        deltaValue.setCurrentValue(spectrum.getDelta());

        betaMonitor.receiveBand(spectrum.getBeta());
        alphaMonitor.receiveBand(spectrum.getAlpha());
        thetaMonitor.receiveBand(spectrum.getTheta());
        deltaMonitor.receiveBand(spectrum.getDelta());

    }

    public void receiveSample(final double rawSample)
    {
        // update the values
        allWavesValue.setCurrentValue(rawSample);
    }


    private class RMSLabel extends JLabel implements IAmplitudeListener
    {
        public void receiveAmplitude(final double rms)
        {
            // This method will be invoked from the reader thread, so
            // schedule a job for the event-dispatching thread to update our values
            javax.swing.SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    setText(String.valueOf(Math.round(rms)));
                }
            });
        }
    }


}

