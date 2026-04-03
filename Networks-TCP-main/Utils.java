
public class Utils {

    public static String stringToBits(String input) {
        StringBuilder bits = new StringBuilder();
        for (char c : input.toCharArray()) {
            bits.append(Integer.toBinaryString(c)).append(" ");
        }
        return bits.toString();
    }
}