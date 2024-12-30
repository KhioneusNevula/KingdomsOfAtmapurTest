package utilities.graph;

public class NodeNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8195211712543865145L;

	public <E> NodeNotFoundException(String msg) {
		super(msg);
	}

	/**
	 * Constructs an exception idnicating the given item is not found as a node
	 */
	public <E> NodeNotFoundException(E node) {
		super("No node present for: " + node);
	}

	/**
	 * Constructs an exception indicating the given item is not found as a node, and
	 * that the item is argument {ordinal}
	 */
	public <E> NodeNotFoundException(E value, int ordinal) {
		super("No node present for argument " + ordinal + ": " + value);
	}

}