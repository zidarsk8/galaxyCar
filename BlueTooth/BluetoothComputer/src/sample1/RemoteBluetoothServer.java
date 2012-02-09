package sample1;
public class RemoteBluetoothServer {
	public static void main(String[] args) {
		Thread waitThread = new Thread(new WaitThread());
		waitThread.start();
	}
}
