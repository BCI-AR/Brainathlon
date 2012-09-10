package com.webkitchen.eeg.acquisition;

import java.io.IOException;
import java.net.UnknownHostException;


/**
 * Interface for connection objects which read EDF packets
 *
 * @author Amy Palke
 */
interface INeuroServerConnection
{
    /**
     * Open the socket connection
     *
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     */
    void connect() throws IOException, UnknownHostException;

    /**
     * Retreive the EDF header record
     *
     * @return the EDF header record
     * @throws java.io.IOException
     */
    String getEDFHeader() throws IOException;

    /**
     * Begin watching for data
     *
     * @throws java.io.IOException
     */
    void startWatch() throws IOException;

    /**
     * Check if more data is available
     *
     * @return true if more data is available, false otherwise
     */
    boolean hasNextLine();

    /**
     * Retreive the next line of data
     *
     * @return the next line of data
     */
    String getNextLine() throws IOException;

    /**
     * Close the socket connection
     */
    void close();

}
