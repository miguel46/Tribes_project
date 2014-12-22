package tribe_army_and_obstacle_and_buildings;

import java.awt.Point;
import java.io.Serializable;

public class Obstacle extends WorldObject implements Serializable{

	/**
	 * Creates a new obstacle int the point
	 * @param type
	 * @param point
	 */
	public Obstacle(WorldObjectType type, Point point) {
		super(type);
		this.type = type;
		setPoint(point);
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUnpressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public int range() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMoving() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int health() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int attack() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeHealth(int attack) {
		// TODO Auto-generated method stub

	}

	@Override
	public int attack_rate() {
		// TODO Auto-generated method stub
		return 0;
	}

}
