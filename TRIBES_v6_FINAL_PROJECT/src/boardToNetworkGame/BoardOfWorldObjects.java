package boardToNetworkGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import tribe_army_and_obstacle_and_buildings.Hospital;
import tribe_army_and_obstacle_and_buildings.WorldObjectType;


public class BoardOfWorldObjects extends JLabel implements Serializable{
	private static final int LIN = 20;
	private static final int COL = 20;
	private WorldObjectInBoard[][] world;
	LinkedList<AttackPoints> attacks ;
	private Hospital hospital;
	private ImageIcon obstacle;
	private ImageIcon wallpaper;
	private ImageIcon command;
	private ImageIcon sniper;
	private ImageIcon king;
	private ImageIcon soldier;
	private ImageIcon hospitalImage;

	/**
	 * Creates a board to client
	 * @param lin
	 * @param col
	 */
	public BoardOfWorldObjects(int lin, int col) {
		world = new WorldObjectInBoard[LIN][COL];
		attacks = new LinkedList<AttackPoints>();
		obstacle = new ImageIcon("obstacle.png");
		wallpaper = new ImageIcon("wallpaper.jpg");
		command= new ImageIcon("command.png");
		sniper= new ImageIcon("sniper.png");
		king = new ImageIcon("general.png");
		soldier = new ImageIcon("soldier.png");
		hospitalImage = new ImageIcon("hospital.png");

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		g.drawImage(wallpaper.getImage(), 0, 0, null);

		// DESENHA RANGE
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[0].length; j++) {
				if (world[i][j] != null)
					if (!world[i][j].getType().equals(WorldObjectType.OBSTACLE)) {
						Point point = world[i][j].getPoint();
						int range = world[i][j].range();
						Color color=null;
						if(world[i][j].getID()==1)
							color = new Color (0.0f, 0.0f, 1.0f, 0.5f);
						else if(world[i][j].getID()==2)
							color = new Color (0xFF, 0, 0, 0x33);
						else if(world[i][j].getID()==3)
							color = new Color (0, 0xFF, 0, 0x33);
						g.setColor(color);

						// range*2+1 para contar com a casa onde se encontra
						g.fillRect((point.y - range) * (getWidth() / COL),
								(point.x - range) * (getHeight() / LIN),
								(getWidth() / COL) * (range * 2 + 1),
								(getHeight() / LIN) * (range * 2 + 1));
					}
			}
		}

		if(hospital!=null)
			for (Point hospitalPoints : hospital.getPoints()) {
				g.drawImage(hospitalImage.getImage(), hospitalPoints.y * (getWidth() / COL), hospitalPoints.x
						* (getHeight() / LIN), null);
			}

		// Desenhar personagens e obstaculos
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[0].length; j++) {

				if (world[i][j] != null) {
					Point point = world[i][j].getPoint();



					// DESENHA PERSONAGEM
					if (!world[i][j].getType().equals(WorldObjectType.OBSTACLE)) {
						if (!world[i][j].isPressed()) {

							if (world[i][j].getType().equals(WorldObjectType.GENERAL)){
								g.drawImage(king.getImage(), point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), null);

							}
							else if (world[i][j].getType().equals(WorldObjectType.COMMAND)){
								g.drawImage(command.getImage(), point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), null);

							}
							else if (world[i][j].getType().equals(WorldObjectType.INFANTRY)){
								g.drawImage(soldier.getImage(), point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), null);

							}
							else if (world[i][j].getType().equals(WorldObjectType.SNIPER)){
								g.drawImage(sniper.getImage(), point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), null);

							}



						} else {
							g.setColor(Color.ORANGE);
							if (world[i][j].getType().equals(
									WorldObjectType.GENERAL)){


								g.drawImage(king.getImage(), point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), null);
							}else if (world[i][j].getType().equals(
									WorldObjectType.COMMAND)){
								g.drawImage(command.getImage(), point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), null);
							}else if (world[i][j].getType().equals(
									WorldObjectType.INFANTRY)){
								g.drawImage(soldier.getImage(), point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), null);
							}else if (world[i][j].getType().equals(
									WorldObjectType.SNIPER)){
								g.drawImage(sniper.getImage(), point.y * (getWidth() / COL), point.x
										* (getHeight() / LIN), null);
							}

							g.drawOval(point.y * (getWidth() / COL), point.x
									* (getHeight() / LIN), (getWidth() / COL),
									(getHeight() / LIN));
						}


						if (world[i][j].isMoving()){
							g.setColor(Color.YELLOW);
							g.fillOval(point.y * (getWidth() / COL), point.x
									* (getHeight() / LIN), (getWidth() / COL) / 2,
									(getHeight() / LIN) / 2);
						}

						// DESENHAR BARRA VIDA

						g.setColor(Color.black);
						g.fillRect(point.y * (getWidth() / COL), point.x
								* (getHeight() / LIN), (getWidth() / COL), 5);

						if (world[i][j].health() >= 0) {
							g.setColor(Color.GREEN);
							g.fillRect(
									point.y * (getWidth() / COL),
									point.x * (getHeight() / LIN),
									((getWidth() / COL) * world[i][j].health()) / 100,
									5);
						}
					} else {
						//DESENHAR OBSTACULOS

						g.drawImage(obstacle.getImage(), point.y * (getWidth() / COL), point.x
								* (getHeight() / LIN), null);


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



					for (AttackPoints attack : attacks) {


						g.setColor(new Color(255, 28, 174));

						g.drawLine(
								attack.getAttackingObject().y
								* (getHeight() / LIN)
								+ (getHeight() / LIN) / 2,

								attack.getAttackingObject().x
								* (getHeight() / LIN)
								+ (getHeight() / LIN) / 2,

								attack.getDefendeObject().y
								* (getHeight() / LIN)
								+ (getHeight() / LIN) / 2,

								attack.getDefendeObject().x
								* (getHeight() / LIN)
								+ (getHeight() / LIN) / 2);
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
	 * Set position 
	 * @param i
	 * @param j
	 * @param worldObjectInBoard
	 */
	public void setPosition(int i, int j, WorldObjectInBoard worldObjectInBoard) {

		world[i][j]=worldObjectInBoard;
	}

	/**
	 * Set the attackList to be paint
	 * @param attacks
	 */
	public void setAttackList(LinkedList<AttackPoints> attacks){
		if(attacks.size()>0){
			this.attacks.clear();
			this.attacks.addAll(attacks);


		}else if(attacks.size()==0){
			this.attacks.clear();
			this.attacks.addAll(attacks);
		}
	}

	/**
	 * Set hospital to the board
	 * @param hostipal
	 */
	public void setHospital(Hospital hostipal) {
		this.hospital=hostipal;

	}

	/**
	 * Ger the attackPoints
	 * @return
	 */
	public LinkedList<AttackPoints> getAttackList() {

		return attacks;
	}


	/**
	 * Get the board hospital
	 * @return
	 */
	public Hospital getHospital() {

		return hospital;
	}



	/**
	 * Set the world to new soldiers positions
	 * @param worldR
	 */
	public void setWorld(WorldObjectInBoard[][] worldR){
		for (int i = 0; i < this.world.length; i++) {
			for (int j = 0; j < this.world[0].length; j++) {
				this.world[i][j]=worldR[i][j];
			}
		}
	}

	/**
	 * Get the matrix
	 * @return
	 */
	public WorldObjectInBoard[][] getWorld(){
		return world;
	}

}