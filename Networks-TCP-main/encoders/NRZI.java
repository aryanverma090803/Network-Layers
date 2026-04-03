package encoders;

public class NRZI {
    public String encode(String data) {
        StringBuilder result = new StringBuilder();
        char lastSign = '-'; 

        for (char bit : data.toCharArray()) {
            if (bit == '1') {
                lastSign = (lastSign == '+') ? '-' : '+';
            }
            result.append(lastSign).append("V ");
        }
        return result.toString().trim();
    }
}

// if bit is 1 , then inversion . + to - || - to +
// if bit 0 , then simply append with lastsign