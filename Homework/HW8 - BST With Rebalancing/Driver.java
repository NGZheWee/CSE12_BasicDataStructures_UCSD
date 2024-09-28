
/**
 * Name:           Tracker Wonderdog
 * PID:            A12345678
 * USER:           cs12sp21zz
 * File name:      Driver.java
 * Description:    The class file will help to test Tree.
 *
*/
import java.io.*;


/**
  * Class:            UCSDStudent
  * Description:      used to store information about a student.
  *
  * Fields:           name             - name stored in the node
  *                   studentnum       - studentnum stored in the node
  *                   tracker          - used to track memory
  *
  * Public functions: UCSDStudent     - constructor function
  *                   jettison        - free up the student from the tracker.
  *                   equals          - check the equality between students
  *                   getName         - get name of the student
  *                   hashCode        - get the hash code of the student
  *                   isLessThan      - comapare between students
  *                   toString        - get the formatted string
*/
class UCSDStudent extends Base {
    public String name;    // name of variable
    private long studentnum;// number of variable
    private Tracker tracker;// to track memory

    /**
     * constructor function
     *
     * @param nm String : student name
     * @param sn long : student number
     *
     */
    public UCSDStudent (String nm, long sn) {

        studentnum = sn;
        name = nm;

        tracker = new Tracker("UCSDStudent",   
                      Size.of (name) + Size.of (studentnum) + Size.of (tracker),
		              "calling UCSDStudent ctor");

    }

    /**
     * copy constructor: copy name and student number
     * @param student UCSDStudent
     */
    public UCSDStudent (UCSDStudent student) {  
        tracker = new Tracker("UCSDStudent",   
                Size.of (name) + Size.of (studentnum) + Size.of (tracker),  
                "UCSDStudent ctor");  
        name = new String (student.name);  
        studentnum = student.studentnum;  
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
     * @return String: a formatted string.
     */
    public String toString () {
        return "name:  " + name + " with studentnum:  " + studentnum;
    }

    /**
     * @param val long : new studentnum  
     * @return UCSDStudent: a new student with same properties.
     */
    public UCSDStudent assignValue (long val) {  
                  
        UCSDStudent retval;// return value  

        studentnum = val;  
        retval = new UCSDStudent (this);  

        return retval;  
    }  
}

public class Driver {
private static final short NULL = 0;
private static final int EOF = -1;

public static void main (String [] args) {

	/* initialize debug states */
	Tree.debugOff ();

	/* check command line options */
	for (int index = 0; index < args.length; ++index) {
		if (args[index].equals ("-x"))
		Tree.debugOn ();
	}


	/* The real start of the code */
	SymTab<UCSDStudent> symtab = 
		new SymTab<UCSDStudent> ("Driver");

	String buffer = null;
	int command;
	long number = 0;
	UCSDStudent stu = null;

	System.out.println ("Initial Symbol Table:\n" + symtab);

	while (true) {
		command = NULL; // reset command each time in loop
		System.out.print ("Please enter a command:\n"
		+ "(c)heck memory, is(e)mpty, "
		+ "(i)nsert, (l)ookup, "
		+ "(r)emove, (w)rite:  ");

		command = MyLib.getchar ();
		if (command == EOF) 
			break;

		MyLib.clrbuf ((char) command); // get rid of return

		switch (command) {
			case 'c':
				Tracker.checkMemoryLeaks ();
				System.out.println ();
				break;

			case 'e': 
				if (symtab.isEmpty ()) 
					System.out.println ("Tree is empty.");
				else
					System.out.println (
						"Tree is not empty.");
				break;

			case 'i':
				System.out.print
				("Please enter UCSD student name to insert:  ");

				buffer = MyLib.getline ();// formatted input

				System.out.print
					("Please enter UCSD student number:  ");

				number = MyLib.decin ();

				// get rid of return
				MyLib.clrbuf ((char) command); 

				// create student and place in symbol table
				stu = new UCSDStudent (buffer, number);

				symtab.insert (stu);
				stu = null;
				break;

			case 'l':  
				UCSDStudent found;      // whether found or not

				System.out.print
				("Please enter UCSD student name to lookup:  ");
				buffer = MyLib.getline ();// formatted input

				stu = new UCSDStudent (buffer, 0);
				found = symtab.lookup (stu);

				if (found != null) {
					System.out.println ("Student found!!!");
					System.out.println (found);

					found.jettison ();
					found = null;
				}
				else
					System.out.println ("student " + buffer
						+ " not there!");

				stu.jettison ();
				stu = null;
				break;

			case 'r':  
				UCSDStudent removed; // data to be removed

				System.out.print
				("Please enter UCSD student name to remove:  ");

				buffer = MyLib.getline ();

				stu = new UCSDStudent (buffer, 0);

				removed = symtab.remove (stu);

				if (removed != null) {
					System.out.println (
						"Student removed!!!"); 
					System.out.println (removed);

					removed.jettison ();
					removed = null;
				}
				else
					System.out.println ("student "
						+ buffer
						+ " not there!");

				stu.jettison ();
				stu = null;
				break;

			case 'w':
				System.out.print ("The Symbol Table " +
					"contains:\n" + symtab);
			}
		}

		System.out.print ("\nFinal Symbol Table:\n" + symtab);
		symtab.jettison ();

		Tracker.checkMemoryLeaks ();
	}
}

