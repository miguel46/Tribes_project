package boardToNetworkGame;

import java.awt.Point;
import java.io.Serializable;

public class AttackPoints implements Serializable{

	private Point attackPoint;
	private Point defendePoint;
	

	/**
	 * Create a attack point, consist in the point from the attack soldier and the point of the defende soldier
	 * @param attackPoint
	 * @param defendePoint
	 */
	public AttackPoints(Point attackPoint, Point defendePoint) {
		this.attackPoint = attackPoint;
		this.defendePoint = defendePoint;
	}

	/**
	 * Get the attackingObject point
	 * @return attackPoint
	 */
	public Point getAttackingObject() {
		return attackPoint;
	}

	/**
	 * Get the defendeObject point
	 * @return defendePoint
	 */
	public Point getDefendeObject() {
		return defendePoint;
	}

}
