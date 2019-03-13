package environment.maze

import environment.EnvironmentPiece
import environment.state.State

class MazePiece(grid: Array[Array[State]], protected val goalStates: Set[State]) extends Maze(grid, None.orNull, None.orNull) with EnvironmentPiece {

	private var mostValuableStartingState: State = _

	override def setStartingState(state: State): Unit = mostValuableStartingState = state

	override def isStartingStateValid: Boolean =
		Option(mostValuableStartingState).isDefined && (grid.exists(hArray => hArray.last == mostValuableStartingState) || (grid.last contains mostValuableStartingState))

	override def getStartingState: State = mostValuableStartingState

	override def isGoal(state: State): Boolean = goalStates contains state
}
