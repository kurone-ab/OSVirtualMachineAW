package os;

import pc.mainboard.MainBoard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class FileManagerAW {
	private HashMap<String, Integer> fileMap;
	public String getFile(String filename){
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("exe/test3.awx"));
			StringBuilder builder = new StringBuilder();
			while (scanner.hasNextLine())
				builder.append(scanner.nextLine()).append("\r\n");
			return builder.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
