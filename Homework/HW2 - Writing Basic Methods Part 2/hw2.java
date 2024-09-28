
/****************************************************************************** 
 *                                                           CSE 12, SP21 
 *                                                           April 5th, 2021 
 *                                                           cs12bjh 
 *                                 Assignment 2 
 * File Name:   hw2.java 
 * Description: 
 * * As you already know in java when you pass literal strings like 
 * <P> 
 *   writeline("a literal string\n", stream); 
 * <P> 
 * In java it's automatically 
 * converted and treated as a String object.  Therefore  
 * the function writeline accepts literal strings and  
 * String types.  The getaline function returns a String type
 *****************************************************************************/

import java.io.*; // System.in and System.out  
import java.util.*; // Stack  

class MyLibCharacter {
    private Character character;

    public MyLibCharacter(int ch) {
        character = new Character((char) ch);
    }

    public char charValue() {
        return character.charValue();
    }

    public String toString() {
        return "" + character;
    }
}

public class hw2 {
    private static final int ASCII_ZERO = 48;

    private static final int CR = 13; // Carriage Return
    private static final int MAXLENGTH = 80; // Max string length

    private static final int EOF = -1; // process End Of File

    private static final long COUNT = 16; // # of hex digits

    private static final long DECIMAL = 10; // to indicate base 10
    private static final long HEX = 16; // to indicate base 16

    private static final int NUMBER32 = 32;//32 constant variable.

    private static final char digits[] = // for ASCII conversion
            new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    private static final String DEBUG_GETALINE = 
                        "[*DEBUG:  The length of the string just entered is ";

    private static final String DIGIT_STRING = "digit ";
    private static final String REENTER_NUMBER = "\nPlease reenter number: ";
    private static final String OUT_OF_RANGE = " out of range!!!\n";
    private static final String CAUSED_OVERFLOW = " caused overflow!!!\n";
    private static final String DEBUG_WRITELINE = 
                        "\n[*DEBUG:  The length of the string displayed is ";

    private static Stack<MyLibCharacter> InStream = new Stack<MyLibCharacter>();

    private static boolean debug_on = false;
    private static int hexCounter = 0; // counter for the number hex digits

    /*--------------------------------------------------------------------- 
        Copy your function header and code for baseout from hw1 
    ---------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------
    Function Name:   clrbuf
    Description:
     Removes any characters in the System.in buffer by repeatedly calling 
     fgetc until a newline character is detected. The method is implemented 
     by calling fgetc until the buffer is empty. You know the buffer is empty
     when the last character you removed was a a newline character '\n'.
    
    Input:
     Values of character are expected to be the most recent character read 
     from System.in.
    --------------------------------------------------------------------------*/
    public static void clrbuf(int character) {
        int readValue = 0;// read a value from std.in
        while (true) {
            readValue = fgetc(System.in);
            //notice for EOF.
            if (readValue == '\n' || readValue == EOF) {
                break;
            }
        }
    }
    /*--------------------------------------------------------------------------
    Function Name:     decin
    Description:
     Reads a decimal number from System.in and converts it from a sequence of 
     ASCII characters into a decimal integer. This converted value is returned. 
     When an overflow occurs or non-digit characters are entered, the user is 
     prompted to reenter the number using the digiterror function (provided). 
    Result:
     reading integer value.
    --------------------------------------------------------------------------*/
    public static long decin() {
        long result = 0;// returned value
        boolean readOccur = false;// a byte(not '\n') will set is true.
        while (true) {
            int readValue = fgetc(System.in);// read a value from System.in
            // check EOF or '\n'
            if (readValue == EOF) {
                if (!readOccur) {
                    return EOF;
                }
                break;
            } else if (readValue == '\n') {
                if (!readOccur)// the user input an empty line.
                {
                    continue;
                } else {
                    break;
                }
            }
            readOccur = true;
            // check non-digit characters
            if (!('0' <= readValue && readValue <= '9')) {
                digiterror(readValue, OUT_OF_RANGE);
                // reset them
                result = 0;
                readOccur = false;
                continue;
            } else {
                long newResult = result * 10;// do multiplication
                if (newResult / 10 != result) {
                    // check overflow
                    digiterror(readValue, CAUSED_OVERFLOW);
                    // reset them
                    result = 0;
                    readOccur = false;
                    continue;
                }
                result = newResult;
                newResult += (readValue - '0');// do addition
                if (newResult - (readValue - '0') != result) {
                    // check overflow
                    digiterror(readValue, CAUSED_OVERFLOW);
                    // reset them
                    result = 0;
                    readOccur = false;
                    continue;
                }
                result = newResult;
            }
        }
        return result;
    }

    /*--------------------------------------------------------------------- 
        Copy your function header and code for decout from hw1 
    ---------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------
    Function Name:      digiterror 
    Purpose:            This function handles erroneous user input. 
    Description:        This function  displays and error message to the user, 
                and asks for fresh input. 
    Input:              character:  The character that began the problem. 
                message:  The message to display to the user. 
    Result:             The message is displayed to the user. 
                The result in progress needs to be set to 0 in 
                decin after the call to digiterror. 
    --------------------------------------------------------------------------*/
    public static void digiterror(int character, String message) {

        /* handle error */
        clrbuf(character);

        /* output error message */
        writeline(DIGIT_STRING, System.err);
        fputc((char) character, System.err);
        writeline(message, System.err);

        writeline(REENTER_NUMBER, System.err);
    }

    // YOUR HEADER FOR getaline GOES HERE
    public static long getaline(char message[], int maxlength) {
        maxlength--;//remaing space for tailing zero.
        int readCount = 0;//current reading count
        boolean readAnyValid = false;// any byte(not EOF)will set it true.
        for (readCount = 0; readCount < maxlength; readCount++) {
            int readValue = fgetc(System.in);// read value from system.in
            if (readValue == EOF) {
                break;
            }
            readAnyValid = true;
            if (readValue == '\n') {
                break;
            }
            message[readCount] = (char) readValue;
            message[readCount + 1] = 0;
        }
        if (readCount == maxlength) {
            clrbuf(0);
        }
        if (debug_on) {
            System.err.printf("%s %d\n", DEBUG_WRITELINE, readCount);
        }
        return !readAnyValid ? EOF : readCount;
    }

    /*--------------------------------------------------------------------- 
        Copy your function header and code for hexout from hw1 
    ---------------------------------------------------------------------*/

    /**
     * Returns a character from the input stream.
     * 
     * @return <code>char</code>
     */
    public static int fgetc(InputStream stream) {

        char ToRet = '\0';

        // Check our local input stream first.
        // If it's empty read from System.in
        if (InStream.isEmpty()) {

            try {
                // Java likes giving the user the
                // CR character too. Dumb, so just
                // ignore it and read the next character
                // which should be the '\n'.
                ToRet = (char) stream.read();
                if (ToRet == CR)
                    ToRet = (char) stream.read();

                // check for EOF
                if ((int) ToRet == 0xFFFF)
                    return EOF;
            }

            // Catch any errors in IO.
            catch (EOFException eof) {

                // Throw EOF back to caller to handle
                return EOF;
            }

            catch (IOException ioe) {

                writeline("Unexpected IO Exception caught!\n", System.out);
                writeline(ioe.toString(), System.out);
            }

        }

        // Else just pop it from the InStream.
        else
            ToRet = ((MyLibCharacter) InStream.pop()).charValue();
        return ToRet;
    }

    /**
     * Displays a single character.
     * 
     * @param Character to display.
     */
    public static void fputc(char CharToDisp, PrintStream stream) {

        // Print a single character.
        stream.print(CharToDisp);

        // Flush the system.out buffer, now.
        stream.flush();
    }

    /*--------------------------------------------------------------------- 
        Copy your function header and code for newline() from hw1 
    ---------------------------------------------------------------------*/
    /**
     * Prints out a newline character.
     * 
     * @param Where to display, likely System.out or System.err.
     * 
     */
    public static void newline(PrintStream stream) {
        fputc('\n', stream);
    }

    /*--------------------------------------------------------------------- 
        Copy your function header and code for writeline() from hw1 
    ---------------------------------------------------------------------*/
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
        if (debug_on) {
            System.err.printf("%s %d\n", DEBUG_GETALINE, message.length());
        }
        return message.length();
    }

    /**
     * Places back a character into the input stream buffer.
     * 
     * @param A character to putback into the input buffer stream.
     */
    public static void ungetc(int ToPutBack) {

        // Push the char back on our local input stream buffer.
        InStream.push(new MyLibCharacter(ToPutBack));
    }

    public static void main(String[] args) {

        char buffer[] = new char[MAXLENGTH]; /* to hold string */

        long number; /* to hold number entered */
        long strlen; /* length of string */
        long base; /* to hold base entered */

        /* initialize debug states */
        debug_on = false;

        /* check command line options for debug display */
        for (int index = 0; index < args.length; ++index) {
            if (args[index].equals("-x"))
                debug_on = true;
        }

        /* infinite loop until user enters ^D */
        while (true) {
            writeline("\nPlease enter a string:  ", System.out);

            strlen = getaline(buffer, MAXLENGTH);
            newline(System.out);

            /* check for end of input */
            if (EOF == strlen)
                break;
            if ((strlen==1) && buffer[0]==('D')) {
            	break;
            }
            writeline("The string is:  ", System.out);
            writeline(new String(buffer), System.out);

            writeline("\nIts length is ", System.out);
            decout(strlen, System.out);
            newline(System.out);

            writeline("\nPlease enter a decimal number:  ", System.out);
            if ((number = decin()) == EOF)
                break;
            
            writeline("\nPlease enter a decimal base:  ", System.out);
            if ((base = decin()) == EOF)
                break;

            /* Correct for bases that are out of range */
            if (base < 2)
                base = 2;
            else if (base > 36)
                base = 36;

            newline(System.out);

            writeline("Number entered in base ", System.out);
            decout(base, System.out);
            writeline(" is: ", System.out);
            baseout(number, base, System.out);

            writeline("\nAnd in decimal is:  ", System.out);
            decout(number, System.out);

            writeline("\nAnd in hexidecimal is:  ", System.out);
            hexout(number, System.out);

            writeline("\nNumber entered multiplied by 8 is:  ", System.out);
            decout(number << 3, System.out);
            writeline("\nAnd in hexidecimal is:  ", System.out);
            hexout(number << 3, System.out);

            newline(System.out);
        }
    }

    /**
     * Takes in a positive number and displays in a given base.
     * 
     * @param Numeric value to be displayed.
     * @param Base    to used to display number.
     * @param Where   to display, likely System.out or System.err.
     */
    private static void baseout(long number, long base, PrintStream stream) {
        char[] bytes = new char[NUMBER32];// the pre-allocated bytes buffer
        // initiate it with 0.
        for (int i = 0; i < NUMBER32; i++) {
            bytes[i] = '0';
        }
        int length = 0;// the length of the final result
        do {
            bytes[length++] = digits[(int)(number % base)];
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
    public static void decout(long number, PrintStream stream) {
        baseout(number, 10, stream);
    }

    /**
     * Takes in a positive number and displays it in hex.
     * 
     * @param A     positive numeric value to be displayed in hex.
     * @param Where to display, likely System.out or System.err.
     */
    public static void hexout(long number, PrintStream stream) {
        // Output "0x" for hexidecimal.
        writeline("0x", stream);
        baseout(number, HEX, stream);
    }

}