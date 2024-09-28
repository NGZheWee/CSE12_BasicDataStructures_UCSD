/**
 * Name:           Tracker Wonderdog
 * PID:            A12345678
 * USER:           cs12sp21zz
 * File name:      HashTable.java
 * Description:    The class file will help to manage a hash table dynamically.
 *
*/


/**
  * Class:            HashTable
  * Description:      implement different operations of a hash table
  *
  *
  * Fields:
  *                   occupancy     - how many elements are in the Hash Table
  *                   size          - size of Hash Table
  *                   table         - the Hash Table itself ==> array of Base
  *                   tableCount    - which hash table it is
  *                   tracker       - to track memory
  *
  * Public functions: HashTable       - constructor function
  *                   debugOn         - show debug message
  *                   debugOff        - hide debug message
  *                   jettison        - free up all memory
  *                   insert 1        - add elements into hashtable
  *                   insert 2        - add elements into hashtable iteratively
  *                                     or recursively
  *                   lookup          - search element
  *                   toString        - get a string represented by hash table.
*/
public class HashTable extends Base {

    // counters, flags and constants
    private static int counter = 0;         // number of HashTables so far
    private static boolean debug;           // allocation of debug states

    // data fields
    private long occupancy;     // how many elements are in the Hash Table
    private int size;           // size of Hash Table
    private Base table[];       // the Hash Table itself ==> array of Base
    private int tableCount;     // which hash table it is
    private Tracker tracker;    // to track memory

    // initialized by Locate function
    private int index;      // last location checked in hash table

    // set in insert/lookup, count of location in probe sequence
    private int count = 0;

    // messages
    private static final String DEBUG_ALLOCATE = " - Allocated]\n";
    private static final String DEBUG_LOCATE = " - Locate]\n";
    private static final String DEBUG_LOOKUP = " - Lookup]\n";
    private static final String AND = " and ";
    private static final String BUMP = "[Bumping To Next Location...]\n";
    private static final String COMPARE = " - Comparing ";
    private static final String FULL = " is full...aborting...]\n";
    private static final String FOUND_SPOT = " - Found Empty Spot]\n";
    private static final String HASH = "[Hash Table ";
    private static final String HASH_VAL = "[Hash Value Is ";
    private static final String INSERT = " - Inserting ";
    private static final String PROCESSING = "[Processing ";
    private static final String TRYING = "[Trying Index ";

    /**
     * Turns on debugging for this HashTable.
     */
    public static void debugOn () {

        debug = true;
    }

    /**
     * Turns off debugging for this HashTable.
     */
    public static void debugOff () {

        debug = false;
    }

    /**
     * Allocates and initializes the memory associated with a hash table.
     * @param sz int : table size
     * @param caller String: Values of caller are expected to be a String
     *                       with a class name to help debug memory issues.
     */
    public HashTable (int sz, String caller) {

        occupancy = 0;
        size = sz;
        table = new Base[sz];
        for (int i = 0; i < sz; i++) {
            table[i] = null;
        }

        /**
         * Don't forget to increment the listCounter and then assign count.
         */
        counter++;
        tableCount = counter;

        // DO NOT CHANGE THIS PART
        tracker = new Tracker ("HashTable",
                Size.of (index)
                + Size.of (occupancy)
                + Size.of (size)
                + Size.of (table)
                + Size.of (tableCount)
                + Size.of (tracker),
                caller + " calling HashTable Ctor");

        if (debug) {
            System.err.print(String.format(HASH + tableCount + DEBUG_ALLOCATE));
        }
    }

    /**
     * Called when jettisoning this HashTable. Jettisons each Base object
     * stored in the HashTable, and the Tracker object.
     */
    public void jettison () {

        /**
         * Jettison each object in hashtable
         */
        for (int i = 0; i < size; i++) {
            if (table[i] != null) {
                table[i].jettison();
            }
        }

        tracker.jettison();
        counter--;
        tracker = null;
    }

    /**
     * @return long : Called to get the current occupancy of the Hash Table.
     */
    public long getOccupancy () {

        return occupancy;
    }

    /**
     * Performs insertion into the table via delegation to the
     * private insert method.
     *
     * @param   element       The element to insert.
     * @return  true or false indicating success of insertion
     */
    public boolean insert (Base element) {

        return insert (element, false);
    }

    /**
     * Inserts the element in the hash table.
     * Returns true or false indicating success of insertion.
     * @param element Base: complete elements to insert.
     * @param recursiveCall: represent whether or not this function was called
     *                                                              recursively.
     * @return boolean : true or false indicating success of insertion
     */
    public boolean insert (Base element, boolean recursiveCall) {

        if(debug) {
            System.err.println(String.format("%s%d%s%s]", HASH, tableCount,
                                                    INSERT, element.getName()));
        }

        /**
         * reset it if the call is from Driver's main
         */
        if(!recursiveCall) {
            count = 0;
        }


        Base searched = locate(element);
        if (searched == null && count != size) {

            if (debug) {
                System.err.print(String.format("%s%d%s", HASH, tableCount,
                                                                   FOUND_SPOT));
            }

            table[index] = element;
            occupancy++;
            return true;
        } else {

            /**
             * if a less element is found, call 'bump'.
             */
            if (searched.isLessThan(element)) {

                if (debug) {
                    System.err.print(String.format("%s", BUMP));
                }

                table[index] = element;
                insert(searched, true);
                return true;
            }

        }

		if(debug && count == size) {
			System.err.print(String.format("%s%d%s", HASH,  tableCount, FULL));
		}

        return false;
    }

    /**
     * Locates the index in the table where the insertion is to be performed,
     * an item is found, or an item is determined not to be there. Sets the
     * variable index to the last location checked; it will be used by insert
     * and lookup.
     * @param element Base: it is expected to be complete and incomplete
     *                      elements depending on whether it's called from
     *                      insert or lookup.
     * @return Base: Returns the object if you find one, and null if the index
     * is empty.
     */
    private Base locate (Base element) {

        if(debug) {
            System.err.print(String.format("%s%d%s", HASH, tableCount,
                                                                 DEBUG_LOCATE));
            System.err.println(String.format("%s%s]", PROCESSING,
                                                            element.getName()));
            System.err.println(String.format("%s%d]", HASH_VAL,
                                                           element.hashCode()));
        }

        int hashCode = element.hashCode();// a numeric attribute.

        // initial checking element index in table.
        int initial = hashCode % size;

        // offset value if any collision
        int increment = (hashCode % (size - 1)) + 1;

        /**
         * search at most 'size' times
         */
        while(count < size)
        {
            // update the last fetch index
            index = calculateLocation(initial, increment, count, size);
            count++;

            if(debug) {
                System.err.println(String.format("%s%d]", TRYING, index));
            }
            /**
             * fetch the result and update count
             */
            Base result = table[index];

            if (result!=null && debug) {
                System.err.print(String.format("%s%d%s%s%s%s]\n", HASH,
                                tableCount, COMPARE, element.getName(), AND,
                                                         result.getName()));
            }

            if(result == null || result.isLessThan(element)
                                                      || result.equals(element))
            {
                return result;
            }
        }

        return null;
    }

    /**
     * Looks up the element in the hash table.
     * @param element Base: it is expected to be incomplete elements (name is
     *                      present but number is missing) to look up.
     * @return Returns reference to the element if found, null otherwise.
     */
    public Base lookup(Base element) {

        count = 0;

        if(debug) {
            System.err.print(String.format("%s%d%s", HASH, tableCount,
                                                                 DEBUG_LOOKUP));
        }

        Base searched = locate(element);
        if (searched != null) {
            if (searched.equals(element)) {
                return searched;
            }
        }
        return null;
    }


    /**
     * Creates a string representation of the hash table. The method
     * traverses the entire table, adding elements one by one ordered
     * according to their index in the table.
     *
     * @return  String representation of hash table
     */
    public String toString () {
        String string = "Hash Table " + tableCount + ":\n";
        string += "size is " + size + " elements, ";
        string += "occupancy is " + occupancy + " elements.\n";

        /* go through all table elements */
        for (int index = 0; index < size; index++) {

            if (table[index] != null) {
                string += "at index " + index + ": ";
                string += "" + table[index];
                string += "\n";
            }
        }

        string += "\n";

        if(debug)
            System.err.println(tracker);

        return string;
    }

    /**
     * a helper function to calculate the index during probe sequence.
     * @param initial int: initial location
     * @param increment int: increment step every time
     * @param count int: current iterations
     * @param tableSize int: table size
     * @return int: new location index
     */
    private static int calculateLocation(int initial, int increment, int count,
                                                                  int tableSize)
    {
        return (initial + count * increment) % tableSize;
    }
}
