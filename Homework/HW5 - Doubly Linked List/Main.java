import java.io.*;      // System.in and System.out

public class Main {
	private static final String
		INFIX_EXPRESSION
			= "\nPlease enter an expression to calculate:  ",
		POSTFIX_EXPRESSION
			= "\nThe expression in postfix order is:  ",
		RESULT
			= "\n\twhich evaluates to:  ";
	private static final int FALSE = 0;

	public static void main (String [] args) throws EOFException {
		char option;
		long intopost_status;   // return value from intopost
		BaseStack main_Stack;

		// initialize debug states
		BaseStack.debugOff ();

		// check for stack debug options
		for (int index = 0; index < args.length; ++index) {
			if (args[index].equals ("-x"))
				BaseStack.debugOn ();
		}

		// for the postfix expression
		main_Stack = new BaseStack (null, "main");

		while (true) {
			try {
			MyLib.writeline (INFIX_EXPRESSION, System.out);
			intopost_status = Calc.intopost (main_Stack);
			if (intopost_status == FALSE) {
				main_Stack.empty ();
				continue;
			}

			MyLib.writeline (POSTFIX_EXPRESSION, System.out);

			// Store the current occupancy of the stack
			long occupancy = main_Stack.getOccupancy ();
			// Iterate the stack to print the postfix exp
			for (int idx = 0; idx < occupancy; idx++) {
				main_Stack.advanceNext ();
				if (idx != 0) System.out.print(" ");
					CalcWord ret
						= (CalcWord) main_Stack.top ();
				if (ret.ascii != 0) {
					System.out.print (ret.ascii);
				}
				else {
					System.out.print (ret.value);
				}
			}

			MyLib.writeline (RESULT, System.out);
			MyLib.decout (Calc.eval (main_Stack));
			MyLib.newline ();


			} catch (EOFException e) {
				main_Stack.jettison ();
				Tracker.checkMemoryLeaks ();
				MyLib.newline ();
				break;
			}
		}
	}
}
