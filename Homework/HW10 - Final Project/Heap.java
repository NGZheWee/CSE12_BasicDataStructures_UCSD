/**
 * Name:           Tracker Wonderdog
 * PID:            A12345678
 * USER:           cs12sp21zz
 * File name:      Heap.java
 * Description:    The class file will help to manage a heap dynamically.
 *
*/

/**
  * Class:            Heap
  * Description:      implement different operations of a heap
  *
  *
  * Fields:
  *                   data          - entire element buffer
  *                   occupancy     - how many elements are in the heap
  *                   tracker       - to track memory
  *                   
  *
  * Public functions: Heap            - constructor function
  *                   debugOn         - show debug message
  *                   debugOff        - hide debug message
  *                   jettison        - free up all memory
  *                   insert          - add elements into heap
  *                   remove          - remove element from heap
  *                   toString        - get a string represented by heap
  *                   isEmpty         - test whether the heap have any data
*/
public class Heap<Whatever extends Base> {

	// data fields
	private int occupancy;		// number of TNode's in the Heap
	private Base[] data;    // entire element buffer
	private Tracker tracker;	// track Heap's memory

	// debug flag
	private static boolean debug;


	// debug messages
	private static final String 
		REHEAPING = "[Reheaping up at child index: ",
		REHEAPING_DOWN = "[Reheaping down]\n",
		JETTISON = "[Jettisoning Heap]",
		CLOSE = "]\n",
		AND = " and ",
		WITH = " with ",
		COMPARE = "[Comparing parent ",
		SWAP = "[Swapping ",
		INSERT = "[Inserting ",
		REMOVING = "[Removing ";

	/**
	 * Allocates the heap object.
	 * 
	 * @param caller String: Values of caller are expected to be a string 
	 * specifying where the method is called from.
	 * 
	 * @param size int: Values of size are expected to be the maximal elements
	 * count in the array.
	 * 
	 */
	public Heap (int size, String caller) {
		tracker = new Tracker("Heap", Size.of(occupancy) + Size.of(tracker) 
		                                    + Size.of(data)*size , "Heap Ctor");

		occupancy = 0;

		/**
		 * all memory should be requested.
		 */
		data = new Base[size];
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
	 * Inserts an element into heap.
	 * 
	 * @param element Whatever: Values of element are expected to be a reference
	 * to the data to be inserted into the heap. They must be of the same type 
	 * as the rest of the objects present in the heap.
	 * 
	 * @return boolean: return true if success.
	 * when the heap is full, return false.
	 */
	public boolean insert (Whatever element) {
		if(debug) {
			System.err.printf("%s%s%s", INSERT, element.getName(),CLOSE);
		}
		if(isFull()) {
			return false;
		} else {
			data[occupancy++] = element;
			if(occupancy > 1) {

				/**
				 * there is need to reheapUp only when there are one more 
				 * elements in the array.
				 */
				reheapUp();
			}
		}
		return true;
	}

	/**
	 * help function: swap data[idx] and data[idy]
	 * @param idx int: location in the array
	 * @param idy int: location in the array
	 */
	private void swap(int idx, int idy) {
		Base tmp = data[idx];
		data[idx] = data[idy];
		data[idy] = tmp;
	}

	/**
	 * starting from the first element, compare it with its children.
	 * if there is a child who is less than it, pick the smallest child and swap
	 * the child and the parent.
	 */
	private void reheapDown() {
		if(occupancy > 1) {

			if(debug) {
				System.err.print(String.format("%s", REHEAPING_DOWN));
			}

			/**
			 * there is need to reheapDown only when there are one more 
			 * elements in the array.
			 */
			int idx = 0; // the reheap-down location
			while (idx < occupancy) {

				int leftIdx = 2 * idx + 1; //right child position
				int rightIdx = 2 * idx + 2;//left child position
				//the smallest position in parent and two children.
				int optIdx = idx;

				/**
				 * leftIdx is valid, compare it with the parent
				 */
				if (leftIdx < occupancy) {
					if (data[leftIdx].isLessThan(data[optIdx])) {
						optIdx = leftIdx;
					}
				}

				/**
				 * rightIdx is valid, compare it with the parent
				 */
				if (rightIdx < occupancy) {
					if (data[rightIdx].isLessThan(data[optIdx])) {
						optIdx = rightIdx;
					}
				}

				if (debug) {
					/**
					 * only show the log for at most one child.
					 */
					int comapareIdx = leftIdx;
					if (optIdx != idx) {
						comapareIdx = optIdx;
					} else if(leftIdx < occupancy && rightIdx < occupancy) {
						if(data[rightIdx].isLessThan(data[leftIdx])) {
							comapareIdx = rightIdx;
						}
					}
					if (comapareIdx < occupancy) {
						System.err.printf("%s%s%s%s%s", COMPARE, 
						data[idx].getName(), WITH, data[comapareIdx].getName(),
																		 CLOSE);
					}
				}

				if (optIdx != idx) {

					/**
					 * a smaller child is found
					 */
					if (debug) {
						System.err.printf("%s%s%s%s%s", SWAP, 
					   data[idx].getName(), AND, data[optIdx].getName(), CLOSE);
					}
					swap(idx, optIdx);

					/**
				 	 * update idx for next usage
				 	 */
					idx = optIdx;
				} else {
					break;
				}
			}
		}
	}

	/**
	 * starting from the last element, compare it with its parennt.
	 * if the parent is larger than it, swap the child and the parent.
	 */
	private void reheapUp() {

		int idx = occupancy - 1;// the reheap-up location
		if (debug) {
			System.err.printf("%s%d%s", REHEAPING, idx, CLOSE);
		}

		while (idx > 0) {

			int parentIdx = (idx - 1) / 2; // the parent location of idx

			if (debug) {
				System.err.printf("%s%s%s%s%s", COMPARE, 
				   data[parentIdx].getName(), WITH, data[idx].getName(), CLOSE);
			}
			if (data[idx].isLessThan(data[parentIdx])) {
				if(debug) {
					System.err.printf("%s%s%s%s%s", SWAP, 
					data[parentIdx].getName(), AND, data[idx].getName(), CLOSE);
				}
				swap(idx, parentIdx);

				/**
				 * update idx for next usage
				 */
				idx = parentIdx;
			} else {
				break;
			}
		}
	}

	/**
     * @return boolean: Checks if heap is empty or not.
     */
	public boolean isEmpty () {
		return occupancy == 0;
	}

	/**
     * @return boolean: Checks if heap is full or not.
     */
	public boolean isFull() {
		return occupancy == data.length;
	}

	/*
	 * jettison method for the Heap object. Untracks all memory associated
	 * with the Heap.
	 */
	public void jettison () {
		// Debug messages
		if (debug) {
			System.err.printf("%s", JETTISON);
		}

		for (int i = 0; i < occupancy; i++) {
			data[i].jettison();
		}
		
		tracker.jettison();
	}

	/**
	 * Removes an element from the Heap. 
	 * 
	 * @return Whatever: Returns the first data in the heap. 
	 * Or returns null when the heap is memory.
	 */
	@SuppressWarnings ("unchecked")
	public Whatever remove () {
		if(isEmpty()) {
			return null;
		} else {
			Whatever result = (Whatever)data[0];
			if(debug) {
				System.err.printf(String.format("%s%s%s", REMOVING, 
				                                      result.getName(), CLOSE));
			}
			data[0] = data[occupancy-1];
			data[occupancy-1] = null;
			occupancy--;
			reheapDown();
			return result;
		}
	}

	/**
	 * Creates a string representation of this heap. 
	 *
	 * @return  String representation of this heap
	 */
	public String toString () {
		String str = String.format("The Heap has %d items:", occupancy);
		if(occupancy == 1) {
			str = String.format("The Heap has %d item:", occupancy);
		}
		for (int i = 0; i < occupancy; i++) {
			str += String.format("\nAt index %d:  %s.", i, data[i]);
		}
		return str;
	}
}
