/**
 * Name:           Tracker Wonderdog
 * PID:            A12345678
 * USER:           cs12sp21zz
 * File name:      Calc.java
 * Description:    The classss file will help to read a postfix stack from
 *                 an infix stream and evaluate it finally.
 */
import java.io.*;      // System.in and System.out

/**
 * Class:            Calc
 * Description:      Implement the function to read a postfix stack from an
 *               infix stream
 *                   Implement how to evaluate a postfix stack
 *
 * Fields:           none
 *
 * Public functions: eval       - evaluate a postfix stack
 *                   intopost   - read a postfix stack from an infix stream
 */
public class Calc {
	// the calculator operation interface
	interface Operation {
		long operation (long op1, long op2);
	}

	// the calculator operations in priority order
	private static Operation[] functions = new Operation[] {
		null,
		null,
		new Operation () { public long operation (long op1, long op2)
		{ return add (op1, op2); }},
		new Operation () { public long operation (long op1, long op2)
		{ return sub (op1, op2); }},
		new Operation () { public long operation (long op1, long op2)
		{ return mult (op1, op2); }},
		new Operation () { public long operation (long op1, long op2)
		{ return divide (op1, op2); }},
		new Operation () { public long operation (long op1, long op2)
		{ return exponent (op1, op2); }},
		null,
		new Operation() { public long operation (long op1, long op2)
		{ return fact (op1, op2); }}
	};

	// maximum size of a calculator stack
	private static final int CALCSTACKSIZE = 100;

    //my magic number
    private static final int NUMBER1 = 1;
    private static final int NUMBER8 = 8;
    private static final int NUMBER10 = 10;

	// constants for EOF and true/false values
	private static final int
		EOF = -1,
		TRUE = 1,
		FALSE = 0,
		BYTE = 8;

	// array of operators, according to their priority
	private static final char[] operators = "()+-*/^ !".toCharArray ();

    // array of space character
    private static final char[] spaces = " \r\n\t".toCharArray();

	// sign bit for the operator when setupword is called
	private static final long SIGN_BIT = 1L << ((Long.BYTES << 3) - 1);

	/**
	 * Getter method to find index of operator in the operators array
	 *
	 * @param word: the operator word to get the index of
	 * @return long: index of operator in the operators array
	 */
	private static int getIndex (long word) {
		return (int)(word & 0xFF00) >> BYTE;
	}

	/**
	 * Getter method to extract the operator from a word
	 *
	 * @param word: the word to extract the operator from
	 * @return char: the operator as a char
	 */
	private static char getOperator (long word) {
		return (char) (word & 0xFF);
	}

	/**
	 * Getter method that returns the priority of an operator
	 *
	 * @param word: the word that contains the operator
	 * @return long: priority of the operator
	 */
	private static long getPriority (long word) {
		return (word & 0xFE00);
	}

	/**
	 * Checks if an item is an operator
	 *
	 * @param item: the item to check if it is an operator
	 * @return boolean:
	 *      true: item is an operator
	 *      false: item is not an operator
	 */
	private static boolean isOperator (long item) {
		return item < 0;
	}

	/**
	 * Checks if an item is a number
	 *
	 * @param item: the item to check if it is a number
	 * @return boolean:
	 *      true: item is a number
	 *      false: item is not a number
	 */
	private static boolean isNumber (long item) {
		return item >= 0;
	}

	/* [remove after use]
	   ALGORITHM FOR POST-FIX EVALUATION:
	   You will need 2 stacks for this algorithm.
	   Your function "eval" will take a reference to a stack (stack1) as
	   a parameter. It will use this stack as one of its needed stacks.
	   The other stack will be local.  After the evaluation, the parameter
	   stack will be empty.

	   Reverse stack1 onto stack2, then begin the evaluation:
	   While stack2 is not empty
	   	pop numbers from stack2, pushing all digits popped to stack1.
		Once a non-numbers is encountered from stack2,
		the appropriate number of operands will be popped from stack1
		and evaluated using the operator just popped from stack2.
		The result of this computation will be pushed on stack1.
	   The final result that remains on stack1 is the final result of the
	   expression.  This result is then used as the return value for this
	   function.  During the evaluation process,
	   stack2 holds positive integers and operators,
	   but stack1 only holds signed integers.
	 */
    public static long eval(LongStack stack1) {
        // a local stack that store the reverse of stack1
        LongStack stack2 = new LongStack(CALCSTACKSIZE, "stack3");
        while (!stack1.isEmptyStack()) {
            stack2.push(stack1.pop());
        }
        // Reverse stack1 onto stack2
        while (!stack2.isEmptyStack()) {
            // pop numbers from stack2, pushing all digits popped to stack1
            long word = stack2.top();
            stack2.pop();
            if (isNumber(word)) {
                stack1.push(word);
            }
            // Once a non-numbers is encountered from stack2
            else if (isOperator(word)) {
                // location index in operators
                int idx = getIndex(word);
                long word1 = 0;
                long word2 = 0;
                //the appropriate number of operands will be popped from stack1
                // it is factorial
                if (idx + NUMBER1 == operators.length) {
                    if (!stack1.isEmptyStack()) {
                        word1 = stack1.pop();
                    }
                } else {
                    if (!stack1.isEmptyStack()) {
                        word1 = stack1.pop();
                    }
                    if (!stack1.isEmptyStack()) {
                        word2 = stack1.pop();
                    }
                }
                //evaluated using the operator just popped from stack2
                stack1.push(functions[idx].operation(word1, word2));
            }
        }
        stack2.jettisonStack();
        long result = 0;
        // The final result that remains on stack1 is the final result of the
        // expression
        if (!stack1.isEmptyStack()) {
            result = stack1.pop();
        }
        return result;
    }

	/* [remove after use]
	   ALGORITHM FOR INFIX-TO-POSTFIX:
	   You will need 2 stacks for this algorithm.
	   Your function "intopost" will take a reference to a stack (stack1)
	   as a parameter.  It will use this stack as one of its needed stacks.
	   The other stack will be local.  The parameter stack will also serve
	   as the place to store the resultant post-fix expression.
	   The return value from intopost is either EOF when ^D is entered or
	   a non-zero value indicating that the function succeeded.

	   Process each character of the input in turn
	   if character is EOF, return EOF
	   if character is blank, then ignore it
	   if character is a digit, then continue to read digits until you read
	   	a non-digit, converting this number to decimal as you go.
		Store this decimal number on stack1
	   else if character is '(' then push it to stack2
	   else if character is ')' then repeatedly pop stack2, pushing all
	   	symbols popped from stack2 onto stack1 until the first '('
	   	encountered is popped from stack2.  Discard '(' and ')'
	   else repeatedly push to stack1 what is popped from stack2
		until stack2 is empty or stack2's top symbol has a lower
		priority than the character entered.  Then push the character
		onto stack2.
	   After processing all characters, pop anything remaining on stack2,
	   pushing all symbols popped from stack2 to stack1.  Stack1 now
	   contains the post-fix expression, in reverse order.
	 */
    public static int intopost(LongStack stack1) {
        // a local stack that store infix expression
        LongStack stack2 = new LongStack(CALCSTACKSIZE, "stack2");
        while (true) {
            // value is reading from stdin
            int value = MyLib.getchar();
            if (value == EOF) {
                stack2.jettisonStack();
                return EOF;
            }
            // end line
            if (value == 10) {
                break;
            }
            // is blank
            if (isSpaceChar((char) value)) {
                continue;
            }
            // is digit
            else if ('0' <= value && value <= '9') {
                // all digits accumulative value
                long digits = value - '0';
                while (true) {
                    // anotherValue is reading from stdin
                    int anotherValue = MyLib.getchar();
                    if (anotherValue == EOF) {
                        break;
                    } else if ('0' <= anotherValue && anotherValue <= '9') {
                        digits *= NUMBER10;
                        digits += anotherValue - '0';
                    } else {
                        MyLib.ungetc((char) anotherValue);
                        break;
                    }
                }
                stack1.push(digits);
            } else if (value == '(') {
                stack2.push(setupword((char) value));
            } else if (value == ')') {
                // pop a word from stack2
                Long word = null;
                // if word is null, it may be an expression error
                while ((word = stack2.pop()) != null) {
                    if (getOperator(word) == '(') {
                        break;
                    } else {
                        stack1.push(word);
                    }
                }
            } else {
                long valueWord = setupword((char) value);
                while (!stack2.isEmptyStack()) {
                    Long word = stack2.top();
                    if (getPriority(word) < getPriority(valueWord)) {
                        break;
                    }
                    stack1.push(stack2.pop());
                }
                stack2.push(valueWord);
            }
        }
        while (!stack2.isEmptyStack()) {
            stack1.push(stack2.pop());
        }
        stack2.jettisonStack();
        return 1;
    }

	/**
	 * Add two operands together
	 *
	 * @param augend: the first operand
	 * @param addend: the second operand
	 *
	 * @return long: the result of the adding augend and addend
	 */
	private static long add (long augend, long addend) {
		return augend + addend;
	}

	/**
	 * Divide the divisor by the dividend
	 *
	 * @param divisor: the long to be divided
	 * @param dividend: the long that divides the divisor
	 *
	 * @return long: the result of dividing the divisor by the dividend
	 */
	private static long divide (long divisor, long dividend) {
		return dividend / divisor;
	}

    /**
     * calculate base^power
     *
     * @param power: the long of a power
     * @param base:  the long of a base
     *
     * @return long: base^power
     */
    private static long exponent(long power, long base) {
        long result = NUMBER1;
        for (long i = 0; i < power; i++) {
            result *= base;
        }
        return result;
    }

    /**
     * calculate xxx*(xxx-1)*...*1
     *
     * @param xxx:     the long
     * @param ignored: any number
     *
     * @return long: xxx*(xxx-1)*...*1
     */
	private static long fact (long xxx, long ignored) {
        long result = NUMBER1;
        for (long i = NUMBER1; i <= xxx; i++) {
            result *= i;
        }
        return result;
	}

	/**
	 * Multiply two numbers together
	 *
	 * @param multiplier: the num by which the multiplicand is multiplied
	 * @param factory: the num to be multiplied
	 *
	 * @return long: the result of the multiplication
	 */
	private static long mult (long multiplier, long multiplicand) {
		return multiplier * multiplicand;
	}

	/**
	 * Subtract the minuend from the subtrahend
	 *
	 * @param subtrahend: the number to subtract from
	 * @param minuend: the number to subtract from subtrahend
	 *
	 * @return long: value of subtracting the minuend from subtrahend
	 */
	private static long sub (long subtrahend, long minuend) {
		return minuend - subtrahend;
	}

    /**
     * enclose character as long.
     * its layout:
     *      1) A distinction from numbers(sign bit is set)
     *      2) The index in the functions array corresponding to that operator
     *      3) The priority of that operator, it resides in 2).
     *      4) The ASCII code
     * @param character
     * @return long: enclosed character
     */
    private static long setupword(char character) {
        // return result
        long result = character;
        result |= SIGN_BIT;

        // the position in operators
        int index = 0;
        for (index = 0; index < operators.length; index++) {
            if (operators[index] == character) {
                break;
            }
        }
        index <<= NUMBER8;
        result |= (long) index;
        return result;
    }
    /**
     * check whether c is in spaces or not?
     * @param c char
     * @return return boolean: whether c is in spaces or not?
     */
    private static boolean isSpaceChar(char c) {
        for (int i = 0; i < spaces.length; i++) {
            if (spaces[i] == c) {
                return true;
            }
        }
        return false;
    }
}
