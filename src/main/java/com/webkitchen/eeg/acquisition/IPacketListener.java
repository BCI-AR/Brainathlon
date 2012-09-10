package com.webkitchen.eeg.acquisition;

import java.util.EventListener;


/**
 * Listener interface for receiving <code>Packets</code> of data in EDF format
 *
 * @author Amy Palke
 */
interface IPacketListener extends EventListener
{
    /**
     * Receive the latest EDF packet of EEG data
     *
     * @param packet the latest EDF packet
     */
    void receivePacket(Packet packet);
}
