import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {
	private static int PORT = 27780;
	private ServerSocket serverSocket;

	public TimeServer() {
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server started on port: " + PORT);
			//
			while (true) {
				Socket clientSocket = serverSocket.accept();
				NTPRequestHandler ntpRequestHandler = new NTPRequestHandler(clientSocket);
				Thread t = new Thread(ntpRequestHandler);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TimeServer();
	}

	private class NTPRequestHandler implements Runnable {
		private Socket client;

		public NTPRequestHandler(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			///
			NTPRequest request = new NTPRequest();
			sendNTPAnswer(request);
		}

		private void sendNTPAnswer(NTPRequest request) {
			///
			try {
				InputStream is = client.getInputStream();
				OutputStream os = client.getOutputStream();
				
				ObjectInputStream ois = new ObjectInputStream(is); 
				ObjectOutputStream oos = new ObjectOutputStream(os);
				
				request = (NTPRequest) ois.readObject();
				
				int randomCommunicationDelayAtServer = 10 + (int) (Math.random() * (100-10) + 1);
				//System.out.println("Communication delay from client to server :"+randomCommunicationDelayAtServer);
				
				threadSleep(randomCommunicationDelayAtServer);
				
				threadSleep(1000);
				request.t2 = System.currentTimeMillis();

				oos.writeObject(request);
				oos.flush();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
