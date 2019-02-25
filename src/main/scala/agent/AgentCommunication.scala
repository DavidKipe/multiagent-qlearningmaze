package agent

import environment.state.State

trait AgentCommunication {

	def putQValueAt(state: State, value: Double): Unit

}
