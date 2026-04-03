package physical;

import java.util.*;
import main.*;

public class Hub {

    public String name;
    List<EndStation> devices = new ArrayList<>();

    public Hub(String name) {
        this.name = name;
    }

    public void connect(EndStation s) {
        devices.add(s);
    }

    public void receiveAndTransmit(Data d, EndStation sender) {

       System.out.println("\n[HUB " + name + "] Broadcasting signal...");

for (EndStation s : devices) {

    if (s == sender) continue;

    System.out.println(" -> Signal reaches " + s.stationName);

    if (s.stationName.equals(d.dest)) {
        System.out.println("    OK " + s.stationName + " ACCEPTS data");
        s.check(d);
    } else {
        System.out.println("    NO " + s.stationName + " DISCARDS data");
    }
}
    }
}