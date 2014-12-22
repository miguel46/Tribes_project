package server_client;

import game.Engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;



import tribe_army_and_obstacle_and_buildings.Army;
import world.World;

public class Server{

	public final static int PORT = 8080;

	private JFrame window;
	private JFrame gameWindow;

	private JTextArea textArea;
	private JButton close ;

	private ServerSocket serverSocket;
	private ClientsAcceptServer server_accept;
	private LinkedList<ObjectOutputStream> listClientsOuts;
	private LinkedList<Thread> listClientIns;

	// JOGO
	private World world;
	private Engine engine;
	private int numberOfTribes = 1;
	private ClientOrders clientOrders;
	private int MAX_NUMBER_OF_TRIBES=0;

	/**
	 * Creates a new server to the game
	 * @throws InterruptedException
	 */
	public Server() throws InterruptedException {

		world = new World(window);
		engine = new Engine(world);
		clientOrders = new ClientOrders();

		gameWindow = new JFrame("GAME WINDOW SERVER");
		gameWindow.setSize(706, 728);
		gameWindow.setLocation(25, 10);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameWindow.getContentPane().add(world.getBoard());
		gameWindow.setResizable(false);

		listClientsOuts = new LinkedList<ObjectOutputStream>();
		listClientIns = new LinkedList<Thread>();

		window = new JFrame("Servidor");
		window.setSize(500, 400);
		window.setLocation(750, 250);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);

		textArea = new JTextArea();

		window.getContentPane().add(textArea, BorderLayout.CENTER);

		close = new JButton("Shut Down Server");

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {

					//QUANDO DO ENCERRAMENTO DA LIGAÇAO O SERVIDOR ENVIA UM BOOLEAN PARA  OS CLIENTES, 
					//FICANDO ASSIM A SABER QUE A LIGAÇAO TERMINOU
					//ENCERRA OS OOP, AS THREADS E O SOCKET
					for (ObjectOutputStream oop : listClientsOuts) {
						Boolean booleanA = new Boolean(false);
						oop.writeObject(booleanA);
					}

					for (ObjectOutputStream oop : listClientsOuts) {
						oop.close();
					}

					addTextToTextArea("Closing...");

					serverSocket.close();

					for (Thread client_in : listClientIns) {
						client_in.interrupt();
						client_in.join();
					}

					server_accept.interrupt();
					server_accept.join();
					window.dispose();
					gameWindow.dispose();

//					System.exit(0);


				} catch (IOException e) {
					System.err.println("ERROR CLOSE BUTTON IOEXCEPTION");
				} catch (InterruptedException e) {
					System.err.println("ERROR CLOSE BUTTON Interrupted EXCEPTION");
				}
			}
		});
		window.getContentPane().add(close, BorderLayout.SOUTH);
	}


	/**
	 * Thread to deal with the orders of the players
	 * @author Toshiba
	 *
	 */
	class OrdersManager extends Thread {

		private ClientOrders clientOrders;

		/**
		 * Creates a new thread with a list of orders to the processed
		 * @param clientOrders
		 */
		public OrdersManager(ClientOrders clientOrders) {
			this.clientOrders = clientOrders;
		}

		public void run() {
			try {
				while (!isInterrupted()) {

					sleep(25);

					//VERIFICA SE O JOGO JA TERMINOU
					if(engine.gameFinished()){

						broadcast(new Message("MASTER", "GAME FINISH"+"\n"+"Player "+engine.getWinnerPlayer()+" was won!", Color.RED));
						engine.finishGame();
						interrupt();

					}else{
						//VAI RETIRAR ORDENS QUE SAO ENVIADAS PELOS CLIENTE 
						ClientOrder order = clientOrders.getOrder();

						if (order != null) {

							//SE A ORDEM QUE FOI ENVIADA CORRESPONDE AO BOTAO DO LADO ESQUERDO DO RATO
							if (order.getButton() == MouseEvent.BUTTON1) {

								//VERIFICA SE TEM O JOGADOR SELECCIONADO
								if (engine.hasSelectSoldier(order.getiD())) {

									if (engine.pointIsEmpty(order.getiD(),new Point(order.getPoint().x, order.getPoint().y))){
										//SE O PONTO QUE O CLIENTE PREMIU ESTA VAZIO, O SOLDADO QUE ESTAVA SELECCIONADO IRÁ MOVER-SE PARA ESTA POSICAO
										engine.moveSoldier(new Point(world.getPointInPixels(new Point(order.getPoint().x,order.getPoint().y))),order.getiD(), false);
									}

								}else if (engine.checkPosition(new Point(order.getPoint().x, order.getPoint().y), order.getiD())){
									//SE NAO HOUVER SOLDADOS SELECCIONADOS, O PONTO QUE CARREGOU SE NAO FOR UM PONTO INVALIDO, IRA SELECCIONA UM SOLDADO DA SUA EQUIPA
									engine.presseSoldier(new Point(order.getPoint().x, order.getPoint().y), order.getiD());

								}

							} else if (order.getButton() == MouseEvent.BUTTON3) {

								//SE A ORDEM FOI ENVIADA COM O BOTAO DO LADO DIREITO DO RATO SIGNIFICA QUE O CLIENTE PRETENDE DESSELECCIONAR O SOLDADO PREMIDO
								engine.unselectSoldier();
							}

						}

					}

				}
			} catch (InterruptedException e) {
				System.err.println("INTERRUPTED EXCEPTION THREAD ORDERS MANAGER");
			}
		}
	}

	/**
	 * Broadcast the object to all clients
	 * @param object
	 */
	public synchronized void broadcast(Object object) {

		for (ObjectOutputStream oos : listClientsOuts) {

			try {
				oos.reset();

				oos.writeObject(object);

			} catch (IOException e) {
				//NAO ENVIA MENSAGENS QUANDO PERSONAGEM SE ESTÁ A MOVER
				System.err.println("STREAM OCCUPIED IOEXCEPTION SERVER BROADCAST");

			}
		}
	}
	/**
	 * Thread to listen deal with new connections to the server
	 * @author Toshiba
	 *
	 */
	class ClientsAcceptServer extends Thread {
		public void run() {
			try {

				addTextToTextArea("SERVER: " + serverSocket);

				while (!interrupted()) {

					addTextToTextArea("LISTENING FOR CONNECTIONS...");

					Socket clientSocket = serverSocket.accept();

					addTextToTextArea("New connection: " + clientSocket);

					GameClientListener clientListener = new GameClientListener(
							new ObjectInputStream(
									clientSocket.getInputStream()));

					listClientIns.add(clientListener);

					clientListener.start();

					ObjectOutputStream oos = new ObjectOutputStream(
							clientSocket.getOutputStream());

					oos.writeObject(new Integer(numberOfTribes));

					listClientsOuts.add(oos);
					engine.addOutputStream(oos);

					world.addPlayersTribe(new Army(numberOfTribes, engine, world));

					broadcast(world.getBoard().copyFromBoard());
					addTextToTextArea("Number of players in the server: "+numberOfTribes);
					
					if (numberOfTribes == MAX_NUMBER_OF_TRIBES) {

						broadcast(new Message("MASTER", "LET'S FIGHT!!", Color.RED));

						world.execute();

						new OrdersManager(clientOrders).start();

						addTextToTextArea("Server full... Shuting down the connections listener!");

						//						interrupt();

					}
					
					numberOfTribes++;
					


				}
			} catch (IOException e) {
				System.err.println("IOEXCEPTION ClientServerAccept");
			} finally {

				try {
					serverSocket.close();
					for (Thread clientIn : listClientIns) {
						clientIn.interrupt();
						clientIn.join();
					}
				} catch (IOException e) {
					System.err.println("IOEXCEPTION ClientServerAccept ");

				} catch (InterruptedException e) {
					System.err.println("INTERRUPTED EXCEPTION ClienteServerAccept");
				}
			}
		}
	}

	/**
	 * Thread to listen one objectInputStream of one client
	 * @author Toshiba
	 *
	 */
	class GameClientListener extends Thread {

		private ObjectInputStream objectInputStream;

		public GameClientListener(ObjectInputStream objectInputStream) {
			this.objectInputStream = objectInputStream;
		}

		public void run() {
			try {
				while (!interrupted()) {

					Object object = null;
					try {
						object = objectInputStream.readObject();
					} catch (ClassNotFoundException e) {
						System.err.println("ClassNotFoundException GameClientListener");
					}

					if (object instanceof ClientOrder) {


						ClientOrder order = (ClientOrder) object;
						clientOrders.putOrder(order);



					}else if(object instanceof Boolean){
						//VERIFICA O FECHO DE LIGACAO POR PARTE DO CLIENTE, SERVER ENCERRA!


						for (ObjectOutputStream oos : listClientsOuts) {
							oos.writeObject(new Boolean(false));
							oos.close();
						}

						for (Thread listener : listClientIns) {
							listener.interrupt();
							try {
								listener.join();
							} catch (InterruptedException e) {

								System.err.println("INTERRUPTED EXCEPTION GameClientListener");
							}

							JOptionPane.showMessageDialog(gameWindow, "Cliente desligou a sessão! Jogo interrompido!");

							gameWindow.dispose();
							window.dispose();

//							System.exit(0);


						}
					}else {
						addTextToTextArea("BROADCAST MSG");
						broadcast(object);


					} 
				}
			} catch (IOException e) {
				System.err.println("IO EXCEPTION GameClientListener");
			}
		}
	}

	/**
	 * Execute the server app, set de frames visible to true, start the server socket and the thread to deal with new connections
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void execute() throws IOException, InterruptedException {

		String s =null;

		while(s==null){
			Object[] possibilities = {"2", "3"};
			s = (String)JOptionPane.showInputDialog(gameWindow, "CHOOSE THE NUMBER OF CLIENTS TO YOUR SERVER:", "NUMBER OF CLIENTS",
					JOptionPane.PLAIN_MESSAGE,
					null,
					possibilities,
			"2");


			if ((s != null) && (s.length() > 0))
				this.MAX_NUMBER_OF_TRIBES=Integer.parseInt(s);

		}



		window.setVisible(true);
		gameWindow.setVisible(true);

		serverSocket = new ServerSocket(PORT);

		addTextToTextArea("Hello! I'm the server!");

		server_accept = new ClientsAcceptServer();
		server_accept.start();
	}

	/**
	 * Add text to the textArea of the server
	 * @param msg
	 */
	public synchronized void addTextToTextArea(String msg) {

		textArea.setText(textArea.getText() + "\n" + msg);
	}

	public static void main(String[] args) throws IOException,InterruptedException {
		Server server = new Server();
		server.execute();
	}
}
