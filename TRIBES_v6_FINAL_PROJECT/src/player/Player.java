package player;

import game.Engine;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.JFrame;

import tribe_army_and_obstacle_and_buildings.Army;

public class Player implements Serializable{

	private JFrame frame;
	private Engine engine;
	private Army tribe;

	private final int iD;

	/**
	 * Creates a new player, with a mouse listener to client play
	 * @param frame
	 * @param engine
	 * @param iD
	 */
	public Player(JFrame frame, Engine engine, int iD) {
		this.frame = frame;
		this.engine = engine;
		this.iD = iD;

		addMouseListener();

	}

	/**
	 * Add mouse listener to the board
	 */
	private void addMouseListener() {
		frame.getContentPane().addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {

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

				try {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (engine.hasSelectSoldier(iD)) {

							if (engine.pointIsEmpty(iD,new Point(e.getX(), e.getY()))) {
								engine.moveSoldier(new Point(e.getX(), e.getY()), iD,false);
							}
						}

						// Seleccionar soldiers
						else if (engine.checkPosition(new Point(e.getX(), e.getY()), iD)){
							engine.presseSoldier(new Point(e.getX(), e.getY()), iD);

						}

					} else if (e.getButton() == MouseEvent.BUTTON3) {

						engine.unselectSoldier();
					}


				} catch (InterruptedException ie) {
					System.err
					.println("InterruptedException LISTENER Player");
				}
			}
		});

	}

	/**
	 * Get the tribe to this player
	 * @return
	 */
	public Army getTribe() {
		return tribe;
	}

}
