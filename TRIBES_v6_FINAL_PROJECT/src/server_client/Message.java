package server_client;

import java.awt.Color;
import java.io.Serializable;

public class Message implements Serializable {
	private String iD;
	private String message;
	private Color color;

	/**
	 * Creates a text message to be send over the stream
	 * @param iD
	 * @param string
	 */
	public Message(String iD, String string, Color color) {
		this.setiD(iD);
		this.setMessage(string);
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

	public String getMessage() {
		return getiD()+": "+message;
	}

	public void setiD(String iD) {
		this.iD = iD;
	}

	public String getiD() {
		return iD;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}
