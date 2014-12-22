package main;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import server_client.Client;
import server_client.Server;

public class Start {

	private JFrame frame;
	private JButton startServer = new JButton("START SERVER");
	private JButton startCliente = new JButton("START CLIENT");


	public Start(){

		frame = new JFrame("Tribes - The Game");
		frame.setSize(500, 250);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout());
		final Background background = new Background(frame);
		frame.getContentPane().add(background);

		frame.getContentPane().addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				//POSIÇAO DA IMAGEM DO SERVIDOR
				if(e.getX()>=50 && e.getY()>=50 && e.getX()<=125 && e.getY()<=125){
					try {
						Server server = new Server();
						server.execute();
					} catch (InterruptedException e1) {
						System.err.println("INTERRUPTED EXCEPTION START SERVER");

					} catch (IOException ei) {
						System.err.println("IO EXCEPTION START SERVER");
					}
					//POSIÇAO DA IMAGEM DO CLIENTE
				}else if(e.getX()>=350 && e.getY()>=50 && e.getX()<=425 && e.getY()<=125){
					Client client = new Client();
					try {
						client.execute();
					} catch (IOException e1) {
						System.err.println("IO EXCEPTION START CLIENT");
					}
				}


			}
		});


		frame.getContentPane().addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent arg0) {


				background.setPoints(new Point(arg0.getX(), arg0.getY()));


			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});



	}

	public void execute(){
		frame.setVisible(true);
	}

	public static void main(String[] args){

		Start start = new Start();
		start.execute();

	}
}
