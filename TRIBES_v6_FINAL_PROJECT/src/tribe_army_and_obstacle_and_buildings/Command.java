package tribe_army_and_obstacle_and_buildings;

import game.Engine;


import java.io.Serializable;

import world.World;



public class Command extends ArmySoldiers implements Serializable{

	private int HEALTH = 100;
	private final int RANGE = 2;
	private final int ATTACK = 2;
	private final int ATTACK_RATE = 3;
	private final int SPEED = 3;
	
	/**
	 * Creates a new command 
	 * @param type
	 * @param engine
	 * @param iD
	 * @param world
	 */
	public Command(WorldObjectType type, Engine engine, int iD, World world) {
		super(type, engine, iD, world);
		isMoving = false;
		isPressed = false;

		this.health = HEALTH;
		this.range = RANGE;
		this.attack = ATTACK;
		this.attack_rate = ATTACK_RATE;
		this.speed = SPEED;

	}

	@Override
	public int health() {
		return health;
	}

	@Override
	public int range() {
		return range;
	}

	@Override
	public int attack() {
		return attack;
	}

	@Override
	public int attack_rate() {
		return attack_rate;
	}

	@Override
	public int speed() {
		return speed;
	}

}
