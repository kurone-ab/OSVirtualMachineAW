package os;

import pc.mainboard.MainBoard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

public class FileManagerAW {
	private static final String CSV = ".csv", AWX = ".awx", EXW = ".exw", TXT = ".txt";
	private HashMap<String, Integer> fileMap = new HashMap<>();

	public String getFile(String filename){
		return MainBoard.disk.getFile(this.fileMap.get(filename));
	}

	public void loadFile(String filename, String content){
		int index = MainBoard.disk.saveFile(content);
		this.fileMap.put(filename, index);
	}
	public class DirectoryAW{
		Vector<DirectoryAW> directoryAWS = new Vector<>();
		Vector<FileAW> fileAWS = new Vector<>();

	}

	public class FileAW<T>{
		public int fid;
		public String extension;
		public T content;
	}
}
