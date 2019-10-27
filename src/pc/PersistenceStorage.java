 package pc;

 import java.io.File;
 import java.util.Objects;
 import java.util.Vector;

 public class PersistenceStorage {
	private int id;
	private Vector<File> storage;

	public PersistenceStorage() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PersistenceStorage)) return false;
		PersistenceStorage that = (PersistenceStorage) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
