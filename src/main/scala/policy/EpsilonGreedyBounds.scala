package policy

import environment.action.Action
import environment.state.State
import exception.NoActionFound
import learning.QMatrix

class EpsilonGreedyBounds(startEpsilon: Double, finalEpsilon: Double, numberOfActionToReachFinal: Int, val anglesBoundaries: ((Int, Int), (Int, Int)))
	extends EpsilonGreedy(startEpsilon, finalEpsilon, numberOfActionToReachFinal) {

	override def nextAction(state: State, qMatrix: QMatrix): Action = {
		var bestActions: Seq[Action] = Seq.empty
		_isLastActionRandom = false

		if (random.nextDouble < calcNewEpsilon()) { // if the random value is less than epsilon value
			bestActions = state.getActions(anglesBoundaries) // all the actions are possible
			_isLastActionRandom = true
		}
		else
			bestActions = qMatrix.bestActions(state, anglesBoundaries) // otherwise get only the best actions (probably one, but could be more)

		if (bestActions == null || bestActions.isEmpty)
			throw new NoActionFound(state, "Failed to found the next action")

		val random_i: Int = random.nextInt(bestActions.size) // select a random action
		bestActions(random_i) // return the action
	}

}
