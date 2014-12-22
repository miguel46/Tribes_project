package server_client;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import board.Board;
import boardToNetworkGame.BoardOfWorldObjects;


public class Client  {

	public final static int PORT = 8080;

	private JFrame chatWindow;
	private JFrame gameWindow;

	private JTextArea chatArea;
	private JTextField textFieldInput;
	private JButton shutDownTheSession;
	private JScrollPane jScrollPane; 

	private Socket socket;
	private ObjectOutputStream oos;
	private ClientListener listener;

	private String nickName;
	private int iD;

	private PlayerServer player;
	private BoardOfWorldObjects board;
	
	private Color color;

	/**
	 * Creates a new Client to play the game, have a game window and a chat window
	 */
	public Client() {

		//GAME WINDOW
		gameWindow = new JFrame("game");
		gameWindow.setSize(706, 728);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameWindow.setLocationRelativeTo(null);
		gameWindow.setResizable(false);

		board = new BoardOfWorldObjects(20, 20);
		gameWindow.getContentPane().add(board);

		//CHAT WINDOW
		chatWindow = new JFrame("Chat");
		chatWindow.setSize(280, 500);
		chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);

		textFieldInput = new JTextField(20);

		//KEY LISTENER FOR TEXT INPUT
		addListenerToTextField();

		JPanel panel = new JPanel();
		panel.add(textFieldInput);

		shutDownTheSession = new JButton("Log Off");

		shutDownTheSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					//ENVIA UM BOOLEANO PARA SER IDENTIFICADO PELOS CLIENTES QUE É UMA MENSAGEM ESPECIAL, NESTE CASO SERVE PARA ENCERRAR A LIGAÇAO
					Boolean booleanMessage = new Boolean(false);
					oos.writeObject(booleanMessage);
					oos.writeObject(new Message(nickName, "Close the session!", color));

				} catch (IOException e) {
					System.err.println("IOException Cliente shutDownTheSession");
				} 
				chatWindow.dispose();
			}
		});

		chatWindow.getContentPane().add(chatArea, BorderLayout.CENTER);
		chatWindow.getContentPane().add(panel, BorderLayout.SOUTH);
		chatWindow.getContentPane().add(shutDownTheSession, BorderLayout.NORTH);

		jScrollPane = new JScrollPane(chatArea);

		jScrollPane
		.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		chatWindow.getContentPane().add(jScrollPane);

		//AUTOSCROLLBAR PARA A JANELA DO CHAT
		jScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  

			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				arg0.getAdjustable().setValue(arg0.getAdjustable().getMaximum());  
			}}); 
	}

	/**
	 * Add the keyListener to the textField, press "enter" and the message will be send
	 */
	private void addListenerToTextField() {
		textFieldInput.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						oos.writeObject(new Message(nickName, textFieldInput.getText(), color));
					} catch (IOException e) {
						System.err.println("IOException Client KeyListener");
					}
					textFieldInput.setText("");
				}
			}
		});
	}

	/**
	 * Add text to the chatArea
	 * @param msg
	 */
	public void addTextChatArea(String msg) {
		
		chatArea.setText(chatArea.getText()+ System.getProperty("line.separator") + msg);
		
	}

	/**
	 * Get the iD from this client
	 * @return
	 */
	public int getiD() {
		return iD;
	}

	/**
	 * Creates a thread to listen the stream
	 * @author Toshiba
	 *
	 */
	class ClientListener extends Thread {
		private ObjectInputStream in;

		/**
		 * Creates a thread to listem de objectInputStream
		 * @param objectInputStream
		 */
		public ClientListener(ObjectInputStream objectInputStream) {
			this.in = objectInputStream;
		}

		@Override
		public void run() {

			try {

				while (!interrupted()) {

					Object object = null;
					try {
						object = in.readObject();
					} catch (ClassNotFoundException e) {
						System.err.println("ClassNotFoundException ClientListener");
					}

					if (object instanceof Message) {

						Message message = (Message) object;
						chatArea.setForeground(message.getColor());
						addTextChatArea(message.getMessage());

					}else if (object instanceof BoardOfWorldObjects) {

						board.setAttackList(((BoardOfWorldObjects) object).getAttackList());
						board.setWorld(((BoardOfWorldObjects) object).getWorld());
						board.setHospital(((BoardOfWorldObjects) object).getHospital());

						board.repaint();
						board.validate();


					} else if (object instanceof Integer) {

						//CLIENTE RECEBE UM ID QUE PERMITE IDENTIFICAR O SEU ARMY NO DECORRER DO JOGO
						Integer integer = (Integer) object;
						iD = integer.intValue();

						player = new PlayerServer(gameWindow, getiD());

						gameWindow.setTitle("GAME - Player "+iD +": "+nickName);

						if(iD==1){
							addTextChatArea("You are the BLUE corner");
							color = new Color(0, 0, 255);
						}else if(iD==2){
							addTextChatArea("You are the RED corner");
							color = new Color(150, 150, 150);

						}else if(iD==3){
							addTextChatArea("You are the GREEN corner");
							color = new Color(0, 255, 0);

						}

						
						
					} else if( object instanceof Board){

						gameWindow.getContentPane().removeAll();
						gameWindow.getContentPane().add((Board) object);

					} else if(object instanceof Boolean){

						listener.interrupt();
						oos.close();
						socket.close();
						JOptionPane.showMessageDialog(gameWindow, "SERVIDOR ENCERROU LIGAÇAO");

						gameWindow.dispose();
						chatWindow.dispose();

//						System.exit(0);

					}

				}
			} catch (IOException e) {
				System.err.println("IOException NO CLIENTE LISTENER DO CANAL INPUT CLIENT");
			}
		}
	}

	/**
	 * Connect to server
	 * @return
	 * @throws IOException
	 */
	public Socket connectToServer() throws IOException {
		InetAddress address = InetAddress.getByName(null);
		addTextChatArea("Connecting to: " + address);
		return new Socket(address, PORT);
	}

	/**
	 * Creates a new player to this client, with a mouse listener to play the game
	 * @author Toshiba
	 *
	 */
	class PlayerServer {

		private JFrame frame;
		private int iD;

		/**
		 * New player server, with iD, and a frame to add the listener
		 * @param frame
		 * @param iD
		 */
		public PlayerServer(JFrame frame, int iD) {
			this.frame = frame;
			this.iD = iD;
			addMouseListener();

		}

		/**
		 * Add the mouseListener to the contentpane of the frame
		 */
		private void addMouseListener() {
			frame.getContentPane().addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {

						try {

							oos.writeObject(new ClientOrder(iD, new Point(e.getX(), e.getY()), e.getButton()));

						} catch (IOException e1) {
							System.err.println("PLAYER SERVER IOException AO ENVIAR BUTTON 1");
						}

					} else if (e.getButton() == MouseEvent.BUTTON3) {
						try {

							oos.writeObject(new ClientOrder(iD, new Point(e.getX(), e.getY()), e.getButton()));

						} catch (IOException e1) {
							System.err.println("PLAYER SERVER IOException AO ENVIAR BUTTON 3");
						}
					}

				}
			});
		}
	}

	public static void main(String[] args) throws IOException {
		Client client = new Client();
		client.execute();
	}

	/**
	 * Execute the cliente, set the frames to visible, connect to server, and add a new listener to the stream
	 * @throws IOException
	 */
	public void execute() throws IOException {
		this.nickName=null;
		while(nickName==null){
			this.nickName = JOptionPane.showInputDialog(chatWindow, "Nickname:");
			if(nickName==""){
				nickName=null;
			}
		}

		chatWindow.setTitle("Chat - "+nickName);
		gameWindow.setTitle("Game - "+nickName);

		chatWindow.setVisible(true);
		gameWindow.setVisible(true);

		addTextChatArea("Conecting...");
		try{
			socket = connectToServer();
			addTextChatArea("ACCESS GRANTED: " + socket);

			oos = new ObjectOutputStream(socket.getOutputStream());

			listener = new ClientListener(new ObjectInputStream(socket.getInputStream()));
			listener.start();
			
		}catch (ConnectException e) {
			JOptionPane.showMessageDialog(gameWindow,
					"No server found!",
					"Server connect error",
					JOptionPane.ERROR_MESSAGE);
		}

	}
}
