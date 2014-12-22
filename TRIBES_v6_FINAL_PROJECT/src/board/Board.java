package board;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;




import boardToNetworkGame.AttackPoints;
import boardToNetworkGame.BoardOfWorldObjects;
import boardToNetworkGame.WorldObjectInBoard;


import tribe_army_and_obstacle_and_buildings.Hospital;
import tribe_army_and_obstacle_and_buildings.WorldObjectType;
import world.Position;

public class Board extends JLabel implements Serializable {

	private static final int LIN = 20;
	private static final int COL = 20;
	private Position[][] world = new Position[LIN][COL];
	LinkedList<Attack> attacks = new LinkedList<Attack>();

	private JFrame frame;

	private Hospital hospital;
	private ImageIcon hospitalImage;

	/**
	 * Creates a new Board
	 * @param frame
	 */
	public Board(JFrame frame) {
		this.frame = frame;

		hospitalImage = new ImageIcon("hospital.png");

	}

	/**
	 * Convert the pixel point to the matrix point
	 * @param pressedPoint in pixels
	 * @return point in matrix values
	 */
	public Point convertePoint(Point pressedPoint) {

		//ELIMINA O ERRO, ISTO É, RETIRA A PARTE DECIMAL DA DIVISAO DO PONTO, FICANDO ASSIM COM UM INTEIRO QUE REPRESENTA A POSIÇAO

		Point newPoint = new Point();

		double pointX = pressedPoint.getY() / (getWidth() / world.length);
		double pointY = pressedPoint.getX() / (getHeight() / (world[0]).length);


		double errorInX = pressedPoint.getY() / (getWidth() / world.length) % 1;
		double errorInY = pressedPoint.getX() / (getHeight() / (world[0]).length) % 1;

		newPoint.setLocation(pointX - errorInX, pointY - errorInY);

		return newPoint;
	}

	@Override
	public synchronized void paint(Graphics g) {
		super.paint(g);



		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		Position[][] world = this.world;

		synchronized (world) {



			// DESENHA RANGE
			for (int i = 0; i < world.length; i++) {
				for (int j = 0; j < world[0].length; j++) {
					if (world[i][j] != null)
						if (world[i][j].getObstacle()!=null && world[i][j].getSoldier()!=null) {
							Point point = world[i][j].getPoint();
							int range = world[i][j].getSoldier().range();

							Color color = new Color(235, 235, 235);
							g.setColor(color);
							// range*2+1 para contar com a casa onde se encontra
							g.fillRect((point.y - range) * (getWidth() / COL),
									(point.x - range) * (getHeight() / LIN),
									(getWidth() / COL) * (range * 2 + 1),
									(getHeight() / LIN) * (range * 2 + 1));
						}
				}
			}

			//Dessenha hospital
			for (Point hospitalPoints : hospital.getPoints()) {
				g.drawImage(hospitalImage.getImage(), hospitalPoints.y * (getWidth() / COL), hospitalPoints.x
						* (getHeight() / LIN), null);
			}


			// Desenhar personagens e obstaculos
			for (int i = 0; i < world.length; i++) {
				for (int j = 0; j < world[0].length; j++) {

					if(world[i][j]!=null)
						if (world[i][j].getSoldier()!=null ) {

							Point point = world[i][j].getSoldier().getPoint();

							if (world[i][j].getSoldier().getID() == 1) {
								g.setColor(new Color(255, 192, 37));
								g.fillRect(point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), (getWidth() / COL),
										(getHeight() / LIN));
							} else if (world[i][j].getSoldier().getID() == 2) {
								g.setColor(new Color(0, 0, 156));
								g.fillRect(point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), (getWidth() / COL),
										(getHeight() / LIN));
							} else if (world[i][j].getSoldier().getID() == 3) {
								g.setColor(new Color(0, 139, 0));
								g.fillRect(point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), (getWidth() / COL),
										(getHeight() / LIN));
							} else if (world[i][j].getSoldier().getID() == 4) {
								g.setColor(new Color(127, 255, 0));
								g.fillRect(point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), (getWidth() / COL),
										(getHeight() / LIN));
							}

							// DESENHA PERSONAGEM
							if (world[i][j].getSoldier()!=null) {
								if (!world[i][j].getSoldier().isPressed()) {
									if (world[i][j].getSoldier().getType().equals(
											WorldObjectType.GENERAL))
										g.setColor(Color.RED);
									else if (world[i][j].getSoldier().getType().equals(
											WorldObjectType.COMMAND))
										g.setColor(Color.GREEN);
									else if (world[i][j].getSoldier().getType().equals(
											WorldObjectType.INFANTRY))
										g.setColor(Color.MAGENTA);
									else if (world[i][j].getSoldier().getType().equals(
											WorldObjectType.SNIPER))
										g.setColor(Color.blue);
								} else {
									if (world[i][j].getSoldier().getType().equals(
											WorldObjectType.GENERAL))
										g.setColor(Color.orange);
									else if (world[i][j].getSoldier().getType().equals(
											WorldObjectType.COMMAND))
										g.setColor(Color.orange);
									else if (world[i][j].getSoldier().getType().equals(
											WorldObjectType.INFANTRY))
										g.setColor(Color.orange);
									else if (world[i][j].getSoldier().getType().equals(
											WorldObjectType.SNIPER))
										g.setColor(Color.orange);

								}

								g.fillOval(point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), (getWidth() / COL),
										(getHeight() / LIN));


								if (world[i][j].getSoldier().isMoving())
									g.setColor(Color.YELLOW);
								g.fillOval(point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), (getWidth() / COL) / 2,
										(getHeight() / LIN) / 2);

								// DESENHAR BARRA VIDA

								g.setColor(Color.black);
								g.fillRect(point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), (getWidth() / COL), 5);



								if (world[i][j].getSoldier().health() >= 0) {
									g.setColor(Color.GREEN);
									g.fillRect(
											point.y * (getWidth() / COL),
											point.x * (getHeight() / LIN),
											((getWidth() / COL) * world[i][j].getSoldier().health()) / 100,
											5);
								}
							} 


							// DESENHAR LINHAS

							for (int k = 0; k < getBoardWidth(); k++) {
								g.setColor(Color.GRAY);

								g.drawLine(0, (getHeight() / getBoardWidth()) * k,
										getWidth(), (getHeight() / getBoardWidth()) * k);

							}

							for (int h = 0; h < getBoardHeight(); h++) {

								g.drawLine((getWidth() / getBoardHeight()) * h, 0,
										(getWidth() / getBoardHeight()) * h,
										getHeight());
							}

							// Desenhar ATTACKS

							for (Attack attack : attacks) {

								g.setColor(new Color(255, 28, 174));

								g.drawLine(
										attack.getAttackingObject().getPoint().y
										* (getHeight() / LIN)
										+ (getHeight() / LIN) / 2,

										attack.getAttackingObject().getPoint().x
										* (getHeight() / LIN)
										+ (getHeight() / LIN) / 2,

										attack.getDefendeObject().getPoint().y
										* (getHeight() / LIN)
										+ (getHeight() / LIN) / 2,

										attack.getDefendeObject().getPoint().x
										* (getHeight() / LIN)
										+ (getHeight() / LIN) / 2);


							}

						}else if(world[i][j].getObstacle()!=null) {

							Point pointObstacle = world[i][j].getObstacle().getPoint();
							g.setColor(Color.BLACK);
							g.fillRect(pointObstacle.y * (getWidth() / COL), pointObstacle.x
									* (getHeight() / LIN), (getWidth() / COL),
									(getHeight() / LIN));

						}
				}
			}

		}
	}

	public int getBoardHeight() {
		return (world[0]).length;
	}

	public int getBoardWidth() {

		return world.length;

	}

	/**
	 * Updates the current matrix to the new players positions
	 * @param world2 - matrix
	 */
	public void updateWorld(Position[][] world) {


		for (int i = 0; i < this.world.length; i++) {
			for (int j = 0; j < this.world[0].length; j++) {
				this.world[i][j]=world[i][j];
			}
		}

	}

	/**
	 * Add a new attackList for draw
	 * @param attackList
	 */
	public void addAttack(LinkedList<Attack> attackList) {
		attacks.clear();

		attacks.addAll(attackList);
	}


	/**
	 * Creates a copy from the current world to be send to clients
	 * @return boardOfWorldObjects
	 */
	public BoardOfWorldObjects copyFromBoard() {

		BoardOfWorldObjects boardToSend = new BoardOfWorldObjects(LIN, COL);

		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[0].length; j++) {
				if (world[i][j].getSoldier() != null) {
					//COPIA PARAMENTROS DOS SOLDADOS
					int iD = world[i][j].getSoldier().getID();

					WorldObjectType type = world[i][j].getSoldier().getType();
					Point point = world[i][j].getPoint();
					int range = world[i][j].getSoldier().range();
					int health = world[i][j].getSoldier().health();
					boolean isMoving = world[i][j].getSoldier().isMoving();
					boolean iSPressed = world[i][j].getSoldier().isPressed();
					boardToSend.setPosition(i, j, new WorldObjectInBoard(iD,
							type, point, range, health, isMoving, iSPressed));
				} else if (world[i][j].getObstacle() != null) {
					//COPIA PARAMETROS DOS OBJECTOS
					WorldObjectType type = world[i][j].getObstacle().getType();
					Point point = world[i][j].getPoint();
					boardToSend.setPosition(i, j, new WorldObjectInBoard(0,
							type, point, 0, 0, false, false));
				}
			}
		}

		boardToSend.setAttackList(copyAttackPoints());
		boardToSend.setHospital(hospital);

		return boardToSend;
	}

	/**
	 * Copy the current AttackPoints
	 * @return LinkedList<AttackPoints>
	 */
	private LinkedList<AttackPoints> copyAttackPoints() {

		LinkedList<AttackPoints> attackPoints = new LinkedList<AttackPoints>();
		if(attacks.size()>0){
			for (Attack attack : attacks) {
				attackPoints.add(new AttackPoints(attack.getAttackingObject().getPoint(), attack.getDefendeObject().getPoint()));
			}
			return attackPoints;
		}
		return attackPoints;
	}

	/**
	 * Set the Hospital 
	 * @param hostipal
	 */
	public void setHospital(Hospital hostipal) {
		this.hospital=hostipal;

	}

	/**
	 * Get the Hospital from the Board
	 * @return hospital
	 */
	public Hospital getHospital(){
		return hospital;
	}
}