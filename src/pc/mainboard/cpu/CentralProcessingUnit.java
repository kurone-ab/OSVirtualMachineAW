package pc.mainboard.cpu;

import org.jetbrains.annotations.Contract;

public class CentralProcessingUnit {
	private static final CentralProcessingUnit cpu = new CentralProcessingUnit();

	private static final ControlUnit cu = new ControlUnit();

	@Contract(pure = true)
	private CentralProcessingUnit() {

	}

	@Contract(pure = true)
	public static CentralProcessingUnit getInstance(){
		return cpu;
	}
}
