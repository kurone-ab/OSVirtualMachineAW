package os;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.Vector;

public class ActivationRecord {
	public int returnHSR;
	public int returnPC;
	public int[] local;

	public ActivationRecord(int returnHSR, int returnPC, int size) {
		this.returnHSR = returnHSR;
		this.returnPC = returnPC;
		this.local = new int[size];
	}

	@Override
	public String toString() {
		return "{" + "returnHSR=" + returnHSR +
				", returnPC=" + returnPC +
				", variables=" + Arrays.toString(local) +
				'}';
	}
}
