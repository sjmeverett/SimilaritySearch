package commandline;

/**
 * Thrown as a result of invalid command line parameters etc.
 * @author stewart
 *
 */
public class ParameterException extends Exception {
	private static final long serialVersionUID = -9011028556042384638L;

	public ParameterException(String message) {
		super(message);
	}
}
