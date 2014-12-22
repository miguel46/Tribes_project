package tribe_army_and_obstacle_and_buildings;

import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;

public class Hospital implements Serializable{


	private LinkedList<Point> points;

	/**
	 * Creates a new Hospital in some points
	 * @param points
	 */
	public Hospital(LinkedList<Point> points){
		this.setPoints(points);
	}

	public void setPoints(LinkedList<Point> points) {
		this.points = points;
	}

	public LinkedList<Point> getPoints() {
		return points;
	}




}
