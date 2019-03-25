package environment

import environment.state.State

trait EnvironmentPiece extends Environment {

	def setStartingState(state: State): Unit

	def isStartingStateValid: Boolean

	def getAngleStatesAbsCoords: ((Int, Int), (Int, Int)) // returns the absolute coords of states in the top left and bottom right corner of this environment piece

	def getPieceCoords: (Int, Int) // returns the coordinates of this environment piece, respect to the whole environment

	def getBorderState: Set[State]

	def getAllFinalStates: Set[State]

	def getFinalStatesForLastEpisode: Set[State]

	def isPartOfThisEnvPiece(state: State): Boolean

}
