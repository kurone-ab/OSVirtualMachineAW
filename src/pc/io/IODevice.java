package pc.io;

import os.Driver;

public interface IODevice {
	int buffer = 0;
	Driver driver = null;

	Driver getDriver();
}
