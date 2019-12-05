package pc;

import os.FileManagerAW.FileAW;

import java.util.Objects;
import java.util.Vector;

public class PersistentStorage {
	private Vector<FileAW> storage;

	public PersistentStorage() {
		this.storage = new Vector<>();
	}

	public FileAW getFile(int index) {
		return storage.get(index);
	}

	public int saveFile(FileAW file) {
		storage.add(file);
		return this.storage.size() - 1;
	}
}
