package ndi.files;

/**
 * @author stewart
 */
public class FileFormatException extends Exception {
	private static final long serialVersionUID = 6202445116861822397L;

	public FileFormatException(String message) {
        super("File format error: " + message);
    }
}
