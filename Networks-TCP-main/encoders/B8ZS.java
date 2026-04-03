package encoders;

public class B8ZS {
    public String encode(String data) {
        StringBuilder result = new StringBuilder();
        char lastSign = '-';   
        int countOfZeros = 0;
        int index0 = -1;  

        for (int i = 0; i < data.length(); i++) {
            char bit = data.charAt(i);

            if (bit == '1') {
                lastSign = (lastSign == '+') ? '-' : '+';
                result.append(lastSign).append("V ");
                countOfZeros = 0;
                index0 = -1;
            } else { 
                if (countOfZeros == 0) {
                    index0 = result.length();
                }
                countOfZeros++;
                result.append("0 ");

                if (countOfZeros == 8) {
                    result.delete(index0, result.length()); 

                    if (lastSign == '+') {
                        result.append("0 0 0 -V +V 0 +V -V ");
                        lastSign = '-';
                    } else {
                        result.append("0 0 0 +V -V 0 -V +V ");
                        lastSign = '+';
                    }
                    countOfZeros = 0;
                    index0 = -1;
                }
            }
        }

        return result.toString().trim();
    }
}
// When 8 consecutive zeros occur, remove those zeros and insert the B8ZS substitution "000VB0VB".
// Here B = same polarity as last mark, V = bipolar violation (opposite polarity) to preserve sync.
// we iterate over data , first toh we do basic AMI conversion . if 0 , then leave , if 1 , then check the previous sign , and invert it .
// in else , we count the zeros and increment index . 
// if i get 8 zeros , then check previous polarity and accordingly add 8 bits .