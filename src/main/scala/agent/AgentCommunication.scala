package agent

import environment.state.State

trait AgentCommunication {

	def getCoords: (Int, Int) // returns the coordinates of this piece inside the entire environment

	def getAngleStatesAbsCoords: ((Int, Int), (Int, Int)) // returns the absolute coords of states in the top left and bottom right corner of environment piece of this agent

	def updateQValue(toState: State, maxValueAction: Double): Unit // this is the procedure that receives the value from a neighbor and update the own q-matrix properly

	def isThisPartOfYourEnv(state: State): Boolean // tests if a state is part of environment of this agent

	def setStartingState(startingState: State): Unit

}
