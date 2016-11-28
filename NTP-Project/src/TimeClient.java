import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TimeClient {
	private static String hostUrl = "127.0.0.1";
	private static int PORT = 27780;
	private Double minD;
	//private NTPRequest minNTPrequest;
	private Socket socket;

	public TimeClient() {
		
		try {
			for (int i = 0; i < 10; i++) {
				socket = new Socket(InetAddress.getByName(hostUrl), PORT);
				//

				
				NTPRequest request = new NTPRequest();
				
				request.t1 = System.currentTimeMillis();
				
				sendNTPRequest(request);
				
				InputStream is = socket.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				try {
					request =  (NTPRequest) ois.readObject();

					int randomCommunicationDelayAtClient = 10 + (int) (Math.random() * (100-10) + 1);
					//System.out.println("Communication delay from server to client :"+randomCommunicationDelayAtClient);
					
					request.t4 = System.currentTimeMillis();
					threadSleep(1000 - randomCommunicationDelayAtClient);
					request.t3 = System.currentTimeMillis();
					
					request.calculateOandD();
					
					if (i == 0) {
						minD = request.d;
					} else {
						if (minD > request.d)
							minD = request.d;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				System.out.println("Measurement["+(i+1)+"] done.");
				
				/*if (i < 9)
					threadSleep(500);
				*/socket.close();
			}
			System.out.println("Minimum delay :"+minD);
			socket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private void sendNTPRequest(NTPRequest request) throws IOException {
		//

		OutputStream os = socket.getOutputStream();
		
		ObjectOutputStream oos = new ObjectOutputStream(os);
		
		oos.writeObject(request);
	}

	private void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TimeClient();
	}
}
