
package datalink;

import datalink.error.ChecksumControl;
import datalink.frame.Frame;
import java.util.*;

/**
 * Data-Link-Layer aware end station.
 * Each node has a MAC address and an inbox of frames.
 */
public class DLLNode {
    public final String name;
    public final String mac;
    // DELETE : inbox: frames delivered to this node (simulates NIC buffer)
    private final List<Frame> inbox = new ArrayList<>();

    public DLLNode(String name, String mac) {
        this.name = name;
        this.mac  = mac;
    } 

    /** Called by Switch/Bridge when forwarding a frame to this node */
 public void receive(Frame f) {

    // Accept only if:
    // 1. MAC matches
    // 2. OR broadcast

    if (f.destMAC.equals(this.mac) || // -> DIRECT MSG 
        f.destMAC.equals("FF:FF:FF:FF:FF:FF")) { // -> BROADCAST 

        System.out.println("✔ " + name + " ACCEPTED frame");
        // ADD THIS LINE HERE 
        inbox.add(f); //demonstrate NIC 

        // checksum verify
        if (ChecksumControl.verify(f)) {
            System.out.println("   Checksum OK ");
        } else {
            System.out.println("   Checksum ERROR ");
        }

    } else {
        System.out.println(name + " ignored frame");
    }
}
    /** Pull all received frames (clears the buffer) */
    // “It copies all frames from the node’s inbox, clears the buffer, and returns the frames for processing.”
    public List<Frame> drainInbox() {
        List<Frame> copy = new ArrayList<>(inbox);
        inbox.clear();
        return copy;
    }
    
    // Does this node have any frames waiting 
    public boolean hasFrames() { return !inbox.isEmpty(); }

    @Override
    public String toString() { return name + "(" + mac + ")"; }
}

