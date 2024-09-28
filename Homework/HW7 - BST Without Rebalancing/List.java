/**
 * Name:           Tracker Wonderdog
 * PID:            A12345678
 * USER:           cs12sp21zz
 * File name:      List.java
 * Description:    The class file will help to manage a circular linked list
 *                 dynamically.
 *
*/

import java.io.*;

public class List <Arbitrary extends Base> {

    static boolean debug = false;    // debug status
    static int listCounter = 0;    // used to number each List

    public static final int        // List controls
        END = 0,
        FRONT = 1;

    /**
      * Class:            ListEngine
      * Description:      implement different operations of a circular linked
     *                      list
      *
      * Fields:           count             - the list identifier
     *                       end            - the tail node speicified by me
     *                     occupancy      - node count in the list
     *                   sample         - the sample data stored in the list
     *                   tracker        - used to track memory
      *
      * Public functions: ListEngine      - constructor function
      *                   jettisonList    - free up all nodes and itself
      *                   advanceNext     - end move one step forward
      *                   advancePre      - end move one step backward
      *                   checkToGoForward- decide whether move forward or
     *                                        backward
      *                   isEmpty         - check whether a lst is empty or not
      *                   insert          - insert an element at the specified
      *                                     location
      *                   locate          - calculate the node specified the
     *                                        location
      *                   remove          - remove the element at the specified
     *                                       location
      *                   view            - return the element at the specified
     *                                       location
     *                    writeReverseList- print the reverse list to the stream
     */
    private class ListEngine {

        // catastrophic error messages
        static final String
            ADNEXT_EMPTY = "Advance next from empty list!!!\n",
            ADPRE_EMPTY = "Advance pre from empty list!!!\n",
            REMOVE_EMPTY = "Remove from empty list!!!\n",
            VIEW_EMPTY = "Viewing an empty list!!!\n",
            WRITE_NONEXISTFILE
                = "Writing to a non-existent file!!!\n";

        // debug messages
        static final String
            ADNEXT = "[List %d - Advancing next]\n",
            ADPRE = "[List %d - Advancing pre]\n",
            INSERT = "[List %d - Inserting node]\n",
            REMOVE = "[List %d - Removing node]\n",
            VIEW = "[List %d - Viewing node]\n",
            LIST_ALLOCATE
                = "[List %d has been allocated]\n",
            LIST_JETTISON
                = "[List %d has been jettisoned]\n";

        int count;        // which list is it
        Node end;        // end of the List
        long occupancy;        // how many items stored
        Base sample;        // sample object of what is stored
        Tracker tracker;    // to track memory

        /**
         *
         * @param sample Base: Values of sample are expected to be an empty
         *                     Base object of the same type that will be
         *                     stored in the list (Driver2), or null (Driver1).
         * @param caller String: Values of caller are expected to be a String
         *                       with a class name to help debug memory issues.
         * tracker.
        */
        ListEngine (Base sample, String caller) {
            
            //I can not jettison it in MyLib.java, so I comment it.
            
            // tracker = new Tracker ("ListEngine",
            //     Size.of (count)
            //     + Size.of (end)
            //     + Size.of (occupancy)
            //     + Size.of (sample)
            //     + Size.of (tracker),
            //     caller + " calling ListEngine Ctor");
            // ---- DO NOT CHANGE TRACKER ---- //

            this.sample = sample;
            this.occupancy = 0;

            /**
             * Don't forget to increment the listCounter and then assign count.
            */
            listCounter++;
            this.count = listCounter;


            if (debug) {
                System.err.print(String.format(LIST_ALLOCATE, count));
            }

        }

        /**
         * call 'jettisonNode' for all node in the list
         * call jettison of the tracker
         */
        void jettisonList () {
            if (debug) {
                System.err.print(String.format(LIST_JETTISON, count));
            }
            Node node = end;//the following code will not modify node.
            for (int i = 0; i < occupancy; i++) {
                Node next = node.getNext();// store next before jettison
                node.jettisonNode();
                node = next;
            }
            if(tracker != null) {
                tracker.jettison();
            }
            listCounter--;
        }

        /**
         * end move one step forward.
         * if there is no element in the list, do nothing.
         */
        void advanceNext () {
            if (end != null) {
                if (debug) {
                    System.err.printf(String.format(ADNEXT, count));
                }
                end = end.getNext();
            } else {
                if (debug) {
                    System.err.printf(String.format(ADNEXT_EMPTY, count));
                }
            }
        }

        /**
         * end move one step backward.
         * if there is no element in the list, do nothing.
        */
        void advancePre () {
            if (end != null) {
                if (debug) {
                    System.err.printf(String.format(ADPRE, count));
                }
                end = end.getPre();
            } else {
                if (debug) {
                    System.err.printf(String.format(ADPRE_EMPTY, count));
                }
            }
        }

        /**
         * This function checks whether it would be more efficient to reach
         * item number in the list by looping forwards from the end of the list
         * (true is returned) or backwards from the end of the list
         * (false is returned).
         *
         * @param where long: where the location index where we want to
         *                       fetch node.
         * @return boolean: true if forwards. Otherwise, false
        */
        boolean checkToGoForward (long where) {

            long forwardSteps = where;// steps when direction is forward

            /**
             * steps when direction is forward
             */
            long backwardSteps = occupancy - where;

            if (forwardSteps <= backwardSteps) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * Check whether the list is empty or not.
         * @return true if empty. Otherwise, false
        */
        boolean isEmpty () {
            return occupancy == 0;
        }

        /**
         * Insert a Arbitrary object into the list.
         * @param element Arbitrary: Values of element are expected to be a
         *                           Arbitrary   object to insert into the list.
         * @param where long: Values of where are expected to be the place in
         *                       the list where to store the element
         * @return boolean: it always return true
        */
        boolean insert(Arbitrary element, long where) {
            if (debug) {
                System.err.printf(String.format(INSERT, count));
            }
            Node location = locate(where);// search the location firstly
            occupancy++;
            if (location != null) {
                if (where != 0) {
                    location = location.getPre();
                }
                Node newNode = location.insert(element);// record returned node.
                if (where == 0) {
                    end = newNode;
                }
            } else {
                end = new Node(element);

                /**
                 * if there is only one element in the circular linked list,
                 * its pre and next are itself.
                 */
                end.setNext(end);
                end.setPre(end);
            }
            return true;
        }

        /**
         * This function is used to eliminate code duplication in the list
         * functions by finding the location at which we wish to insert,
         * remove, or view. locate should be used to locate the Node at
         * location where.
         * @param where long: Values of where are expected to be a the number
         *                    of the item in the list (1 for first item, 2 for
         *                    second item and so forth, 0 for last item)
         * @return Node: the node at the specified location
         */
        Node locate(long where) {
            Node result = end;// the begin location is at node.
            if(checkToGoForward(where))
            {
                while (result != null && where > 0) {
                    result = result.getNext();
                    where--;
                }
            }
            else
            {
                where = occupancy - where;
                while (result != null && where > 0) {
                    result = result.getPre();
                    where--;
                }
            }

            return result;
        }

        /**
         * Remove a node from the list, return its data.
         * @param where long: Values of where are expected to be the place in
         *                       the list of the element to be removed.
         * @return Arbitrary: the data of the removed node.
        */
        Arbitrary remove(long where) {
            Node location = locate(where);// search the location firstly
            if (location != null) {
                if (debug) {
                    System.err.printf(String.format(REMOVE, count));
                }
                Arbitrary result = location.remove();

                if (where == 0) {
                    advancePre();
                }

                /**
                 * be careful when there is only one element in the list.
                 */
                if (this.occupancy == 1) {
                    end = null;
                }

                this.occupancy--;
                return result;
            } else {
                if (debug) {
                    System.err.printf(String.format(REMOVE_EMPTY, count));
                }
                return null;
            }
        }

        /**
         * Return the data stored at location where.
         * @param where long: Values of where are expected the place in the
         *                       list which holds the element to view.
         * @return Arbitrary: the data stored at location where.
         */
        Arbitrary view(long where) {
            Node location = locate(where);// search the location firstly
            if (location != null) {
                if (debug) {
                    System.err.printf(String.format(VIEW, count));
                }
                return location.view();
            } else {
                if (debug) {
                    System.err.printf(String.format(VIEW_EMPTY, count));
                }
                return null;
            }
        }

        // ListEngine WRITELIST
        void writeList (PrintStream stream) {

            if (stream == null) {
                System.err.print (WRITE_NONEXISTFILE);
                return;
            }

            // extra output if we are debugging
            if (stream == System.err) {
                stream.print("List " + count + " has " + occupancy +
                                                            " items in it:\n");
            }

            // display each Node in the List
            Node oldEnd = end;  // to save prior end
            if (occupancy > 0) {
                advanceNext ();
            }
            for (long idx = 1; idx <= occupancy; idx++) {
                stream.print (" element " + idx + ": ");
                end.writeNode (stream);
                advanceNext ();
            }

            // memory tracking output if we are debugging
            if (debug) {
                System.err.print (tracker);
            }

            // restore end to prior value
            end = oldEnd;
        }

        /**
         * Write the elements of ListEngine backwards, starting with the last
         * item. The list is printed to filestream stream.
         * @param stream PrintStream: a writeble target stream
         */
        void writeReverseList (PrintStream stream) {

            if (stream == null) {
                System.err.print(WRITE_NONEXISTFILE);
                return;
            }

            // extra output if we are debugging
            if (stream == System.err) {
                stream.print("List " + count + " has " + occupancy +
                                                            " items in it:\n");
            }

            Node node = end;// the first print node is end.
            for (int i = 0; i < occupancy; i++) {
                stream.print(" element " + i + 1 + ": ");
                node.writeNode(stream);
                node = node.getPre();
            }

            // memory tracking output if we are debugging
            if (debug) {
                System.err.print(tracker);
            }

        }

        private class Node {

            /**
               * Class:            NodeEngine
               * Description:      implement different operations of a linked
              *                   node.
               *
               * Fields:           data          - data stored in the node
              *                    next          - next node after the node
              *                    prev          - previous node before the node
              *                    tracker       - used to track memory
               *
               * Public functions: NodeEngine        - constructor function
               *                   jettisonNodeAndData
                                                     - free up all node and data
               *                   jettisonNodeOnly  - free up only node
              *                    remove            - remove the current node
              *                    view              - return the data of the
              *                                        current node
             */
            private class NodeEngine {

                static final String WRITE_NONEXISTFILE
                    = "Writing to a "
                    + "non-existent file!!!\n";

                Arbitrary data;    // the item stored
                Node next;    // to get to following item
                Node pre;    // to get to previous item
                Tracker tracker; // to track memory

                /**
                 * Creates a new node to hold element, or, if sample of the
                 * ListEngine is non-NULL, a copy of element.
                 * @param newNode Node: Values of newNode are expected to be a
                 *                      Node that the newly created NodeEngine
                 *                      belongs to.
                 * @param element Arbitrary: Values of element are expected to
                 *       be a Arbitrary object we wish to store in the new node.
                 * @param caller  String: Values of caller are expected to be a
                 *                        String of the where this constructor
                 *                        is called.
                */
                NodeEngine (Node newNode,
                Arbitrary element, String caller) {

                    tracker = new Tracker ("NodeEngine",
                        Size.of (data)
                        + Size.of (next)
                        + Size.of (pre)
                        + Size.of (tracker),
                        caller
                        += " calling NodeEngine Ctor");
                    // ---- DO NOT CHANGE TRACKER ---- //

                    this.data = element;
                    newNode.nodeEngine = this;
                }

                /**
                 * Jettison the NodeEngine and the data that it holds.
                */
                void jettisonNodeAndData () {
                    tracker.jettison();
                    if (data != null) {
                        data.jettison();
                    }
                }

                /**
                 * Jettison the NodeEngine.
                */
                void jettisonNodeOnly () {

                    tracker.jettison();
                }

                /**
                 * Create a new Node object to hold element. This new node is
                 * then incorporated into the list at the location AFTER
                 * thisNode.
                 * @param thisNode Node: Values of thisNode are expected to be
                 *                       the Node object that the new node is
                 *                       inserted AFTER.
                 * @param element Arbitrary: Values of element are expected to
                 *        be a Arbitrary object we wish to store in the new node
                 * @return Node: The insert method should return the new Node
                 *                  that it created.
                */
                Node insert (Node thisNode, Arbitrary element) {

                    /**
                     * newly created node who should be after this location
                    */
                    Node newNode = new Node(element);

                    /**
                     * store the next before modify.
                    */
                    Node oldNext = thisNode.getNext();

                    /**
                     * arrange the references of the surrounding Node objects
                    */
                    newNode.setPre(thisNode);
                    thisNode.setNext(newNode);
                    newNode.setNext(oldNext);
                    oldNext.setPre(newNode);

                    return newNode;
                }

                /**
                 * "Unlinks" this Node object to be removed from the list by
                 * arranging the references of the surrounding Node objects
                 * so that they no longer reference to this Node object. The
                 * memory associated with the Node object is jettisoned,
                 * but the Node object's data is not jettisoned.
                 * @return Arbitrary: A reference to the data is returned.
                 */
                Arbitrary remove() {
                    Arbitrary storeBase = data;

                    /**
                     * arrange the references of the surrounding Node objects
                     */
                    pre.setNext(next);
                    next.setPre(pre);

                    jettisonNodeOnly();
                    return storeBase;
                }

                /**
                 * @return Return the data stored in the Node object.
                 */
                Arbitrary view () {
                    return this.data;
                }

                // NodeEngine WRITENODE METHOD
                void writeNode (PrintStream stream) {
                    if (stream == null) {
                        System.err.print (
                            WRITE_NONEXISTFILE);
                        return;
                    }
                    stream.print (data + "\n");
                }
            }

            // -------- YOUR CODE SHOULD GO ABOVE --------
            // NOTE:
            // READ THE CODE BELOW TO SEE WHAT METHOD YOU CAN USE

            static final String
                GETPRE_NONEXISTNODE
                = "Getting pre of a non-existent node!!!\n",
                GETNEXT_NONEXISTNODE
                = "Getting next of a non-existent node!!!\n",
                SETPRE_NONEXISTNODE
                = "Setting pre of a non-existent node!!!\n",
                SETNEXT_NONEXISTNODE
                = "Setting next of a non-existent node!!!\n",
                JETTISON_NONEXISTNODE
                = "Jettisoning a non-existent node!!!\n",
                LOOKUP_NONEXISTNODE
                = "Looking up a non-existent node!!!\n",
                INSERT_NONEXISTNODE
                = "Inserting a non-existent node!!!\n",
                REMOVE_NONEXISTNODE
                = "Removing a non-existent node!!!\n",
                VIEW_NONEXISTNODE
                = "Viewing a non-existent node!!!\n",
                WRITE_NONEXISTNODE
                = "Writing from a non-existent node!!!\n";

            NodeEngine nodeEngine;    // To be wrapped by a Node

            // Node CTOR METHOD
            Node (Arbitrary element) {
                nodeEngine = new NodeEngine (
                    this, element, "Node Ctor");
            }

            // Node GETPRE METHOD
            Node getPre () {
                if (!exist ()) {
                    System.err.print (
                        GETPRE_NONEXISTNODE);
                    return null;
                }
                return nodeEngine.pre;
            }

            // Node GETNEXT METHOD
            Node getNext () {
                if (!exist ()) {
                    System.err.print (
                        GETNEXT_NONEXISTNODE);
                    return null;
                }
                return nodeEngine.next;
            }

            // Node SETNEXT METHOD
            void setNext (Node next) {
                if (!exist ()) {
                    System.err.print (
                        SETNEXT_NONEXISTNODE);
                    return;
                }
                nodeEngine.next = next;
            }

            void setPre (Node pre) {
                if (!exist ()) {
                    System.err.print (
                        SETPRE_NONEXISTNODE);
                    return;
                }
                nodeEngine.pre = pre;
            }

            // Node JETTISON METHOD
            boolean jettisonNode () {
                if (!exist ()) {
                    System.err.print (
                        JETTISON_NONEXISTNODE);
                    return false;
                }
                nodeEngine.jettisonNodeAndData ();
                nodeEngine = null;
                return true;
            }

            // Node EXIST METHOD
            boolean exist () {
                return nodeEngine != null;
            }

            // Node INSERT METHOD
            Node insert (Arbitrary element) {
                if (!exist ()) {
                    System.err.print (INSERT_NONEXISTNODE);
                    return null;
                }
                return nodeEngine.insert (this, element);
            }

            // Node REMOVE METHOD
            Arbitrary remove () {
                if (!exist ()) {
                    System.err.print (REMOVE_NONEXISTNODE);
                    return null;
                }
                return nodeEngine.remove ();
            }

            // Node VIEW METHOD
            Arbitrary view () {
                if (!exist ()) {
                    System.err.print (
                        VIEW_NONEXISTNODE);
                    return null;
                }
                return nodeEngine.view ();
            }

            // Node WRITENODE METHOD
            void writeNode (PrintStream stream) {
                nodeEngine.writeNode (stream);
            }
        }
    }

    // catastrophic error messages
    static final String
        ADNEXT_NONEXIST = "Advance next from non-existent list!!!\n",
        ADPRE_NONEXIST = "Advance pre from non-existent list!!!\n",
        EMPTY_NONEXIST = "Empyting from non-existent list!!!\n",
        ISEMPTY_NONEXIST = "Is empty check from non-existent list!!!\n",
        INSERT_NONEXIST = "Inserting to a non-existent list!!!\n",
        JETTISON_NONEXIST = "Jettisoning from non-existent list!!!\n",
        REMOVE_NONEXIST = "Removing from non-existent list!!!\n",
        OCCUPANCY_NONEXIST
            = "Occupancy check from non-existent list!!!\n",
        VIEW_NONEXIST = "Viewing a non-existent list!!!\n",
        WRITE_NONEXISTLIST = "Writing from a non-existent list!!!\n",
        WRITE_MISSINGFUNC = "Don't know how to write out elements!!!\n";

    private ListEngine listEngine;    // The ListEngine instance

    public static void debugOn () {
        debug = true;
    }

    public static void debugOff () {
        debug = false;
    }

    // List CTOR METHOD
    public List (Base sample, String caller) {
        caller += "\n\tcalling List Ctor";
        listEngine = new ListEngine (sample, caller);
    }

	/**
	 * default constructor.
	 */
    public List()
    {
        this((Base)null, "");
    }

    // list JETTISON
    public void jettison () {
        jettisonList ();
    }

    // list JETTISON
    public boolean jettisonList () {

        if (!exist ()) {
            System.err.print (JETTISON_NONEXIST);
            return false;
        }

        listEngine.jettisonList ();
        listEngine = null;
        return true;
    }

    // List ADVANCENPRE METHOD
    public void advancePre () {

        if (!exist ()) {
            System.err.print (ADPRE_NONEXIST);
            return;
        }

        listEngine.advancePre ();
    }

    // List ADVANCENEXT METHOD
    public void advanceNext () {

        if (!exist ()) {
            System.err.print (ADNEXT_NONEXIST);
            return;
        }

        listEngine.advanceNext ();
    }

    // List EMPTY METHOD
    public void empty () {

        if (!exist ()) {
            System.err.print (EMPTY_NONEXIST);
            return;
        }
        while (!isEmpty ()) {
            listEngine.remove (0);
        }
    }

    // List EXIST METHOD
    public boolean exist () {

        return listEngine != null;
    }

    // List GETOCCUPANCY METHOD
    public long getOccupancy () {

        return listEngine.occupancy;
    }

    // List ISEMPTY METHOD
    public boolean isEmpty () {

        if (!exist ()) {
            System.err.print (ISEMPTY_NONEXIST);
            return false;
        }

        return listEngine.isEmpty ();
    }

    // List INSERT METHOD
    /**
     * why dose 'push' in stack need to return 'Arbitrary'???
     * @param element
     * @param where
     * @return
     */
    public Arbitrary insert (Arbitrary element, long where) {

        if (!exist ()) {
            System.err.print (INSERT_NONEXIST);
            return null;
        }

        listEngine.insert (element, where);
        
        return null;
    }

    // List REMOVE METHOD
    public Arbitrary remove (long where) {

        if (!exist ()) {
            System.err.print (REMOVE_NONEXIST);
            return null;
        }

        return listEngine.remove (where);
    }

    // List TOSTRING METHOD
    public String toString () {
        writeList (System.out);
        return "";
    }

    // List VIEW METHOD
    public Arbitrary view (long where) {

        if (!exist ()) {
            System.err.print (VIEW_NONEXIST);
            return null;
        }

        return listEngine.view (where);
    }

    // List WRITELIST METHOD
    public void writeList (PrintStream stream) {

        if (!exist ()) {
            System.err.print (WRITE_NONEXISTLIST);
            return;
        }

        listEngine.writeList (stream);
    }

    // List WRITEREVERSELIST METHOD
    public void writeReverseList (PrintStream stream) {

        if (!exist ()) {
            System.err.print (WRITE_NONEXISTLIST);
            return;
        }

        listEngine.writeReverseList (stream);
    }
}

