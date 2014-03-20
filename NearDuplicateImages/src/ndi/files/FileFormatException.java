package ndi.files;

/**
 * @author stewart
 */
public class FileFormatException extends Exception {
    public FileFormatException(String message) {
        super("File format error: " + message);
    }
}
