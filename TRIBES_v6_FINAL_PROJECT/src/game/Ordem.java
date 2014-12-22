package game;

import java.awt.Point;

import java.io.Serializable;

import board.AStar;


import tribe_army_and_obstacle_and_buildings.ArmySoldiers;

public class Ordem implements Serializable{

	private ArmySoldiers soldier;
	private Point goToPoint;
	private boolean follow;
	private AStar pp;

	/**
	 * Creates a new order to the soldier, could the to follow or not, with a path Astar to the destiny points
	 * @param soldier
	 * @param goToPoint
	 * @param follow
	 * @param a
	 */
	public Ordem(ArmySoldiers soldier, Point goToPoint, boolean follow, AStar a) {

		this.soldier = soldier;
		this.goToPoint = goToPoint;
		this.follow = follow;
		this.setPp(a);

	}

	/**
	 * Get the soldier in the order
	 * @return
	 */
	public ArmySoldiers getSoldier() {
		return soldier;
	}

	/**
	 * Point to the soldier go
	 * @return
	 */
	public Point getGoToPoint() {


		return goToPoint;
	}

	/**
	 * If the order is to follow someone
	 * @return
	 */
	public boolean getFollow() {
		return follow;
	}

	@Override
	public String toString() {
		return "Ordem [soldier=" + soldier + ", goToPoint=" + goToPoint
		+ ", follow=" + follow + "]";
	}

	/**
	 * Set the pathplanner to this order
	 * @param a
	 */
	public void setPp(AStar a) {
		this.pp = a;
	}

	/**
	 * Get the pathplanner
	 * @return
	 */
	public AStar getPp() {
		return pp;
	}
}