package agent

import environment.state.State

trait AgentCommunication {

	def getCoords: (Int, Int)

	def updateQValue(toState: State, maxValueAction: Double): Unit

}
