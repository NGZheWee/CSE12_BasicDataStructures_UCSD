import java.io.EOFException;

import java.io.*;

public class Driver1 {

	private static class MyRec extends Base {
		long xxx;
		Tracker tracker;

		public MyRec (long num, String caller) {
			xxx = num;
			caller += " calling Ctor";
			tracker = new Tracker ("MyRec",
					Size.of (xxx) + Size.of (tracker),
					caller);
		}
		public String toString () {
			return xxx + "";
		}
		public void jettison () {
			tracker.jettison ();
		}
	}

	private static int fromWhere (boolean fromInsert) {
		char character;
		int retval;
		try {
		MyLib.writeline ("Specify 1 for FRONT, 0 for END, or"
					  + " location number", System.out);

		MyLib.writeline ("\nPlease enter choice:  ", System.out);

		character = (char) MyLib.getchar ();
		MyLib.clrbuf (character);
		if (character == '\n' || !Character.isDigit (character)) {
			retval = -1;
		}
		else {
			retval = (int) character;
			retval -= (int) '0';
		}
		return retval;
		}
		catch (EOFException eof) {
			return -1;
		}
	}

	private static final short NULL = 0;
	public static void main (String[] args) {

		BaseStack.debugOff ();
		MyRec element;
		BaseStack baseStack;
		int option;
		char command;
		boolean status;
		String caller = "Driver.main";

		for (int index = 0; index < args.length; ++index) {
			if (args[index].equals ("-x"))
				BaseStack.debugOn ();
		}

		baseStack = new BaseStack (null, "baseStack in Driver.main");

		while (true) {
			command = NULL; // reset command each time in loop
			MyLib.writeline ("\nThe commands are:\n"
				+ "    is(e)mpty, (o)ccupancy, (c)heck memory,"
				+ "\n"
				+ "    (a)dvance pre, advance (n)ext,\n"
				+ "    p(u)sh, (p)op, (t)op,\n"
				+ "    (i)nsert, (r)emove, (v)iew\n"
				+ "    (w)rite, (W)rite reverse,\n"
				+ "\nPlease enter a command:  ", System.out);

			try {
				// We have to use try catch cuz -1
				MyLib.clrbuf ('\n');
				command = (char) MyLib.getchar ();
				MyLib.clrbuf (command); // get rid of return
			switch (command) {
			case 'a':
				baseStack.advancePre ();
				break;

			case 'n':
				baseStack.advanceNext ();
				break;

			case 'e':
				if (baseStack.isEmpty ()) {
					System.out.println ("Stack is empty");
				}
				else {
					System.out.println (
						"Stack is not empty");
				}
				break;

			case 'o':
				System.out.print ("Number of elements "
						+ "in the list is:  ");
				System.out.println (baseStack.getOccupancy());
				break;

			case 'c':
				Tracker.checkMemoryLeaks ();
				break;

			case 'i':
				System.out.print ("Please enter "
					+ "a number to insert into list: ");
				element = new MyRec ((int)MyLib.decin (),
					"Driver.main");
				MyLib.clrbuf ((char) 0);
				status = baseStack.insert (element,
					Driver1.fromWhere (true));
				if (!status) {
					System.err.println (
						"\nWARNING: insert FAILED");
				}
				break;

			case 'r':
				element = (MyRec) baseStack.remove (
					Driver1.fromWhere (false));

				if (element == null)
					System.err.println (
						"\nWARNING:  remove FAILED\n");
				else {
					MyLib.writeline (
						"\nNumber removed "
						+ "from list is:  ",
						System.out);
					System.out.println (
						element.toString ());
					element.jettison ();
				}
				break;

			case 'W':
				baseStack.writeReverseList (System.err);
				break;
			case 'w':
				baseStack.writeList (System.err);
				break;
			case 'p':
				element = (MyRec) baseStack.pop ();
				if (element == null)
					System.err.println (
						"\nWARNING:  pop FAILED\n");
				else {
					MyLib.writeline (
						"\nNumber popped "
						+ "from list is:  ",
						System.out);
					System.out.println (
						element.toString ());
					element.jettison ();
				}
				break;
			case 't':
				element = (MyRec) baseStack.top ();
				if (element == null)
					System.err.println (
						"\nWARNING:  top FAILED\n");
				else {
					MyLib.writeline (
						"\nNumber topped from "
						+ "list is:  ",
						System.out);
					System.out.println (
						element.toString ());
				}
				break;
			case 'u':
				System.out.print("Please enter "
					+ "a number to push into stack: ");
				element = new MyRec ((int) MyLib.decin (),
					"Driver.main");
				MyLib.clrbuf ((char) 0);
				status = baseStack.push (element);
				if (!status) {
					System.err.println (
						"\nWARNING: push FAILED");
				}
				break;
			case 'v':
				element = (MyRec) baseStack.view (
					Driver1.fromWhere (false));
				if (element == null)
					System.err.println (
						"\nWARNING:  view FAILED\n");
				else {
					MyLib.writeline (
						"\nNumber viewed from "
						+ "list is:  ",
						System.out);
					System.out.println (
						element.toString ());
				}
				break;
			default:
				break;
			}
			}
			catch (Exception eof) {
				baseStack.jettison ();
				Tracker.checkMemoryLeaks ();
				break;
			}
		}
	}
}
