/**
 * Name:           Tracker Wonderdog
 * PID:            A12345678
 * USER:           cs12sp21zz
 * File name:      Driver.java
 * Description:    The class file will help to test Heap.
 *
 **/
import java.io.*;

/**
  * Class:            UCSDStudent
  * Description:      used to store information about a student.
  *
  * Fields:           name             - name stored in the node
  *                   studentNum       - studentNum stored in the node
  *                   tracker          - used to track memory
  *
  * Public functions: UCSDStudent     - constructor function
  *                   jettison        - free up the student from the tracker.
  *                   equals          - check the equality between students
  *                   getName         - get name of the student
  *                   getTrimName     - get name with no space in head or tail
  *                   isLessThan      - comapare between students
  *                   toString        - get the formatted string
*/
class UCSDStudent extends Base {

	private String name;
	private long studentNum;
	private Tracker tracker;

	/*
	 * Constructor for the UCSDStudent object given a name and student
	 * number. Tracks the memory associated with the UCSDStudent.
	 *
	 * @param nm the name of the UCSDStudent being created
	 * @param sn the student number of the UCSDStudent being created
	 */
	public UCSDStudent (String nm, long sn) {
		tracker = new Tracker ("UCSDStudent",
		nm.length ()
		+ Size.of (studentNum)
		+ Size.of (tracker),
		"UCSDStudent Ctor");

		studentNum = sn;
		name = nm;
		
	}

	/**
	 * @return Base: return a copied student
	 */
	public Base copy () {  
        return new UCSDStudent (name, studentNum);  
    }  
  

	/**
     * jettison from tracker.
     */
    public void jettison () {
		if(tracker != null) {
			tracker.jettison();
			tracker = null;
		}
    }

    /**
     * @param object Object: another student object
     * @return boolean : return true if they jave same same.
     */
    public boolean equals (Object object) {
        if (this == object)
            return true;

        if (!(object instanceof UCSDStudent))
            return false;

        UCSDStudent otherVar = (UCSDStudent) object;

        return name.equals(otherVar.getName());
    }

    /**
     * @return string : student name
     */
    public String getName () {
        return name;
    }

    /**
     * @param bbb Base : another student
     * @return boolean : this.name < bbb.name
     */
    public boolean isLessThan (Base bbb) {
        return (name.compareTo (bbb.getName ()) < 0) ? true : false;
    }

	/**
	 * @return String: a formatted string with name and number.
	 */
	public String toString() {

		return "name:  " + name + " with studentNum:  " + studentNum;
	}
}

/**
  * Class:            Driver
  * Description:      used to test the Heap class.
  *
*/
public class Driver {
	private static final int NULL = 0, EOF = -1;
	private static final String SIZE_INPUT 
	            = "Please enter the number of objects to be able to store:  ";
	private static final String END_LINE = "\n";
	private static final String  MENU1 = 
	"Please enter a command:  (c)heck memory, (d)ebug on (i)nsert, (w)rite:  ";
	private static final String  MENU2 = 
	"Please enter a command:  (c)heck memory, (d)ebug on (r)emove, (w)rite:  ";
	private static final String  MENU3 = 
	"Please enter a command:  (c)heck memory, (d)ebug on (i)nsert, (r)emove, "
	+"(w)rite:  ";
	private static final String  MENU4 = 
	"Please enter a command:  (c)heck memory, (d)ebug off (i)nsert, (w)rite:  ";
	private static final String  MENU5 = 
	"Please enter a command:  (c)heck memory, (d)ebug off (r)emove, (w)rite:  ";
	private static final String  MENU6 = 
	"Please enter a command:  (c)heck memory, (d)ebug off (i)nsert, (r)emove, "
	+"(w)rite:  ";

	public static void main(String[] args) {

		/* initialize debug states */
		Heap.debugOff();

		Writer os = new FlushingPrintWriter(System.out, true);
		Reader is = new InputStreamReader(System.in);
		long size = 0;
		try {

			/**
			 * process invalid input or not positive size.
			 */
			os.write(END_LINE);
			while (size <= 0) {
				os.write(SIZE_INPUT);
				while (true) {
					int c = MyLib.getchar(is);
					if (c == EOF) {
						return;
					}
					if (Character.isDigit(c) || c == '-' || c == '+') {
						MyLib.ungetc((char) c);
						break;
					} else {
						System.out.println();
						return;
					}
				}
				size = MyLib.decin(is);
				MyLib.clrbuf((char) 0, is);
			}
		} catch (IOException e) {
			return;
		}
		if (size > 0) {
			/* The real start of the code */
			SymTab<UCSDStudent> symtab = new SymTab<UCSDStudent>((int)size, 
																	  "Driver");

			String buffer = null;
			int command;
			long number = 0;

			System.out.println("Initial Heap:\n" + symtab);

			while (true) {
				try {
					command = NULL; // reset command each time in loop

					/**
					 * the menu should change according to heap state.
					 */
					if (!Heap.getDebug()) {
						if (symtab.isEmpty()) {
							os.write(MENU1);
						} else {
							if (symtab.isFull()) {
								os.write(MENU2);
							} else {
								os.write(MENU3);
							}
						}
					} else {
						if (symtab.isEmpty()) {
							os.write(MENU4);
						} else {
							if (symtab.isFull()) {
								os.write(MENU5);
							} else {
								os.write(MENU6);
							}
						}
					}
					command = MyLib.getchar(is);

					if (command == EOF) {
						break;
					}

					if (command != EOF)
						MyLib.clrbuf((char) command, is);

					boolean invalidCommand = false;
					switch (command) {
						case 'c':
							Tracker.checkMemoryLeaks();
							System.out.println();

							break;

						case 'i':
							if (!symtab.isFull()) {
								os.write("Please enter UCSD student name to i"
								                                   +"nsert:  ");

								buffer = MyLib.getline(is);

								os.write("Please enter UCSD student number:  ");

								number = MyLib.decin(is);
								MyLib.clrbuf((char) command, is);

								symtab.insert(new UCSDStudent(buffer, number));
							}
							break;
						case 'd':
							if(!Heap.getDebug()) {
								Heap.debugOn();
							} else {
								Heap.debugOff();
							}
							break;

						case 'r':

							if(!symtab.isEmpty())
							{
								// data to be removed
								UCSDStudent removed = symtab.remove();
								if (removed != null) {
									System.out.println("Student " 
									                            + "removed!!!");
									System.out.println(removed);

									removed.jettison();
									removed = null;
								} else
									System.out.println("student " + buffer 
									                           + " not there!");
							}
							break;

						case 'w':
							System.out.println(symtab);
							break;
						default:
							invalidCommand = true;
							break;
					}

					if (invalidCommand) {
						/**
						 * if any invalid command is detected, exit the while 
						 * loop.
						 */
						break;
					}
				} catch (IOException ioe) {
					System.err.println("IOException in Driver main");
				}
			}

			System.out.println("\nFinal Heap:\n" + symtab);

			symtab.jettison();
			Tracker.checkMemoryLeaks();
		}

	}
}
