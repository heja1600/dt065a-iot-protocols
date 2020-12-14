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

    public static String bytesToString(byte [] bytes) {
        StringBuilder builder = new StringBuilder();
        for(byte _byte : bytes) {
            builder.append(ByteUtil.byteToString(_byte) + "\n");
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
        System.out.println(bytesToString(bytes));
    }

    public static int boolArrayToInteger(boolean [] bools) {
        byte val = 0;
        for(int i = 0; i < bools.length; i++) {
            val <<= 1;
            if (bools[i]) val |= 1;
        }

        return val & ((int)Math.pow(2, bools.length) - 1);
    }
}
