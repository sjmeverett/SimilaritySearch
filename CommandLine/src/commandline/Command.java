package commandline;

/**
 * Represents a command.
 * @author stewart
 *
 */
public interface Command {
	/**
	 * Initialises the command.
	 * @param parameters The parsed command line parameters.
	 */
	void init(Parameters parameters);
	
	/**
	 * Runs the command.
	 */
	void run();
	
	/**
	 * Gets the name of the command.
	 * @return
	 */
	String getName();
	
	/**
	 * Gets the description of the command and fills out the descriptions of the parameters.
	 * @return
	 */
	String describe();
}
