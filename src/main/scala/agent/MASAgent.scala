package agent

import environment.Environment
import environment.state.State
import learning.QFunction
import policy.EpsilonGreedy

class MASAgent(maze: Environment, qFunction: QFunction, neighboringStates: Seq[State]) extends SingleAgent(maze, qFunction) with AgentCommunication {

	override def runEpisode(eGreedyPolicy: EpsilonGreedy): Unit = {
		super.runEpisode(eGreedyPolicy)
		/* TODO
		*  get the avg for each state in the edge
		*  send new values to the neighborhood
		*  in async way
		* */
	}

	override def runEpisodes(eGreedyPolicy: EpsilonGreedy, numberOfIterations: Int): Unit = {
		for (_ <- 1 to numberOfIterations)
			runEpisode(eGreedyPolicy)
	}

	override def putQValueAt(state: State, value: Double): Unit = {
		/* TODO
		*  get all my states that can reach such given state
		*  for each state put in some coherent way the new value given into the qMatrix
		* */
	}

}
