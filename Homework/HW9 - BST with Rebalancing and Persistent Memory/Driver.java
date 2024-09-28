/**
 * Name:           Tracker Wonderdog
 * PID:            A12345678
 * USER:           cs12sp21zz
 * File name:      Driver.java
 * Description:    The class file will help to test Tree.
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
  *                   count            - student count
  *
  * Public functions: UCSDStudent     - constructor function
  *                   jettison        - free up the student from the tracker.
  *                   equals          - check the equality between students
  *                   getName         - get name of the student
  *                   getTrimName     - get name with no space in head or tail
  *                   isLessThan      - comapare between students
  *                   toString        - get the formatted string
  *                   read            - read from file
  *                   write           - write to file
*/
class UCSDStudent extends Base {

	private String name;
	private long studentNum;
	private Tracker tracker;
	private static long counter = 0;
	private long count;

	/*
	 * Default constructor for the UCSDStudent object.
	 * Tracks the memory associated with the UCSDStudent object.
	 */
	public UCSDStudent () {
		tracker = new Tracker ("UCSDStudent " + count + " " + name,
		Size.of (name) 
		+ Size.of (studentNum)
		+ Size.of (count)
		+ Size.of (tracker),
		"UCSDStudent Ctor");

		count = ++counter;
		name = String.format ("%1$-14s", "default");
	}

	/*
	 * Constructor for the UCSDStudent object given a name and student
	 * number. Tracks the memory associated with the UCSDStudent.
	 *
	 * @param nm the name of the UCSDStudent being created
	 * @param sn the student number of the UCSDStudent being created
	 */
	public UCSDStudent (String nm, long sn) {
		tracker = new Tracker ("UCSDStudent " + count + " " + nm,
		nm.length ()
		+ Size.of (studentNum)
		+ Size.of (count)
		+ Size.of (tracker),
		"UCSDStudent Ctor");

		count = ++counter;
		name = String.format ("%1$-14s", nm);
		studentNum = sn;
	}

	/**
	 * @return Base: return a copied student
	 */
	public Base copy () {  
        return new UCSDStudent (name, studentNum);  
    }  
  

	/**
     * copy constructor: copy name and student number
     * @param student UCSDStudent
     */
    public UCSDStudent (UCSDStudent student) {  
        tracker = new Tracker("UCSDStudent",   
                Size.of (name) + Size.of (studentNum) + Size.of (tracker),  
                "UCSDStudent ctor"); 
		count = ++counter; 
        name = new String (student.name);  
        studentNum = student.studentNum;  
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
     * @param val long : new studentnum  
     * @return UCSDStudent: a new student with same properties.
     */
    public UCSDStudent assignValue (long val) {  
                  
        UCSDStudent retval;// return value  

        studentNum = val;  
        retval = new UCSDStudent (this);  

        return retval;  
    } 

	/**
	 * return name with no space in head or tail
	 */
	public String getTrimName () {
		return getName().trim();
	}

	/**
	 * read a student from the file.
	 * @param fio RandomAccessFile: source reading file
	 */ 
	public void read (RandomAccessFile fio) {  
		try {  
			name = fio.readUTF ();  
			studentNum = fio.readLong ();  
		}  
	
		catch (IOException ioe) {  
			System.err.println ("IOException in Variable Read");  
		}  
	}  
	
	/**
	 * write a student into the file.
	 * @param fio RandomAccessFile: target writing file
	 */
	public void write (RandomAccessFile fio) {  
	
		try {  
			fio.writeUTF (name);  
			fio.writeLong (studentNum);  
		}  
	
		catch (IOException ioe) {  
			System.err.println ("IOException in Variable Write");  
		}  
	} 

	public String toString () {
		if (Tree.getDebug ())
			return "UCSDStudent #" + count + ":  name:  " 
				+ name.trim () + "  studentnum:  " + studentNum;

		return "name:  " + name.trim () + "  studentnum:  "
			+ studentNum;
	}
}

/**
  * Class:            Driver
  * Description:      used to test the Tree class.
  *
*/
public class Driver {
	private static final int NULL = 0, FILE = 0, KEYBOARD = 1, EOF = -1;

	public static void main(String[] args) {

		/* initialize debug states */
		Tree.debugOff();

		/* check command line options */
		for (int index = 0; index < args.length; ++index) {
			if (args[index].equals("-x"))
				Tree.debugOn();
		}

		UCSDStudent sample = new UCSDStudent();
		/* The real start of the code */
		SymTab<UCSDStudent> symtab = new SymTab<UCSDStudent>("Driver.datafile",
		                                                    sample, "Driver");

		String buffer = null;
		int command;
		long number = 0;
		UCSDStudent stu = null;

		Writer os = new FlushingPrintWriter(System.out, true);
		Reader is = new InputStreamReader(System.in);
		int readingFrom = KEYBOARD;

		System.out.println("Initial Symbol Table:\n" + symtab);

		// SUGGESTED TEST STUDENT NUMBERS FOR VIEWING IN OCTAL DUMPS
		// 255, 32767, 65535, 8388607, 16777215
		// FF 7FFF FFFF 7FFFFF FFFFFF
		while (true) {
			try {
				command = NULL; // reset command each time in loop
				os.write("Please enter a command ((c)heck memory, " 
				                     + "(f)ile, (i)nsert, (l)ookup, (r)emove, "
						                                       + "(w)rite):  ");
				command = MyLib.getchar(is);

				if (command == EOF) {
					if(readingFrom == KEYBOARD) {
						break;	
					} else {
						/**
						 * restore is,os and readingFrom
						 */
						is = new InputStreamReader(System.in);
						os = new FlushingPrintWriter(System.out, true); 
						readingFrom = KEYBOARD;
						continue;
					}
				}

				if (command != EOF)
					MyLib.clrbuf((char) command, is);

				switch (command) {
					case 'c':
						Tracker.checkMemoryLeaks();
						System.out.println();

						break;

					case 'f':
						os.write("Please enter file name for commands:  ");

						/**
						 * read from file
						 * write to /dev/null
						 */
						buffer = MyLib.getline(is);
						is = new FileReader(buffer);
						os = new FileWriter("/dev/null");
						readingFrom = FILE;

						// DON'T FORGET A BREAK STATEMENT
						break;

					case 'i':
						os.write("Please enter UCSD student name to insert:  ");

						buffer = MyLib.getline(is);

						os.write("Please enter UCSD student number:  ");

						number = MyLib.decin(is);
						MyLib.clrbuf((char) command, is);

						// create student and place in symtab
						stu = new UCSDStudent(buffer, number);
						symtab.insert(stu);

						break;

					case 'l':
						UCSDStudent found;

						os.write("Please enter UCSD student name to " 
						                                         + "lookup:  ");
						buffer = MyLib.getline(is);

						stu = new UCSDStudent(buffer, 0);
						found = symtab.lookup(stu);
						stu.jettison();
						stu = null;

						if (found != null) {
							System.out.println("Student found!!!\n");
							System.out.println(found);

							found.jettison();
							found = null;
						} else
							System.out.println("student " + buffer 
							                                   + " not there!");

						break;

					case 'r':
						// data to be removed
						UCSDStudent removed;

						os.write("Please enter UCSD student name to remove:  ");

						buffer = MyLib.getline(is);

						stu = new UCSDStudent(buffer, 0);
						removed = symtab.remove(stu);

						stu.jettison();
						stu = null;

						if (removed != null) {
							System.out.println("Student " + "removed!!!");
							System.out.println(removed);

							removed.jettison();
							removed = null;
						} else
							System.out.println("student " + buffer 
							                                   + " not there!");

						break;

					case 'w':
						System.out.print("The Symbol Table " 
						                              + "contains:\n" + symtab);
						break;
				}
			} catch (IOException ioe) {
				System.err.println("IOException in Driver main");
			}
		}

		System.out.print("\nFinal Symbol Table:\n" + symtab);

		if (symtab.getOperation() != 0) {
			System.out.print("\nCost of operations:    ");
			System.out.print(symtab.getCost());
			System.out.print(" tree accesses");

			System.out.print("\nNumber of operations:  ");
			System.out.print(symtab.getOperation());

			System.out.print("\nAverage cost:          ");
			System.out.print(((float) (symtab.getCost())) / 
			                                           (symtab.getOperation()));
			System.out.print(" tree accesses/operation\n");
		} else {
			System.out.print("\nNo cost information available.\n");
		}
		symtab.jettison();
		sample.jettison();
		Tracker.checkMemoryLeaks();
	}
}
