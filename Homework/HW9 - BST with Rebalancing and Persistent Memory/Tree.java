/**
 * Name:           Tracker Wonderdog
 * PID:            A12345678
 * USER:           cs12sp21zz
 * File name:      Tree.java
 * Description:    The class file will help to manage a tree dynamically.
 *
*/
import java.io.*;

/**
  * Class:            Tree
  * Description:      implement different operations of a tree
  *
  *
  * Fields:
  *                   root          - the root position in the data file
  *                   occupancy     - how many elements are in the tree
  *                   fio           - data file
  *                   representation- representation string
  *                   tracker       - to track memory
  *                   sample        - generic sample data
  *                   sampleNode    - generic sample node
  *                   treeCount     - which tree it is
  *                   
  *
  * Public functions: Tree            - constructor function
  *                   debugOn         - show debug message
  *                   debugOff        - hide debug message
  *                   jettison        - free up all memory
  *                   insert          - add elements into tree
  *                   lookup          - search element
  *                   remove          - remove element from tree
  *                   toString        - get a string represented by tree
  *                   isEmpty         - test whether the tree have any data
  *                   write           - write root and occupancy to the tree
  *                   resetRoot       - set root the the end of the file
*/
public class Tree<Whatever extends Base> {

	private static final long BEGIN = 0;

	// data fields
	private RandomAccessFile fio;	// to write to and read from disk
	private long occupancy;		// number of TNode's in the Tree
	private long root;		// position of the root of the Tree
	private String representation;	// String representation of Tree
	private Base sample;		// copy Base object in TNode's read CTor
	private TNode sampleNode;	// to call TNode searchTree
	private Tracker tracker;	// track Tree's memory
	private long treeCount;		// which Tree it is
	private static long treeCounter;// how many Tree's are allocated

	// debug flag
	private static boolean debug;

	// number of disk reads and writes
	public static long cost = 0;

	// number of insert, remove, locate operations
	public static long operation = 0;

	// debug messages
	private static final String 
		TREE = "[Tree ",
		ALLOCATE = " - Allocating]\n",
		JETTISON = " - Jettisoning]\n",
		CLOSE = "]\n",
		COST_READ = "[Cost Increment (Disk Access): Reading ",
		COST_WRITE = "[Cost Increment (Disk Access): Writing ",
		AND = " and ",
		COMPARE = " - Comparing ",
		INSERT = " - Inserting ",
		CHECK = " - Checking ",
		UPDATE = " - Updating ",
		REPLACE = " - Replacing ";

	/*
	 * PositionBox class creates a PositionBox object to wrap a long type
	 * to be passed by reference in TNode methods.
	 */
	private class PositionBox {
		public long position;	// position value to be wrapped

		/*
		 * Constructor for PositionBox object, wraps position parameter.
		 *
		 * @param position the value to be wrapped by PositionBox
		 */
		public PositionBox (long position) {
			this.position = position;
		}
	}

	/**
	 * Allocates the tree object. Checks the datafile to see if it contains Tree
	 * data. If it is empty, root and occupancy fields are written to the file.
	 * If there is data in the datafile, root and occupancy fields are read into
	 * memory. HINT: You will want to seek before writing or reading anything 
	 * to / from disk.
	 * 
	 * @param caller String: Values of caller are expected to be a string 
	 * specifying where the method is called from.
	 * 
	 * @param datafile String: Values of datafile is the name of the datafile 
	 * that you would need to write to the disk.
	 * 
	 * @param sample Whatever: Values of sample are expected to be a sample 
	 * object that will be stored in the tree.
	 * 
	 */
	public Tree (String datafile, Whatever sample, String caller) {
		tracker = new Tracker ("Tree", Size.of (root)
			+ Size.of (occupancy)
			+ Size.of (representation)
			+ Size.of (treeCount)
			+ Size.of (tracker)
			+ Size.of (fio)
			+ Size.of (this.sample),
			caller + " calling Tree CTor");

		// DO NOT CHANGE TRACKER CODE ABOVE
		treeCount = ++treeCounter;
		if (debug) {
			System.err.printf(String.format("%s%d%s", TREE, treeCount, 
																     ALLOCATE));
		}
		occupancy = 0;
		root = 0;
		try {
			fio = new RandomAccessFile(datafile, "rw");
			if (fio.length() == 0) {
				write();
				resetRoot();
			} else {
				root = fio.readLong();
				occupancy = fio.readLong();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sampleNode = new TNode("sample");
		this.sample = sample;
	}

	/**
	 * Disable debug messager
	 */
	public static void debugOff () {
		debug = false;
	}

	/**
	 * Enable debug messages
	 */
	public static void debugOn () {
		debug = true;
	}

	/**
	 * Debug accessor
	 *
	 * @return true if debug is one, false otherwise
	 */
	public static boolean getDebug () {
		return debug;
	}

	/**
	 * Getter method for cost
	 *
	 * @return number of disk reads and writes of TNode
	 */
	public long getCost () {
		return cost;
	}

	/**
	 * Getter method for operation
	 *
	 * @return number of insert, lookup, remove operations
	 */
	public long getOperation () {
		return operation;
	}

	/**
	 * Count a TNode disk read or write
	 */
	public void incrementCost () {
		cost++;
	}

	/**
	 * Count an insert, lookup, or remove
	 */
	public void incrementOperation () {
		operation++;
	}

	/**
	 * Inserts an element into the binary tree. Inserts at the root TNode if 
	 * Tree is empty, otherwise delegates to TNode's insert by calling the 
	 * searchTree method on the sampleTNode.
	 * 
	 * @param element Whatever: Values of element are expected to be a reference
	 * to the data to be inserted into the tree. They must be of the same type 
	 * as the rest of the objects present in the tree.
	 * 
	 * @return Whatever: return the element if success or return null.
	 */
	public Whatever insert (Whatever element) {

		incrementOperation();
		if (debug) {
			System.err.printf(String.format("%s%d%s%s%s", TREE, treeCount,
									 INSERT, element.getTrimName(), CLOSE));
		}
		if (isEmpty()) {
			(new TNode(element, "insert")).jettisonTNode();
			occupancy++;
			write();
			return element;
		} else {
			PositionBox rootBox = new PositionBox(root);
			Whatever result = sampleNode.searchTree(element, rootBox, "insert");
			root = rootBox.position;
			write();
			return result;
		}
	}

	/**
     * @return boolean: Checks if tree is empty or not.
     */
	public boolean isEmpty () {
		return occupancy == 0;
	}

	/*
	 * jettison method for the Tree object. Untracks all memory associated
	 * with the Tree.
	 */
	public void jettison () {
		// Debug messages
		if (debug) {
			System.err.print (TREE);
			System.err.print (treeCount);
			System.err.print (JETTISON);
		}

		write (); // write the final root and occupancy to disk

		try {
			fio.close (); // close the file accessor
		} catch (IOException ioe) {
			System.err.println ("IOException in Tree's jettison");
		}

		// Jettison TNodes and then tree itself
		sampleNode.jettisonTNode ();
		sampleNode = null;
		tracker.jettison ();
		tracker = null;
		treeCounter--;
	}

	/**
	 * Write the field root and occupancy fields at the beginning of the 
	 * diskfile.
	 */
	public void write () {
		try {
			fio.seek(BEGIN);
			fio.writeLong(root);
			fio.writeLong(occupancy);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		incrementOperation();

		if(isEmpty()) {
			return null;
		} else {
			PositionBox rootBox = new PositionBox (root);
			return sampleNode.searchTree (element, rootBox, "lookup"); 
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

		incrementOperation();

		if(isEmpty()) {
			return null;
		} else {
			PositionBox rootBox = new PositionBox(root);
			Whatever result = sampleNode.searchTree(element, rootBox, "remove");
			if (result != null) {
				root = rootBox.position;
				write();
				if(occupancy == 0) {
					resetRoot();
				}
			}
			return result;
		}
	}

	/**
	 * Resets the root datafield of this tree to be at the end of the datafile. 
	 * This should be called when the last TNode has been removed from the Tree.
	 */
	private void resetRoot () {
		try {
			root = fio.length();
		} catch (IOException e) {
			e.printStackTrace();
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

		try {
			fio.seek (fio.length ());
			long end = fio.getFilePointer ();

			long oldCost = getCost ();

			if (root != end) {
				TNode readRootNode = new TNode (root,
							"Tree's toString");
				readRootNode.writeAllTNodes ();
				readRootNode.jettisonTNode ();
				readRootNode = null;
			}

			cost = oldCost;
		} catch (IOException ioe) {
			System.err.println ("IOException in Tree's toString");
		}

		return representation;
	}

	/**
      * Class:            TNode
      * Description:      implement different operations of a tree node
      *
      *
      * Fields:
      *                   data          - the data of the node
      *                   left          - left position of the node
      *                   right         - right position of the node
      *                   tracker       - to track memory
      *                   balance       - balance factor
      *                   height        - height of the node.
	  *                   position      - the location in the data file
      *
      * Public functions: TNode               - constructor function
      *                   jettisonTNode   - free up the node
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
	  *                   read              - read data from file
	  *                   write             - write data to the file
	  *                   searchTree        - a wrapper of different operations. 
    */
	private class TNode {
		private Whatever data;	// data to be stored in the TNode
		// 1 + height of tallest child, or 0 for leaf
		private long height;
		// left child's height - right child's height
		private long balance;
		// positions of the TNode and its left and right children
		private long left, right, position;
		private Tracker tracker;// to track memory of the tree


		// threshold to maintain in the Tree
		private static final long THRESHOLD = 2;

		/*
		 * TNode constructor to create an empty TNode
		 *
		 * @param caller method object was created in
		 */
		public TNode (String caller) {
			tracker = new Tracker ("TNode", Size.of (data)
				+ Size.of (left)
				+ Size.of (right)
				+ Size.of (position)
				+ Size.of (height)
				+ Size.of (balance)
				+ Size.of (tracker),
				caller + " calling TNode CTor");
		}
		/**
		 * Called when creating a TNode for the first time:
		 * @param element Whatever: Values of element are expected to be the 
		 * data to be stored in the TNode. They must be of the same type as the 
		 * rest of the objects present in the Tree.
		 * 
		 * @param caller String: Values of caller are expected to be a string 
		 * describing where this method is called from.
		 */
		public TNode (Whatever element, String caller) {
			tracker = new Tracker ("TNode", Size.of (data)
				+ Size.of (left)
				+ Size.of (right)
				+ Size.of (position)
				+ Size.of (height)
				+ Size.of (balance)
				+ Size.of (tracker),
				caller + " calling TNode CTor");

			// DO NOT CHANGE TRACKER CODE ABOVE

			left = 0;
			right = 0;
			height = 0;
			balance = 0;
			data = element;

			try {
				position = fio.length();
				write();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		/**
		 * Called when reading a TNode present on disk into memory. You would 
		 * need to first create a copy using the sample belongs to the tree and
		 * assign it to be the data of the newlly created TNode.
		 * 
		 * @param position long: Values of position are expected to be the 
		 * offset in the datafile corresponding to the position of the TNode we
		 * wish to read into memory.
		 * 
		 * @param caller String: Values of caller are expected to be a string 
		 * describing where this method is called from.
		 */
		@SuppressWarnings ("unchecked")
		public TNode (long position, String caller) {
			tracker = new Tracker ("TNode", Size.of (data)
				+ Size.of (left)
				+ Size.of (right)
				+ Size.of (position)
				+ Size.of (height)
				+ Size.of (balance)
				+ Size.of (tracker),
				caller + " calling TNode CTor");

			// DO NOT CHANGE TRACKER CODE ABOVE
			this.position = position;
			data = (Whatever)sample.copy();
			read(position);
		}

		/**
		 * Reads a TNode which is present on the datafile into memory. 
		 * The TNode is read from position. The TNode's information in the 
		 * datafile overwrites this TNode's data. In order to read in the data 
		 * field of the TNode, because the read constructor has generated a data
		 * of the correct type, you can call the read method of your data to 
		 * read in the information of data from the disk file.
		 * 
		 * @param position long: Values of position are expected to be the 
		 * offset in the datafile corresponding to the position of the TNode we 
		 * wish to read into memory.
		 */
		public void read (long position) {
			incrementCost();
			try {
				fio.seek(position);
				data.read(fio);
				height = fio.readLong();
				balance = fio.readLong();
				left = fio.readLong();
				right = fio.readLong();
				if (debug && position != fio.readLong()) {
					System.err.println("position read error");
				}
				if(debug) {
					System.err.print(String.format("%s%s%s", COST_READ, 
													data.getTrimName(), CLOSE));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Writes this TNode object to disk at position in the datafile.
		 */
		public void write() {
			if(debug) {
				System.err.print(String.format("%s%s%s", COST_WRITE, 
													data.getTrimName(), CLOSE));
			}
			incrementCost();
			try {
				fio.seek(position);
				data.write(fio);
				fio.writeLong(height);
				fio.writeLong(balance);
				fio.writeLong(left);
				fio.writeLong(right);
				fio.writeLong(position);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Inserts an element into the binary tree.
		 * 
		 * @param element Whatever: Values of positionInParentBox are expected
		 * to be a reference to a wrapper object that holds the TNode position 
		 * in the parent TNode that was used to get to the current TNode's 
		 * offset in the datafile.
		 * 
		 * @param positionInParentBox: Values of positionInParentBox are 
		 * expected to be a reference to a wrapper object that holds the TNode 
		 * position in the parent TNode that was used to get to the 
		 * current TNode's offset in the datafile.
		 * 
		 * @return Whatever: Returns the data inserted.
		 */
		private Whatever insert(Whatever element, PositionBox 
												          positionInParentBox) {

			if (debug) {
				System.err.printf(String.format("%s%d%s%s%s%s%s", TREE, 
				     treeCount, COMPARE, element.getTrimName(), AND, 
					                                data.getTrimName(), CLOSE));
			}

			if (data.equals(element)) {
				/**
				 * duplicate element
				 */
				if(data != null) {
					data.jettison();
				}
				data = element;
				write();
				positionInParentBox.position = position;
				return element;
			} else if (data.isLessThan(element)) {

				/**
				 * inserting on the right tree.
				 */
				if (right == 0) {
					if (debug) {
						System.err.printf(String.format("%s%d%s%s%s", TREE, 
						      treeCount, INSERT, element.getTrimName(), CLOSE));
					}
					TNode rightNode = new TNode(element, "insert");
					right = rightNode.position;
					setHeightAndBalance(positionInParentBox);
					rightNode.jettisonTNode();
					occupancy++;
					return element;
				} else {
					TNode rightNode = new TNode(right, "insert");
					if (rightNode.insert(element, positionInParentBox) != null) 
					{
						right = positionInParentBox.position;
						setHeightAndBalance(positionInParentBox);
						rightNode.jettisonTNode();
						return element;
					}
					return null;
				}
			} else {
				/**
				 * inserting on the left tree.
				 */
				if (left == 0) {
					if (debug) {
						System.err.printf(String.format("%s%d%s%s%s", TREE, 
							treeCount, INSERT, element.getTrimName(), CLOSE));
					}
					TNode leftNode = new TNode(element, "insert");
					left = leftNode.position;
					setHeightAndBalance(positionInParentBox);
					leftNode.jettisonTNode();
					occupancy++;
					return element;
				} else {
					TNode leftNode = new TNode(left, "insert");
					if (leftNode.insert(element, positionInParentBox)!=null) {

						left = positionInParentBox.position;
						setHeightAndBalance(positionInParentBox);
						leftNode.jettisonTNode();
						return element;
					}
					leftNode.jettisonTNode();
					return null;
				}
			}
		}

		/*
		 * Jettison method for TNode object, untracks memory associated
		 * with the calling TNode.
		 */
		private void jettisonTNode () {
			left = right = 0; // reset left and right positions

			// jettison the data stored
			if (data != null) {
				data.jettison ();
				data = null;
			}

			// jettison tracker
			tracker.jettison ();
			tracker = null;
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
		@SuppressWarnings ("unchecked")
		private Whatever lookup (Whatever element) {
			if(debug) {
				System.err.printf(String.format("%s%d%s%s%s%s%s", TREE, 
				treeCount, COMPARE, element.getTrimName(), AND, 
				                                    data.getTrimName(), CLOSE));
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
				if (right == 0) {
					return null;
				} else {
					TNode rightNode = new TNode(right, "lookup");
					Whatever result = rightNode.lookup(element);
					rightNode.jettisonTNode();
					return result;
				}
			} else {
				/**
				 * loopup in the left tree.
				 */
				if (left == 0) {
					return null;
				} else {
					TNode leftNode = new TNode(left, "lookup");
					Whatever result = leftNode.lookup(element);
					leftNode.jettisonTNode();
					return result;
				}
			}
		}

		/**
		 * Removes the matching data from the binary tree. 
		 * 
		 * @param element Whatever: Values of element are expected to be a 
		 * reference to the data that identifies the element to remove.
		 * 
		 * @param positionInParentBox PositionBox: Values of positionInParentBox
		 * are expected to be a reference to a wrapper object that holds the 
		 * TNode position in the parent TNode that was used to get to the 
		 * current TNode's offset in the datafile.
		 * 
		 * @param fromSHB boolean: Values of fromSHB are expected to be true or 
		 * false, keeping track of whether or not Remove was called from 
		 * SetHeightAndBalance so that Remove can determine whether or not to 
		 * call SetHeightAndBalance.
		 * 
		 * @return Returns Whatever: the data if it is removed, otherwise 
		 * returns null.
		 */
		@SuppressWarnings("unchecked")
		private Whatever remove(Whatever element, PositionBox 
		                                 positionInParentBox, boolean fromSHB) {
			if (debug) {
				System.err.printf(String.format("%s%d%s%s%s%s%s", TREE, 
				treeCount, COMPARE, element.getTrimName(), AND, 
				                                    data.getTrimName(), CLOSE));
			}
			if (data.equals(element)) {

				/**
				 * data may be modified by the following code, so save it.
				 */
				Whatever oldData = data;

				if (left == 0 && right == 0) {
					positionInParentBox.position = 0;
				} else if (left != 0 && right != 0) {
					replaceAndRemoveMin(positionInParentBox);
					positionInParentBox.position = position;
					if (!fromSHB) {
						setHeightAndBalance(positionInParentBox);
					} else {
						write();
					}
				} else if (left != 0) {
					positionInParentBox.position = left;
				} else {
					positionInParentBox.position = right;
				}
				occupancy--;
				return oldData;
			} else if (data.isLessThan(element)) {

				/**
				 * remove it from the right tree.
				 */
				if (right == 0) {
					return null;
				} else {
					TNode rightNode = new TNode(right, "remove");
					Whatever result = rightNode.remove(element, 
					                              positionInParentBox, fromSHB);
					if (result != null) {
						right = positionInParentBox.position;
						positionInParentBox.position = position;
						if (!fromSHB) {
							setHeightAndBalance(positionInParentBox);
						} else {
							write();
						}
					}
					rightNode.jettisonTNode();
					return result;
				}
			} else {

				/**
				 * remove it from the left tree.
				 */
				if (left == 0) {
					return null;
				} else {
					TNode leftNode = new TNode(left, "remove");
					Whatever result = leftNode.remove(element, 
									              positionInParentBox, fromSHB);
					if (result != null) {
						left = positionInParentBox.position;
						positionInParentBox.position = position;
						if (!fromSHB) {
							setHeightAndBalance(positionInParentBox);
						} else {
							write();
						}
					}
					leftNode.jettisonTNode();
					return result;
				}
			}
		}
		
		/**
		 * 
		 * Called when removing a TNode with 2 children, replaces that TNode 
		 * with the minimum TNode in it's right subtree to maintain the Tree 
		 * structure.
		 * 
		 * @param positionInParentBox PositionBox: Values of positionInParentBox
		 * are expected to be a reference to a wrapper object that holds the 
		 * TNode position in the parent TNode that was used to get to the 
		 * current TNode's offset in the datafile.
		 * 
		 * @return Whatever: final remove data.
		 */
		@SuppressWarnings ("unchecked")
		private Whatever replaceAndRemoveMin(PositionBox positionInParentBox) {
			if (position == positionInParentBox.position) {
				TNode rightNode = new TNode(right, "replaceAndRemoveMin");

				positionInParentBox.position = 0;
				Whatever result = rightNode.replaceAndRemoveMin
				                                          (positionInParentBox);
				right = positionInParentBox.position;
				data = result;
				rightNode.jettisonTNode();
				return result;
			} else {
				if (debug) {
					System.err.printf(String.format("%s%d%s%s%s", TREE, 
					              treeCount, CHECK, data.getTrimName(), CLOSE));
				}
				if (left != 0) {
					TNode leftNode = new TNode(left, "replaceAndRemoveMin");
					Whatever result = leftNode.replaceAndRemoveMin
														  (positionInParentBox);
					left = positionInParentBox.position;
					positionInParentBox.position = position;
					setHeightAndBalance(positionInParentBox);
					leftNode.jettisonTNode();
					return result;
				} else {

					if (debug) {
						System.err.printf(String.format("%s%d%s%s%s", TREE, 
								treeCount, REPLACE, data.getTrimName(), CLOSE));
					}
					/**
					 * the minimal node in the right sub-tree of the targetTNode
					 * is found.
					 */
					Whatever result = data;
					data = null;
					positionInParentBox.position = right;
					return result;
				}
			}
		}

		/*
		 * Reads in TNode from the disk at positionInParentBox.position
		 * so an action may be performed on that TNode. Centralizes the
		 * the operations needed when reading a TNode from the disk.
		 *
		 * @param element the data the action is to be performed on
		 * @param positionInParentBox the PositionBox holding the
		 *        position of the TNode to be read from the disk
		 * @param action the action to be performed
		 * @return returns the result of the action
		 */
		private Whatever searchTree (Whatever element,
			PositionBox positionInParentBox, String action) {

			Whatever result = null;
			TNode readNode = new TNode 
			 (positionInParentBox.position, "searchTree " + action);

			if (action.equals ("insert")) {
				result = readNode.insert (element,
							positionInParentBox);
			}
			else if (action.equals ("lookup"))
				result = readNode.lookup (element);
			else if (action.equals ("RARM")) {
				result = readNode.replaceAndRemoveMin
							(positionInParentBox);
			}
			else if (action.equals ("remove")) {
				result = readNode.remove (element,
						positionInParentBox, false);
			}

			readNode.jettisonTNode (); // rename to jettisonTNode
			readNode = null;

			return result;
		}

		/* The PointerInParent parameter is used to pass to Remove
		 * and to Insert as the way to restructure the Tree if
		 * the balance goes beyond the threshold.  You'll need to
		 * store the removed data in a working RTS TNode<Whatever>
		 * because the memory for the current TNode<Whatever> will be
		 * deallocated as a result of your call to Remove.
		 * Remember that this working TNode<Whatever> should not be
		 * part of the Tree, as it will automatically be dealloacted
		 * when the function ends. When calling Remove, remember to
		 * tell Remove that its being called from SetHeightAndBalance.
		 *
		 * @param positionInParentBox holds the position of the calling
		 *        TNode on the disk
		 */
		@SuppressWarnings ("unchecked")
		private void setHeightAndBalance(PositionBox positionInParentBox) {
			if (debug) {
				System.err.printf(String.format("%s%d%s%s%s", TREE, treeCount, 
				                                UPDATE, data.getTrimName(), CLOSE));
			}
			positionInParentBox.position = position;
			long leftHeight = 0;
			if (left != 0) {
				TNode leftNode =  new TNode(left, "setHeightAndBalance");
				leftHeight = leftNode.height + 1;
				leftNode.jettisonTNode();
			}
			long rightHeight = 0;
			if (right != 0) {
				TNode rightNode =  new TNode(right, "setHeightAndBalance");
				rightHeight = rightNode.height + 1;
				rightNode.jettisonTNode();
			}
			height = Math.max(leftHeight, rightHeight);
			balance = leftHeight - rightHeight;

			if (Math.abs(balance) > THRESHOLD) {

				Whatever oldData = data;
				remove(data, positionInParentBox, true);
				sampleNode.searchTree(oldData, positionInParentBox, "insert");
			} else {
				write();
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
			if (left != 0) {
				TNode readLeftNode = new TNode (left,
							"writeAllTNodes");
				readLeftNode.writeAllTNodes ();
				readLeftNode.jettisonTNode();
				readLeftNode = null;
			}

			representation += this;

			if (right != 0) {
				TNode readRightNode = new TNode (right,
							"writeAllTNodes");
				readRightNode.writeAllTNodes ();
				readRightNode.jettisonTNode();
				readRightNode = null;
			}
		}
	}
}
