package shared.util;

public class ByteUtil {
    public static int bitToDec(String binaryString) {
        int decimal = Integer.parseInt(binaryString, 2);  
        return decimal;
    }

    public static String byteToString(byte b) {
        byte[] masks = { -128, 64, 32, 16, 8, 4, 2, 1 };
        StringBuilder builder = new StringBuilder();
        for (byte m : masks) {
            if ((b & m) == m) {
                builder.append('1');
            } else {
                builder.append('0');
            }
        }
        return builder.toString();
    }

    public static void printBytesAsString(byte [] bytes) {
        for(byte _byte : bytes) {
            System.out.println(ByteUtil.byteToString(_byte));
        }
    }
}
