package os;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Stack;
import java.util.Vector;

public class ProcessAW {
	ProcessControlBlock pcb;
	public int stackSize;
	public int[] code, data;
	public Vector<Integer> stack, heap;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (!(o instanceof ProcessAW)) return false;

		ProcessAW processAW = (ProcessAW) o;

		return new EqualsBuilder()
				.append(pcb, processAW.pcb)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(pcb)
				.toHashCode();
	}
}