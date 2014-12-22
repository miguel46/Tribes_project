package server_client;

import java.awt.Point;
import java.io.Serializable;

public class ClientOrder implements Serializable {

	private int iD;
	private Point point;
	private int button;

	/**
	 * Creates a order, to the client with the iD, with the button pressed and the point
	 * @param iD
	 * @param point
	 * @param button
	 */
	public ClientOrder(int iD, Point point, int button) {
		this.iD = iD;
		this.point = point;
		this.button = button;
	}

	/**
	 * Get the client iD of the order
	 * @return
	 */
	public int getiD() {

		return iD;
	}

	/**
	 * Get the point
	 * @return
	 */
	public Point getPoint() {

		return point;
	}

	/**
	 * Get the button of the order
	 * @return
	 */
	public int getButton() {
		return button;
	}

}
