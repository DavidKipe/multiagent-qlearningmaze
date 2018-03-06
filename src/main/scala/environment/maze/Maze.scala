package environment.maze

import environment.Environment
import environment.state.State

import scala.util.Random

class Maze private[maze](protected val grid: Array[Array[State]], protected val startingState: State, protected val goalState: State) extends Environment {

	val x: Int = grid.length
	val y: Int = grid(0).length

	private val random = new Random()


	override def gridSize: (Int, Int) = (x, y)

	override def isGoal(state: State): Boolean = state equals goalState

	override def getStartingState: State = startingState

	override def getRandomState: State = {
		val i = random.nextInt(x)
		val j = random.nextInt(y)
		grid(i)(j)
	}

}
