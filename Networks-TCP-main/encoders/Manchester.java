package encoders;

public class Manchester {
    public String encode(String data) {
        StringBuilder result = new StringBuilder();
        for (char bit : data.toCharArray()) {
            if (bit == '1')
                result.append("+V -V ");
            else
                result.append("-V +V ");
        }
        return result.toString().trim();
    }
}

// Manchester encoding is a simple mix of NRZ-L and RZ
// it has a mid bit transition
// if 1 , then one transition shifts from + to - , 
// if 0 , then one transition shifts from - to +