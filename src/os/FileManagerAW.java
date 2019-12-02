package os;

import pc.mainboard.MainBoard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class FileManagerAW {
	private HashMap<String, Integer> fileMap = new HashMap<>();
	public String getFile(String filename){
		return MainBoard.disk.getFile(this.fileMap.get(filename));
	}

	public void loadFile(String filename, String content){
		int index = MainBoard.disk.saveFile(content);
		this.fileMap.put(filename, index);
	}
}
