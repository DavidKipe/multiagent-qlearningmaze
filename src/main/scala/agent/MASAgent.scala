package agent

import environment.EnvironmentPiece
import environment.state.State
import learning.QFunction
import policy.EpsilonGreedy

class MASAgent(maze: EnvironmentPiece, qFunction: QFunction, neighboringStates: Set[MASAgent]) extends SingleAgent(maze, qFunction) with AgentCommunication with AgentNeighborhood {

	override def runEpisode(eGreedyPolicy: EpsilonGreedy): Unit = {
		super.runEpisode(eGreedyPolicy)
		/* TODO
		*  get the max of the action values for each state in the edge
		*  send new values to the neighborhood
		*  in async way
		* */
	}

	override def runEpisodes(eGreedyPolicy: EpsilonGreedy, numberOfIterations: Int): Unit = {
		for (_ <- 1 to numberOfIterations)
			runEpisode(eGreedyPolicy)
	}

	override def putQValueAt(state: State, value: Double): Unit = { // TODO must be an async method
		/* TODO
		*  check if state is a valid state for me, otherwise discard
		*  get all my states that can reach the given state (must be 2 or 3)
		*  for each state put into the qMatrix the given value for the transition
		* */
	}

	override def getNeighborhoodAgents: Set[MASAgent] = ???

	override def setNeighborhoodAgents: Set[MASAgent] = ???


	// the edging state must be the goal states, but the action for going to the neighbor must be present for the algorithm formula
	// but it must be not considered as a real action, it can not be taken

	// the edging action will be "orphan" but will be only in the goal state, so they will not be taken
}