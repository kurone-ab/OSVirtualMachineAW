package pc.io;

public class ConsoleAW implements IODevice {
	@Override
	public Driver getDriver() {
		return null;
	}

	private class ConsoleDriver implements Driver{

		@Override
		public void input() {

		}

		@Override
		public void output() {

		}
	}
}
