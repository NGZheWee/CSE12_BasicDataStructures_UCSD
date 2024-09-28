import java.io.EOFException;

import java.io.*;

public class Driver2 {

	public static boolean copy = true;
	private static class MyRec extends Base {
		long xxx;
		Tracker tracker;
		public MyRec (String caller) {
			xxx = 0;

			caller += " calling Ctor";

			tracker = new Tracker ("MyRec",
					Size.of (xxx) + Size.of (tracker),
					caller);
		}
		public MyRec (long num, String caller) {
			xxx = num;
			caller += " calling Ctor";

			tracker = new Tracker ("MyRec", Size.of(xxx), caller);
		}
		public String toString () {
			return xxx + "";
		}
		public void jettison () {
			tracker.jettison ();
		}
		public Base copy (Base element) {
			MyRec myRec = (MyRec) element;
			MyRec newRec = new MyRec (myRec.xxx, "MyRec.copy");
			return newRec;
		}
	}

	private static int fromWhere (boolean fromInsert) {
		char character;
		int retval;
		try {
		MyLib.writeline ("Specify 1 for FRONT, 0 for END, or "
				+ "location number", System.out);

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
		MyRec element = new MyRec ("element in Driver.main");
		BaseStack baseStack;
		int option;
		char command;
		boolean status;
		String caller = "Driver.main";

		for (int index = 0; index < args.length; ++index) {
			if (args[index].equals ("-x"))
				BaseStack.debugOn ();
		}

		baseStack = new BaseStack (element, "baseStack in Driver.main");

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
						} else {
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
						element.xxx = (int) MyLib.decin ();
						MyLib.clrbuf ((char) 0);
						status = baseStack.insert (element,
								Driver2.fromWhere (true));
						if (! status) {
							System.err.println (
									"\nWARNING: insert FAILED");
						}
						break;

					case 'r':
						element.jettison ();
						element = (MyRec) baseStack.remove(
								Driver2.fromWhere (false));

						if (element == null) {
							System.err.println (
									"\nWARNING:  remove FAILED\n");
							element = new MyRec (
									"element in Driver.main");
						}

						else {
							MyLib.writeline (
									"\nNumber removed from "
									+ "list is:  ",
									System.out);
							System.out.println (
									element.toString ());
						}
						break;

					case 'W':
						baseStack.writeReverseList (System.out);
						break;
					case 'w':
						baseStack.writeList (System.out);
						break;
					case 'p':
						element.jettison ();
						element = (MyRec) baseStack.pop ();
						if (element == null) {
							System.err.println (
									"\nWARNING:  pop FAILED\n");
							element = new MyRec (
									"element in Driver.main");
						}
						else {
							MyLib.writeline (
									"\nNumber popped from "
									+ "list is:  ", System.out);
							System.out.println (
									element.toString ());
						}
						break;
					case 't':
						element.jettison ();
						element = (MyRec) baseStack.top ();
						if (element == null) {
							System.err.println (
									"\nWARNING:  top FAILED\n");
							element = new MyRec (
									"element in Driver.main");
						}
						else {
							MyLib.writeline (
									"\nNumber topped from "
									+ "list is:  ", System.out);
							System.out.println(
									element.toString ());
							element = new MyRec (
									"element in Driver.main");

						}
						break;
					case 'u':
						System.out.print("Please enter "
								+ "a number to push into stack: ");
						element.xxx = (int) MyLib.decin ();
						MyLib.clrbuf ((char) 0);
						status = baseStack.push (element);
						if (! status) {
							System.err.println (
									"\nWARNING: push FAILED");
						}
						break;
					case 'v':
						element.jettison ();
						element = (MyRec) baseStack.view(
								Driver2.fromWhere (false));

						if (element == null) {
							System.err.println (
									"\nWARNING:  view FAILED\n");
							element = new MyRec (
									"element in Driver.main");
						}
						else {
							MyLib.writeline (
									"\nNumber viewed from "
									+ "list is:  ",
									System.out);
							System.out.println (
									element.toString ());
							element = new MyRec (
									"element in Driver.main");

						}
						break;
					default:
						break;
				}
			}
			catch (Exception eof) {
				baseStack.jettison ();
				element.jettison ();
				Tracker.checkMemoryLeaks ();
				break;
			}
		}
	}
}
