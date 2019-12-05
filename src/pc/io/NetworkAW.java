package pc.io;

public class NetworkAW implements IODevice {
	@Override
	public Driver getDriver() {
		return null;
	}

	private class NetworkDriver implements Driver{

		@Override
		public void input() {

		}

		@Override
		public void output() {

		}
	}
}
