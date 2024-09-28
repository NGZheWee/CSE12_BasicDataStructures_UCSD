
/****************************************************************************** 
 *                                                           Tracker Wonderdog 
 *                                                           CSE 12, SP21 
 *                                                           April 3rd, 2021 
 *                                                           cs12xzz 
 *                                 Assignment 1 
 * File Name:   hw1.java 
 * Description: This program prints strings and integers to System.out and  
 *              System.err. 
 *****************************************************************************/

import java.io.*;

public class hw1 {
    private static final int ADD = 12; // to add to the var in the main loop
    private static final int COUNT = 16; // number of hex digits to display
    private static final int DECIMAL = 10; // to indicate base 10
    private static final int HEX = 16; // to indicate base 16
    private static final int NUMBER32 = 32;

    private static final char digits[] = // used for ASCII conversion
            new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
    private static int hexCounter = 0; // counter for the number hex digits

    /**
     * Takes in a positive number and displays in a given base.
     * 
     * @param Numeric value to be displayed.
     * @param Base    to used to display number.
     * @param Where   to display, likely System.out or System.err.
     */
    private static void baseout(int number, int base, PrintStream stream) {
        char[] bytes = new char[NUMBER32];// the pre-allocated bytes buffer
        //initiate it with 0.
        for (int i = 0; i < NUMBER32; i++) {
            bytes[i] = '0';
        }
        int length = 0;// the length of the final result
        do {
            bytes[length++] = digits[number % base];
            number /= base;
        } while (number > 0);
        // when base is 16(a special case). we should show preceeding zero.
        if (base == 16) {
            length = 16;
        }

        // print in reverse order
        for (int i = length - 1; i >= 0; i--) {
            fputc(bytes[i], stream);
        }
    }

    /**
     * Takes in a positive number and displays it in decimal.
     * 
     * @param Positive numeric value to be displayed.
     * @param Where    to display, likely System.out or System.err.
     */
    public static void decout(int number, PrintStream stream) {
        baseout(number, 10, stream);
    }

    /**
     * Displays a single character.
     * 
     * @param Character to display.
     * @param Where     to display, likely System.out or System.err.
     */
    public static void fputc(char CharToDisp, PrintStream stream) {

        // Print a single character.
        stream.print(CharToDisp);

        // Flush the system.out buffer, now.
        stream.flush();
    }

    /**
     * Takes in a positive number and displays it in hex.
     * 
     * @param A     positive numeric value to be displayed in hex.
     * @param Where to display, likely System.out or System.err.
     */
    public static void hexout(int number, PrintStream stream) {
        // Output "0x" for hexidecimal.
        writeline("0x", stream);
        baseout(number, HEX, stream);
    }

    /**
     * Prints out a newline character.
     * 
     * @param Where to display, likely System.out or System.err.
     * 
     */
    public static void newline(PrintStream stream) {
        fputc('\n', stream);
    }

    /**
     * Prints out a string.
     * 
     * @param A     string to print out.
     * @param Where to display, likely System.out or System.err.
     * @return The length of the string.
     */
    public static int writeline(String message, PrintStream stream) {
        for (int i = 0; i < message.length(); i++) {
            fputc(message.charAt(i), stream);
        }
        return message.length();
    }

    public static void main(String[] args) {

        int element = 0;
        int count = 0;

        while (count < 3) {
            element += ADD;
            count++;
        }

        writeline("Hello World", System.err);
        newline(System.out);
        writeline("Ni Hao Shi Jie", System.out);
        newline(System.out);
        decout(123, System.out);
        newline(System.out);
        decout(0, System.out);
        newline(System.out);
        hexout(0xFEEDDAD, System.out);
        newline(System.out);
    }
}