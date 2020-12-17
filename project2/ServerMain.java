import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Base64.Encoder;

import javax.crypto.*;
import javax.swing.JButton;

public class ServerMain extends JFrame implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static SimpleDateFormat formatter = new SimpleDateFormat("[hh:mm a]");
	private static HashMap<String, PrintWriter> connectedClients = new HashMap<>();
	private static final int MAX_CONNECTED = 50;
	private static int PORT;
	private static ServerSocket server;
	private static volatile boolean exit = false;

	// JFrame related
	private JButton btnStart;

	/**
	 * Launch the application.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		getRandomPort();
		new Thread(new ServerHandler()).start();

		SecretKey aesKey = KeyGenerator.getInstance("AES").generateKey();

		SecretKey desKey = KeyGenerator.getInstance("DES").generateKey();
		// get base64 encoded version of the key
		String encodedAesKey = Base64.getEncoder().encodeToString(aesKey.getEncoded());

		String encodedDesKey = Base64.getEncoder().encodeToString(desKey.getEncoded());

		addToLogs("AES Key: " + encodedAesKey);
		addToLogs("DES Key: " + encodedDesKey);

		SecureRandom random_aes = new SecureRandom();
		byte aes_init[] = new byte[16];
		random_aes.nextBytes(aes_init);
		Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		String aes_init64 = encoder.encodeToString(aes_init);

		SecureRandom random_des = new SecureRandom();
		byte des_init[] = new byte[16];
		random_des.nextBytes(des_init);
		Encoder encoder_des = Base64.getUrlEncoder().withoutPadding();
		String des_init64 = encoder_des.encodeToString(des_init);

		addToLogs("AES IV: " + aes_init64);
		addToLogs("DES IV: " + des_init64);

	}

	/**
	 * Create the frame.
	 */
	public ServerMain() {
        
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnStart) {
			if(btnStart.getText().equals("START")) {
				exit = false;
				getRandomPort();
				start();
				btnStart.setText("STOP");
			}else {
				addToLogs("Chat server stopped...");
				exit = true;
				btnStart.setText("START");
			}
		}
		
		//Refresh UI
		refreshUIComponents();
	}
	
	public void refreshUIComponents() {
		
	}

	public static void start() {
		new Thread(new ServerHandler()).start();
	}

	public static void stop() throws IOException {
		if (!server.isClosed()) server.close();
	}

	private static void broadcastMessage(String message) {
		for (PrintWriter p: connectedClients.values()) {
			p.println(message);
		}
	}
	
	public static void addToLogs(String message) {
		System.out.printf("%s\n", message);
	}

	private static int getRandomPort() {
		int port = 34567;
		PORT = port;
		return port;
	}
	
	private static class ServerHandler implements Runnable{
		@Override
		public void run() {
			try {
				server = new ServerSocket(PORT);
				while(!exit) {
					if (connectedClients.size() <= MAX_CONNECTED){
						new Thread(new ClientHandler(server.accept())).start();
					}
				}
			}
			catch (Exception e) {
				addToLogs("\nError occured: \n");
				addToLogs(Arrays.toString(e.getStackTrace()));
				addToLogs("\nExiting...");
			}
		}
	}
	
	// Start of Client Handler
	private static class ClientHandler implements Runnable {
		private Socket socket;
		private PrintWriter out;
		private BufferedReader in;
		private String name;
		
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run(){
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				for(;;) {
					name = in.readLine();
					if (name == null) {
						return;
					}
					synchronized (connectedClients) {
						if (!name.isEmpty() && !connectedClients.keySet().contains(name)) break;
						else out.println("INVALIDNAME");
					}
				}
				connectedClients.put(name, out);
				String message;
				while ((message = in.readLine()) != null && !exit) {
					if (!message.isEmpty()) {
						if (message.toLowerCase().equals("/quit")) break;
						broadcastMessage(String.format("[%s] %s", name, message));
					}
				}
			} catch (Exception e) {
				addToLogs(e.getMessage());
			} finally {
				if (name != null) {
					connectedClients.remove(name);
				}
			}
		}
	}

}

