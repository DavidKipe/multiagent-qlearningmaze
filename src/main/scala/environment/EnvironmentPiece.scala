package environment

import environment.state.State

trait EnvironmentPiece extends Environment {

	def setStartingState(state: State): Unit

	def isStartingStateValid: Boolean

	def getPieceAngleAbsCoords: ((Int, Int), (Int, Int))

	def getPieceCoords: (Int, Int)

}
