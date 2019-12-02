package os;

import java.util.Vector;

public class ActivationRecord {
	public int previousAR, returnPC, returnValue;
	public int[] local;

	public ActivationRecord(int returnPC, int size) {
		this.returnPC = returnPC;
		this.local = new int[size];
	}
}
