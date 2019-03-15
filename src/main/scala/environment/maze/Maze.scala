package environment.maze

import environment.Environment
import environment.state.State

import scala.util.Random

class Maze (protected val grid: Array[Array[State]], protected val startingState: State, protected val goalState: State) extends Environment {

	val x: Int = grid.length
	val y: Int = if (x == 0) 0 else grid(0).length

	private val random = new Random()


	override def gridSize: (Int, Int) = (x, y)

	override def getGrid: Array[Array[State]] = grid

	override def isGoal(state: State): Boolean = state equals goalState

	override def getStartingState: State = startingState

	override def getRandomState: State = {
		val i = random.nextInt(x)
		val j = random.nextInt(y)
		grid(i)(j)
	}

	override def countNewEpisode(): Unit = Unit // not used in this implementation

	override def getEpisodesRun: Int = ???

	override def toString: String = {
		val strBld: StringBuilder = new StringBuilder

		for (row <- grid) {
			for (state <- row)
				strBld.append(state.getLabel).append(" ")
			strBld.append("\n")
		}

		strBld.toString
	}
}
