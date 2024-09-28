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
  *                   insert          - add elements into tree
  *                   lookup          - search element
  *                   remove          - remove element from tree
  *                   toString        - get a string represented by tree
*/
public class Tree<Whatever extends Base> {

	/* data fields */
	private TNode root;
	private long occupancy; 
	private String representation;
	private long treeCount;
	private Tracker tracker;
	private static long treeCounter;

	/* debug flag */
	private static boolean debug;

	/* debug messages */
	private static final String ALLOCATE = " - Allocating]\n";
	private static final String JETTISON = " - Jettisoning]\n";
	private static final String AND = " and ";
	private static final String CLOSE = "]\n";
	private static final String COMPARE = " - Comparing ";
	private static final String INSERT = " - Inserting ";
	private static final String CHECK = " - Checking ";
	private static final String UPDATE = " - Updating ";
	private static final String REPLACE = " - Replacing ";
	private static final String TREE = "[Tree ";

	private class PointerBox {
		public TNode pointer;

		public PointerBox (TNode pointer) {
			this.pointer = pointer;
		}
	}
	/**
	 * Called when creating a Tree.
	 */
	 * @param caller String: Values of caller are expected to be a string 
	 * specifying where the method is called from.
	 * 
	 */
	public Tree (String caller) {
		tracker = new Tracker ("Tree", Size.of (root)
		+ Size.of (occupancy)
		+ Size.of (representation)
		+ Size.of (treeCount)
		+ Size.of (treeCounter)
		+ Size.of (tracker),
		caller + " calling Tree CTor");

		// DO NOT CHANGE THIS PART ABOVE
		treeCounter++;
		treeCount = treeCounter;
		if (debug) {
            System.err.printf(String.format("%s%d%s", TREE, treeCount, 
														             ALLOCATE));
        }
        occupancy = 0;
        root = null;
	}

	/**
	 * Jettisons memory associated with the Tree.
	 */
	public void jettison () {

		tracker.jettison();
		if(root != null) {
			root.jettisonAllTNodes();
		}
		treeCounter--;

		if (debug) {
            System.err.printf(String.format("%s%d%s", TREE, treeCount,
			                                                         JETTISON));
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
     * @return boolean: Checks if tree is empty or not.
     */
    public boolean isEmpty() {
        return occupancy == 0;
    }

	/**
	 * Inserts an element into the binary tree. Delegates to TNode's insert when
	 * Tree is not empty.
	 * 
	 * @param element Whatever: Values of element are expected to be a reference
	 * to the data to be inserted into the tree. They must be of the same type 
	 * as the rest of the objects present in the tree.
	 * 
	 * @return boolean: Returns true or false indicating success of insertion.
	 */
	public boolean insert (Whatever element) {
		if (isEmpty()) {
			root = new TNode(element, "insert");
			occupancy++;
			if (debug) {
				System.err.printf(String.format("%s%d%s%s%s", TREE, treeCount,
				                             INSERT, element.getName(), CLOSE));
			}
			return true;
		} else {

			PointerBox pb = new PointerBox(null);
			if (root.insert(element, pb)) {
				occupancy++;
				root = pb.pointer;
				return true;
			}
		}

		return false;
	}

	/**
	 * Searches for an element in the Tree. Delegates to TNode's lookup when 
	 * Tree is not empty. 
	 * 
	 * @param element Whatever: Values of element are expected to be a reference
	 * to the data to be looked up in the tree. They must be of the same type 
	 * as the rest of the objects present in the tree.
	 * 
	 * @return Whatever: Returns the data if found in tree, otherwise returns 
	 * null.
	 */
	public Whatever lookup (Whatever element) {

		if (isEmpty()) {
			return null;
		} else {
			return root.lookup(element);
		}
	}

	/**
	 * Removes an element from the Tree. Delegates to TNode's remove when Tree 
	 * is not empty. 
	 * 
	 * @param element Whatever: Values of element are expected to be a reference
	 * to the data to be removed from the tree. They must be of the same type as
	 * the rest of the objects present in the tree.
	 * 
	 * @return Whatever: Returns the data if it is removed from the tree, 
	 * otherwise returns null.
	 */
	public Whatever remove (Whatever element) {

		if (isEmpty()) {
			return null;
		} else {

			PointerBox pb = new PointerBox(null);
			Whatever result = root.remove(element, pb, false);
			if(result != null) {
				root = pb.pointer;
				occupancy--;
			}
			return result;
		}
	}

	/**
	* Creates a string representation of this tree. This method first
	* adds the general information of this tree, then calls the
	* recursive TNode function to add all nodes to the return string 
	*
	* @return  String representation of this tree 
	*/
	public String toString () {

		representation = "Tree " + treeCount + ":\n"
			+ "occupancy is " + occupancy + " elements.\n";

		if (root != null)
		root.writeAllTNodes();

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
      *                   tracker       - to track memory
      *                   balance       - balance factor
      *                   height        - height of the node.
      *
      * Public functions: TNode               - constructor function
      *                   jettisonTNodeOnly   - free up the node
	  *                   jettisonTNodeAndData- free up the node and data
      *
	  *                   jettisonAllTNodes   - free up all node and data and 
	  *                                         its children.
      *
	  *                   insert              - insert an element on the 
	  *                                         sub-tree
      *
	  *					  remove              - insert an element from the
	  *					                        sub-tree
	  *
	  *                   lookup              - lookup an elemnet on the 
	  *                                         sub-tree
	  *
      *                   writeAllTNodes    - print node recursively
      *                   toString          - get a string represented by node
    */
	private class TNode {                
		private Whatever data;
		private TNode left, right;
		private Tracker tracker;

		/* left child's height - right child's height */
		private long balance;
		/* 1 + height of tallest child, or 0 for leaf */
		private long height;

		private static final long THRESHOLD = 2;

		/**
         * Called when creating a TNode.
         * 
		 * @param element Whatever: Values of element are expected to be the
		 * data to be stored in the TNode. They must be of the same type as the
		 * rest of the objects present in the tree.
		 * 
         * @param caller String: tracker message
         */
		public TNode (Whatever element, String caller) {
			tracker = new Tracker ("TNode", Size.of (data)
			+ Size.of (left)
			+ Size.of (right)
			+ Size.of (height)
			+ Size.of (balance)
			+ Size.of (tracker),
			caller + " calling Tree CTor");
			// DO NOT CHANGE THIS PART ABOVE

			balance = 0;
			height = 0;
			data = element;
			left = null;
			right = null;
		}

		/**
         * Called to jettison a TNode, not its data.
         */
		private void jettisonTNodeOnly () {

			tracker.jettison();

		}

		/**
         * Called to jettison a TNode and the data it holds.
         */
		private void jettisonTNodeAndData () {

			tracker.jettison();
			data.jettison();
		}

		/**
	 	 * Performs a post-order traversal through the Tree, jettisoning each 
		  TNode and the data it holds.
	     */
		private void jettisonAllTNodes () {

			if (left != null) {
				left.jettisonAllTNodes();
			}
			if (right != null) {
				right.jettisonAllTNodes();
			}
			jettisonTNodeAndData();
		}

		/**
		 * Inserts an element into the binary tree.
		 * 
		 * @param element Whatever: Values of element are expected to be a 
		 * reference to the data to be inserted into the tree. They must be of 
		 * the same type as the rest of the objects present in the tree.
		 * 
		 * @param pointerInParentBox PointerBox: Values of pointerInParentBox 
		 * are expected to be a reference to a wrapper object that holds the 
		 * TNode pointer in the parent TNode that was used to get to the current
		 * TNode.
		 * 
		 * @return boolean: Returns true or false indicating success of 
		 * insertion.
		 */
		private boolean insert (Whatever element,
				PointerBox pointerInParentBox) {

			if(debug) {
				System.err.printf(String.format("%s%d%s%s%s%s%s", TREE, 
			treeCount, COMPARE, element.getName(), AND, data.getName(), CLOSE));
			}

			if (data.equals(element)) {

				/**
				 * duplicate element
				 */
				return false;
			} else if (data.isLessThan(element)) {

				/**
				 * inserting on the right tree.
				 */
				if (right == null) {
					right = new TNode(element, "insert");
					if (debug) {
						System.err.printf(String.format("%s%d%s%s%s", TREE, 
						          treeCount, INSERT, element.getName(), CLOSE));
					}
					setHeightAndBalance(pointerInParentBox);
					return true;
				} else {
					if (right.insert(element, pointerInParentBox)) {
						right = pointerInParentBox.pointer;
						setHeightAndBalance(pointerInParentBox);
						return true;
					}
					return false;
				}
			} else {

				/**
				 * inserting on the left tree.
				 */
				if (left == null) {
					left = new TNode(element, "insert");
					if (debug) {
						System.err.printf(String.format("%s%d%s%s%s", TREE, 
						          treeCount, INSERT, element.getName(), CLOSE));
					}
					setHeightAndBalance(pointerInParentBox);
					return true;
				} else {
					if (left.insert(element, pointerInParentBox)) {

						left = pointerInParentBox.pointer;
						setHeightAndBalance(pointerInParentBox);
						return true;
					}
					return false;
				}
			}
		}

		/**
		 * Looks up an element from the binary tree.
		 * 
		 * @param element Whatever: Values of element are expected to be a 
		 * reference to the data to be looked up in thetree. They must be of the
		 * same type as the rest of the objects present in the tree.
		 * 
		 * @return Whatever: Returns the data if it is found, otherwise returns
		 * null.
		 */
		@SuppressWarnings("unchecked")
		private Whatever lookup (Whatever element) {

			if(debug) {
				System.err.printf(String.format("%s%d%s%s%s%s%s", TREE, 
			treeCount, COMPARE, element.getName(), AND, data.getName(), CLOSE));
			}

			if (data.equals(element)) {

				/**
				 * lookup is successful.
				 */
				return data;
			} else if (data.isLessThan(element)) {

				/**
				 * loopup in the right tree.
				 */
				if (right == null) {
					return null;
				} else {
					return right.lookup(element);
				}
			} else {

				/**
				 * loopup in the left tree.
				 */
				if (left == null) {
					return null;
				} else {
					return left.lookup(element);
				}
			}
		}

		/**
		 * Removes the matching data from the binary tree.
		 * 
		 * @param element Whatever: Values of element are expected to be a 
		 * reference to the data to be removed from thetree. They must be of the
		 * same type as the rest of the objects present in the tree.
		 * 
		 * @param pointerInParentBox PointerBox: Values of pointerInParentBox 
		 * are expected to be a reference to a wrapper object that holds the 
		 * TNode pointer in the parent TNode that was used to get to the current
		 * TNode.
		 * 
		 * @param fromSHB boolean: alues of fromSHB are expected to be true or 
		 * false, keeping track of whether or not remove was called from 
		 * setHeightAndBalance so that remove can determine whether or not 
		 * to call setHeightAndBalance.
		 * 
		 * @return Whatever: Returns the removed data, or returns null if data 
		 * was not in the tree.
		 */
		private Whatever remove (Whatever element, PointerBox 
										   pointerInParentBox,boolean fromSHB) {
			if(debug) {
				System.err.printf(String.format("%s%d%s%s%s%s%s", TREE, 
			treeCount, COMPARE, element.getName(), AND, data.getName(), CLOSE));
			}
			if (data.equals(element)) {

				/**
				 * data may be modified by the following code, so save it.
				 */
				Whatever oldData = data;

				if (left == null && right == null) {
					pointerInParentBox.pointer = null;
				} else if (left != null && right != null) {
					replaceAndRemoveMin(this, pointerInParentBox);
					pointerInParentBox.pointer = this;
					if(!fromSHB) {
						setHeightAndBalance(pointerInParentBox);
					}
				} else if (left != null) {
					pointerInParentBox.pointer = left;
				} else {
					pointerInParentBox.pointer = right;
				}
				jettisonTNodeOnly();

				return oldData;
			} else if (data.isLessThan(element)) {

				/**
				 * remove it from the right tree.
				 */
				if (right == null) {
					return null;
				} else {
					Whatever result = right.remove(element, pointerInParentBox,
					                                                   fromSHB);
					if (result != null) {
						right = pointerInParentBox.pointer;
						pointerInParentBox.pointer = this;
						if (!fromSHB) {
							setHeightAndBalance(pointerInParentBox);
						}
					}
					return result;
				}
			} else {

				/**
				 * remove it from the left tree.
				 */
				if (left == null) {
					return null;
				} else {
					Whatever result = left.remove(element, pointerInParentBox, 
					                                                   fromSHB);
					if (result != null) {
						left = pointerInParentBox.pointer;
						pointerInParentBox.pointer = this;
						if (!fromSHB) {
							setHeightAndBalance(pointerInParentBox);
						}
					}
					return result;
				}
			}
		}
		
		/**
		 * Called when removing a TNode with 2 children, replaces that TNode 
		 * with the minimum TNode in its right subtree to maintain the Tree
		 * structure.
		 * @param targetTNode TNode: Values of targetTNode are expected to be a
		 *                 reference to the TNode to remove that has 2 children.
		 * 
		 * @param pointerInParentBox PointerBox: Values of pointerInParentBox
		 * are expected to be a reference to a wrapper object that holds the 
		 * TNode pointer in the parent TNode that was used to get to the current
		 * TNode.
		 */
		private void replaceAndRemoveMin (TNode targetTNode,
												PointerBox pointerInParentBox) {

			if (this == targetTNode) {

				right.replaceAndRemoveMin(targetTNode, pointerInParentBox);
				right = pointerInParentBox.pointer;

			} else {
				if (debug) {
					System.err.printf(String.format("%s%d%s%s%s", TREE, 
					                  treeCount, CHECK, data.getName(), CLOSE));
				}
				if (left != null) {
					left.replaceAndRemoveMin(targetTNode, pointerInParentBox);
					left = pointerInParentBox.pointer;
					pointerInParentBox.pointer = this;
					setHeightAndBalance(pointerInParentBox);
				} else {
					
					if (debug) {
						System.err.printf(String.format("%s%d%s%s%s", TREE,
						            treeCount, REPLACE, data.getName(), CLOSE));
					}
					/**
					 * the minimal node in the right sub-tree of the targetTNode
					 * is found.
					 */
					targetTNode.data = data;
					jettisonTNodeOnly();
					pointerInParentBox.pointer = right;
				}
			}
		}

		// The PointerInParent parameter is used to pass to Remove
		// and to Insert as the way to restructure the Tree if
		// the balance goes beyond the threshold.  You'll need to
		// store the removed data in a working RTS TNode<Whatever>
		// because the memory for the current TNode<Whatever> will be
		// deallocated as a result of your call to Remove.  
		// Remember that this working TNode<Whatever> should not be part
		// of the Tree, as it will automatically be deallocated when the
		// function ends. When calling Remove, remember to tell Remove
		// that its being called from SetHeightAndBalance.

		private void setHeightAndBalance(PointerBox pointerInParentBox) {
			if(debug) {
				System.err.printf(String.format("%s%d%s%s%s", TREE, treeCount,
				                                UPDATE, data.getName(), CLOSE));
			}
			pointerInParentBox.pointer = this;
			long leftHeight = 0;
			if (left != null) {
				leftHeight = left.height + 1;
			}
			long rightHeight = 0;
			if (right != null) {
				rightHeight = right.height + 1;
			}
			height = Math.max(leftHeight, rightHeight);
			balance = leftHeight - rightHeight;

			if (Math.abs(balance) > THRESHOLD) {

				Whatever oldData = data;
				remove(data, pointerInParentBox, true);
				pointerInParentBox.pointer.insert(oldData, pointerInParentBox);
			}
		}

		/**
		* Creates a string representation of this node. Information
		* to be printed includes this node's height, its balance,
		* and the data its storing.
		*
		* @return  String representation of this node 
		*/

		public String toString () {

			return "at height:  " + height + " with balance:  "
				+ balance + "  " + data + "\n";
		}

		/**
		* Writes all TNodes to the String representation field. 
		* This recursive method performs an in-order
		* traversal of the entire tree to print all nodes in
		* sorted order, as determined by the keys stored in each
		* node. To print itself, the current node will append to
		* tree's String field.
		*/
		private void writeAllTNodes () {
			if (left != null)
				left.writeAllTNodes ();

			representation += this;

			if (right != null)
				right.writeAllTNodes ();
		}
	}
}

