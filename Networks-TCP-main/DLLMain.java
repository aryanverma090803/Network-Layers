import datalink.*;
import datalink.error.*;
import datalink.flowcontrol.*;
import datalink.frame.*;
import datalink.mac.CSMACD;
import encoders.Manchester;
import encoders.NRZI;
import encoders.NRZL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import main.*;
import physical.*;
import topology.*;

public class DLLMain {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("\n===== DATA LINK LAYER SIMULATOR =====");
        while (true) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║   INTERACTIVE SIMULATOR      ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║ 1. Build Topology            ║");
            System.out.println("║ 2. Exit                      ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Choice: ");
            if (readInt() != 1)
                break;

            System.out.println("\nChoose device:");
            System.out.println("1. Switch");
            System.out.println("2. Bridge");
            System.out.println("3. Hub (Physical Layer)");
            System.out.print("Choice: ");
            int choice = readInt();
            if (choice == 1)
                buildSwitch();
            else if (choice == 2)
                buildBridge();
            else if (choice == 3)
                runPhysicalLayer();
        }
    }

    // Physical Layer (HUB)
    static void runPhysicalLayer() {
        System.out.print("Enter number of stations: ");
        int n = readInt();

        EndStation[] stations = new EndStation[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Enter name of station " + (i + 1) + ": ");
            String name = sc.nextLine().trim();
            if (name.isEmpty())
                name = "D" + (i + 1);
            stations[i] = new EndStation(name);
        }

        System.out.println("\nChoose Topology:");
        System.out.println("1. Direct Connection (2 devices only)");
        System.out.println("2. Star (Hub)");
        int topoChoice = readInt();

        System.out.print("\nEnter sender station: ");
        String senderName = sc.nextLine();
        System.out.print("Enter destination station: ");
        String destName = sc.nextLine();

        System.out.print("\nEnter data: ");
        String data = sc.nextLine();
        System.out.println("\nOriginal Data: " + data);
        System.out.println("Bit Representation: " + Utils.stringToBits(data));
        String newData = Utils.stringToBits(data);

        System.out.println("\nSelect Encoding:");
        System.out.println("1. NRZ-L");
        System.out.println("2. NRZ-I");
        System.out.println("3. Manchester");
        int encChoice = readInt();

        String encoded = switch (encChoice) {
            case 1 -> new NRZL().encode(newData);
            case 2 -> new NRZI().encode(newData);
            case 3 -> new Manchester().encode(newData);
            default -> "";
        };

        System.out.println("\nEncoded Signal:\n" + encoded);

        Data d = new Data(senderName, destName, encoded);

        EndStation sender = null;
        for (EndStation s : stations) {
            if (s.stationName.equals(senderName)) {
                sender = s;
                break;
            }
        }
        if (sender == null) {
            System.out.println("Sender not found!");
            return;
        }

        System.out.println("\n--- DATA FLOW ---");
        System.out.println("Data -> Bits -> Encoded Signal -> Transmission");
        System.out.println("\n--- Transmission Start ---");

        Hub hub = new Hub("HUB1");

        if (topoChoice == 1) {
            DirectTopology direct = new DirectTopology();
            direct.displayTopology(stations);
            direct.transmit(d, stations);
            System.out.println("\n" + senderName + " sends data directly to " + destName);
        } else {
            StarTopology st = new StarTopology(hub);
            st.connectDevices(stations);
            st.displayTopology(stations);
            System.out.println("\n" + senderName + " sends data to HUB");
            hub.receiveAndTransmit(d, sender);
        }

        System.out.println("--- Transmission End ---");
    }

    static void configureSegment(java.util.function.Consumer<DLLNode> portConnector,
            int segIndex,
            List<DLLNode> allNodes) {

        System.out.println("1. Connect via Hub (Star topology)");
        System.out.println("2. Direct Devices");
        int choice = readInt();

        if (choice == 1) {
            System.out.print("Enter hub name: ");
            String hubName = sc.nextLine().trim();
            if (hubName.isEmpty())
                hubName = "HUB" + (segIndex + 1);

            Hub hub = new Hub(hubName);

            System.out.print("How many devices in this hub? ");
            int n = readInt();

            EndStation[] stations = new EndStation[n];
            for (int i = 0; i < n; i++) {
                System.out.print("Device name: ");
                String dname = sc.nextLine().trim();
                if (dname.isEmpty())
                    dname = hubName + "_D" + (i + 1);
                stations[i] = new EndStation(dname);
                allNodes.add(new DLLNode(dname, generateMAC(segIndex * 10 + i + 1)));
            }

            StarTopology star = new StarTopology(hub);
            star.connectDevices(stations);
            star.displayTopology(stations);

            DLLNode hubPort = new DLLNode(hubName + "-PORT", generateMAC(100 + segIndex));
            portConnector.accept(hubPort);

        } else {
            System.out.print("How many devices? ");
            int n = readInt();

            for (int i = 0; i < n; i++) {
                System.out.print("Device name: ");
                String dname = sc.nextLine().trim();
                if (dname.isEmpty())
                    dname = "D" + (i + 1);
                DLLNode node = new DLLNode(dname, generateMAC(200 + segIndex * 10 + i));
                portConnector.accept(node);
                allNodes.add(node);
            }
        }
    }

    // Switch Builder
    static void buildSwitch() {
        System.out.print("\nEnter Switch Name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty())
            name = "SW1";
        Switch sw = new Switch(name);

        List<DLLNode> allNodes = new ArrayList<>();

        System.out.print("\nHow many segments to connect to this switch? ");
        int segCount = readInt();

        for (int i = 0; i < segCount; i++) {
            System.out.println("\n--- Segment " + (i + 1) + " ---");
            configureSegment(sw::connect, i, allNodes);
        }

        System.out.println("\nOK Network created successfully.");
        switchMenu(sw, allNodes.toArray(new DLLNode[0]), segCount);
    }

    // Bridge Builder
    static void buildBridge() {
        Bridge br = new Bridge("BR1");
        List<DLLNode> allNodes = new ArrayList<>();

        System.out.println("\n--- CONFIGURE SIDE A ---");
        configureSegment(br::connectToSideA, 0, allNodes);

        System.out.println("\n--- CONFIGURE SIDE B ---");
        configureSegment(br::connectToSideB, 1, allNodes);

        bridgeMenu(br, allNodes.toArray(new DLLNode[0]));
    }

    // Switch Menu
    static void switchMenu(Switch sw, DLLNode[] nodes, int hubCount) {
        boolean run = true;
        while (run) {
            System.out.println("\n--- SWITCH MENU ---");
            System.out.println("1. Send Unicast");
            System.out.println("2. CSMA/CD Demo");
            System.out.println("3. Go-Back-N");
            System.out.println("4. Show MAC Table");
            System.out.println("5. Checksum Demo");
            System.out.println("6. Domain Summary");
            System.out.println("7. Exit");

            switch (readInt()) {
                case 1 -> sendUnicast(nodes, sw);
                case 2 -> csmacdDemo(nodes);
                case 3 -> goBackNDemo(nodes);
                case 4 -> sw.printMACTable();
                case 5 -> checksumDemo(nodes);
                case 6 -> printDomains(nodes, hubCount);
                case 7 -> run = false;
            }
        }
    }

    // Bridge Menu
    static void bridgeMenu(Bridge br, DLLNode[] nodes) {
        boolean run = true;
        while (run) {
            System.out.println("\n--- BRIDGE MENU ---");
            System.out.println("1. Send Unicast");
            System.out.println("2. Broadcast");
            System.out.println("3. CSMA/CD Demo");
            System.out.println("4. Go-Back-N");
            System.out.println("5. Show MAC Table");
            System.out.println("6. Domain Summary");
            System.out.println("7. Checksum Demo");
            System.out.println("8. Exit");

            switch (readInt()) {
                case 1 -> {
                    DLLNode src = pickNode(nodes, "source");
                    DLLNode dst = pickNode(nodes, "destination");
                    Frame f = new Frame(dst.mac, src.mac, 0, "DATA");
                    ChecksumControl.attachChecksum(f);
                    br.processFrame(f, src);
                }
                case 2 -> {
                    DLLNode src = pickNode(nodes, "source");
                    Frame f = new Frame("FF:FF:FF:FF:FF:FF", src.mac, 0, "BROADCAST");
                    ChecksumControl.attachChecksum(f);
                    br.processFrame(f, src);
                }
                case 3 -> csmacdDemo(nodes);
                case 4 -> goBackNDemo(nodes);
                case 5 -> br.printMACTable();
                case 6 -> br.printDomainSummary();
                case 7 -> checksumDemo(nodes);
                case 8 -> run = false;
            }
        }
    }

    // Shared Actions
    static void sendUnicast(DLLNode[] nodes, Switch sw) {
        DLLNode src = pickNode(nodes, "source");
        DLLNode dst = pickNode(nodes, "destination");
        System.out.println("\n--- TRANSMISSION START ---");
        Frame f = new Frame(dst.mac, src.mac, 0, "DATA");
        ChecksumControl.attachChecksum(f);
        sw.processFrame(f, src);
        System.out.println("--- TRANSMISSION END ---");
    }

    static void csmacdDemo(DLLNode[] nodes) {
        DLLNode a = pickNode(nodes, "node A");
        DLLNode b = pickNode(nodes, "node B");
        CSMACD.transmit(a.name, a.mac);
    }

    static void goBackNDemo(DLLNode[] nodes) {
        List<String> data = Arrays.asList("HELLO", "WORLD", "TEST", "GBN", "NETWORK");
        new GoBackN(3, 0.2, 0.2).send(data, "d1", "00:0A:00:00:00:01", "d2", "00:0A:00:00:00:02");
    }
 
    static void checksumDemo(DLLNode[] nodes) {
        DLLNode src = pickNode(nodes, "source");
        DLLNode dst = pickNode(nodes, "destination");
        System.out.print("Enter payload data: ");
        String payload = sc.nextLine().trim();
        if (payload.isEmpty()) payload = "HELLO";
        ChecksumControl.runDemo(src.mac, dst.mac, payload);
    }

    static void printDomains(DLLNode[] nodes, int hubCount) {
        int collisionDomains = (hubCount == 0) ? nodes.length : hubCount;
        System.out.println("\n DOMAIN SUMMARY");
        System.out.println("Broadcast Domains: 1");
        System.out.println("Collision Domains: " + collisionDomains);
    }

    // Utilities
    static DLLNode pickNode(DLLNode[] nodes, String role) {
        System.out.println("\nSelect " + role + ":");
        for (int i = 0; i < nodes.length; i++)
            System.out.println((i + 1) + ". " + nodes[i].name);
        return nodes[readInt() - 1];
    }

    static String generateMAC(int i) {
        return String.format("00:0A:00:00:00:%02X", i);
    }

    static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.print("Enter number: ");
            }
        }
    }
}