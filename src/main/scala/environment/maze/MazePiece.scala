package environment.maze

import environment.EnvironmentPiece
import environment.state.State

class MazePiece(grid: Array[Array[State]], protected val goalStates: Set[State]) extends Maze(grid, None.orNull, None.orNull) with EnvironmentPiece {

	var mostValuableStartingState: State = _

	override def setStartingState(state: State): Unit = mostValuableStartingState = state

	override def getStartingState: State = mostValuableStartingState

	override def isGoal(state: State): Boolean = goalStates contains state
}
