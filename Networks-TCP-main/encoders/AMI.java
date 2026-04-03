package encoders;

public class AMI {
    public String encode(String data) {
        StringBuilder result = new StringBuilder();
        // default sign as -ve
        char lastSign = '-';

        for (char bit : data.toCharArray()) {
            if (bit == '1') {
                lastSign = (lastSign == '+') ? '-' : '+';
                result.append(lastSign).append("V ");
            } else {
                result.append("0 ");
            }
        }
        return result.toString().trim();
    }
}

// if bit is 0 then 0 
// if bit is 1 , then check last polarity and accordingly invert for every approaching 1s