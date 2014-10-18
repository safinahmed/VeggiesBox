package com.veggiesbox.util;

import java.util.Random;

public class RandomString {
 
    /**
     * The random number generator.
     */
    protected static Random r = new Random();
 
    /* Set of characters that is valid. Must be printable, memorable,
     * and "won't break HTML" (i.e., not '<', '>', '&', '=', ...).
     * or break shell commands (i.e., not '<', '>', '$', '!', ...).
     * I, L and O are good to leave out, as are numeric zero and one.
     */
    protected static char[] goodChar = {
        // Comment out next two lines to make upper-case-only, then
        // use String toUpper() on the user's input before validating.
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N',
        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1',
        '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };

    /* Generate random String */
    public static String getNext(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(goodChar[r.nextInt(goodChar.length)]);
        }
        return sb.toString();
    }
}