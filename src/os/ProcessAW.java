package os;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Vector;

public class ProcessAW {
	int pid;
	public int[] code, data;
	public ActivationRecord[] stack;
	public Vector<Instance> heap;

	public ProcessAW(int pid, int[] code, int[] data, int size) {
		this.pid = pid;
		this.code = code;
		this.data = data;
		this.stack = new ActivationRecord[size];
		this.heap = new Vector<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		ProcessAW processAW = (ProcessAW) o;

		return new EqualsBuilder()
				.append(pid, processAW.pid)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(pid)
				.toHashCode();
	}
}