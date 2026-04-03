package topology;

import main.*;
import physical.*;
public class StarTopology {

    Hub hub;

    public StarTopology(Hub hub) {
        this.hub = hub;
    }

    public void connectDevices(EndStation[] stations) {
        for (EndStation s : stations) {
            hub.connect(s);
        }
    }

   public void displayTopology(EndStation[] stations) {

    System.out.println("\n--- STAR TOPOLOGY ---\n");

    int n = stations.length;

    // Top device
    if (n >= 1) {
        System.out.println("        " + stations[0].stationName);
        System.out.println("          |");
    }

    // Middle line with HUB
    if (n >= 3) {
        System.out.print(stations[1].stationName + " ---- ");
    } else {
        System.out.print("            ");
    }

    System.out.print("HUB");

    if (n >= 3) {
        System.out.print(" ---- " + stations[2].stationName);
    }

    System.out.println();

    // Bottom device
    if (n >= 2) {
        System.out.println("          |");
        System.out.println("        " + stations[n - 1].stationName);
    }

    // Extra devices (if more than 4)
    if (n > 4) {
        System.out.println("\nAdditional connections:");
        for (int i = 3; i < n - 1; i++) {
            System.out.println("   HUB ---- " + stations[i].stationName);
        }
    }
}
    public void transmit(Data d, EndStation sender) {
        hub.receiveAndTransmit(d, sender);
    }
}