package os;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProcessControlBlock {
	int pid;
	int[] registers;
	Status ps;

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
}
