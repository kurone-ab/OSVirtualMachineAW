package os;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

public class ProcessControlBlock implements Comparable<ProcessControlBlock> {
	int pid, priority;
	int[] context;
	ProcessState ps;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (!(o instanceof ProcessControlBlock)) return false;

		ProcessControlBlock that = (ProcessControlBlock) o;

		return new EqualsBuilder()
				.append(pid, that.pid)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(pid)
				.toHashCode();
	}

	@Override
	public int compareTo(@NotNull ProcessControlBlock o) {
		return this.priority > o.priority ? -1 : 1;
	}

	@Override
	public String toString() {
		return "ProcessControlBlock \r\n{" + "pid=" + pid +
				", priority=" + priority +
				", ps=" + ps +
				'}';
	}
}
