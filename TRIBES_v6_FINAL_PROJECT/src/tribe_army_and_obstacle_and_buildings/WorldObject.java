package tribe_army_and_obstacle_and_buildings;

import java.awt.Point;
import java.io.Serializable;

public abstract class WorldObject implements Serializable{

	protected Point point;
	protected WorldObjectType type;

	

	/**
	 * Creates a new world object with a type, this object will be part of the world
	 * @param type
	 */
	public WorldObject(WorldObjectType type) {

		this.type = type;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Point getPoint() {

		return point;
	}

	public abstract int getID();

	public abstract void setPressed();

	public abstract void setUnpressed();

	public WorldObjectType getType() {
		return type;
	}

	public abstract int range();

	public abstract boolean isPressed();

	public abstract boolean isMoving();

	public abstract int health();

	public abstract int attack();

	public abstract int attack_rate();

	public abstract void removeHealth(int attack);

	
}
