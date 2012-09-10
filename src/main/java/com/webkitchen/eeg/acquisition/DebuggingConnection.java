package com.webkitchen.eeg.acquisition;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.UnknownHostException;


/**
 * Connection for testing and debugging - Reads TCP wrapped EDF packets from a text file
 *
 * @author Amy Palke
 * @see NeuroServerReader
 */
class DebuggingConnection implements INeuroServerConnection
{
    // the single instance of DebuggingConnection
    private static final DebuggingConnection INSTANCE = new DebuggingConnection();
    // need to sleep between line reads to avoid using 100% of CPU
    // EEG machine produces 257.1 samples/1 sec;  1 sample/3.89 milliseconds
    private int sleepDuration = 3;   // milliseconds
    // the file that has sample input - when we get to the end, we start again at top
    private File inputFile = new File("C:\\openeeg\\Brainathlon\\software\\debug\\input.txt");
    private RandomAccessFile file;

    /**
     * Private constructor - access instance through getInstance() factory method
     */
    private DebuggingConnection()
    {
        // private to ensure singleton status
    }

    /**
     * Factory method for return the single instance of DebuggingConnection
     *
     * @return the single instance of DebuggingConnection
     */
    protected static DebuggingConnection getInstance()
    {
        return INSTANCE;
    }

    /**
     * Open the file as read-only
     *
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     */
    public void connect() throws IOException, UnknownHostException
    {
        file = new RandomAccessFile(inputFile, "r");
    }

    /**
     * Placeholder for retrieving the EDF header record
     *
     * @return null
     * @throws java.io.IOException
     */
    public String getEDFHeader() throws IOException
    {
        return null;
    }

    /**
     * Begin watching for data
     *
     * @throws java.io.IOException
     */
    public void startWatch() throws IOException
    {
        // no need to do anything
    }

    /**
     * Since more data is always available, we will sleep briefly before returning true
     *
     * @return true
     */
    public boolean hasNextLine()
    {
        try
        {
            Thread.sleep(sleepDuration);
        }
        catch (InterruptedException e)
        {
            // ignore interruptions
        }
        return true;
    }

    /**
     * Retreive the next line of data.  Once we reach the end of the file,
     * loop back to the beginning.
     *
     * @return the next line of data
     */
    public String getNextLine() throws IOException
    {
        String nextLine;
        nextLine = file.readLine();
        // once we reach the end of the file, loop back to the beginning
        if (nextLine == null)
        {
            file.seek(0);
            nextLine = file.readLine();
        }
        return nextLine;
    }

    /**
     * Close the file
     */
    public void close()
    {
        try
        {
            file.close();
        }
        catch (IOException e)
        {
            System.out.println("Unable to close our file");
            e.printStackTrace();
        }
    }
}
