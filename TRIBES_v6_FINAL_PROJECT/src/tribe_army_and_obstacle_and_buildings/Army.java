package tribe_army_and_obstacle_and_buildings;

import game.Engine;

import java.io.Serializable;
import java.util.LinkedList;

import world.World;

public class Army implements Serializable{

	private LinkedList<ArmySoldiers> soldiers;

	private boolean generalAlive;
	private int iD;
	private World world;

	/**
	 * Creates a new army, with four units
	 * @param iD
	 * @param engine
	 * @param world
	 */
	public Army(int iD, Engine engine, World world) {
		this.iD = iD;

		this.setWorld(world);

		soldiers = new LinkedList<ArmySoldiers>();

		soldiers.add(new General(WorldObjectType.GENERAL, engine, iD, world));
		generalAlive=true;
		soldiers.add(new Infantry(WorldObjectType.INFANTRY, engine, this.iD, world));
		soldiers.add(new Command(WorldObjectType.COMMAND, engine, this.iD, world));
		soldiers.add(new Sniper(WorldObjectType.SNIPER, engine, this.iD, world));

	}

	public int getiD() {
		return iD;
	}

	public LinkedList<ArmySoldiers> getSoldiers() {
		return soldiers;
	}


	public void killKing() {
		generalAlive = false;
	}

	public boolean getKingAlive() {
		return generalAlive;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

}
