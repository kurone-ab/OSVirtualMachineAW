package os;

import global.IllegalFileFormatException;
import pc.mainboard.MainBoard;

import java.awt.*;
import java.util.*;

public class FileManagerAW {
	private static final String CSV = ".csv", AWX = ".awx", EXW = ".exw", TXT = ".txt";
	private static DirectoryAW CDrive;

	public FileManagerAW() {
		CDrive = new DirectoryAW("C");
	}

	public DirectoryAW getRootDirectory(){
		return CDrive;
	}

	public FileAW getFile(String filename) throws IllegalFileFormatException {
		StringTokenizer tokenizer = new StringTokenizer(filename, "/");
		String directory = null;
		DirectoryAW directoryAW = CDrive;
		while (tokenizer.hasMoreTokens()) {
			directory = tokenizer.nextToken();
			Integer address = directoryAW.directoryMap.get(directory);
			if (address == null && tokenizer.hasMoreTokens()) throw new IllegalFileFormatException();
			if (address != null) directoryAW = directoryAW.directoryAWS.get(address);
		}
		tokenizer = new StringTokenizer(directory == null ? filename : directory, ".");
		tokenizer.nextToken();
		String extension = tokenizer.nextToken();
		return extension.equals(EXW) ? this.getExecutable(directoryAW, filename) : this.getData(directoryAW, filename);
	}

	private FileAW<ExecutableAW> getExecutable(DirectoryAW directoryAW, String filename) {
		return MainBoard.disk.getFile(directoryAW.fileAWS.get(directoryAW.fileMap.get(filename)));
	}

	private FileAW<String> getData(DirectoryAW directoryAW, String filename) {
		return MainBoard.disk.getFile(directoryAW.fileAWS.get(directoryAW.fileMap.get(filename)));
	}

	public void loadFile(String filename, ExecutableAW executableAW) throws IllegalFileFormatException {
		StringTokenizer tokenizer = new StringTokenizer(filename, ".");
		String extension = null;
		while (tokenizer.hasMoreTokens()) extension = tokenizer.nextToken();
		tokenizer = new StringTokenizer(filename, "/");
		while (tokenizer.hasMoreTokens()) filename = tokenizer.nextToken();
		FileAW<ExecutableAW> fileAW;
		if (extension == null) throw new IllegalFileFormatException();
		if (extension.equals(EXW)) fileAW = new FileAW<>(filename);
		else throw new IllegalFileFormatException();
		fileAW.content = executableAW;
		this.loadFile(filename, MainBoard.disk.saveFile(fileAW));
	}

	public void loadFile(String filename, String content) throws IllegalFileFormatException {
		StringTokenizer tokenizer = new StringTokenizer(filename, ".");
		String extension = null;
		while (tokenizer.hasMoreTokens()) extension = tokenizer.nextToken();
		tokenizer = new StringTokenizer(filename, "/");
		while (tokenizer.hasMoreTokens()) filename = tokenizer.nextToken();
		FileAW<String> fileAW;
		if (extension == null) throw new IllegalFileFormatException();
		if (!extension.equals(EXW)) {
			fileAW = new FileAW<>(filename);
		} else {
			throw new IllegalFileFormatException();
		}
		fileAW.content = content;
		this.loadFile(filename, MainBoard.disk.saveFile(fileAW));
	}

	private void loadFile(String filename, int index){
		StringTokenizer tokenizer = new StringTokenizer(filename, "/");
		DirectoryAW directoryAW = CDrive;
		while (tokenizer.countTokens()>1){
			directoryAW = this.createDirectory(directoryAW, tokenizer.nextToken());
		}
		directoryAW.fileMap.put(tokenizer.nextToken(), directoryAW.fileAWS.size());
		directoryAW.fileAWS.add(index);

	}

	public void createDirectory(String path){
		DirectoryAW directoryAW = CDrive;
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		while (tokenizer.hasMoreTokens()){
			directoryAW = this.createDirectory(directoryAW, tokenizer.nextToken());
		}
	}

	public void createDirectory(String path, DirectoryAW directoryAW){
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		while (tokenizer.hasMoreTokens()){
			directoryAW = this.createDirectory(directoryAW, tokenizer.nextToken());
		}
	}

	private DirectoryAW createDirectory(DirectoryAW directoryAW, String name){
		DirectoryAW directory = new DirectoryAW(name);
		if (directoryAW.directoryMap.containsKey(name)) return directoryAW;
		directoryAW.directoryMap.put(name, directoryAW.directoryAWS.size());
		directoryAW.directoryAWS.add(directory);
		return directory;
	}

	public static class DirectoryAW {
		private String name;
		Vector<DirectoryAW> directoryAWS = new Vector<>();
		Vector<Integer> fileAWS = new Vector<>();//save absolute address
		private Hashtable<String, Integer> fileMap = new Hashtable<>();//save file vector index
		private Hashtable<String, Integer> directoryMap = new Hashtable<>();

		public DirectoryAW(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static class FileAW<T> {
		public int fid;
		public String extension, filename;
		public T content;

		public FileAW(String filename) {
			this.filename = filename;
		}
	}
}
