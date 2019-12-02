package pc;

import java.util.Objects;
import java.util.Vector;

public class PersistenceStorage {
	private Vector<String> storage;

	public PersistenceStorage() {
		this.storage = new Vector<>();
	}

	public String getFile(int index) {
		return storage.get(index);
	}

	public int saveFile(String string) {
		storage.add(string);
		return this.storage.size() - 1;
	}
}
