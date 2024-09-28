/**
 * Name:           Tracker Wonderdog
 * PID:            A12345678
 * USER:           cs12sp21zz
 * File name:      Tree.java
 * Description:    The class file will help to manage a tree dynamically.
 *
*/


/**
  * Class:            Tree
  * Description:      implement different operations of a tree
  *
  *
  * Fields:
  *                   root          - the root node in the tree
  *                   occupancy     - how many elements are in the tree
  *                   treeName      - name of the tree
  *                   representation- representation string
  *                   tracker       - to track memory
  *
  * Public functions: Tree            - constructor function
  *                   debugOn         - show debug message
  *                   debugOff        - hide debug message
  *                   jettison        - free up all memory
  *                   jettisonAllNodes- jettison for all nodes
  *                   insert 1        - add elements into tree
  *                   lookup          - search element
  *                   remove          - remove element from tree
  *                   toString        - get a string represented by tree
*/
public class Tree<Whatever extends Base> {

    // data fields
    private TNode root;
    private long occupancy;
    private String treeName;
    private String representation;
    private Tracker tracker;

    // debug flag
    private static boolean debug;

    // debug messages
    private static final String ALLOCATE = " - Allocating]\n";
    private static final String AND = " and ";
    private static final String CLOSE = "]\n";
    private static final String COMPARE = " - Comparing ";
    private static final String INSERT = " - Inserting ";
    private static final String TREE = "[Tree ";

	/**
	 * Allocates and initializes the memory associated with the Tree object.
	 * You should provide an initial value to each Tree data member.
	 * @param name String: Values of name are expected to be a String displayed
	 *                                                    when writing the Tree.
	 * @param caller String: Values of caller are expected to be a String
	 *                                     indicate where this method is called.
	 */
    public Tree(String name, String caller) {

        tracker = new Tracker("Tree",
                Size.of(root) + Size.of(occupancy) + Size.of(treeName) 
                + Size.of(representation) + Size.of(tracker),
                caller + " calling Tree Ctor");
        // --------- DO NOT CHANGE ABOVE ---------

        treeName = name;
        occupancy = 0;
        root = null;
        if (debug) {
            System.err.printf(String.format("%s%s%s", TREE, name, ALLOCATE));
        }
    }

	/**
	 *  jettison all of the nodes in the tree and the tree itself
	 */
    public void jettison() {
        jettisonAllNodes(root);
        tracker.jettison();
    }

	/**
	 * jettison node and its data recursively.
	 * @param node TNode
	 */
    public void jettisonAllNodes(TNode node) {
        if (node != null) {
            jettisonAllNodes(node.left);
            jettisonAllNodes(node.right);
            if (node.data != null) {
                node.data.jettison();
            }
            node.jettison();
        }
    }

	/**
	 * Turns on debugging for this Tree.
	 */
    public static void debugOff() {
        debug = false;
    }

	/**
	 * Turns off debugging for this Tree.
	 */
    public static void debugOn() {
        debug = true;
    }

	/**
	 * Inserts the element into the binary tree.
	 * @param element Whatever: Values of element are expected to be complete
	 *                     elements (both name and number are preset) to insert.
	 * @return boolean: Returns true or false indicating success of insertion.
	 */
    public boolean insert(Whatever element) {
        TNode newNode = new TNode(element, "");
        if (root == null) {

			/**
			 * the tree is empty now.
			 */
            root = newNode;
            if (debug) {
                System.err.printf(String.format("%s%s%s%s%s", TREE, 
                            treeName, INSERT, element.getName(), CLOSE));
            }
            occupancy++;
            return true;
        } else {

            TNode curRoot = root;

            while (curRoot != null) {
                if (debug) {
                    System.err.printf(String.format("%s%s%s%s%s%s%s", TREE, 
                                           treeName, COMPARE, element.getName(),
                                           AND, curRoot.data.getName(), CLOSE));
                }
                if (curRoot.data.equals(element)) {
                    if (!curRoot.hasBeenDeleted) {
                        
						/**
                         * duplicate elements is forbidden.
                         */
                        newNode.jettison();
                        element.jettison();
                        return false;
                    }
                    else {

						/**
						 * although the name has existed, but it has been 
						 * deleted.
						 */
                        curRoot.hasBeenDeleted = false;
                        if (curRoot.data != null) {
                            curRoot.data.jettison();
                        }
                        newNode.jettison();
                        curRoot.data = element;
                        if(debug) {
                            System.err.printf(
                                String.format("%s%s%s%s%s", TREE, treeName, 
                                             INSERT, element.getName(), CLOSE));
                        }
                        return true;
                    }
                } else if (curRoot.data.isLessThan(element)) {

					/**
					 * the candidate node must exist in the right
					 */
                    if (curRoot.right == null) {
                        curRoot.right = newNode;
                        newNode.parent = curRoot;
                        updateHeightAndBalance(newNode.parent);
                        occupancy++;
                        if (debug) {
                            System.err.printf(
                                    String.format("%s%s%s%s%s", TREE, treeName,
                                             INSERT, element.getName(), CLOSE));
                        }
                        return true;
                    }
                    curRoot = curRoot.right;
                } else {

					/**
					 * the candidate node must exist in the left
					 */
                    if (curRoot.left == null) {
                        curRoot.left = newNode;
                        newNode.parent = curRoot;
                        updateHeightAndBalance(newNode.parent);
                        occupancy++;
                        if (debug) {
                            System.err.printf(
                                    String.format("%s%s%s%s%s", TREE, 
                                   treeName, INSERT, element.getName(), CLOSE));
                        }
                        return true;
                    }
                    curRoot = curRoot.left;
                }
            }
        }

        return false;
    }

    /**
     * @return boolean: Checks if tree is empty or not.
     */
    public boolean isEmpty() {
        return occupancy == 0;
    }

	/**
	 * Removes the matching data from the binary tree.
	 * @param element Whatever: Values of element are expected to be incomplete
	 *               elements (name is present but number is missing) to remove.
	 * @return Whatever: Returns a pointer to the data if found, null otherwise.
	 */
    public Whatever remove(Whatever element) {

        TNode node = lookup1(element);
        Whatever data = null;
        if (node != null) {
            occupancy--;
            node.hasBeenDeleted = true;
            data = node.data;
        }
        return data;
    }

    /**
     * Looks up the matching data in the binary tree. Returns a pointer to the
     * data if found, null otherwise.
     * @param element Whatever: Values of element are expected to be incomplete
     *              elements (name is present but number is missing) to look up.
     * @return
     */
    public Whatever lookup(Whatever element) {

        TNode node = lookup1(element);
        if (node != null) {
            return node.data;
        }
        return null;
    }

    /**
     * the following are help functions
     */

    /**
     * the function will be used in lookup and remove.
     * use the binary search to find the node. 
     * @param element Whatever: the searching element.
     * @return TNode: node with same same. If the final searched node has beed
     * deleted, return null.
     */

    private TNode lookup1(Whatever element) {
        TNode curRoot = root;
        while (curRoot != null) {
            if (debug) {
                System.err.printf(String.format("%s%s%s%s%s%s%s", TREE, 
                                           treeName, COMPARE, element.getName(),
                                           AND, curRoot.data.getName(), CLOSE));
            }
            if (curRoot.data.equals(element)) {

                if (!curRoot.hasBeenDeleted) {
                    return curRoot;
                } else {

                    /**
                     * the node has been deleted, reture null.
                     */
                    return null;
                }

            } else if (curRoot.data.isLessThan(element)) {
                curRoot = curRoot.right;
            } else {
                curRoot = curRoot.left;
            }
        }
        return null;
    }

    /**
     * @param node TNode
     * @return if node is empty, return 0, else, return its height.
     */
    private long nodeHeight(TNode node) {
        if (node != null) {
            return node.height;
        }
        return 0;
    }

    /**
     * starting from node, update the height and balance until root node.
     * 
     * @param node TNode: the bottommost node(also the first node) need to 
     *                       update height and balance.
     *                       
     */
    private void updateHeightAndBalance(TNode node) {
        while (node != null) {
            if (node.left == null && node.right == null) {

                /**
                 * leaf node has zero height.
                 */
                node.height = 0;
                node.balance = 0;
            } else {

                long leftHeight = nodeHeight(node.left);
                long rightHeight = nodeHeight(node.right);
                if (node.left != null) {
                    leftHeight++;
                }
                if (node.right != null) {
                    rightHeight++;
                }
                node.height = Math.max(leftHeight, rightHeight);
                node.balance = leftHeight - rightHeight;
            }
            node = node.parent;
        }
    }

    /**
     * Creates a string representation of this tree. This method first adds the
     * general information of this tree, then calls the recursive TNode function
     * to add all nodes to the return string
     *
     * @return String representation of this tree
     */
    public String toString() {

        representation = "Tree " + treeName + ":\noccupancy is ";
        representation += occupancy + " elements.";

        if (root != null)
            root.writeAllTNodes();

        if (debug)
            System.err.println(tracker);

        return representation;
    }


    /**
      * Class:            TNode
      * Description:      implement different operations of a tree node
      *
      *
      * Fields:
      *                   data          - the data of the node
      *                   left          - left node of the node
      *                   right         - right node of the node
      *                   parent        - parent node of the node
      *                   hasBeenDeleted- has been deleted or not
      *                   tracker       - to track memory
      *                   balance       - balance factor
      *                   height        - height of the node.
      *
      * Public functions: TNode           - constructor function
      *                   jettison        - free up all memory
      *                   writeAllTNodes  - print node recursively
      *                   toString        - get a string represented by node
    */
    private class TNode {

        public Whatever data;
        public TNode left, right, parent;
        public boolean hasBeenDeleted;
        private Tracker tracker;

        // left child's height - right child's height
        public long balance;
        // 1 + height of tallest child, or 0 for leaf
        public long height;

        /**
         * constructor function
         * @param element Whatever
         * @param caller String: tracker message
         */
        public TNode(Whatever element, String caller) {

            tracker = new Tracker("TNode", Size.of(data) + Size.of(left) 
                    + Size.of(right) + Size.of(parent)
                    + Size.of(balance) + Size.of(height), 
                    caller + " calling TNode Ctor");

            // --------- DO NOT CHANGE ABOVE ---------

            balance = 0;
            height = 0;
            data = element;
            hasBeenDeleted = false;
            left = null;
            right = null;
            parent = null;
        }

        /**
         * jettisons the node from tracker.
         */
        public void jettison() {
            tracker.jettison();
            hasBeenDeleted = true;
        }

        /**
         * Creates a string representation of this node. Information to be 
         * printed includes this node's height, its balance, and 
         * the data its storing.
         *
         * @return String representation of this node
         */

        public String toString() {
            return "at height:  " + height + "  with balance:  " + balance
                                                                 + "  " + data;
        }

        /**
         * Writes all TNodes to the String representation field. This recursive
         * method performs an in-order traversal of the entire tree to print all
         * nodes in sorted order, as determined by the keys stored in each node.
         * To print itself, the current node will append to tree's String field.
         */
        public void writeAllTNodes() {
            if (left != null)
                left.writeAllTNodes();
            if (!hasBeenDeleted)
                representation += "\n" + this;
            if (right != null)
                right.writeAllTNodes();
        }
    }
}
