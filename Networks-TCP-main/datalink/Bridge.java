package datalink;

import datalink.frame.Frame;
import java.util.*;


public class Bridge {

    public final String name;

    // Two sides: "A" and "B"
    private final List<DLLNode> sideA = new ArrayList<>();
    private final List<DLLNode> sideB = new ArrayList<>();

    // MAC -> side learning table
    private final Map<String, String> macTable = new LinkedHashMap<>();

    public Bridge(String name) {
        this.name = name;
    }

    public void connectToSideA(DLLNode node) {
        sideA.add(node);
        System.out.printf("  [BRIDGE %s] Side-A <- %s%n", name, node);
    }

    public void connectToSideB(DLLNode node) {
        sideB.add(node);
        System.out.printf("  [BRIDGE %s] Side-B <- %s%n", name, node);
    }

    public void printMACTable() {
        System.out.printf("%n  ╔═════════════════════════════════╗%n");
        System.out.printf("  ║  Bridge %s - MAC Table           ║%n", name);
        System.out.printf("  ╠══════════════╦══════════════════╣%n");
        System.out.printf("  ║   MAC        ║   Side           ║%n");
        System.out.printf("  ╠══════════════╬══════════════════╣%n");
        macTable.forEach((mac, side) ->
                System.out.printf("  ║  %-12s║   Side-%s         ║%n", mac, side));
        System.out.printf("  ╚══════════════╩══════════════════╝%n");
    }

    public void printDomainSummary() {
        System.out.printf("%n  ╔═════════════════════════════════╗%n");
        System.out.printf("  ║  Bridge %s - Domain Summary      ║%n", name);
        System.out.printf("  ╠═════════════════════════════════╣%n");
        System.out.printf("  ║  Broadcast Domains  : 1          ║%n");
        System.out.printf("  ║  Collision Domains  : 2          ║%n");
        System.out.printf("  ║  (one per segment/side)          ║%n");
        System.out.printf("  ╚═════════════════════════════════╝%n");
    }

    /**
     * Process a frame coming from a sender on one of the two sides.
     */
    public void processFrame(Frame frame, DLLNode sender) {
        String senderSide = getSide(sender);
        if (senderSide == null) {
            System.out.printf("  [BRIDGE %s]  Sender %s not connected to this bridge%n", name, sender.name);
            return;
        }

        System.out.printf("%n  [BRIDGE %s] Frame from %s (Side-%s): %s%n",
                name, sender.name, senderSide, frame);

        // Learn
        if (!macTable.containsKey(frame.srcMAC)) {
            macTable.put(frame.srcMAC, senderSide);
            System.out.printf("  [BRIDGE %s] Learned: %s -> Side-%s%n", name, frame.srcMAC, senderSide);
        }

        String destSide = macTable.get(frame.destMAC);

        if (frame.destMAC.equals("FF:FF:FF:FF:FF:FF")) {
            // Broadcast: flood to all other nodes
            System.out.printf("  [BRIDGE %s] Broadcast - forwarding to all%n", name);
            forwardToAll(frame, sender);
            return;
        }

        if (destSide == null) {
            System.out.printf("  [BRIDGE %s] Unknown dest - flooding%n", name);
            forwardToAll(frame, sender);
        } else if (destSide.equals(senderSide)) {
            System.out.printf("  [BRIDGE %s]  Dest on same side (%s) - filtered%n", name, senderSide);
        } else {
            List<DLLNode> targetSide = destSide.equals("A") ? sideA : sideB;
            for (DLLNode n : targetSide) {
                if (n.mac.equals(frame.destMAC)) {
                    n.receive(frame);
                    System.out.printf("  [BRIDGE %s] OK  Forwarded to %s (Side-%s)%n", name, n.name, destSide);
                }
            }
        }
    }

    private void forwardToAll(Frame frame, DLLNode sender) {
        for (DLLNode n : sideA) {
            if (n != sender) { n.receive(frame); System.out.printf("  [BRIDGE %s]  -> %s (A)%n", name, n.name); }
        }
        for (DLLNode n : sideB) {
            if (n != sender) { n.receive(frame); System.out.printf("  [BRIDGE %s]  -> %s (B)%n", name, n.name); }
        }
    }

    private String getSide(DLLNode node) {
        if (sideA.contains(node)) return "A";
        if (sideB.contains(node)) return "B";
        return null;
    }
}
