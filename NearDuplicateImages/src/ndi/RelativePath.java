package ndi;

import java.io.File;

public class RelativePath {
	private String[] path;
	
	public RelativePath(String path) {
		this.path = new File(path).getAbsolutePath().split(File.separator);
	}

	public String getRelativeTo(String contextPath) {
		String[] contextParts = new File(contextPath).getAbsolutePath().split(File.separator);
		int i;
		
		for (i = 0; i < contextParts.length && i < path.length; i++)
			if (!path[i].equals(contextParts[i]))
				break;
		
		StringBuilder result = new StringBuilder();
		
		for (int j = i + 1; j < contextParts.length; j++)
			result.append("..").append(File.separator);
		
		for (int j = i; j < path.length; j++)
			result.append(path[j]).append(File.separator);
		
		result.setLength(result.length() - 1);
		return result.toString();
	}
}
