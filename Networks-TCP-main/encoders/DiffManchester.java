package encoders;

public class DiffManchester {
    public String encode(String data) {
        StringBuilder result = new StringBuilder();
        char lastSign = '+';

        for (char bit : data.toCharArray()) {
            if (bit == '0') {
                lastSign = (lastSign == '+') ? '-' : '+';
            }
            result.append(lastSign == '+' ? "+V -V " : "-V +V ");
            lastSign = (lastSign == '+') ? '-' : '+';
        }
        return result.toString().trim();
    }
}
// It is a mixture of inversion of NRZ-I and RZ , 
// it also has a mid bit transition like Manchester 
// if bit is 1 , then same sign as before 
// if bit is 0 , then it inverts its sign
