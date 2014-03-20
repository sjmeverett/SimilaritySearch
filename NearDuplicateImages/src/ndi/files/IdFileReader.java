package ndi.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class IdFileReader {
	private Scanner scanner;
	
	public IdFileReader(String path) throws FileNotFoundException {
		scanner = new Scanner(new File(path));
	}
	
	public Set<Integer> read() {
		Set<Integer> ids = new HashSet<Integer>();
		
		while (scanner.hasNextInt()) {
			ids.add(scanner.nextInt());
		}
		
		scanner.close();
		return ids;
	}
}
