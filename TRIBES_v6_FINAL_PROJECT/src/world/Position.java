package world;

import java.awt.Point;

import tribe_army_and_obstacle_and_buildings.ArmySoldiers;
import tribe_army_and_obstacle_and_buildings.Obstacle;

public class Position implements Comparable<Position>{

	private Point point;
	private Obstacle obstacle;
	private ArmySoldiers soldier;

	public Position father = null;
	public double      dist   = 0.0;
	public double      cost   = 0.0;
	public int         tipo   = 0;

	public Position(Point point){
		this.setPoint(point);
		this.obstacle=null;
		this.soldier=null;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Point getPoint() {
		return point;
	}

	public void setObstacle(Obstacle obstacle) {
		this.obstacle = obstacle;
	}

	public Obstacle getObstacle() {
		return obstacle;
	}

	public void setSoldier(ArmySoldiers soldier) {
		this.soldier = soldier;
	}

	public ArmySoldiers getSoldier() {
		return soldier;
	}

	

	@Override
	public int compareTo(Position arg0) {
		return Double.compare(cost, arg0.cost);
	}
}
