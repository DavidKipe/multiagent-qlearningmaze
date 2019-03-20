package environment.maze

import environment.EnvironmentPiece
import environment.state.State

import scala.collection.mutable

class MazePiece(grid: Array[Array[State]], val coordY: Int, val coordX: Int) extends Maze(grid, None.orNull, None.orNull) with EnvironmentPiece {

	require(coordY >= 0 && coordX >= 0, "The coordinates inside the entire environment cannot be negative")

	protected val goalStatesSets: Seq[Set[State]] = createGoalStatesSets()

	protected val nOfGoalSets: Int = goalStatesSets.length

	protected var _numberOfEpisodesRun: Int = 0

	private var mostValuableStartingState: State = _


	private def createGoalStatesSets(): Seq[Set[State]] = { // calculation of goalStates
		// TODO the final states in the edge should be only those can reach another environment piece (so there is a transaction to outside the env piece and not a wall for examples)
		if (coordY == 0 && coordX == 0) // for the ending piece it is the original goal state
			return Seq(Set[State](grid(0)(0)))

		val (lastY, lastX) = (y-1, x-1)

		val seqGoalSets: Array[Set[State]] = Array.ofDim(4) // max 4 sets (top, right, bottom and left edges)
		var count: Int = 0

		// top edge goals
		if (coordY > 0) {
			seqGoalSets(count) = Set(grid.head: _*)
			count += 1
		}

		// right and left edges
		if (coordX < lastX || coordX > 0) {
			val goalStatesSetL: mutable.Set[State] = new mutable.HashSet[State] // init the set
			val goalStatesSetR: mutable.Set[State] = new mutable.HashSet[State] // init the set

			for (horizArray <- grid) {
				if (coordX < lastX)
					goalStatesSetR += horizArray.last
				if (coordX > 0)
					goalStatesSetL += horizArray.head
			}

			if (goalStatesSetL.nonEmpty) {
				seqGoalSets(count) = goalStatesSetL.toSet
				count += 1
			}

			if (goalStatesSetR.nonEmpty) {
				seqGoalSets(count) = goalStatesSetR.toSet
				count += 1
			}

		}

		// bottom edge
		if (coordY < lastY) {
			seqGoalSets(count) = Set(grid.last: _*)
			count += 1
		}

		seqGoalSets.take(count)
	}

	override def setStartingState(state: State): Unit = mostValuableStartingState = state

	override def isStartingStateValid: Boolean =
		Option(mostValuableStartingState).isDefined && (grid.exists(hArray => hArray.last == mostValuableStartingState) || (grid.last contains mostValuableStartingState))

	override def getStartingState: State = mostValuableStartingState

	override def isGoal(state: State): Boolean = goalStatesSets(_numberOfEpisodesRun % goalStatesSets.length) contains state

	override def getPieceAngleAbsCoords: ((Int, Int), (Int, Int)) = (grid(0)(0).getCoord, grid(y-1)(x-1).getCoord)

	override def getPieceCoords: (Int, Int) = (coordY, coordX)

	override def countNewEpisode(): Unit = _numberOfEpisodesRun += 1

	override def numberOfEpisodesRun: Int = _numberOfEpisodesRun

	def printGoalStates(): Unit = println(goalStatesSets(_numberOfEpisodesRun % goalStatesSets.length)) // debug print function
}
