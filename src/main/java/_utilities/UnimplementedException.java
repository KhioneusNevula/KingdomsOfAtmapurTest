package _utilities;

/**
 * Exception indicating a method is unimplemented
 * 
 * @author borah
 *
 */
public class UnimplementedException extends UnsupportedOperationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6348428033188382368L;

	public UnimplementedException() {
	}

	public UnimplementedException(String message) {
		super(message);
	}

	public UnimplementedException(Throwable cause) {
		super(cause);
	}

	public UnimplementedException(String message, Throwable cause) {
		super(message, cause);
	}

}
