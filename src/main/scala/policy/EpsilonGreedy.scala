package policy
import environment.action.Action
import environment.state.State
import exception.NoActionFound
import learning.QMatrix

import scala.util.Random

class EpsilonGreedy(val epsilon: Double) extends ExplorationPolicy {

	require(epsilon > 0.0 && epsilon < 1.0, "Epsilon value must be in interval (0.0, 1.0)")

	protected val random = new Random()

	protected var _isLastActionRandom: Boolean = _ // only for printing help

	def isLastActionRandom: Boolean = _isLastActionRandom

	override def nextAction(state: State, qMatrix: QMatrix): Action = {
		var bestActions: Seq[Action] = Seq.empty
		_isLastActionRandom = false

		if (random.nextDouble < epsilon) { // if the random value is less than epsilon value
			bestActions = state.getActions // all the actions are possible
			_isLastActionRandom = true
		}
		else
			bestActions = qMatrix.bestActions(state) // otherwise get only the best actions (probably one, but could be more)

		if (bestActions == null || bestActions.isEmpty)
			throw new NoActionFound(state, "Failed to found the next action")

		val random_i: Int = random.nextInt(bestActions.size) // select a random action
		bestActions(random_i) // return the action
	}

	def printHeadAction(): Unit = {
		if (_isLastActionRandom)
			print("*   ")
		else
			print("    ")
	}

}
