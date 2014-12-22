package server_client;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class ClientOrders implements Serializable{

	/**
	 * Queue of orders that are submitted to the server
	 */
	private Queue<ClientOrder> clientsOrders;

	/**
	 * Creates a list of orders received by the server
	 */
	public ClientOrders() {
		clientsOrders = new LinkedList<ClientOrder>();
	}

	/**
	 * Submitte a order to the server
	 * @param order
	 */
	public synchronized void putOrder(ClientOrder order){
		clientsOrders.add(order);

		notifyAll();
	}

	/**
	 * Get the order to server processes
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized ClientOrder getOrder() throws InterruptedException{



		while(clientsOrders.size()==0)
			wait();

		return clientsOrders.poll();
	}




}
