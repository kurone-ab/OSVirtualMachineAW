package os;

import global.IllegalFileFormatException;
import os.compiler.ConverterAW;
import pc.mainboard.MainBoard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileManagerAW {
	private static final String CSV = ".csv", AWX = ".awx", EXW = ".exw", TXT = ".txt";
	private static DirectoryAW CDrive;

	public FileManagerAW() {
		CDrive = new DirectoryAW();
	}

	public FileAW getFile(String filename) throws IllegalFileFormatException {
		StringTokenizer tokenizer = new StringTokenizer(filename, "/");
		String directory;
		DirectoryAW directoryAW = CDrive;
		while (tokenizer.hasMoreTokens()) {
			directory = tokenizer.nextToken();
			Integer address = directoryAW.directoryMap.get(directory);
			if (address == null && tokenizer.hasMoreTokens()) throw new IllegalFileFormatException();
			if (address != null) directoryAW = directoryAW.directoryAWS.get(address);
		}
// TODO: 2019-12-04 return file
	}

	public void loadFile(String filename, ConverterAW.ExecutableAW executableAW) throws IllegalFileFormatException {
		StringTokenizer tokenizer = new StringTokenizer(filename);
		String extension = null;
		while (tokenizer.hasMoreTokens()) {
			extension = tokenizer.nextToken();
		}
		FileAW<ConverterAW.ExecutableAW> fileAW;
		if (extension == null) throw new IllegalFileFormatException();
		if (extension.equals(EXW)) fileAW = new FileAW<>();
		else throw new IllegalFileFormatException();
		fileAW.content = executableAW;
		int index = MainBoard.disk.saveFile(fileAW);
		CDrive.fileMap.put(filename, index);
	}

	public void loadFile(String filename, String content) throws IllegalFileFormatException {
		StringTokenizer tokenizer = new StringTokenizer(filename);
		String extension = null;
		while (tokenizer.hasMoreTokens()) extension = tokenizer.nextToken();
		FileAW<String> fileAW;
		if (extension == null) throw new IllegalFileFormatException();
		if (!extension.equals(EXW)) {

			fileAW = new FileAW<>();
		} else {
			throw new IllegalFileFormatException();
		}
		fileAW.content = content;
		int index = MainBoard.disk.saveFile(fileAW);
		CDrive.fileMap.put(filename, index);
	}

	public static class DirectoryAW {
		private Hashtable<String, Integer> fileMap = new Hashtable<>();//save file vector index
		private Hashtable<String, Integer> directoryMap = new Hashtable<>();
		Vector<DirectoryAW> directoryAWS = new Vector<>();
		Vector<Integer> fileAWS = new Vector<>();//save absolute address
	}

	public static class FileAW<T> {
		public int fid;
		public String extension, filename;
		public T content;
	}
}
