package policy
import environment.action.Action
import environment.state.State
import exception.NoActionFound
import learning.QMatrix

import scala.util.Random

class EpsilonGreedy(val startEpsilon: Double, val finalEpsilon: Double, val numberOfActionToReachFinal: Int) extends ExplorationPolicy {

	require(startEpsilon > 0.0 && startEpsilon < 1.0, "Start epsilon value must be in interval (0.0, 1.0)")
	require(finalEpsilon > 0.0 && finalEpsilon < 1.0, "Final epsilon value must be in interval (0.0, 1.0)")
	require(finalEpsilon < startEpsilon, "The final epsilon value must be less than start epsilon value")

	protected var epsilon: Double = startEpsilon
	protected val epsilonStep: Double = (startEpsilon - finalEpsilon) / numberOfActionToReachFinal

	protected var actionCount: Int = 0

	protected var _isLastActionRandom: Boolean = _ // only for printing help

	protected val random = new Random

	def isLastActionRandom: Boolean = _isLastActionRandom

	override def nextAction(state: State, qMatrix: QMatrix): Action = {
		var bestActions: Seq[Action] = Seq.empty
		_isLastActionRandom = false

		if (random.nextDouble < calcNewEpsilon()) { // if the random value is less than epsilon value
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

	protected def calcNewEpsilon(): Double = {
		if (actionCount < numberOfActionToReachFinal) {
			epsilon -= epsilonStep
			actionCount += 1
			epsilon
		}
		else
			epsilon
	}

}
