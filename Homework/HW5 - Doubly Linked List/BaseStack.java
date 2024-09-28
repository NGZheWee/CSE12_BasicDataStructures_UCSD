/**
 * Name:           Tracker Wonderdog
 * PID:            A12345678
 * USER:           cs12sp21zz
 * File name:      BaseStack.java
 * Description:    The class file is an implementation of stack derived from
 * 				   List.
 *
 */


/**
 * Class:            BaseStack
 * Description:      implement basic operations of a stack
 *
 * Public functions: BaseStack      - constructor function
 *                   pop     		- remove an element from the stack top
 *                   push    		- push an element into the stack top
 *                   top   			- get the element on the stack top
 *
*/
public class BaseStack extends List {

	/**
	 * constructor function
	 *
	 * @param sample Base: data stored in the inner list
	 * @param caller String: helpful message stored in the inner memory tracker.
	 */
	public BaseStack (Base sample, String caller) {
		super (sample, caller + " calling BaseStack Ctor");
	}

	/**
	 * remove an element from the stack top
	 * @return Base: remove the inner list tail node and return its data.
	 */
	public Base pop () {
		return remove (END);
	}

	/**
	 * push an element into the stack top
	 * @param element Base: add the element as the new back of inner list
	 * @return boolean: the returned value of insert.
	 */
	public boolean push (Base element) {
		return insert (element, END);
	}

	/**
	 * get the element on the stack top
	 * @return Base: the data of inner list tail node
	 */
	public Base top () {
		return view (END);
	}
}
