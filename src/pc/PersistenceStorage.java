 package pc;

 import java.util.Objects;
 import java.util.Vector;

 public class PersistenceStorage {
	private static Vector<String> storage;

	public PersistenceStorage() {
	}

	public static String getFile(int index){
		return storage.get(index);
	}
}
