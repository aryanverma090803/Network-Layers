package datalink;

import datalink.frame.Frame;
import java.util.*;

/**
 * Layer-2 Switch with Address Learning.
 */
public class Switch {

    public final String name;

    // port → node
    private final Map<Integer, DLLNode> ports = new LinkedHashMap<>();

    // MAC → port (learning table)
    private final Map<String, Integer> macTable = new LinkedHashMap<>();

    private int totalFramesForwarded = 0;
    private int totalBroadcasts = 0;

    public Switch(String name) {
        this.name = name;
    }


    // CONNECT DEVICE
 
    public int connect(DLLNode node) {
        int port = ports.size() + 1;
        ports.put(port, node);

        System.out.printf("  [SWITCH %s] Port %-2d <- connected to %s%n",
                name, port, node.name);

        return port;
    }


    // MAC LEARNING 

    public void learnMAC(String mac, int port) {
        macTable.put(mac, port);
        System.out.println("  [SWITCH " + name + "] Learned: " + mac + " -> Port " + port);
    }

    public boolean hasMAC(String mac) {
        return macTable.containsKey(mac);
    }


    // MAC TABLE

    public void printMACTable() {
        System.out.println("\n=== MAC TABLE ===");

        if (macTable.isEmpty()) {
            System.out.println("MAC Table is empty.");
            return;
        }

        for (Map.Entry<String, Integer> entry : macTable.entrySet()) {
            System.out.println(entry.getKey() + " -> PORT " + entry.getValue());
        }
    }


    // DOMAIN SUMMARY
    public void printDomainSummary() {
        System.out.println("\n=== DOMAIN SUMMARY ===");
        System.out.println("Broadcast Domains: 1");
        System.out.println("Collision Domains: " + ports.size());
    }

    // CORE SWITCH LOGIC 
    public void processFrame(Frame f, DLLNode sender) {

        int inPort = getPort(sender);

        System.out.printf("\n  [SWITCH %s] Frame received on Port %d: %s%n",
                name, inPort, f);

        // STEP 1: LEARN SOURCE MAC
        learnMAC(f.srcMAC, inPort);
        // uper me f.srcMAC is frame object 

        // STEP 2: BROADCAST (destination mac can be : all/uni/unknown/same as sender) 
        if (f.destMAC.equals("FF:FF:FF:FF:FF:FF")) {

            System.out.printf("  [SWITCH %s] Broadcast → Flooding%n", name);

            for (Map.Entry<Integer, DLLNode> e : ports.entrySet()) {
                if (e.getKey() != inPort) { // agar inPort ya port of sender hai to 
                    // skip as we will broadcast to all except sender 

                    System.out.printf("  [SWITCH %s] → Port %d (%s)%n",
                            name, e.getKey(), e.getValue().name);

                    e.getValue().receive(f); // all nodes receive frame f && unke inbox me add(f) 
                }
            }

            totalBroadcasts++; // count bcast kitne bare hua 
            return;
        }

        // STEP 3: UNICAST
        Integer destPort = macTable.get(f.destMAC);

        // UNKNOWN → FLOOD
        if (destPort == null) {

            System.out.printf("  [SWITCH %s] Unknown MAC %s → Flooding%n",
                    name, f.destMAC);

            for (Map.Entry<Integer, DLLNode> e : ports.entrySet()) {
                if (e.getKey() != inPort) { // broadcast to all except sender 

                    System.out.printf("  [SWITCH %s] → Flood to Port %d (%s)%n",
                            name, e.getKey(), e.getValue().name);

                    e.getValue().receive(f);
                }
            }
        }

        // SAME PORT → DROP
        else if (destPort == inPort) {

            System.out.printf("  [SWITCH %s] Same port → Frame dropped%n", name);
        }

        // KNOWN → DIRECT UNICAST
        else {

            DLLNode destNode = ports.get(destPort);

            System.out.printf("  [SWITCH %s] Unicast → Port %d (%s)%n",
                    name, destPort, destNode.name);

            destNode.receive(f);

            totalFramesForwarded++;
        }
    }

    private int getPort(DLLNode node) {
        for (Map.Entry<Integer, DLLNode> e : ports.entrySet()) {
            if (e.getValue() == node) return e.getKey();
        }
        return -1;
    }
}

 