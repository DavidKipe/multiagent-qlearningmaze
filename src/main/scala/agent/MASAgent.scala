package agent

import environment.EnvironmentPiece
import environment.state.State
import learning.QFunction
import policy.EpsilonGreedy

import scala.collection.mutable

class MASAgent(maze: EnvironmentPiece, qFunction: QFunction, private var neighboringAgents: Map[(Int, Int), MASAgent]) extends SingleAgent(maze, qFunction) with AgentCommunication with AgentNeighborhood {

	val ((firstY, firstX), (lastY, lastX)) = maze.getAngleStatesAbsCoords // delimiters coordinates of this environment
	val (envCoordY, envCoordX) = maze.getPieceCoords

	override def runEpisode(eGreedyPolicy: EpsilonGreedy): Unit = {
		_runEpisode(eGreedyPolicy)
	}

	override def runEpisodes(eGreedyPolicy: EpsilonGreedy, numberOfEpisodes: Int): Unit = {
		for (_ <- 1 to numberOfEpisodes)
		_runEpisode(eGreedyPolicy)
	}

	private def _runEpisode(eGreedyPolicy: EpsilonGreedy): Unit = {
		super.runEpisode(eGreedyPolicy)

		val updatingStates: Set[State] = maze.getBorderState // get the border states to update for this environment

		for (updatingState <- updatingStates) {
			val maxActionValue = qMatrix.getMax(updatingState, ((firstY, firstX), (lastY, lastX)))
			val (y, x) = updatingState.getCoord

			val sendTo = (coordY: Int, coordX: Int) => {
				val neighborOpt = neighboringAgents get(coordY, coordX)
				if (neighborOpt.isDefined) neighborOpt.get.updateQValue(updatingState, maxActionValue)
			}

			if (x == firstX)
				sendTo(envCoordY, envCoordX - 1)
			if (y == firstY)
				sendTo(envCoordY - 1, envCoordX)
			if (x == lastX)
				sendTo(envCoordY, envCoordX + 1)
			if (y == lastY)
				sendTo(envCoordY + 1, envCoordX)
		}
		/* TODO
		*  get the max of the action values for each state in the edge
		*  send new values to the neighborhood
		*  in async way
		* */
	}

	override def updateQValue(toState: State, maxValueAction: Double): Unit = { // TODO must be an async method
		// this procedure receive the max value action for the given state in another environment and calculate an approximation for this environment
		val statesToUpdate: mutable.Set[State] = mutable.Set.empty[State]

		var newValue = maxValueAction
		var valueUpdated = false

		for (s <- maze.getGrid.flatten) {
			val optAction = s.getActionTo(toState)

			if (optAction.isDefined) {
				statesToUpdate += s

				if (!valueUpdated) { // only once because the reward is always the same for transaction to 'toState'
					newValue += optAction.get.act.reward * qFunction.discountFactor
					valueUpdated = true
				}
			}
		}

		for (stateToUpdate <- statesToUpdate)
			qMatrix.put(stateToUpdate, toState, newValue)

		/* TODO
		*  check if state is a valid state for me, otherwise discard (could be simply checking if the state is not part of my environment piece)
		*  get all my states that can reach the given state (must be 2 or 3)
		*  for each state put into the qMatrix the given value for the transition
		* */
	}

	override def getNeighboringAgents: Map[(Int, Int), MASAgent] = neighboringAgents

	override def setNeighboringAgents(neighbors: Map[(Int, Int), MASAgent]): Unit = neighboringAgents = neighbors

	def getEnvironmentPiece: EnvironmentPiece = maze

	override def getCoords: (Int, Int) = (envCoordY, envCoordX)

	// the edging state must be the goal states, but the action for going to the neighbor must be present for the algorithm formula
	// but it must be not considered as a real action, it can not be taken

	// the edging action will be "orphan" but will be only in the goal state, so they will not be taken
}
