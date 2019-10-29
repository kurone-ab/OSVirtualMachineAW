package pc.mainboard.cpu;

import org.jetbrains.annotations.Contract;
import pc.mainboard.cpu.bus.AddressBus;

public class CentralProcessingUnit {
	private static final CentralProcessingUnit cpu = new CentralProcessingUnit();
	private AddressBus addressBus;

	private static final ControlUnit cu = new ControlUnit();

	@Contract(pure = true)
	private CentralProcessingUnit() {
		this.addressBus = new AddressBus();

	}

	@Contract(pure = true)
	public static CentralProcessingUnit getInstance(){
		return cpu;
	}
}
