package environment.maze

import environment.EnvironmentPiece
import environment.state.State

import scala.collection.mutable

class MazePiece(grid: Array[Array[State]], val coordY: Int, val coordX: Int) extends Maze(grid, None.orNull, None.orNull) with EnvironmentPiece {

	require(coordY >= 0 && coordX >= 0, "The coordinates inside the entire environment cannot be negative")

	protected val angleStatesAbsCoords: ((Int, Int), (Int, Int)) = (grid(0)(0).getCoord, grid(y-1)(x-1).getCoord)

	protected val finalStatesSets: Seq[Set[State]] = createFinalStatesSets()

	protected val nOfGoalSets: Int = finalStatesSets.length

	protected val borderStates: Set[State] = createBorderStateSet()

	protected var _numberOfEpisodesRun: Int = 0

	protected var mostValuableStartingState: State = _


	private def createFinalStatesSets(): Seq[Set[State]] = { // calculation of goalStates
		// TODO the final states in the edge should be only those can reach another environment piece (so there is a transaction to outside the env piece and not a wall for examples)
		if (coordY == 0 && coordX == 0) // for the ending piece it is the original goal state
			return Seq(Set[State](grid(0)(0)))

		val (lastY, lastX) = (y-1, x-1)

		val seqFinalSets: Array[Set[State]] = Array.ofDim(4) // max 4 sets (top, right, bottom and left edges)
		var count: Int = 0

		// top edge goals
		if (coordY > 0) {
			seqFinalSets(count) = Set(grid.head: _*)
			count += 1
		}

		// right and left edges
		if (coordX < lastX || coordX > 0) {
			val finalStatesSetL: mutable.Set[State] = mutable.Set.empty[State] // init the set
			val finalStatesSetR: mutable.Set[State] = mutable.Set.empty[State]

			for (horizArray <- grid) {
				if (coordX < lastX)
					finalStatesSetR += horizArray.last
				if (coordX > 0)
					finalStatesSetL += horizArray.head
			}

			if (finalStatesSetR.nonEmpty) {
				seqFinalSets(count) = finalStatesSetR.toSet
				count += 1
			}

			if (finalStatesSetL.nonEmpty) {
				seqFinalSets(count) = finalStatesSetL.toSet
				count += 1
			}

		}

		// bottom edge
		if (coordY < lastY) {
			seqFinalSets(count) = Set(grid.last: _*)
			count += 1
		}

		seqFinalSets.take(count)
	}

	private def createBorderStateSet(): Set[State] = {
		if (coordY == 0 && coordX == 0) { // for the ending piece, the border states are not equivalent to the final states
			val bStates: mutable.Set[State] = mutable.Set.empty[State]

			bStates ++= grid.last // all the bottom edge states

			for (horizArray <- grid) // all the right edge states
				bStates += horizArray.last

			return bStates.toSet
		}

		getAllPossibleFinalStates // otherwise is simply all the final states
	}

	override def setStartingState(state: State): Unit = mostValuableStartingState = state

	override def isStartingStateValid: Boolean =
		Option(mostValuableStartingState).isDefined && (grid.exists(hArray => hArray.last == mostValuableStartingState) || (grid.last contains mostValuableStartingState))

	override def getStartingState: State = mostValuableStartingState

	override def isGoal(state: State): Boolean = getFinalStatesForLastEpisode contains state

	override def getAngleStatesAbsCoords: ((Int, Int), (Int, Int)) = angleStatesAbsCoords

	override def getPieceCoords: (Int, Int) = (coordY, coordX)

	override def countNewEpisode(): Unit = _numberOfEpisodesRun += 1

	def numberOfEpisodesRun: Int = _numberOfEpisodesRun

	override def getBorderState: Set[State] = borderStates

	override def getAllPossibleFinalStates: Set[State] = finalStatesSets.flatten.toSet

	override def getFinalStatesForLastEpisode: Set[State] = finalStatesSets(_numberOfEpisodesRun % finalStatesSets.length)

	override def isPartOfThisEnvPiece(state: State): Boolean = state.isInsideBoundaries(angleStatesAbsCoords)

}
