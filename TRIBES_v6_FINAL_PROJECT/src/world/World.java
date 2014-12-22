package world;

import java.awt.Point;
import java.io.Serializable;

import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


import board.Board;

import tribe_army_and_obstacle_and_buildings.Hospital;
import tribe_army_and_obstacle_and_buildings.Obstacle;
import tribe_army_and_obstacle_and_buildings.Army;
import tribe_army_and_obstacle_and_buildings.ArmySoldiers;
import tribe_army_and_obstacle_and_buildings.WorldObjectType;

public class World implements Serializable{

	private JFrame frame;
	private Board board;

	private static final int LIN = 20;
	private static final int COL = 20;

	private LinkedList<Army> tribes = new LinkedList<Army>();
	private LinkedList<Thread> soldiersThreads = new LinkedList<Thread>();

	private Position[][] world = new Position[LIN][COL];

	private Hospital hostipal;

	private int pathPlannerX = 0;
	private int pathPlannerY = 0;


	/**
	 * Creates a new world with obstacles and hospital positions
	 * @param frame
	 */
	public World(JFrame frame) {
		this.frame = frame;

		board = new Board(frame);

		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[0].length; j++) {
				world[i][j]= new Position(new Point(i, j));
			}
		}

		addObstacleToWorld();

	}

	/**
	 * Add a player army to the world, in valide position, and updates the world
	 * @param army
	 */
	public void addPlayersTribe(Army army) {

		tribes.add(army);

		addTribesToWorld(army);

		board.updateWorld(world);

		board.repaint();
		board.validate();

	}

	/**
	 * Add the obstacles and the hospital to the world, in standard positions
	 */
	private void addObstacleToWorld() {

		//ADICIONA A POSICAO DE ALGUNS OBSTACULOS NO WORLD E DO HOSPITAL, SE NECESSARIO PODEM SER ADICIONADOS MAIS OBSTACULOS E MAIS POSIÇOES DO HOSPITAL
		//APENAS COLOCO TRES POSIÇOES DE HOSPITAL PARA QUE EXISTA DISPUTA PELAS POSIÇOES
		world[5][5].setObstacle(new Obstacle(WorldObjectType.OBSTACLE, new Point(5, 5)));
		world[6][5].setObstacle(new Obstacle(WorldObjectType.OBSTACLE, new Point(6, 5)));
		world[7][5].setObstacle(new Obstacle(WorldObjectType.OBSTACLE, new Point(7, 5)));

		world[6][8].setObstacle(new Obstacle(WorldObjectType.OBSTACLE, new Point(6, 8)));


		LinkedList<Point> pointsToHospital = new LinkedList<Point>();
		pointsToHospital.add(new Point(9, 9));
		pointsToHospital.add(new Point(9, 8));
		pointsToHospital.add(new Point(9, 10));

		hostipal = new Hospital(pointsToHospital);

		board.setHospital(hostipal);

	}

	/**
	 * Add the army to the board, set a point to the soldiers
	 * @param army
	 */
	public void addTribesToWorld(Army army) {

		checkPositions(army, army.getiD());

		for (ArmySoldiers soldier : army.getSoldiers()) {
			world[soldier.getPoint().x][soldier.getPoint().y].setSoldier(soldier);
		}
	}

	/**
	 * Set the soldiers positions
	 * @param army
	 * @param iD
	 */
	public void checkPositions(Army army, int iD) {

		//PODERÁ SER ADICIONADA UMA QUARTA TRIBO NO CANTO INFERIOR ESQUERDO
		int i = 0;

		for (ArmySoldiers soldier : army.getSoldiers()) {

			//COLOCA AS PERSONAGENS NO WORLD DE ACORDO COM O SEU ID
			if (iD == 1) {

				soldier.setPoint(new Point(0 + i, 0));
				i++;

			} else if (iD == 2) {
				soldier.setPoint(new Point(0, COL - 1 - i));
				i++;
			} else if (iD == 3) {

				soldier.setPoint(new Point(LIN - 1, COL - 1 - i));
				i++;
			}
		}
	}

	/**
	 * Verify if the soldier in the point, is from the player with the iD
	 * @param iD
	 * @param point
	 * @return
	 */
	public boolean soldierIsFromAPlayer(int iD, Point point) {

		Point aux = board.convertePoint(point);

		if (aux.x >= 0 && aux.x < LIN && aux.y >= 0 && aux.y < COL)
			if (world[aux.x][aux.y].getSoldier() != null){

				if (world[aux.x][aux.y].getSoldier().getID() == iD) {

					return true;
				}
			}
		return false;
	}

	/**
	 * Set the soldier in the point to pressed
	 * @param iD
	 * @param point
	 */
	public void	setPressedSoldier(int iD, Point point){
		Point aux = board.convertePoint(point);
		world[aux.x][aux.y].getSoldier().setPressed();
	}

	/**
	 * Verify if in the army with the iD is a pressedSoldier
	 * @param iD
	 * @return
	 */
	public boolean hasSelectedSoldier(int iD) {
		for (Army tribe : tribes) {

			if (tribe.getiD() == iD)
				for (ArmySoldiers soldier : tribe.getSoldiers()) {
					if (soldier.isPressed()){
					
						return true;
					}}
		}

		return false;

	}

	/**
	 * Verify if the point in the world is empty
	 * @param iD
	 * @param point
	 * @return
	 */
	public boolean pointEmpty(int iD, Point point) {

		Point aux = board.convertePoint(point);

		if (aux.x >= 0 && aux.x < LIN && aux.y >= 0 && aux.y < COL)
			if (world[aux.x][aux.y].getObstacle() != null || world[aux.x][aux.y].getSoldier() != null)
				return false;

		return true;
	}

	/**
	 * Get the position in the world
	 * @param i
	 * @param j
	 * @return
	 */
	public Position getPosition(int i, int j) {

		return world[i][j];
	}

	/**
	 * Get the selected soldier in the army with the iD
	 * @param iD
	 * @return
	 */
	public ArmySoldiers getSelectedSoldier(int iD) {

		for (Army tribe : tribes) {
			if (tribe.getiD() == iD)
				for (ArmySoldiers soldier : tribe.getSoldiers()) {
					if (soldier.isPressed()) {

						soldier.setUnpressed();

						return soldier;

					}
				}
		}
		return null;
	}

	/**
	 * Verify if the point is a obstacle or is out of board limits
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isObstacle(int x, int y) {
		if (x < 0 || x >= board.getBoardWidth() || y < 0
				|| y >= board.getBoardHeight())
			return true;
		else
			return isObstacleInMap(x, y);
	}

	/**
	 * Verify if the position is occupied
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isObstacleInMap(int x, int y) {

		if (world[x][y].getObstacle() != null || world[x][y].getSoldier() != null)
			return true;

		return false;
	}


	/**
	 * Unselect a soldier
	 */
	public void unselectSoldier() {

		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[0].length; j++) {
				if (world[i][j].getSoldier() != null)
					world[i][j].getSoldier().setUnpressed();
			}
		}


	}

	/**
	 * Creates a list of thread and start all
	 */
	public void execute() {
		for (Army tribe : tribes) {

			for (ArmySoldiers soldier : tribe.getSoldiers()) {

				soldiersThreads.add(new Thread(soldier));

			}
		}

		for (Thread thread : soldiersThreads) {
			new Thread(thread).start();

		}

	}

	/**
	 * Get point in pixels value
	 * @param pointEmPixels
	 * @return
	 */
	public Point getPointInPixels(Point pointEmPixels) {

		return board.convertePoint(pointEmPixels);
	}

	//	/**
	//	 * Verify if could follow that position
	//	 * @param iD
	//	 * @param point
	//	 * @return
	//	 */
	//	public boolean isToFollow(int iD, Point point) {
	//		if (!world[point.x][point.y].getType().equals(WorldObjectType.OBSTACLE))
	//			return true;
	//		return false;
	//	}


	/**
	 * Remove the soldier from the world
	 * @param armySoldier
	 * @throws InterruptedException
	 */
	public void removeSoldier(ArmySoldiers armySoldier) throws InterruptedException {



		//REMOVE DO MUNDO O SOLDIER
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[0].length; j++) {
				if (world[i][j].getSoldier() != null) {
					if (world[i][j].getSoldier().equals(armySoldier)) {

						world[i][j].setSoldier(null);


					}
				}
			}
		}

		//REMOVE DA TRIBO O SOLDIER
		for (Army tribe : tribes) {
			if(tribe.getiD()==armySoldier.getID())
				tribe.getSoldiers().remove(armySoldier);
		}



		//VERIFICA SE É O REI
		if(armySoldier.getType().equals(WorldObjectType.GENERAL))
			for (Army tribe : tribes) {
				if (tribe.getiD() == armySoldier.getID()) {
					if (armySoldier.getType().equals(WorldObjectType.GENERAL)){
						tribe.killKing();
						tribe.getSoldiers().remove(armySoldier);
						
						for (ArmySoldiers tribeA : tribe.getSoldiers()) {
							tribeA.setAttackToNull();
						}
						
					}
				}
			}
		board.updateWorld(world);
	}

	/**
	 * Get the board from the world
	 * @return
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Verify if the game is finished, check how many generals are in the world
	 * @return
	 */
	public boolean gameFinished() {

		//PROCURA O NUMERO DE GENERAIS NO MUNDo
		int numberOfKingsAlive = 0;

		for (Army tribe : tribes)
			if (tribe.getKingAlive())
				numberOfKingsAlive++;


		if (numberOfKingsAlive == 1)
			for (Army tribe : tribes)
				if (tribe.getKingAlive())
					return true;

		return false;
	}

	/**
	 * Finish de game, interrupt all the threads
	 * @throws InterruptedException
	 */
	public void finishGame() throws InterruptedException {

		for (Thread thread : soldiersThreads) {
			thread.interrupt();


		}

		for (Army tribe : tribes)
			if (tribe.getKingAlive())
				JOptionPane.showMessageDialog(frame, "The tribe number "
						+ tribe.getiD() + " won the game!");

		System.exit(0);

	}

	/**
	 * Get the hospital from this world
	 * @return
	 */
	public Hospital getHospital() {
		return hostipal;
	}

	/**
	 * Get the tribe that won the game
	 * @return
	 */
	public String getWinnerTribe() {
		for (Army tribe : tribes)
			if (tribe.getKingAlive())
				return ""+tribe.getiD();
		return null;
	}

	/**
	 * Get the board of positions
	 * @return
	 */
	public Position[][] getWorld() {
		return world;
	}
}
