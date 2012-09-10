package com.webkitchen;

import com.webkitchen.eeg.acquisition.EEGAcquisitionController;
import com.webkitchen.eeg.acquisition.IRawSampleGenerator;
import com.webkitchen.eeg.acquisition.IRawSampleListener;
import com.webkitchen.eeg.acquisition.RawSample;

import java.io.IOException;

/**
 * @author Amy Palke
 */
public class SampleApp
{
    private EEGAcquisitionController eegAcquisitionController;

    public SampleApp()
    {
        eegAcquisitionController = EEGAcquisitionController.getInstance();
        IRawSampleGenerator sampleGenerator = eegAcquisitionController.getChannelSampleGenerator();

        // Listens to both channels 1 and 2
        SampleListener twoChannelListener = new SampleListener("Channel One and Two");
        sampleGenerator.addSampleListener(twoChannelListener, new int[]{1, 2});

        // Listens to only channel 1
        SampleListener oneChannelListener = new SampleListener("Channel One");
        sampleGenerator.addSampleListener(oneChannelListener, new int[]{1});
    }

    public void start() throws IOException
    {
        eegAcquisitionController.startReading(false);
    }

    public void stop()
    {
        eegAcquisitionController.stopReading();
    }

    public static void main(String[] args) throws IOException
    {
        SampleApp app = new SampleApp();
        app.start();
    }

    class SampleListener implements IRawSampleListener
    {
        String name;

        SampleListener(String name)
        {
            this.name = name;
        }

        public void receiveSample(RawSample rawSample)
        {
            System.out.println(name);
            int packetNumber = rawSample.getPacketNumber();
            System.out.println(" Packet number:" + packetNumber);
            for (int i = 0, length = rawSample.getChannelNumbers().length; i < length; i++)
            {
                int channelNumber = rawSample.getChannelNumbers()[i];
                int sample = rawSample.getSamples()[i];
                System.out.println("  Channel #" + channelNumber + " = " + sample);
            }
        }
    }
}
