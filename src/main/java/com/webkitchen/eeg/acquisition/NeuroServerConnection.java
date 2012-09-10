package com.webkitchen.eeg.acquisition;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;


/**
 * NeuroServerConnection connects to NeuroServer's socket to collect TCP wrapped EDF packets
 *
 * @author Amy Palke
 * @see NeuroServerReader
 */
class NeuroServerConnection implements INeuroServerConnection
{
    // the single instance of NeuroServerConnection
    private static NeuroServerConnection ourInstance = new NeuroServerConnection();

    // The socket connection to NeuroServer
    private Socket socket;

    // The input and output streams to/from NeuroServer
    private BufferedReader input;
    private DataOutputStream output;

    // NeuroServer uses port 8336 on localhost, TCPTrace uses 8335 proxy
    private static final int TCP_TRACE_PORT = 8335;
//    private static final int PORT = TCP_TRACE_PORT; // comment out for direct access/in to debug
    private static final int PORT = 8336;          // comment out to debug/in for direct access
    private static final String HOST = "localhost";

    // NeuroServer response codes
    private static final int ok = 200;
    private static final int error = 400;

    // Internal error code
    private static final int internalError = -1;

    // Carriage return & line feed for terminating each NeuroServer command
    private static final String CRLF = "\r\n";

    // Are we currently connected to NeuroServer
    private boolean isConnected = false;

    // Log file for debugging
    private static Logger logger = Logger.getLogger(NeuroServerConnection.class);


    /**
     * Private constructor - access instance through getInstance() factory method
     */
    private NeuroServerConnection()
    {
        // private to ensure singleton status
    }

    /**
     * Factory method for return the single instance of NeuroServerConnection
     *
     * @return the single instance of NeuroServerConnection
     */
    public static NeuroServerConnection getInstance()
    {
        return ourInstance;
    }

    /**
     * Open the socket connection
     *
     * @throws java.io.IOException if we are unable to create a connection to the
     *                             EEG device
     */
    public void connect() throws IOException
    {
        socket = new Socket(HOST, PORT);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new DataOutputStream(socket.getOutputStream());

        // Enter "display" role and check that the reply code is 200 OK
        sendCommand("display", ok);

        isConnected = true;
        logger.debug("Connected");
    }

    /**
     * Retreive the EDF header record
     *
     * @return the EDF header record
     * @throws java.io.IOException if we are unable to communicate with the EEG device
     */
    public String getEDFHeader() throws IOException
    {
        checkConnection();
        // Send getheader command, and check that the reply code is 200 OK
        sendCommand("getheader", ok);
        String reply = getNextLine();
        logger.debug("Header:" + CRLF + reply);
        return reply;
    }

    /**
     * Begin watching for data
     *
     * @throws java.io.IOException if we are unable to communicate with the EEG device
     */
    public void startWatch() throws IOException
    {
        checkConnection();
        // Send watch command, and check that the reply code is 200 OK
        sendCommand("watch", ok);
        logger.debug("Watching");
    }

    /**
     * Check if more data is available
     *
     * @return true if more data is available, false otherwise
     */
    public boolean hasNextLine()
    {
        checkConnection();
        boolean result = false;
        try
        {
            result = input.ready();
        }
        catch (IOException ignore)
        {
            // Print out the error, and continue on (returning false)
            logger.error("Error calling input.ready() " + ignore);
            ignore.printStackTrace();
        }
        return result;
    }

    /**
     * Retreive the next line of data
     *
     * @return the next line of data
     * @throws java.io.IOException if we are unable to communicate with the EEG device
     */
    public String getNextLine() throws IOException
    {
        checkConnection();
        String reply;
        if (input.ready())
        {
            reply = input.readLine();
            logger.debug(reply);
        }
        else
        {
            logger.warn("Input isn't ready");
            throw new IOException("Input isn't ready");
        }
        return reply;
    }

    /**
     * Close the socket connection
     */
    public void close()
    {
        if (isConnected)
        {
            isConnected = false;
            try
            {
                // Don't look for a reply, since we could have unread EDF packets in our buffer
                sendCommand("close");
                input.close();
                output.close();
                socket.close();
            }
            catch (IOException e)
            {
                logger.warn("Unable to close connection: " + e);
            }
            logger.info("Closed connection");
        }
    }

    private void checkConnection()
    {
        if (!isConnected)
        {
            throw new IllegalStateException("Not connected.");
        }
    }

    /**
     * Send a command to the server
     *
     * @param command the command to send
     * @throws java.io.IOException if we are unable to communicate with the EEG device
     */
    private void sendCommand(String command) throws IOException
    {
        logger.debug("Sending: " + command);
        // Write command (terminated with CRLF) to server
        output.writeBytes(command + CRLF);
        String reply = input.readLine();
        logger.debug("Server said: " + reply);
    }

    /**
     * Send a command to the server, and check that the response code
     * is correct
     *
     * @param command      the command to send
     * @param responseCode the response we expect
     * @return the server's reply
     * @throws java.io.IOException if we are unable to communicate with the EEG device
     */
    private String sendCommand(String command, int responseCode) throws IOException
    {
        logger.debug("Sending: " + command + ", expecting: " + responseCode);
        // Write command (terminated with CRLF) to server and read reply from server
        output.writeBytes(command + CRLF);
        String reply = input.readLine();

        // Check that the server's reply code is the same as responseCode.
        // If not, throw an IOException
        int replyCode = parseReply(reply);
        if (replyCode != responseCode)
        {
            logger.warn("Error, server says: " + reply);
            throw new IOException("Error, server says: " + reply);
        }
        logger.debug("Server said: " + reply);
        return reply;
    }

    /**
     * Parse the reply line from the server
     *
     * @param reply the reply string
     * @return the response code
     */
    private int parseReply(String reply)
    {
        int i;
        StringTokenizer tokenizer = new StringTokenizer(reply);
        try
        {
            i = Integer.parseInt(tokenizer.nextToken());
        }
        catch (NumberFormatException e)
        {
            logger.error("First token in reply was not an int: " + reply);
            i = internalError;
        }
        return i;
    }

    /**
     * Try to close the connection if something bad happens
     */
    protected void finalize() throws Throwable
    {
        if (isConnected)
        {
            close();
        }
        super.finalize();
    }

}
