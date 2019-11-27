package pc.io;

public interface IODevice {
	int buffer = 0;
	Driver driver = null;

	Driver getDriver();
}
