package com.webkitchen.brainathlon.data;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.webkitchen.brainathlon.gameControl.BandIncreaseConfiguration;
import com.webkitchen.brainathlon.gameControl.DualBandRatioConfiguration;
import com.webkitchen.brainathlon.gameControl.SustainedIncreaseConfiguration;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Handles reading and writing to the XML configuration files
 *
 * @author Amy Palke
 */
public class Configuration
{
    private static final Configuration ourInstance = new Configuration();
    private ConfigData ourData;
    private XStream xStream = new XStream(new DomDriver()); // does not require XPP3 library
    private String configFileDirectory = "C:\\openeeg\\Brainathlon\\software\\config\\";
    private String mainConfigurationFile = "MainConfiguration.xml";
    private BandIncreaseConfiguration bandIncreaseConfiguration;
    private SustainedIncreaseConfiguration sustainedIncreaseConfiguration;
    private DualBandRatioConfiguration dualBandRatioConfiguration;


    private Configuration()
    {
        // Private to ensure singleton status
    }

    /**
     * Save course configurations
     *
     * @throws IOException if we are unable to save
     */
    public static void save() throws IOException
    {
        ourInstance.saveConfiguration();
    }

    private void saveConfiguration() throws IOException
    {
        toFile(sustainedIncreaseConfiguration, ourData.sustainedIncreaseConfigurationFile);
        toFile(bandIncreaseConfiguration, ourData.bandIncreaseConfigurationFile);
        toFile(dualBandRatioConfiguration, ourData.dualBandRatioConfigurationFile);
    }

    /**
     * Load configuration information.  This method must be called before calling any other
     * methods.
     */
    public static void load()
    {
        ourInstance.loadConfiguration();
    }

    private void loadConfiguration()
    {
        ourData = (ConfigData) loadObject(mainConfigurationFile);
        bandIncreaseConfiguration = (BandIncreaseConfiguration) loadObject(ourData.bandIncreaseConfigurationFile);
        sustainedIncreaseConfiguration = (SustainedIncreaseConfiguration) loadObject(ourData.sustainedIncreaseConfigurationFile);
        dualBandRatioConfiguration = (DualBandRatioConfiguration) loadObject(ourData.dualBandRatioConfigurationFile);
    }

    private Object loadObject(String fileName)
    {
        Object obj = null;
        try
        {
            obj = fromFile(fileName);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);  // Can't continue without a configuration object

        }
        return obj;
    }

    private Object fromFile(String fileName) throws IOException
    {
        FileReader reader = new FileReader(configFileDirectory + fileName);
        return (Object) xStream.fromXML(reader);
    }

    private void toFile(Object obj, String fileName) throws IOException
    {
        String xml = xStream.toXML(obj);
        FileWriter writer = new FileWriter(configFileDirectory + fileName);
        writer.write(xml);
        writer.close();
    }

    public static int getSampleRate()
    {
        return ourInstance.ourData.sampleRate;
    }

    public static Integer[] getChannels()
    {
        return ourInstance.ourData.channels;
    }

    public static String getCourseOverSongFile()
    {
        return ourInstance.ourData.midiFileDirectory + ourInstance.ourData.courseOverSong;
    }

    public static boolean getDebugMode()
    {
        return ourInstance.ourData.debugMode;
    }

    public static String getUserLogFileDirectory()
    {
        return ourInstance.ourData.userLogFileDirectory;
    }

    public static BandIncreaseConfiguration getBandIncreaseConfiguration()
    {
        return ourInstance.bandIncreaseConfiguration;
    }

    public static SustainedIncreaseConfiguration getSustainedIncreaseConfiguration()
    {
        return ourInstance.sustainedIncreaseConfiguration;
    }

    public static DualBandRatioConfiguration getDualBandRatioConfiguration()
    {
        return ourInstance.dualBandRatioConfiguration;
    }

    private static class ConfigData
    {
        // ModEEG information
        private int sampleRate;
        private Integer[] channels;
        private boolean debugMode;

        // File information
        private String midiFileDirectory;
        private String courseOverSong;
        private String userLogFileDirectory;
        private String bandIncreaseConfigurationFile;
        private String sustainedIncreaseConfigurationFile;
        private String dualBandRatioConfigurationFile;
    }
}
