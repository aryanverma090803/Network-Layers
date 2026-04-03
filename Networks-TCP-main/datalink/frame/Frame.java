package datalink.frame;

public class Frame {
    public String destMAC;
    public String srcMAC;
    public int seqNum;
    public String data;
    public int checksum;
    public boolean isACK;
    public boolean isNAK;
    public int ackNum;
    public boolean corruptedOnce;

    // Data frame constructor
    public Frame(String destMAC, String srcMAC, int seqNum, String data) {
        this.destMAC = destMAC;
        this.srcMAC  = srcMAC;
        this.seqNum  = seqNum;
        this.data    = data;
        this.checksum = computeChecksum(data);
        this.isACK = false;
        this.isNAK = false;
    }

    // ACK/NAK constructor
    public Frame(String destMAC, String srcMAC, int ackNum, boolean isACK, boolean isNAK) {
        this.destMAC = destMAC;
        this.srcMAC  = srcMAC;
        this.ackNum  = ackNum;
        this.isACK   = isACK;
        this.isNAK   = isNAK;
        this.data    = "";
        this.checksum = 0;
    }

    // Simple 8-bit checksum: XOR of all bytes in the data string
    public static int computeChecksum(String data) {
        int sum = 0;

        for (char c : data.toCharArray()) {
            sum += (int) c;
        }

        while (sum > 0xFF) { 
            sum = (sum & 0xFF) + (sum >> 8);
        }

        int checksum = ~sum & 0xFF;

        return checksum;
    }

    public boolean isCorrupted() {
        return computeChecksum(this.data) != this.checksum;
    }

    public void corrupt() {
        this.checksum ^= 0xFF;
        this.corruptedOnce=false;
    }

    @Override
    public String toString() {
        if (isACK) return String.format("[ACK  | seq=%d | %s -> %s]", ackNum, srcMAC, destMAC);
        if (isNAK) return String.format("[NAK  | seq=%d | %s -> %s]", ackNum, srcMAC, destMAC);
        return String.format("[FRAME| seq=%-2d | %s -> %s | data=\"%s\" | chk=0x%02X]",
                seqNum, srcMAC, destMAC, data, checksum);
    }
}
