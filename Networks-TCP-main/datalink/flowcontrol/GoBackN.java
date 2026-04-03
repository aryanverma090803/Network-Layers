package datalink.flowcontrol;

import datalink.frame.Frame;
import java.util.*;

public class GoBackN {

    private final int    windowSize;
    private final double errorRate;
    private final double lossRate;
    private final long   timeoutMs;  
    private final Random rng;

    public GoBackN(int windowSize, double errorRate, double lossRate) {
        this.windowSize = windowSize;
        this.errorRate  = errorRate;
        this.lossRate   = lossRate;
        this.timeoutMs  = 800;        
        this.rng        = new Random(99);
    }

    private void sleep(long time){
        // wait
    }

    public void send(List<String> chunks, String srcName, String srcMAC,String destName,    String destMAC) {
        // DEFINITION
        int total = chunks.size();
        int S_f = 0;              
        int S_n = 0;           
        int R_n = 0;           
        Frame[] frames = new Frame[total];

        for (int i = 0; i < total; i++)
            frames[i] = new Frame(destMAC, srcMAC, i, chunks.get(i));

        System.out.println("\n===== GO-BACK-N START =====\n");


        // SENDER SIDE BC ND
        while (S_f < total) {
            while (S_n < S_f + windowSize && S_n < total) {
                Frame f = frames[S_n];


                // FRAMES
                // loss
                if (rng.nextDouble() < lossRate) {
                    System.out.println("[SENDER]   Frame " + S_n + " LOST in channel");
                    frames[S_n] = null;
                    S_n++;
                    continue;
                }

                // corrupt
                if (!f.corruptedOnce && rng.nextDouble() < errorRate) {
                    f.corrupt();
                    System.out.println("[SENDER]   Frame " + S_n + " sent  (CORRUPTED)");
                } else {
                    System.out.println("[SENDER]   Frame " + S_n + " sent");
                }

                S_n++;
            }
            System.out.println();

            sleep(timeoutMs);

            // ── RECEIVER: process window ─────────────────────────
            boolean timeoutTriggered = false;

            for (int i = S_f; i < S_n; i++) {
                Frame f = frames[i];

                if (f == null) {
                    System.out.println("[RECEIVER] Frame " + i + " missing");
                    System.out.println("[RECEIVER] NAK  — last good ACK was " + (R_n - 1));
                    timeoutTriggered = true;
                    break;
                }
                System.out.println("[RECEIVER] Frame " + f.seqNum + " received");

                // ACK IS CHECKED 
                if (f.isCorrupted() || f.seqNum != R_n) {
                    System.out.println("[RECEIVER] Frame " + f.seqNum + " rejected (error/out-of-order)");
                    System.out.println("[RECEIVER] NAK  — last good ACK was " + (R_n - 1));
                    timeoutTriggered = true;
                    break;
                }
                System.out.println("[RECEIVER] Frame " + f.seqNum + " accepted");

                if (rng.nextDouble() < lossRate) {
                    System.out.println("[CHANNEL]  ACK " + f.seqNum + " LOST");
                    timeoutTriggered = true;
                } else {
                    System.out.println("[SENDER]   ACK " + f.seqNum + " received  ✓");
                    S_f++;
                }

                R_n++;
                System.out.println();
            }

            // ── TIMEOUT ──────────────────────────────────
            if (timeoutTriggered) {
                System.out.println("[TIMER]    Timeout! Retransmitting from frame " + S_f);
                S_n = S_f;
                R_n = S_f;
                for (int j = S_f; j < total; j++)
                    frames[j] = new Frame(destMAC, srcMAC, j, chunks.get(j));
                System.out.println();
            }

            System.out.println("[WINDOW]   Sliding...\n");
        }

        

        System.out.println("===== ALL FRAMES DELIVERED =====");
    }
}