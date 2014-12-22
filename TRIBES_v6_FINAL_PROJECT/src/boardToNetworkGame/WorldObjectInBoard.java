package boardToNetworkGame;

import java.awt.Point;
import java.io.Serializable;

import tribe_army_and_obstacle_and_buildings.WorldObjectType;

public class WorldObjectInBoard implements Serializable{

	private int iD;
	private WorldObjectType type;
	private Point point;
	private int range;
	private int health;
	private boolean isMoving;
	private boolean iSPressed;
	
	/**
	 * Creates a new WorldObjectInBoard to the send throw the socket
	 * @param iD
	 * @param type
	 * @param point
	 * @param range
	 * @param health
	 * @param isMoving
	 * @param iSPressed
	 */
	public WorldObjectInBoard(int iD, WorldObjectType type, Point point,
			int range, int health, boolean isMoving, boolean iSPressed) {
		super();
		this.iD = iD;
		this.type = type;
		this.point = point;
		this.range = range;
		this.health = health;
		this.isMoving = isMoving;
		this.iSPressed = iSPressed;
	}

	/**
	 * Type of the object
	 * @return
	 */
	public WorldObjectType getType() {
		return type;
	}

	/**
	 * Location of the object (int matrix values)
	 * @return
	 */
	public Point getPoint() {
		return point;
	}

	/**
	 * Range of the object
	 * @return
	 */
	public int range() {
		return range;
	}

	/**
	 * ID of the object
	 * @return
	 */
	public int getID() {
		return iD;
	}

	/**
	 * Check if the object is pressed
	 * @return
	 */
	public boolean isPressed() {
		return iSPressed;
	}

	/**
	 * check if the object is moving
	 * @return
	 */
	public boolean isMoving() {
		return isMoving;
	}

	/**
	 * Get health of the object
	 * @return
	 */
	public int health() {
		return health;
	}

}