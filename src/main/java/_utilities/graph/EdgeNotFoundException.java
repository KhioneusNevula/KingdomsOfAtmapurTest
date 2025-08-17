package _utilities.graph;

public class EdgeNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3165605990403471665L;

	public <E> EdgeNotFoundException(String msg) {
		super(msg);
	}

	/**
	 * Constructs an exception idnicating the given item is not found as an edge
	 */
	public <E> EdgeNotFoundException(E node) {
		super("No edge found: " + node);
	}

}