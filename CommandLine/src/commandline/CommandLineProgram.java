package commandline;

import java.util.HashMap;
import java.util.Map;

public class CommandLineProgram {
	private Map<String, Command> commands;
	
	public CommandLineProgram() {
		commands = new HashMap<>();
	}
	
	public void addCommand(Command command) {
		commands.put(command.getName(), command);
	}
	
	public void run(String[] args) {
		if (args.length == 0 || args[0].equals("-h")) {
			help();
			return;
		}
		
		Command command = commands.get(args[0]);
		
		if (command == null) {
			System.err.println("Command not found.");
		}
		else if (args.length > 1 && args[1].equals("-h")) {
			help(command);
		}
		else {
			Parameters parameters = new Parameters();
			command.init(parameters);
			//describe the parameters so we can check which ones are being used without being described
			command.describe();
			
			if (args.length > 1)
				parameters.parse(args, 1);
			
			command.run();
		}
	}
	
	public void help() {
		for (Command command: commands.values()) {
			help(command);
		}
	}
	
	public void help(Command command) {
		Parameters parameters = new Parameters();
		command.init(parameters);
		String description = command.describe();
		System.out.printf("\033[1m%s\033[0m  %s\n", command.getName(), description);
		
		for (Map.Entry<String, String> entry: parameters.getDescriptions().entrySet()) {
			System.out.printf("    \033[1m%s\033[0m  %s\n", entry.getKey(), entry.getValue());
		}
		
		System.out.println();
	}
}
