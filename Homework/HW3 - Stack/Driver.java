import java.io.*;   // System.in and System.out  
  
public class Driver {  
    private static final int FIRST_STACK_SIZE = 10;  
    public static void main (String[] args) {  
  
        LongStack mainStack = new LongStack (FIRST_STACK_SIZE,   
            "first mainStack in Driver.main");  // the test stack  
        int amount;         // max numbers of items on stack  
        char command;       // stack command entered by user  
        Long item;          // item to go on stack  
        char option;        // the command line option  
        boolean status;     // return status of stack functions  
  
        LongStack.debugOff (); // initialize debug states  
  
        // check command line options   
        for (int index = 0; index < args.length; ++index) {  
            if (args[index].equals ("-x"))  
                LongStack.debugOn ();  
        }  
  
        while (true) {  
            command = 0;    // initialize command, need for loops  
            MyLib.writeline (  
                "\nPlease enter a command:"  
                + "\n\t(a)llocate, "   
                + "(c)apacity, "  
                + "(C)heck memory, "      
                + "is(e)mpty, "   
                + "(E)mpty, "  
                + "is(f)ull, "  
                + "(j)ettison, "  
                + "\n\t"  
                + "(o)ccupancy, "   
                + "(p)op, "   
                + "(t)op, "   
                + "p(u)sh, "  
                + "(w)rite to System.out, "  
                + "(W)rite to System.err.\n"  
                + "Please enter choice:  ", System.out);  
  
            try {  
                command = MyLib.getchar ();  
                  
                // get rid of extra characters  
                MyLib.clrbuf (command);   
  
            // process commands  
            switch (command) {   
  
            case 'a':   // allocate  
                MyLib.writeline ("Please enter the number of"  
                    + " objects to be able to store:  ",  
                    System.out);  
                amount = (int) MyLib.decin ();  
  
                // get rid of extra characters  
                MyLib.clrbuf ((char) 0);  
  
                mainStack.jettisonStack ();  
  
                mainStack = new LongStack (amount,   
                    "mainStack in Driver.main");  
  
                break;  
              
            case 'C':   // check memory leaks  
                Tracker.checkMemoryLeaks ();  
                break;  
  
            case 'c':   // check capacity  
                MyLib.writeline (  
                    "The capacity of the stack is: "   
                    + mainStack.getCapacity () + ".\n",   
                    System.out);  
                break;            
  
            case 'e':   // isempty  
                if (mainStack.isEmptyStack ()) {  
                    MyLib.writeline ("Stack is empty.\n",   
                        System.out);  
                }  
                else {  
                    MyLib.writeline ("Stack is "  
                        + "not empty.\n",  
                        System.out);  
                }  
                break;  
              
            case 'E':   // empty  
                mainStack.emptyStack ();  
                MyLib.writeline ("Stack is empty.\n",   
                    System.out);  
  
                break;  
  
            case 'f':    // isfull  
                if (mainStack.isFullStack ())  
                    MyLib.writeline ("Stack is full.\n",   
                        System.out);  
                else  
                    MyLib.writeline ("Stack is not full.\n",   
                        System.out);  
  
                break;  
  
            case 'j':   // jettison  
                if (mainStack.jettisonStack ()) {  
                    MyLib.writeline (  
                        "Stack has been jettisoned.\n",  
                        System.out);  
                }  
  
                break;  
  
            case 'o':   // get_occupancy  
                MyLib.writeline ("Number of elements"   
                        + " on the stack is:  ",  
                System.out);  
                if (mainStack.getOccupancy () != null)  
                    MyLib.decout (  
                        mainStack.getOccupancy ());  
  
                MyLib.newline (System.out);  
                break;  
  
            case 'p':   // pop  
                item = mainStack.pop ();  
                if (item == null)  
                    System.err.print (  
                        "\nWARNING:  pop FAILED\n");  
                else {  
                    MyLib.writeline (  
                    "Number popped from the stack is:  ",  
                        System.out);  
                    MyLib.decout (item);  
                    MyLib.newline (System.out);  
                }  
  
                break;  
  
            case 't':   // top  
                item = mainStack.top ();  
                if (item == null)  
                    System.err.print (  
                        "\nWARNING:  top FAILED\n");  
                else {  
                    MyLib.writeline (  
                    "Number at top of the stack is:  ",  
                        System.out);  
                    MyLib.decout (item);  
                    MyLib.newline (System.out);  
                }  
  
                break;  
  
            case 'u':   // push  
                MyLib.writeline (  
                "\nPlease enter a number to push to stack:  ",  
                    System.out);  
                item = MyLib.decin ();  
  
                // get rid of extra characters  
                MyLib.clrbuf ((char) 0);   
                status = mainStack.push (item);  
                if (!status)  
                    System.err.print (  
                        "\nWARNING:  push FAILED\n");  
                  
                break;  
  
            case 'w':   // write  
                MyLib.writeline ("\nThe Stack contains:\n",  
                    System.out);  
                mainStack.writeStack (System.out);  
  
                break;  
  
            case 'W':   // write in stderr  
                MyLib.writeline ("\nThe Stack contains:\n",   
                        System.err);  
                mainStack.writeStack (System.err);  
  
                break;  
            }  
            }   
  
            catch (EOFException eof) {  
                System.err.println ();  
  
                break;  
            }  
        }  
  
        if (mainStack != null)  
            mainStack.jettisonStack (); // deallocate stack  
  
        Tracker.checkMemoryLeaks ();  
        MyLib.newline (System.out);  
    }  
}