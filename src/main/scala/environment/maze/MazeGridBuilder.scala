package environment.maze

import environment.Environment
import environment.action.{Action, BasicAction}
import environment.state.{BasicState, State}

import scala.collection.mutable.ArrayBuffer

class MazeGridBuilder(val x: Int, val y: Int, val rewardBonus: Int = MazeGridBuilder.WEAK_REWARD) extends GridBuilder {
	// this class builds the maze structure and exposes methods to create a maze

	protected val grid: Array[Array[State]] = Array.ofDim[State](x, y) // the grid of the states

	// setting of the reward constants for this instance
	val positiveReward: Int = rewardBonus
	val negativeReward: Int = -2 * positiveReward

	protected val actionCache: Array[Array[Option[Action]]] = Array.fill(x,y)(None) // cache for re-using the actions // in position (i,j) there is the Action to go to State (i,j) [initialized with None]
	protected val actionGrid: Array[Array[ArrayBuffer[Action]]] = Array.fill(x,y)(new ArrayBuffer[Action](4)) // store all the actions in the right grid position [initialized with the starting size (max four actions per state)]

	protected var startingState: State = _
	protected var goalState: State = _

	/* Constructor */
	private def createDoubleLink(s1_i: Int, s1_j: Int, s2_i: Int, s2_j: Int): Unit = { // create a link in both directions for two states in the grid
		var to_s1 = actionCache(s1_i)(s1_j)
		var to_s2 = actionCache(s2_i)(s2_j)

		if (to_s1.isEmpty) {
			val s1 = grid(s1_i)(s1_j)
			to_s1 = Option(new BasicAction("to->" + s1, s1))
			actionCache(s1_i)(s1_j) = to_s1
		}
		if (to_s2.isEmpty) {
			val s2 = grid(s2_i)(s2_j)
			to_s2 = Option(new BasicAction("to->" + s2, s2))
			actionCache(s2_i)(s2_j) = to_s2
		}

		actionGrid(s1_i)(s1_j) += to_s2.get
		actionGrid(s2_i)(s2_j) += to_s1.get
	}

	for (i <- 0 until x; j <- 0 until y) { // create all the (empty) states in the grid
		grid(i)(j) = new BasicState("(" + i + "," + j + ")")
	}

	for (i <- 0 until x; j <- 0 until y) { // create all links (transitions) in the grid
		if (j < y - 1)
			createDoubleLink(i, j, i, j + 1)
		if (i < x - 1)
			createDoubleLink(i + 1, j, i, j)
	}
	/*  */

	/* Aux functions */
	private def setReward(i: Int, j: Int, reward: Int): Unit = actionCache(i)(j).get.setReward(reward)

	private def deleteDoubleLink(s1_i: Int, s1_j: Int, s2_i: Int, s2_j: Int): Unit = {
		val to_s1 = actionCache(s1_i)(s1_j)
		val to_s2 = actionCache(s2_i)(s2_j)
		actionGrid(s1_i)(s1_j) -= to_s2.get
		actionGrid(s2_i)(s2_j) -= to_s1.get
	}
	/*  */

	/* GridBuilder trait */
	override def setGoalState(i: Int, j: Int): GridBuilder = {
		goalState = grid(i)(j)
		setReward(i, j, MazeGridBuilder.GOAL_REWARD)
		this
	}

	override def setStartingState(i: Int, j: Int): GridBuilder = {
		startingState = grid(i)(j)
		this
	}

	override def setPosReward(i: Int, j: Int): GridBuilder = {
		setReward(i, j, positiveReward)
		this
	}

	override def setNegReward(i: Int, j: Int): GridBuilder = {
		setReward(i, j, negativeReward)
		this
	}

	override def setWall(i1: Int, j1: Int, i2: Int, j2: Int): GridBuilder = {
		deleteDoubleLink(i1, j1, i2, j2)
		this
	}

	override def build(): Environment = {
		for (i <- 0 until x; j <- 0 until y) { // setting all actions into the states
			grid(i)(j).setActions(actionGrid(i)(j))
		}

		new Maze(grid, startingState, goalState)
	}
	/*  */
}

object MazeGridBuilder {
	// Standard values for maze rewards

	val GOAL_REWARD: Int = 100

	val WEAK_REWARD: Int = 15
	val STRONG_REWARD: Int = 30

}
