 package pc;

import com.sun.jdi.event.Event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

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
