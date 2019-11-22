package os;

import java.util.Vector;

public class ActivationRecord {
	public int previousAR, returnPC, returnValue;
	public Vector<Integer> local;
}
