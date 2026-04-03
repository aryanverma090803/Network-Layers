package encoders;

public class NRZL {
    public String encode(String data) {
        StringBuilder result = new StringBuilder();
        for (char bit : data.toCharArray()) {
            result.append(bit == '1' ? "+V " : "-V ");
        }
        return result.toString().trim();
    }
}

// convert to char array , and iterate one by one 
// if 1 , then append +V
// if 0 , then append -V