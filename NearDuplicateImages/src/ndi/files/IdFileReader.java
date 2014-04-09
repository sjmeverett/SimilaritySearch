package ndi.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IdFileReader {
	private Scanner scanner;
	
	public IdFileReader(String path) throws FileNotFoundException {
		scanner = new Scanner(new File(path));
	}
	
	public List<Integer> read() {
		List<Integer> ids = new ArrayList<Integer>();
		
		while (scanner.hasNextInt()) {
			ids.add(scanner.nextInt());
		}
		
		scanner.close();
		return ids;
	}
}
