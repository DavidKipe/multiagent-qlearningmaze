package utilities

import environment.Environment
import environment.action.Action
import environment.state.State
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy

object Exploration {

	def episode(qMatrix: QMatrix, qFunction: QFunction, maze: Environment, policy: EpsilonGreedy): Unit = {
		//println("Start episode")
		//maze.asInstanceOf[MazePiece].printGoalStates() // debug only for mas

		var currState: State = maze.getRandomState // each episode starts from a random state of the maze
		var oldState: State = currState

		do {
			val selected_a: Action = policy.nextAction(currState, qMatrix) // get the next action (according to the policy given)

			val q = qFunction.value(qMatrix, currState, selected_a) // get the new q-value

			qMatrix.put(currState, selected_a, q) // update the q-matrix (most important part, here the "memory" of the agent is learning)

			oldState = currState
			currState = selected_a.act.newState // go to the new state

			//policy.printHeadAction() // debug printing part
			//println(oldState + " " + selected_a + " - q: " + q)
		} while (!maze.isGoal(currState)) // the episode ends when it reaches the final state
		//println("End episode\n")
	}

}
