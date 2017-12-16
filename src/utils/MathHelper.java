package utils;

public class MathHelper {

    /**
     * Mehtod returns integer that is represented by
     * sequence of 0 and 1 binary digits in given array.
     * <p>
     * For example, if given binary number is [1100], method
     * will return 12.
     */
    public static int getIntFromByteBinary(byte[] binaryNumber) {
        int number = 0;

        for (int i = 0; i < binaryNumber.length; i++) {
            if (binaryNumber[i] == (byte) 1) {
                number += Math.pow(2, binaryNumber.length - 1 - i);
            }
        }

        return number;
    }
}
