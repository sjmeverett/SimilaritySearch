package commandline;

import java.util.HashMap;
import java.util.Map;

public class Parameters {
	private Map<String, String> parameters;
	private Map<String, String> descriptions;
	
	public Parameters() {
		parameters = new HashMap<>();
		descriptions = new HashMap<>();
	}
	
	public void parse(String[] args, int start) {
		for (int i = start; i < args.length; i++) {
			String[] a = args[i].split("=", 2);
			parameters.put(a[0], a[1]);
		}
	}
	
	public String get(String name) {
		if (!descriptions.containsKey(name))
			System.err.println("No description for parameter " + name);
		
		return parameters.get(name);
	}
	
	public String get(String name, String defaultValue) {
		String value = get(name);
		
		if (value == null)
			return defaultValue;
		else
			return value;
	}
	
	public int getInt(String name, int defaultValue) {
		String value = get(name);
		
		if (value == null)
			return defaultValue;
		else
			return Integer.parseInt(value);
	}
	
	public double getDouble(String name, double defaultValue) {
		String value = get(name);
		
		if (value == null)
			return defaultValue;
		else
			return Double.parseDouble(value);
	}
	
	public double getDouble(String name) throws ParameterException {
		return Double.parseDouble(require(name));
	}
	
	public boolean getBoolean(String name, boolean defaultValue) {
		String value = get(name);
		
		if (value == null)
			return defaultValue;
		else
			return Boolean.parseBoolean(value);
	}
	
	public void describe(String name, String description) {
		descriptions.put(name, description);
	}
	
	public Map<String, String> getDescriptions() {
		return descriptions;
	}

	public String require(String name) throws ParameterException {
		String value = parameters.get(name);
		
		if (value == null)
			throw new ParameterException("no value supplied for parameter " + name);
		
		return value;
	}
}
