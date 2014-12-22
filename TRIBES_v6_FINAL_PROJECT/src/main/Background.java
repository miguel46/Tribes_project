package main;


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Background extends JLabel {

	private JFrame frame;
	private int x;
	private int y;
	
	private ImageIcon background;
	private ImageIcon server;
	private ImageIcon client;
	
	public Background(JFrame frame){
		this.frame=frame;
		background= new ImageIcon("ww2.jpg");
		server = new ImageIcon("server-network.png");
		client = new ImageIcon("client.png");
		
		setLayout(new FlowLayout());
		
	}
	
	public void setPoints(Point point){
		this.x=point.x;
		this.y=point.y;
		repaint();
		validate();
	}
	
	public void  paint(Graphics g){
		super.paint(g);
	
		
		g.drawImage(background.getImage(), 0, 0, null);
		
		g.drawImage(server.getImage(), 50, 50, null);
				
		g.drawImage(client.getImage(), 350, 50, null);
				
		g.setColor(Color.RED);
		g.drawOval(x-(10), y-(10), 20, 20);
		g.drawLine(x, 0, x, getHeight());
		g.drawLine(0, y, getWidth(), y);
		
		
		
	}
	
	
}
