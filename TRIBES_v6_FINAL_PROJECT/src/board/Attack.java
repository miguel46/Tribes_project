package board;

import java.io.Serializable;

import tribe_army_and_obstacle_and_buildings.WorldObject;

public class Attack implements Serializable{

	private WorldObject attackingObject;
	private WorldObject defendeObject;

	/**
	 * Create a new attack
	 * @param attackingObject
	 * @param defendeObject
	 */
	public Attack(WorldObject attackingObject, WorldObject defendeObject) {
		this.setAttackingObject(attackingObject);
		this.setDefendeObject(defendeObject);

	}

	/**
	 * Set the attacking object
	 * @param attackingObject
	 */
	public void setAttackingObject(WorldObject attackingObject) {
		this.attackingObject = attackingObject;
	}

	@Override
	public String toString() {
		return "Attack [attackingObject=" + attackingObject
				+ ", defendeObject=" + defendeObject + "]";
	}

	/**
	 * Get the attackingObject
	 * @return attackingObject
	 */
	public WorldObject getAttackingObject() {
		return attackingObject;
	}

	/**
	 * Set the defende object
	 * @param defendeObject
	 */
	public void setDefendeObject(WorldObject defendeObject) {
		this.defendeObject = defendeObject;
	}

	/**
	 * Get the defendeObject
	 * @return defendeObject
	 */
	public WorldObject getDefendeObject() {
		return defendeObject;
	}

}
