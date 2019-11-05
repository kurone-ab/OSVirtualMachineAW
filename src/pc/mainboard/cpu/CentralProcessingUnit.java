package pc.mainboard.cpu;

import global.StackOverFlowExceptionAW;
import org.jetbrains.annotations.Contract;

public class CentralProcessingUnit {
	private static final CentralProcessingUnit cpu = new CentralProcessingUnit();
	static final ControlUnit cu = new ControlUnit();
	static final ArithmeticLogicUnit alu = new ArithmeticLogicUnit();

	@Contract(pure = true)
	private CentralProcessingUnit() {
	}

	@Contract(pure = true)
	public static CentralProcessingUnit getInstance() {
		return cpu;
	}

	public void cycle() throws StackOverFlowExceptionAW {
		cu.fetch();
		cu.decode();
		cu.execute();
	}
}
