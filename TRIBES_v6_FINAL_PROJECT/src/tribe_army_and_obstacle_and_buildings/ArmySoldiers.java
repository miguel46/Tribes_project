package tribe_army_and_obstacle_and_buildings;

import game.Engine;
import game.Ordem;

import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;

import world.Position;
import world.World;
import board.AStar;
import board.Attack;


public abstract class ArmySoldiers extends WorldObject implements Runnable, Serializable {

	protected int iD;
	protected int health;
	protected int range;
	protected int attack;
	protected int attack_rate;
	protected int speed;

	protected boolean isMoving;
	protected boolean isPressed;
	protected boolean iSDead;

	protected Engine engine;

	protected World world;

	private LinkedList<Attack> attackList;
	/**
	 * Creates a new army Soldier, with a specified type
	 * @param type
	 * @param engine
	 * @param iD
	 * @param world
	 */
	public ArmySoldiers(WorldObjectType type, Engine engine, int iD, World world) {
		super(type);

		this.engine = engine;
		this.type = type;
		this.iD = iD;
		this.world=world;

		isMoving = false;
		isPressed = false;
		iSDead=false;

		attackList = new LinkedList<Attack>();
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving() {
		isMoving = true;
	}

	public boolean isPressed() {

		return isPressed;
	}

	public void setPressed() {
		isPressed = true;
	}

	public void setNotMoving() {
		isMoving = false;

	}

	public void setUnpressed() {
		isPressed = false;
	}

	@Override
	public void run() {
		try {
			while(!iSDead){

				iSAPlayer();
			}

			engine.removeOrderToSoldier(iD, type, this.point);
			engine.removeSoldierFromWorld(this);


		} catch (InterruptedException e) {
			System.err.println("InterruptedException run TribeSoldier");
		}
	}



	private void iSAPlayer() {

		try {
			while (health > 0) {

				
				checkAttack(point);
				//				Thread.sleep(speed()*100);

				//VAI BUSCAR ORDEM À LISTA
				Ordem ordem = engine.getOrderTOSoldier(iD, type, this.point);
				if(ordem!=null){

					//RETIRA O PONTO PARA ONDE TEM QUE SE DESLOCAR 
					Point goToPointInMatrixValue = ordem.getGoToPoint();

					//CALCULA O CAMINHO ATE AO DESTINO
					AStar a = ordem.getPp();
					LinkedList<Position> path = a.calcPath();


					if(path==null){
						engine.removeOrderToSoldier(iD, type, this.point);
					}else{
						setMoving();
						while (isMoving) {	
							//SE O PONTO DE DESTINO ESTIVER OCUPADO O SOLDADO PARA DE SE MOVER
							if (engine.pointIsOcupied(goToPointInMatrixValue) || this.equals(goToPointInMatrixValue) || health<=0) {

								setNotMoving();

							}else{
								//SE O PROXIMO PONTO NAO ESTIVER OCUPADO O SOLDADO MOVE-SE
								if(!engine.pointIsOcupied(path.peekLast().getPoint())){
									engine.setPoint(this, point,path.pollLast().getPoint());

									checkAttack(point);
									engine.repaint();
									Thread.sleep(speed()*100);

								}else{
									//CASO O PONTO ESTEJA OCUPADO IRÁ RECALCULAR UM CAMINHO ALTERNATIVO
									Position position = new Position(point);
									position.setSoldier(this);
									a = new AStar(engine.getWorld().getWorld(),position , engine.getWorldObject(goToPointInMatrixValue));
									path = a.calcPath();
								}
							}
						}
						//REMOVE A ORDEM DE MOVER
						setNotMoving();
						engine.removeOrderToSoldier(iD, type, this.point);
					}}
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			System.err.println("Thread interrompida!"+getType());
		}
	}

	public abstract int health();

	public abstract int range();

	public abstract int attack();

	public abstract int attack_rate();

	public abstract int speed();

	public void removeHealth(int health) {
		if(this.health-health<=0){

			this.health=0;
			setNotMoving();
			setUnpressed();

			iSDead = true;
		}else
			this.health -= health;
	}

	public int getID() {
		return iD;
	}

	public void setHealth(int health){
		if(this.health+health>=100)
			this.health=100;
		else
			this.health+=health;
	}

	/**
	 * Verify if can attack any positions in range
	 * @param point
	 * @throws InterruptedException
	 */
	public void checkAttack(Point point) throws InterruptedException{

		Position[][] board = engine.getWorld().getWorld();

		//SE O SOLDADO NAO ESTIVER NO HOSPITAL PODERA ATACAR
		if(!inHospital(point, board)){

			//LISTA DE ENEMIGOS QUE ESTA NO RANGE DO SOLDADO E QUE SAO ALVOS VALIDOS, OU SEJA, PODEM SER ATACADOS
			LinkedList<WorldObject> enemiesAround = checkNumberOfEnemiesAround(board);

			if (enemiesAround.size() > 0) {
				attackList.clear();

				for (WorldObject tribeElement : enemiesAround) {

					tribeElement.removeHealth((attack() * attack_rate())/ enemiesAround.size());

					//ADICIONA OS ATAQUES QUE O SOLDADO ESTA A REALIZAR PARA SEREM PINTADOS
					attackList.add(new Attack(this,tribeElement));

				}

				if(attackList.size()>0){
					engine.repaintAttack(attackList);
				}


			}else if(attackList.size()>0){
				attackList.clear();
				engine.repaintAttack(attackList);
			}
		}else{
			//SE O SOLDADO ESTIVER NO HOSPITAL GANHA 10 DE VIDA
			setHealth(10);
			engine.repaint();
		}
	}

	/**
	 * Verify if is in hospital
	 * @param point
	 * @param board
	 * @return
	 */
	private boolean inHospital(Point point, Position[][] board) {
		for (Point hospitalPoint : this.world.getBoard().getHospital().getPoints()) {
			if(hospitalPoint.equals(point))
				return true;
		}
		return false;
	}

	/**
	 * Count the number of enemies around my position
	 * @param board
	 * @return
	 */
	private LinkedList<WorldObject> checkNumberOfEnemiesAround(Position[][] board) {

		Position[][] boardA=board;

		LinkedList<WorldObject> enemiesAround = new LinkedList<WorldObject>();

		//CORDENADAS DO PONTO SUPERIOR ESQUERDO DO RANGE, E DO PONTO INFERIOR DIREITO
		int northXPoint = northXPoint(this, boardA);

		int northYPoint = northYPoint(this, boardA);

		int southXPoint = southXPoint(this, boardA);

		int southYPoint = southYPoint(this, boardA);


		for (int i = northXPoint; i <= southXPoint; i++) {
			for (int j = northYPoint; j <= southYPoint; j++) {
				if (boardA[i][j].getSoldier()!=null)
					if (boardA[i][j].getSoldier().getID() != this.getID()) {
						if (boardA[i][j].getSoldier().health() > 0) {
							if (canAttackPosition(this.getPoint(), boardA[i][j].getPoint(), boardA)) {
								enemiesAround.add(boardA[i][j].getSoldier());
							}
						}
					}
			}
		}
		return enemiesAround;
	}

	/**
	 * verify if can attack a position, in the board
	 * @param attackPoint
	 * @param defendePoint
	 * @param board
	 * @return
	 */
	private boolean canAttackPosition(Point attackPoint, Point defendePoint, Position[][] board) {

		//VERIFICA SE O ALVO ESTA NO HOSPITAL, OU SE ESTA NUMA POSICAÇAO VALIDA (nao existe obstaculo/soldados a impedir o ataque)
		for (Point hospitalPoint : world.getBoard().getHospital().getPoints()) {
			if(hospitalPoint.equals(defendePoint))
				return false;
		}
		if(areInTheSameRow(attackPoint, defendePoint)){
			if(canAttackRow(attackPoint, defendePoint, board))
				return true;
		}else if(areInTheSameColumn(attackPoint, defendePoint)){
			if(canAttackColumn(attackPoint, defendePoint, board))
				return true;
		}else if(canAttackTheRestOfThePositions(attackPoint, defendePoint, board))
			return true;


		return false;
	}

	private boolean canAttackTheRestOfThePositions(Point attackPoint,
			Point defendePoint, Position[][] board) {


		if(areAtTheRight(attackPoint,defendePoint)){


			if(canAttackRight( attackPoint, defendePoint,  board)){
				return true;
			}

		}else if(areAteTheLeft(attackPoint,defendePoint)){

			if(canAttackLeft( attackPoint, defendePoint,  board)){
				return true;
			}
		}

		return false;
	}


	private boolean canAttackLeft(Point attackPoint, Point defendePoint,
			Position[][] board) {
		//TAMANHO DE CADA QUADRADO DO BOARD, SE O NUMERO DE QUADRADOS FOR ALTERADO TERA QUE SE ALTERAR AQUI TAMBEM
		int lengthOfTheSquare=35;
		double x1=attackPoint.y*lengthOfTheSquare+(lengthOfTheSquare/2);
		double y1=attackPoint.x*lengthOfTheSquare+(lengthOfTheSquare/2);
		double x2=defendePoint.y*lengthOfTheSquare+(lengthOfTheSquare/2);
		double y2=defendePoint.x*lengthOfTheSquare+(lengthOfTheSquare/2);

		LinkedList<Point> pointBetweenSOldiersInMatrixValues = new LinkedList<Point>();

		for (int i = (int) x2; i <= x1; i++) {

			//Linear Equation
			int y= (int) (((y2-y1)/(x2-x1)*(i-x1))+y1);

			Point point = world.getBoard().convertePoint(new Point(i, y));
			if(!pointBetweenSOldiersInMatrixValues.contains(point)  	
					&& (!point.equals(new Point(attackPoint.x, attackPoint.y)) && !point.equals(new Point(defendePoint.x, defendePoint.y)))){

				pointBetweenSOldiersInMatrixValues.add(point);
			}
		}


		for (Point point : pointBetweenSOldiersInMatrixValues) {
			if(board[point.x][point.y].getSoldier()!=null || board[point.x][point.y].getObstacle()!=null)
				return false;
		}


		return true;
	}



	private boolean canAttackRight(Point attackPoint, Point defendePoint,
			Position[][] board) {

		//TAMANHO DE CADA QUADRADO DO BOARD, SE O NUMERO DE QUADRADOS FOR ALTERADO TERA QUE SE ALTERAR AQUI TAMBEM
		int lengthOfTheSquare=35;
		double x1=attackPoint.y*lengthOfTheSquare+(lengthOfTheSquare/2);
		double y1=attackPoint.x*lengthOfTheSquare+(lengthOfTheSquare/2);
		double x2=defendePoint.y*lengthOfTheSquare+(lengthOfTheSquare/2);
		double y2=defendePoint.x*lengthOfTheSquare+(lengthOfTheSquare/2);

		LinkedList<Point> pointBetweenSOldiersInMatrixValues = new LinkedList<Point>();

		for (int i = (int) x1; i <= x2; i++) {

			//Linear Equation
			int y= (int) (((y2-y1)/(x2-x1)*(i-x1))+y1);

			Point point = world.getBoard().convertePoint(new Point(i, y));
			if(!pointBetweenSOldiersInMatrixValues.contains(point)  	
					&& (!point.equals(new Point(attackPoint.x, attackPoint.y)) && !point.equals(new Point(defendePoint.x, defendePoint.y)))){

				pointBetweenSOldiersInMatrixValues.add(point);
			}
		}


		for (Point point : pointBetweenSOldiersInMatrixValues) {

			if(board[point.x][point.y].getSoldier()!=null || board[point.x][point.y].getObstacle()!=null)
				return false;
		}


		return true;
	}


	/**
	 * Check if the enemie is at my left
	 * @param attackPoint
	 * @param defendePoint
	 * @return
	 */
	private boolean areAteTheLeft(Point attackPoint, Point defendePoint) {

		if(defendePoint.y<attackPoint.y)
			return true;


		return false;
	}

	/**
	 * Check if the enemie is at my right
	 * @param attackPoint
	 * @param defendePoint
	 * @return
	 */
	private boolean areAtTheRight(Point attackPoint, Point defendePoint) {
		if(attackPoint.y<defendePoint.y)
			return true;
		return false;
	}

	/**
	 * Check if the point is a position of the hospital
	 * @param point
	 * @return
	 */
	private boolean pointOfHospital(Point point) {
		for (Point hospitalPoint : world.getBoard().getHospital().getPoints()) {
			if(hospitalPoint.equals(point))
				return true;
		}
		return false;
	}

	/**
	 * Verify if cant attack in the row
	 * @param attackPoint
	 * @param defendePoint
	 * @param board
	 * @return
	 */
	private boolean canAttackRow(Point attackPoint, Point defendePoint, Position[][] board) {
		//PARA VERFICAR A PARTIR DA POSIÇAO A SEGUIR ATÉ À ANTERIOR DAS FIGURAS
		int aux=1;
		int inicialPoint = 0;
		int finalPoint = 0;

		if (attackPoint.y > defendePoint.y) {
			inicialPoint = defendePoint.y;
			finalPoint = attackPoint.y;
		} else {
			inicialPoint = attackPoint.y;
			finalPoint = defendePoint.y;
		}

		for (int j = inicialPoint+aux; j <= finalPoint-aux; j++) {
			if (board[attackPoint.x][j].getSoldier() != null ||  board[attackPoint.x][j].getObstacle() != null ||pointOfHospital(new Point(attackPoint.x, j)))
				return false;
		}

		return true;
	}

	private boolean areInTheSameColumn(Point attackPoint, Point defendePoint) {

		if (attackPoint.y - defendePoint.y == 0)
			return true;

		return false;
	}

	private boolean areInTheSameRow(Point attackPoint, Point defendePoint) {
		if (attackPoint.x - defendePoint.x == 0)
			return true;

		return false;
	}

	/**
	 * Verify if cant attack the column
	 * @param attackPoint
	 * @param defendePoint
	 * @param board
	 * @return
	 */
	private boolean canAttackColumn(Point attackPoint, Point defendePoint, Position[][] board) {
		//PARA VERFICAR A PARTIR DA POSIÇAO A SEGUIR ATÉ À ANTERIOR DAS FIGURAS
		int aux=1;

		int inicialPoint = 0;
		int finalPoint = 0;

		if (attackPoint.x > defendePoint.x) {
			inicialPoint = defendePoint.x;
			finalPoint = attackPoint.x;
		} else {
			inicialPoint = attackPoint.x;
			finalPoint = defendePoint.x;
		}

		for (int i = inicialPoint+aux; i <= finalPoint-aux; i++) {
			if (board[i][attackPoint.y].getSoldier() != null || board[i][attackPoint.y].getObstacle() != null ||pointOfHospital(new Point(i, attackPoint.y)))
				return false;
		}

		return true;
	}

	private int southYPoint(WorldObject object, Position[][] board) {
		int auxParaEliminarAPosicaoForaDaMatrix = 1;

		int southYPoint = object.getPoint().y + object.range();

		if (southYPoint >= board.length) {
			southYPoint = board.length - auxParaEliminarAPosicaoForaDaMatrix;
		}

		return southYPoint;
	}

	private int southXPoint(WorldObject object, Position[][] board) {

		int auxParaEliminarAPosicaoForaDaMatrix = 1;
		int southXPoint = object.getPoint().x + object.range();

		if (southXPoint >= board.length) {
			southXPoint = board.length - auxParaEliminarAPosicaoForaDaMatrix;
		}

		return southXPoint;
	}

	private int northYPoint(WorldObject object, Position[][] board) {

		int northYPoint = object.getPoint().y - object.range();
		if (northYPoint < 0) {

			northYPoint = 0;

		}

		return northYPoint;
	}

	private int northXPoint(WorldObject object, Position[][] board) {

		int northPoint = object.getPoint().x - object.range();

		if (northPoint < 0)
			northPoint = 0;

		return northPoint;

	}

	public void setAttackToNull() {
		this.attack=0;
		
	}

}
