package agent

object TerminationControl {

	private val ourInstance = new TerminationControl

	def getInstance: TerminationControl = ourInstance

}

class TerminationControl private() {

	private var endCallBack: () => Unit = () => Unit

	private var numberAgentRunning: Int = 0

	private var started: Boolean = false
	private var end: Boolean = false


	def setEndCallBack(endingCallBack: () => Unit) : Unit = this.endCallBack = endingCallBack

	def addAgent(): Unit = this.synchronized {
		if (end) // throw exception if trying to add agent after end
			throw new RuntimeException

		numberAgentRunning += 1
	}

	def warnStarting(): Unit = this.synchronized {
		started = true
	}

	def agentDone(): Unit = this.synchronized {
		if (end) // throw exception if trying to warn an agent as done after end
			throw new RuntimeException

		numberAgentRunning -= 1

		if (started && numberAgentRunning <= 0) {
			endCallBack()
			end = true
		}
	}
}
