package os;

import java.util.Vector;

public class ActivationRecord {
	public int previousAR, returnPC, returnValue;
	public int[] local;

	public ActivationRecord(int previousAR, int returnPC) {
		this.previousAR = previousAR;
		this.returnPC = returnPC;
		this.local = new int[10];
	}
}
