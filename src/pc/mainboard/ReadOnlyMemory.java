package pc.mainboard;

import os.OperatingSystem;

public class ReadOnlyMemory {
    private static final BasicInputOutputSystem bios = new BasicInputOutputSystem();
    public void on(){ bios.on(); }

    private static class BasicInputOutputSystem{
        private static final OperatingSystem os = new OperatingSystem();
        private void on() {
            os.on();
            os.connect();
        }
    }
}
