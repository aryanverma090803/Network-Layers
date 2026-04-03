 package datalink.mac;

import java.util.*;

/**
 * CSMA/CD (Carrier Sense Multiple Access with Collision Detection) Simulator.
 *
 * Behaviour simulated:
 *  1. Sense the channel before transmitting (Carrier Sense).
 *  2. If channel is BUSY → wait (random backoff).
 *  3. If channel is FREE → begin transmission.
 *  4. While transmitting, detect collision (another station also transmitting).
 *  5. On collision → send JAM signal, stop, apply Binary Exponential Backoff (BEB).
 *  6. After max retries → drop frame.
 *
 * The shared medium is modelled as a static boolean `channelBusy`
 * plus a Set of "currently transmitting" nodes so we can detect collisions.
 */
public class CSMACD {

    private static boolean channelBusy = false;
    private static final Set<String> transmitting = new HashSet<>();
    private static final Random rng = new Random(42); // generate random no so output is same everytime you 

    private static final int MAX_RETRIES = 10; // after it frop frame 
    private static final int SLOT_TIME_MS = 100; // simulated slot time (ms) 

    /** Reset shared medium state between test scenarios */
    public static void reset() {
        channelBusy = false;
        transmitting.clear();
    }

    /**
     * Attempt to acquire the channel for `senderMAC`.
     * Returns true if transmission can proceed (no persistent collision).
     * Prints the full CSMA/CD trace.
     */
    public static boolean transmit(String senderName, String senderMAC) {
        System.out.println("\n  [CSMA/CD] " + senderName + " wants to transmit...");

        for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
            // Try again and again until success or give up 

            // 1. CARRIER SENSE
            if (channelBusy) {
                System.out.printf("  [CSMA/CD] Channel BUSY -> %s waits (attempt %d)%n",
                        senderName, attempt + 1);
                sleep(SLOT_TIME_MS); // fake simulation 
                continue;
            }

            // 2. Channel appears free — start transmitting
            transmitting.add(senderMAC);
            channelBusy = true;
            System.out.printf("  [CSMA/CD] Channel FREE  -> %s starts transmitting%n", senderName);

            // 3. COLLISION DETECTION: another transmitter snuck in simultaneously?
            boolean collision = transmitting.size() > 1; // agar medium me 1 se zyada devices hai 

            if (collision) {
                // JAM signal : STOPPPPPP COLLISIONNNN 
                System.out.printf("  [CSMA/CD] COLLISION detected! JAM sent by %s%n", senderName);
                transmitting.remove(senderMAC); // stop and remove sender 
                if (transmitting.isEmpty()) channelBusy = false;

                // Binary Exponential Backoff ****** : wait random time before retry 
                int k = Math.min(attempt, 10); // K CANNOT GROW FOREVER 
                int maxSlots = (int) Math.pow(2, k); // range increases exponentially
                int backoff = rng.nextInt(maxSlots); // range 0 to max-1 : pick random wt time 
                System.out.printf("  [CSMA/CD] BEB: k=%d, backoff=%d slots (%d ms)%n",
                        k, backoff, backoff * SLOT_TIME_MS);
                sleep(backoff * SLOT_TIME_MS); // ijcrease wait time ecponentially 

            } else {
                // SUCCESS
                System.out.printf("  [CSMA/CD] %s transmitting (no collision)%n", senderName);
                // Simulate transmission time
                sleep(SLOT_TIME_MS * 2);
                transmitting.remove(senderMAC);
                if (transmitting.isEmpty()) channelBusy = false;
                return true;
            }
        }

        System.out.printf("  [CSMA/CD]  %s exceeded max retries - frame DROPPED%n", senderName);
        return false;
    }

    // Let another station "grab" the channel concurrently (for collision simulation) 
    public static void simulateConcurrentTransmitter(String mac) {
        transmitting.add(mac);
        channelBusy = true;
    }

    /** Release a concurrent transmitter */
    public static void releaseConcurrentTransmitter(String mac) {
        transmitting.remove(mac);
        if (transmitting.isEmpty()) channelBusy = false;
    }

    private static void sleep(int ms) {
        // In simulation mode we don't actually sleep — just print 
    }
}


