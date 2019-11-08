package os;

import java.util.Hashtable;
import java.util.Map;

public class InterruptVectorTable {
	static final Hashtable<Integer, InterruptServiceRoutine> ivt = new Hashtable<>();
}
