package learning

import environment.Transition
import environment.state.State

class QFunction(val learningRate: Double, val discountFactor: Double) {

	def value(qMatrix: QMatrix, fromState: State, transition: Transition, maxValueFutureAction: Option[Double] = None): Double = {
		val newState = transition.newState // get the new state
		val reward = transition.reward // get the reward
		val oldValue = qMatrix.getOrDefault(fromState, newState) // get the old q-value from q-matrix

		var _maxValueFutureAction: Double = 0
		if (maxValueFutureAction.isEmpty) // calculate the max value for future action if not given
			_maxValueFutureAction = qMatrix.getMax(newState)
		else
			_maxValueFutureAction = maxValueFutureAction.get

		val learnedValue = reward + (discountFactor * _maxValueFutureAction) // calculate the learning value

		((1.0 - learningRate) * oldValue) + (learningRate * learnedValue) // calculate the new q-value taking into account the old q-value and the learning rate
	}

}
