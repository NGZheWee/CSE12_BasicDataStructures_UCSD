
/****************************************************************************** 
 *                                                           CSE 12, SP21 
 *                                                           April 14th, 2021 
 *                                                           cs12bjh 
 *                                 Assignment 3 
 * File Name:   LongStack.java 
 *****************************************************************************/
import java.io.*;  
  
public class LongStack {  
  
    private static boolean debug; // debug option  
    private static int stackCounter = 0; // number of stacks allocated so far  
  
    private class LongStackEngine {  
  
        // catastrophic error messages  
        static final String   
            POP_EMPTY = "Popping from an empty stack!!!\n",  
            PUSH_FULL = "Pushing to a full stack!!!\n",  
            TOP_EMPTY = "Topping from an empty stack!!!\n",  
            WRITE_NONEXIST_FILE   
                = "Attempt to write using non-existent"  
                + " file pointer!!!\n";  
  
        // Debug messags  
        // HEX messages are used for negative values, used in hw4  
        static final String  
            ALLOCATE = "[Stack %d has been allocated]\n",  
            EMPTY = "[Stack %d - Emptied]\n",  
            JETTISON = "[Stack %d has been jettisoned]\n",  
            HEXPOP = "[Stack %d - Popping 0x%x]\n",  
            HEXPUSH = "[Stack %d - Pushing 0x%x]\n",  
            HEXTOP = "[Stack %d - Topping 0x%x]\n",  
            POP = "[Stack %d - Popping %d]\n",  
            PUSH = "[Stack %d - Pushing %d]\n",  
            TOP = "[Stack %d - Topping %d]\n";  
  
        long[] stack;       // array to hold the data in stack order  
        int stackPointer;   // index of the last occupied space  
        int stackSize;      // size of the stack  
        int stackID;        // which stack we are using  
        Tracker tracker;    // to keep track of memory usage  
  
        LongStackEngine (int stackSize, String caller) {  
  
            // allocate a new array to represent the stack  
            stack = new long[stackSize];   
  
            // hold the memory size of the LongStackEngine object  
            long size = Size.of (stackPointer) + Size.of (stackSize)   
                    + Size.of (stackID) + Size.of (stack);  
            tracker = new Tracker ("LongStackEngine", size, caller);  
            
            //initiate them
            this.stackSize = stack.length;
            this.stackPointer = 0;
            this.stackID = stackCounter + 1;

            if (debug) {
                System.err.print(String.format(ALLOCATE, stackID));
            }

            // update it for next usage.
            stackCounter++;
        }  

        void jettisonStackEngine() {
            if (debug) {
                System.err.print(String.format(JETTISON, stackID));
            }
            tracker.jettison();
            stackCounter--;

        }

        void emptyStack() {
            if (debug) {
                System.err.print(String.format(EMPTY, stackID));
            }
            // only modify stackPointer.
            this.stackPointer = 0;
        }

        Integer getCapacity() {
            return stackSize;
        }

        Integer getOccupancy() {
            return stackPointer;
        }

        boolean isEmptyStack() {
            return stackPointer == 0;
        }

        boolean isFullStack() {
            //the next insert position is stack size.
            return stackPointer == stackSize;
        }

        Long pop() {
            //the returned result.
            Long result = null;
            if (!isEmptyStack()) {
                // 'stackPointer-1' is the top of the stack.
                result = stack[stackPointer - 1];
                if (debug) {
                    System.err.print(String.format(POP, stackID, result));
                }
                //move stackPointer forward by 1
                stackPointer--;
            } else {
                // error check
                if (debug) {
                    System.err.print(POP_EMPTY);
                }
            }
            return result;
        }

        boolean push(long item) {
            if (isFullStack()) {
                // error check
                if (debug) {
                    System.err.print(PUSH_FULL);
                }
                return false;
            } else {
                if (debug) {
                    System.err.print(String.format(PUSH, stackID, item));
                }
                stack[stackPointer] = item;
                // move pointer by one backwrads
                stackPointer++;
                return true;
            }
        }
  
        Long top() {
            if (!isEmptyStack()) {
                //the returned result.
                Long result = stack[stackPointer - 1];
                if (debug) {
                    System.err.print(String.format(TOP, stackID, result));
                }
                return result;
            } else {
                //error check
                if (debug) {
                    System.err.print(TOP_EMPTY);
                }
                return null;
            }
        }
  
        void writeStack (PrintStream stream) {  
  
            int index = 0;  // index into the stack  
  
            if (stream == null) {  
                System.err.print (WRITE_NONEXIST_FILE);  
                return;  
            }  
  
            int stackOccupancy = getOccupancy ();  
  
            if (stream.equals (System.err)) {  
                stream.print (  
                "\nStack " + stackID + ":\n"  
                + "Stack's capacity is " + stackSize + ".\n"  
                + "Stack has "  
                + stackOccupancy + " item(s) in it.\n"  
                + "Stack can store "  
                + (stackSize - stackOccupancy)   
                + " more item(s).\n");  
                Tracker.checkMemoryLeaks ();  
            }  
  
            for (index = 0; index < stackOccupancy; index++) {  
                if (stream.equals (System.err))  
                    stream.print (String.format (  
                        "Value on stack is |0x%x|\n",   
                        stack[index]));  
                else {  
                    if (stack[index] < 0)  
                        stream.print (String.format (  
                            "%c ",   
                            (char) stack[index]));  
                    else  
                        stream.print (String.format (  
                            "%d ", stack[index]));  
                }  
            }  
        }  
    }  
  
    // PROVIDED INFRASTRUCTURE BELOW, YOUR CODE SHOULD GO ABOVE  
    // CHANGING THE CODE BELOW WILL RESULT IN POINT DEDUCTIONS  
  
    // catastrophic error messages  
    private static final String   
    EMPTY_NONEXIST = "Emptying a non-existent stack!!!\n",  
    ISEMPTY_NONEXIST = "Isempty check from a non-existent stack!!!\n",  
    ISFULL_NONEXIST = "Isfull check from a non-existent stack!!!\n",  
    JETTISON_NONEXIST = "Jettisoning a non-existent stack!!!\n",  
    NUM_NONEXIST = "get_occupancy check from a non-existent stack!!!\n",  
    POP_NONEXIST = "Popping from a non-existent stack!!!\n",  
    PUSH_NONEXIST = "Pushing to a non-existent stack!!!\n",  
    TOP_NONEXIST = "Topping from a non-existent stack!!!\n",  
    WRITE_NONEXIST_STACK = "Attempt to write to a non-existent stack!!!\n";  
  
    private LongStackEngine stackEngine; // the object that holds the data  
  
    public LongStack (int stackSize, String caller) {  
        stackEngine = new LongStackEngine (stackSize, caller);  
    }  
  
    // Debug state methods  
    public static void debugOn () {  
        debug = true;  
    }  
  
    public static void debugOff () {  
        debug = false;  
    }  
  
    public boolean jettisonStack () {  
  
        // ensure stack exists  
        if (!exists ()) {  
            System.err.print (JETTISON_NONEXIST);  
            return false;  
        }  
  
        stackEngine.jettisonStackEngine ();  
        stackEngine = null;  
        return true;  
    }  
  
    public void emptyStack () {  
  
        // ensure stack exists  
        if (!exists ()) {  
            System.err.print (EMPTY_NONEXIST);  
            return;  
        }  
  
        stackEngine.emptyStack ();  
    }  
  
    private boolean exists () {  
        return !(stackEngine == null);  
    }  
  
    public Integer getCapacity () {  
              
        // ensure stack exists  
        if (!exists ()) {  
            System.err.print ("CAPACITY_NONEXIST");//where is CAPACITY_NONEXIST
            return null;  
        }  
          
        return stackEngine.getCapacity ();  
    }  
  
    public Integer getOccupancy () {  
  
        // ensure stack exists  
        if (!exists ()) {  
            System.err.print (NUM_NONEXIST);  
            return null;  
        }  
  
        return stackEngine.getOccupancy ();  
    }  
  
    public boolean isEmptyStack () {  
  
        // ensure stack exists  
        if (!exists ()) {  
            System.err.print (ISEMPTY_NONEXIST);  
            return false;  
        }  
  
        return stackEngine.isEmptyStack ();  
    }  
  
    public boolean isFullStack () {  
  
            // ensure stack exists  
        if (!exists ()) {  
            System.err.print (ISFULL_NONEXIST);  
            return false;  
        }  
  
        return stackEngine.isFullStack ();  
    }  
  
    public Long pop () {  
  
        // ensure stack exists  
        if (!exists ()) {  
            System.err.print (POP_NONEXIST);  
            return null;  
        }  
  
        return stackEngine.pop ();  
    }  
  
    public boolean push (long item) {  
  
        // ensure stack exists  
        if (!exists ()) {  
            System.err.print (PUSH_NONEXIST);  
            return false;  
        }  
  
        return stackEngine.push (item);  
    }  
  
    public Long top () {  
  
        // ensure stack exists  
        if (!exists ()) {  
            System.err.print (TOP_NONEXIST);  
            return null;  
        }  
  
        return stackEngine.top ();  
    }  
  
    public void writeStack (PrintStream stream) {  
  
        // ensure stack exists  
        if (!exists ()) {  
            System.err.print (WRITE_NONEXIST_STACK);  
        return;  
        }  
  
        stackEngine.writeStack (stream);  
    }  
}