package ndi.files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import metricspaces.util.Progress;

public class JudgementFile {
	private Map<String, Boolean> judgements;
	
	
	public JudgementFile(String path, Progress progress) throws IOException {
		judgements = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		progress.setOperation("Reading judgements", LineCounter.count(path) - 1);
		String line;
		
		//skip header
		reader.readLine();
		
		while ((line = reader.readLine()) != null) {
			String[] a = line.split(",");
			judgements.put(a[0] + ":" + a[1], a[2].indexOf("true") > -1);
		}
	}
}
