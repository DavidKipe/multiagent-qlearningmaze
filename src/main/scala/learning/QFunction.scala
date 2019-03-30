package learning

import environment.Transition
import environment.action.Action
import environment.state.State

class QFunction(val learningRate: Double, val discountFactor: Double) {

	def value(qMatrix: QMatrix, state: State, transition: Transition): Double = {
		val newState = transition.newState // get the new state from transition

		val oldValue = qMatrix.getOrDefault(state, newState) // get the old q value from q-matrix for this transition
		val learnedValue = transition.reward + (discountFactor * qMatrix.getMax(newState)) // calculate the learning value

		((1.0 - learningRate) * oldValue) + (learningRate * learnedValue) // calculate the new q-value taking into account the old q-value and the learning rate
	}

	def value(qMatrix: QMatrix, state: State, action: Action): Double = value(qMatrix, state, action.act)

	def valueGivenMaxFutureAction(qMatrix: QMatrix, maxFutureAction: Double, state: State, action: Action): Double = valueGivenMaxFutureAction(qMatrix, maxFutureAction, state, action.act)

	// TODO not repeat the code
	def valueGivenMaxFutureAction(qMatrix: QMatrix, maxFutureAction: Double, state: State, transition: Transition): Double = {
		val newState = transition.newState // get the new state from transition

		val oldValue = qMatrix.getOrDefault(state, newState) // get the old q value from q-matrix for this transition
		val learnedValue = transition.reward + (discountFactor * maxFutureAction) // calculate the learning value

		((1.0 - learningRate) * oldValue) + (learningRate * learnedValue) // calculate the new q-value taking into account the old q-value and the learning rate
	}

}
