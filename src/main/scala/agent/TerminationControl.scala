package agent

object TerminationControl {

	private var endCallBack: Unit => Unit = _

	private var numberOfAgents: Int = 0

	private var started: Boolean = false
	private var end: Boolean = false
	private var callbackSetUp = false


	def setEndCallBack(endCallBack: Unit => Unit) : Unit = {
		this.endCallBack = endCallBack
		this.callbackSetUp = true
	}

	def setNumberOfAgents(numberOfAgents: Int): Unit = TerminationControl.synchronized {
		require(!started, "The number of agents is already set up")
		require(numberOfAgents >= 0, "The number of agents in the simulation should be positive")

		this.numberOfAgents += numberOfAgents
		this.started = true
	}

	def agentDone(): Unit = TerminationControl.synchronized {
		if (end) // if trying to warn an agent as done after end, nothing will be done
			return

		numberOfAgents -= 1

		triggerIfAllDone()
	}

	def triggerIfAllDone(): Unit = TerminationControl.synchronized {
		if (started && !end && callbackSetUp && numberOfAgents <= 0) {
			endCallBack()
			end = true
		}
	}

}
