package datalink.error;

import datalink.frame.Frame;

public class ChecksumControl {

    public static void attachChecksum(Frame f) {
        f.checksum = Frame.computeChecksum(f.data);
    }

    public static boolean verify(Frame f) {
        return !f.isCorrupted();
    }

    public static void printVerification(Frame f) {
        int recomputed = Frame.computeChecksum(f.data);
        System.out.printf("  [CHECKSUM] Frame seq=%-2d | Embedded=0x%02X | Computed=0x%02X | %s%n",
                f.seqNum, f.checksum, recomputed,
                f.isCorrupted() ? "CORRUPTED" : "OK");
    }

    public static void runDemo(String srcMac, String dstMac, String payload) {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║      CHECKSUM DEMO           ║");
        System.out.println("╚══════════════════════════════╝");

        // Step 1: Build frame
        System.out.println("\n[STEP 1] Building frame...");
        Frame frame = new Frame(dstMac, srcMac, 0, payload);
        System.out.println("  Src MAC : " + srcMac);
        System.out.println("  Dst MAC : " + dstMac);
        System.out.println("  Payload : " + payload);

        // Step 2: Attach checksum
        System.out.println("\n[STEP 2] Sender attaches checksum...");
        attachChecksum(frame);
        System.out.printf("  Checksum embedded: 0x%02X%n", frame.checksum);

        // Step 3: Verify clean frame
        System.out.println("\n[STEP 3] Receiver verifies intact frame...");
        printVerification(frame);
        System.out.println(verify(frame) ? "  ✔ Frame ACCEPTED." : "  ✘ Frame REJECTED.");

        // Step 4: Corrupt checksum
        System.out.println("\n[STEP 4] Simulating transmission bit error...");
        int original = frame.checksum;
        frame.checksum = (original ^ 0xFF) & 0xFF;
        System.out.printf("  Original : 0x%02X  →  Corrupted: 0x%02X%n", original, frame.checksum);

        // Step 5: Verify corrupted frame
        System.out.println("\n[STEP 5] Receiver verifies corrupted frame...");
        printVerification(frame);
        System.out.println(verify(frame)
                ? "  ✔ ACCEPTED. (undetected!)"
                : "  ✘ REJECTED — corruption DETECTED, frame dropped.");

        // Step 6: Restore and re-verify
        System.out.println("\n[STEP 6] Restoring and re-verifying...");
        frame.checksum = original;
        printVerification(frame);
        System.out.println(verify(frame) ? "  ✔ Frame ACCEPTED." : "  ✘ Frame REJECTED.");

        System.out.println("\n══════════════════════════════════");
        System.out.println("  Checksum Demo Complete.");
        System.out.println("══════════════════════════════════");
    }
}