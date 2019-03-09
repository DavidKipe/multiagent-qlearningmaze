package environment

import environment.state.State

trait EnvironmentPiece extends Environment {

	def setStartingState(state: State): Unit

	def isStartingStateValid: Boolean

}
