package game;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Random;



import board.AStar;
import board.Attack;


import tribe_army_and_obstacle_and_buildings.ArmySoldiers;
import tribe_army_and_obstacle_and_buildings.WorldObjectType;
import world.Position;
import world.World;

public class Engine{

	public World world;

	public LinkedList<Ordem> ordensToSoldiers = new LinkedList<Ordem>();


	private LinkedList<ObjectOutputStream> listClientsOuts;


	/**
	 * Creates a new Engine to deal with orders
	 * @param world
	 */
	public Engine(World world) {

		this.world = world;

		listClientsOuts = new LinkedList<ObjectOutputStream>();

	}

	/**
	 * Return the world
	 * @return
	 */
	public synchronized World getWorld(){
		synchronized (world){
			return world;
		}
	}

	/**
	 * Verify if in the position are a soldier from a team, with a specified iD
	 * @param point
	 * @param playeriD
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized boolean checkPosition(Point point, int playeriD) throws InterruptedException {

		synchronized (world){
			
			if (world.soldierIsFromAPlayer(playeriD, point))
				return true;
			return false;
		}
	}

	/**
	 * Set the status of the player in the position, from a iD team, to pressed and repaint the world
	 * @param point
	 * @param playeriD
	 * @throws InterruptedException
	 */
	public synchronized void presseSoldier(Point point, int playeriD) throws InterruptedException{

		synchronized (world){
			world.setPressedSoldier(playeriD, point);
			repaint();
			notifyAll();
		}
	}

	/**
	 * Verify if there is a selected soldier in the team 
	 * @param iD
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized boolean hasSelectSoldier(int iD) throws InterruptedException {

		synchronized (world){

			return world.hasSelectedSoldier(iD);
		}
	}

	/**
	 * Verify in the world if the position is empty
	 * @param iD
	 * @param point
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized boolean pointIsEmpty(int iD, Point point)
	throws InterruptedException {
		synchronized (world){
			return world.pointEmpty(iD, point);
		}
	}

	/**
	 * Add a order to move a speficied soldier, and creates a path to him
	 * @param point
	 * @param iD
	 * @param follow
	 * @throws InterruptedException
	 */
	public synchronized void moveSoldier(Point point, int iD, boolean follow)
	throws InterruptedException {

		synchronized (world){

			ArmySoldiers soldier = world.getSelectedSoldier(iD);

			//ADAPTEI A CLASSE ASTAR DISPONIBILIZADA PELOS DOCENTES
			AStar a = new AStar(getWorld().getWorld(), getWorldObject(soldier.getPoint()) , getWorldObject(point));


			synchronized (ordensToSoldiers) {
				ordensToSoldiers.add(new Ordem(soldier, point, follow, a));
			}
			notifyAll();
		}
	}

	/**
	 * Get the list of orders to soldiers
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized LinkedList<Ordem> getOrdens()
	throws InterruptedException {
		synchronized (ordensToSoldiers) {

			return ordensToSoldiers;
		}}

	/**
	 * Change the pressed soldier from a team, to unpressed status
	 * @throws InterruptedException
	 */
	public synchronized void unselectSoldier() throws InterruptedException {
		synchronized (world) {
			world.unselectSoldier();
			repaint();

			notifyAll();
		}
	}

	/**
	 * Verify if there is a order to the soldier from a iD Team
	 * @param iD
	 * @param type
	 * @param point
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized boolean hasAOrderToSoldier(int iD,WorldObjectType type, Point point) throws InterruptedException {

		//ADICIONAR O ID DO PLAYER
		synchronized (ordensToSoldiers) {

			for (Ordem ordem : ordensToSoldiers) {
				if (ordem.getSoldier().getID()==iD && ordem.getSoldier().getType().equals(type)) {

					return true;
				}
			}
			return false;
		}}

	/**
	 * Get the order to the soldier type, if there isn't any order returns null, with the team iD.
	 * @param iD
	 * @param type
	 * @param point
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized Ordem getOrderTOSoldier(int iD, WorldObjectType type,Point point) throws InterruptedException {

		synchronized (ordensToSoldiers) {

			for (Ordem ordem : ordensToSoldiers) {
				if (ordem.getSoldier().getID()==iD && ordem.getSoldier().getType().equals(type)){
					return ordem;
				}
			}
		}
		return null;
	}

	/**
	 * Convert the point in matrix values, to pixels values
	 * @param goTO
	 * @return
	 */
	public synchronized Point converte(Point goTO) {
		synchronized (world){
			return world.getPointInPixels(goTO);
		}
	}

	/**
	 * Remove a order to the soldier type
	 * @param iD
	 * @param type
	 * @param point
	 * @throws InterruptedException
	 */
	public synchronized void removeOrderToSoldier(int iD, WorldObjectType type, Point point) throws InterruptedException {
		synchronized (world){
			synchronized (ordensToSoldiers) {

				ordensToSoldiers.remove(getOrderTOSoldier(iD, type, point));
			}
			repaint();

		}
	}

	//Funcionalidade não implementada
	//	/**
	//	 * Verify if could follow someone
	//	 * @param iD
	//	 * @param point
	//	 * @return
	//	 */
	//	public synchronized boolean isToFollow(int iD, Point point) {
	//		synchronized (world){
	//			return world.isToFollow(iD, world.getPointInPixels(point));
	//		}
	//	}


	/**
	 * Verify if the position is occupied
	 * @param point
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized boolean pointIsOcupied(Point point) throws InterruptedException {

		synchronized (world){
			if (world.getPosition(point.x, point.y).getObstacle() != null || world.getPosition(point.x, point.y).getSoldier() != null){
				return true;
			}
			return false;
		}
	}

	/**
	 * Remove a soldier from the world, and repaint
	 * @param tribeSoldier
	 * @throws InterruptedException
	 */
	public synchronized void removeSoldierFromWorld(ArmySoldiers tribeSoldier)
	throws InterruptedException {

		synchronized (world) {

			world.removeSoldier(tribeSoldier);
			repaint();

		}
	}

	/**
	 * Generate a random position to autonomous soldier go, in the board range
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized Point getPointToComputerPlayerGo()
	throws InterruptedException {
		Point newPoint = null;

		synchronized (world) {

			do {
				newPoint = new Point(new Random().nextInt(world.getWorld().length),
						new Random().nextInt(world.getWorld()[0].length));

			} while (!checkValidePoint(newPoint));

			return newPoint;
		}}

	/**
	 * Verify if the point is valid
	 * @param newPoint
	 * @return
	 * @throws InterruptedException
	 */
	private synchronized boolean checkValidePoint(Point newPoint)
	throws InterruptedException {

		synchronized (world){
			if (newPoint.x < world.getWorld().length && newPoint.y < world.getWorld()[0].length)
				if (world.getPosition(newPoint.x, newPoint.y) == null){
					return true;
				}
			return false;
		}
	}

	/**
	 * Broadcast the currentBoard to clients, and repaint the server board
	 */
	public synchronized void repaint() {

		synchronized (world) {
			world.getBoard().repaint();
			broadcast(world.getBoard().copyFromBoard());

		}
	}

	/**
	 * Update de attacklist from the world and repaint
	 * @param attackList
	 * @throws InterruptedException
	 */
	public synchronized void repaintAttack(LinkedList<Attack> attackList)
	throws InterruptedException {
		synchronized (world) {

			world.getBoard().addAttack(attackList);
			repaint();

		}
	}

	/**
	 * Broadcast the object to the objectOutPutStreams
	 * @param object
	 */
	public synchronized void broadcast(Object object) {
		synchronized (world) {
//			synchronized (world.getBoard()) {



				for (ObjectOutputStream oos : listClientsOuts) {

					try {
						oos.reset();
						oos.writeObject(object);
						world.getBoard().repaint();
					} catch (IOException e) {
						System.err.println("ERRO NO BROADCAST ENGINE SOCKET");
					}
//				}
			}}}

	/**
	 * Add the object out put stream to the list
	 * @param oos
	 */
	public synchronized void addOutputStream(ObjectOutputStream oos){
		listClientsOuts.add(oos);
	}

	/**
	 * Check if the game finished
	 * @return
	 */
	public synchronized boolean gameFinished() {
		synchronized (world){
			return world.gameFinished();
		}
	}

	/**
	 * Finish the game, terminate all the threads
	 * @throws InterruptedException
	 */
	public synchronized void finishGame() throws InterruptedException {
		synchronized (world){
			world.finishGame();
		}
	}

	/**
	 * Verify if the soldier is in the hospital positions
	 * @param tribeSoldiers
	 * @return
	 */
	public boolean inHospital(ArmySoldiers tribeSoldiers) {
		synchronized (world) {

			for (Point hospitalPoint : world.getHospital().getPoints()) {
				if(tribeSoldiers.getPoint().equals(hospitalPoint))
					return true;
			}
		}

		return false;
	}

	/**
	 * Get the winner player
	 * @return
	 */
	public String getWinnerPlayer() {
		synchronized (world) {


			return world.getWinnerTribe();
		}	}

	/**
	 * Allow to get the the position in the world, with the point
	 * @param goToPoint
	 * @return
	 */
	public Position getWorldObject(Point goToPoint) {
		synchronized (world) {


			return world.getPosition(goToPoint.x, goToPoint.y);
		}}

	/**
	 * Set the soldiers point to the parameter
	 * @param armySoldiers
	 * @param point
	 * @param pointToMove
	 */
	public void setPoint(ArmySoldiers armySoldiers, Point point, Point pointToMove) {
		synchronized (world) {


			world.getWorld()[point.x][point.y].setSoldier(null);
			world.getWorld()[pointToMove.x][pointToMove.y].setSoldier(armySoldiers);
			world.getWorld()[pointToMove.x][pointToMove.y].getSoldier().setPoint(pointToMove);
		}
	}

}
