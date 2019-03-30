package learning

import environment.Transition
import environment.action.Action
import environment.state.{BasicState, State}
import exception.NoActionFound

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class QMatrix {

	/*
		This class implements the q-matrix structure. It provides methods to put and get values from the matrix and other methods on the actions
	 */

	val defaultValue = .0 // it there is no value yet, the initial value is 0


	private val qValues = mutable.Map[(State, State), Double]() // this is the real q-matrix, it is a map from a pair of State (fromState, toState) to a real number


	def put(state: State, newState: State, q: Double): Unit = qValues += (state, newState) -> q

	def put(state: State, transition: Transition, q: Double): Unit = put(state, transition.newState, q)

	def put(state: State, action: Action, q: Double): Unit = put(state, action.act, q)

	def get(state: State, newState: State): Option[Double] = qValues.get(state, newState)

	def getOrDefault(state: State, newState: State): Double = qValues.getOrElse((state, newState), defaultValue)

	def get(state: State, transition: Transition): Option[Double] = get(state, transition.newState)

	def getOrDefault(state: State, transition: Transition): Double = getOrDefault(state, transition.newState)

	def get(state: State, action: Action): Option[Double] = get(state, action.act)

	def getOrDefault(state: State, action: Action): Double = getOrDefault(state, action.act)

	def getByLabel(fromCoords: (Int, Int), toCoords: (Int, Int)): Option[Double] = get(new BasicState(fromCoords), new BasicState(toCoords))


	def getMax(state: State): Double = { // return the highest value for an Action given a State
		var max_q = Double.NegativeInfinity

		for (a <- state.getActions) {
			val q = getOrDefault(state, a)
			if (q > max_q)
				max_q = q
		}

		max_q
	}

	def bestActions(state: State): Seq[Action] = { // returns the best actions for a State
		val bestActions = ArrayBuffer[Action]()
		var max_q = Double.NegativeInfinity

		for (a <- state.getActions) {
			val q = getOrDefault(state, a)
			if (q == max_q) bestActions += a
			if (q > max_q) {
				bestActions.clear()
				bestActions += a
				max_q = q
			}
		}

		bestActions
	}

	def bestAction(state: State): Action = { // returns only the first best action found for a State
		var bestAction: Option[Action] = None
		var max_q = Double.NegativeInfinity

		for (a <- state.getActions) {
			val q = getOrDefault(state, a)
			if (q > max_q) {
				bestAction = Some(a)
				max_q = q
			}
		}

		if (bestAction.isEmpty)
			throw new NoActionFound(state, "Failed to found the best action, because such state has no actions")
		else
			bestAction.get
	}

	/* Overloading these functions for MAS implementation  */
	// TODO find a better way to not replicate the code
	def bestActions(state: State, anglesBoundaries: ((Int, Int), (Int, Int))): Seq[Action] = { // returns the best actions for a State
		val bestActions = ArrayBuffer[Action]()
		var max_q = Double.NegativeInfinity

		for (a <- state.getActions(anglesBoundaries)) {
			val q = getOrDefault(state, a)
			if (q == max_q) bestActions += a
			if (q > max_q) {
				bestActions.clear()
				bestActions += a
				max_q = q
			}
		}

		bestActions
	}

	def bestAction(state: State, anglesBoundaries: ((Int, Int), (Int, Int))): Action = { // returns only the first best action found for a State
		var bestAction: Option[Action] = None
		var max_q = Double.NegativeInfinity

		for (a <- state.getActions(anglesBoundaries)) {
			val q = getOrDefault(state, a)
			if (q > max_q) {
				bestAction = Some(a)
				max_q = q
			}
		}

		if (bestAction.isEmpty)
			throw new NoActionFound(state, "Failed to found the best action, because such state has no actions")
		else
			bestAction.get
	}

}
