package shared.util;

import java.math.BigInteger;

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

    public static byte [] integerToByteArray(Integer value) {
        BigInteger bigInt = BigInteger.valueOf(value);
        return bigInt.toByteArray();
    }

    public static byte [] stringToByteArray(String value) {
        return value.getBytes();
    }

    public static Integer byteArrayToInteger(byte [] bytes) {
        return new BigInteger(bytes).intValue();
    }

    public static String byteArrayToString(byte [] bytes) {
        return new String(bytes);
    }

    public static void printBytesAsString(byte [] bytes) {
        for(byte _byte : bytes) {
            System.out.println(ByteUtil.byteToString(_byte));
        }
    }
}
