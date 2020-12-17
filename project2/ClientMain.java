import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JLabel;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextField;

public class ClientMain{	

    // Socket Related
	private static Socket clientSocket;
	private static PrintWriter out;

    public static final Color VERY_LIGHT_GREY = new Color(204, 204, 204);

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        JFrame frame = new JFrame("Crypto Messenger");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        JPanel topPanel = new JPanel();
        JPanel methodPanel = new JPanel();
        JPanel modePanel = new JPanel();
        JPanel serverPanel = new JPanel();
        

        JButton connect = new JButton("Connect");
        JButton disconnect = new JButton("Disconnect");
        serverPanel.add(connect);
        serverPanel.add(disconnect);
        disconnect.setEnabled(false);
        serverPanel.setBorder(BorderFactory.createTitledBorder("Server"));
        JTextField name = new JTextField();

        connect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame login = new JFrame("Input");
                login.setVisible(true);
                login.setSize(300, 100);

                JPanel panel = new JPanel();
                JLabel username = new JLabel("Enter user name:");
                
                name.setPreferredSize(new Dimension(150, 20));
                panel.add(username);
                panel.add(name);

                JButton ok = new JButton("OK");
                ok.addActionListener(new ActionListener() {
                    String userName;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        userName = name.getText();
                        //System.out.println(userName);
                        start(userName);
                        connect.setEnabled(false);
                        disconnect.setEnabled(true);
                        login.dispose();

                    }

                });

                JButton cancel = new JButton("Cancel");

                cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        login.dispose();
                    }
                });

                panel.add(ok);
                panel.add(cancel);

                login.add(panel);

            }
            // button functions
        });

        topPanel.add(serverPanel);

        JRadioButton aes = new JRadioButton("AES");
        JRadioButton des = new JRadioButton("DES");
        ButtonGroup methodGroup = new ButtonGroup();
        methodGroup.add(aes);
        methodGroup.add(des);
        methodPanel.add(aes);
        methodPanel.add(des);
        methodPanel.setBorder(BorderFactory.createTitledBorder("Method"));

        topPanel.add(methodPanel);

        JRadioButton cbc = new JRadioButton("CBC");
        JRadioButton ofb = new JRadioButton("OFB");
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(cbc);
        modeGroup.add(ofb);
        modePanel.add(cbc);
        modePanel.add(ofb);
        modePanel.setBorder(BorderFactory.createTitledBorder("Mode"));

        topPanel.add(modePanel);

        // Creating the panel at bottom and adding components
        JPanel bottomPanel = new JPanel(); // the panel is not visible in output
        JPanel textPanel = new JPanel();
        JPanel cryptPanel = new JPanel();

        JTextField text = new JTextField();
        textPanel.add(text);
        textPanel.setBorder(BorderFactory.createTitledBorder("Text"));

        text.setPreferredSize(new Dimension(150, 50));
        JTextField crypt = new JTextField();
        cryptPanel.add(crypt);
        cryptPanel.setBorder(BorderFactory.createTitledBorder("Crypted Text"));

        crypt.setPreferredSize(new Dimension(150, 50));
        JButton encrypt = new JButton("Encrypt");
        JButton send = new JButton("Send");
        // Components Added using Flow Layout
        String nameBottom = name.getText().trim();
        JLabel notconnected = new JLabel();
        if(nameBottom != null){
            notconnected.setText("Connected: " + nameBottom);
        }
        else {
            notconnected.setText("<html><br><br><br><br><br><br>Not Connected</html>");
        }
        
        bottomPanel.add(notconnected);
        bottomPanel.add(textPanel);

        bottomPanel.add(cryptPanel);
        bottomPanel.add(encrypt);
        bottomPanel.add(send);

        send.setEnabled(false);

        encrypt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String message = text.getText().trim();
                crypt.setText(message);
                encrypt.setEnabled(false);

                
                send.setEnabled(true);
            }
            
        });

        send.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String message = text.getText().trim();
                if(!message.isEmpty()) {
                    out.println(message);
                    text.setText("");
                }
                refreshUIComponents();

                send.setEnabled(false);
                encrypt.setEnabled(true);
            }

            
            
        });

        
        // Text Area at the Center
        JScrollPane scrollPane = new JScrollPane();
        JTextArea txtAreaLogs = new JTextArea();
		txtAreaLogs.setBackground(VERY_LIGHT_GREY);
		txtAreaLogs.setForeground(Color.BLACK);
        txtAreaLogs.setLineWrap(true);
        
        scrollPane.setViewportView(txtAreaLogs);

        System.setOut(new PrintStream(new TextAreaOutputStream(txtAreaLogs)));

        // Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
        frame.getContentPane().add(BorderLayout.NORTH, topPanel);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
        frame.setVisible(true);
        
    }


    public static void refreshUIComponents() {

    }

    public static void start(String clientName) {
        try {
            clientSocket = new Socket("localhost", 34567);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
			new Thread(new Listener()).start();
			//send name
			out.println(clientName);
		} catch (Exception err) {
			addToLogs("[ERROR] "+err.getLocalizedMessage());
		}
	}

	public void stop(){
		if(!clientSocket.isClosed()) {
			try {
				clientSocket.close();
			} catch (IOException e1) {}
		}
	}

	public static void addToLogs(String message) {
		System.out.printf("%s\n", message);
	}

	private static class Listener implements Runnable {
		private BufferedReader in;
		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String read;
				for(;;) {
					read = in.readLine();
					if (read != null && !(read.isEmpty())) addToLogs(read);
				}
			} catch (IOException e) {
				return;
			}
		}

	}


}


