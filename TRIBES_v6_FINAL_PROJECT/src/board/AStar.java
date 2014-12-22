package board;

import java.util.LinkedList;
import java.util.PriorityQueue;


import world.Position;

public class AStar {
	private final Position[][]      mapa;
	private final Position          start;
	private final Position          target;
	private PriorityQueue<Position> open;
	private PriorityQueue<Position> closed;
	LinkedList<Position> path;

	public AStar(Position[][] positions, Position start, Position position) {
		this.mapa = positions;
		this.start = start;
		this.target = position;
		path = new LinkedList<Position>();
	}

	public LinkedList<Position> calcPath() {


		open = new PriorityQueue<Position>();
		closed = new PriorityQueue<Position>();

		start.cost = Math.abs(target.getPoint().x - start.getPoint().x) + Math.abs(target.getPoint().y - start.getPoint().y);
		open.add(start);

		while (open.peek() != target && !open.isEmpty()) {
			Position current = open.poll();
			closed.add(current);

			// Determinar os vizinhos da casa current
			Position norte = null, este = null, sul = null, oeste = null;

			if (current.getPoint().y > 0)
				norte = mapa[current.getPoint().x][current.getPoint().y - 1];
			if (current.getPoint().x < mapa.length-1)
				este = mapa[current.getPoint().x + 1][current.getPoint().y];
			if (current.getPoint().x > 0)
				oeste = mapa[current.getPoint().x - 1][current.getPoint().y];
			if (current.getPoint().y < mapa[0].length-1)
				sul = mapa[current.getPoint().x][current.getPoint().y + 1];

			Position[] viz = { norte, este, sul, oeste };

			double dist2 = current.dist + 1.0;

			for (Position vizinho : viz) {
				if (vizinho == null) {
					continue;
				}
				if (vizinho.getObstacle()!=null || vizinho.getSoldier()!=null) {
					continue;
				}
				if (open.contains(vizinho) && dist2 < vizinho.dist) {
					open.remove(vizinho);
					vizinho.dist = 0;
					vizinho.cost = 0;
					vizinho.father = null;
				}
				if (closed.contains(vizinho) && dist2 < vizinho.dist) {
					closed.remove(vizinho);
					vizinho.dist = 0;
					vizinho.cost = 0;
					vizinho.father = null;
				}
				if (!open.contains(vizinho) && !closed.contains(vizinho)) {
					// Calcular a heuristica
					double h = Math.abs(target.getPoint().x - vizinho.getPoint().x) + Math.abs(target.getPoint().y - vizinho.getPoint().y);
					vizinho.dist = dist2;
					vizinho.cost = dist2 + h;
					vizinho.father = current;

					open.add(vizinho);
				}
			}
		}
		// Reconstruir a path a partir do target.
		if (target.father != null) {
			path.add(target);
			Position current = target;
			while (current.father != start) {
				path.add(current.father);
				current = current.father;
				current.tipo = 4;
			}
			return path;
		} else {
			return null;
		}
	}

	public LinkedList<Position> getCalcPath() {

		return path;
	}
}