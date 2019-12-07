package os;

import java.util.Arrays;

public class Instance {
	public int[] instance_variables;

	public Instance(int size) {
		this.instance_variables = new int[size];
	}

	@Override
	public String toString() {
		return "Instance{" +
				"instance variables=" + Arrays.toString(instance_variables) +
				'}';
	}
}
