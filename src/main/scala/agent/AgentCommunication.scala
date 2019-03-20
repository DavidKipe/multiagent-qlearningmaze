package agent

import environment.state.State

trait AgentCommunication {

	def getCoords: (Int, Int)

	def putQValueAt(state: State, value: Double): Unit

}
