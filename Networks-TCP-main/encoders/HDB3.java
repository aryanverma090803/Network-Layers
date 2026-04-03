package encoders;

public class HDB3 {
    public String encode(String data) {
        StringBuilder result = new StringBuilder();
        char lastSign = '-';    
        int count0 = 0;          
        int NonzeroBitCounter = 0;         
        int indexOfZero = -1;    

        for (int i = 0; i < data.length(); i++) {
            char bit = data.charAt(i);
            if (bit == '1') {
                lastSign = (lastSign == '+') ? '-' : '+';
                result.append(lastSign).append("V ");
                NonzeroBitCounter++;           
                count0 = 0;
                indexOfZero = -1;
            } else { 
                if (count0 == 0) {
                    indexOfZero = result.length();
                }
                count0++;
                result.append("0 ");

                if (count0 == 4) {
                    result.delete(indexOfZero, result.length());

                    if (NonzeroBitCounter % 2 == 1) {
                        char V = lastSign;         
                        result.append("0 0 0 ").append(V).append("V ");
                        lastSign = V;            
                    } else {
                        char opposite = (lastSign == '+') ? '-' : '+';
                        result.append(opposite).append("V 0 0 ").append(opposite).append("V ");
                        lastSign = opposite;      
                    }
                    count0 = 0;
                    NonzeroBitCounter = 0;
                    indexOfZero = -1;
                }
            }
        }

        return result.toString().trim();
    }
}
// normal AMI conversion , we will count the number of 0s , index of 0 and non zero bits counter .
// if we get 4 consecutive 0s , we replace those using the indexofzero
// if non zero bit counter is odd , then replace it with 000V , else with B00V 
// V - violation 
// B - balancing bit 