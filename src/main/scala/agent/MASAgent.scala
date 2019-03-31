package agent

import environment.EnvironmentPiece
import environment.path.Path
import environment.state.State
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy
import utilities.{Analyze, Utils}

class MASAgent(qMatrix: QMatrix, maze: EnvironmentPiece, qFunction: QFunction, protected var neighboringAgents: Map[(Int, Int), MASAgent], eGreedyPolicy: EpsilonGreedy, numberOfEpisodes: Int = 0)
		extends SingleAgent(qMatrix, maze, qFunction, eGreedyPolicy, numberOfEpisodes) with AgentCommunication with AgentNeighborhood {
	// TODO adapt for JADE
	val ((firstY, firstX), (lastY, lastX)) = maze.getAngleStatesAbsCoords // delimiters coordinates of this environment
	val (envCoordY, envCoordX) = maze.getPieceCoords

	override protected def runOneEpisode(eGreedyPolicy: EpsilonGreedy): Unit = {
		super.runOneEpisode(eGreedyPolicy)

		val updatingStates: Set[State] = maze.getBorderState // get the border states to update for this environment

		for (updatingState <- updatingStates) {
			val maxActionValue = qMatrix.getMax(updatingState)
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
		/*
		*  get the max of the action values for each state in the edge
		*  send new values to the neighborhood
		*  in async way
		* */
	}

	override def updateQValue(toState: State, maxValueAction: Double): Unit = { // TODO must be an async method
		// this procedure receive the max value action for the given state in another environment and calculate the new q-value for this environment

		for (s <- maze.getBorderState) {
			val optAction = s.getActionTo(toState)

			if (optAction.isDefined) {
				val newValue = qFunction.valueGivenMaxFutureAction(qMatrix, maxValueAction, s, optAction.get)
				qMatrix.put(s, toState, newValue)
			}
		}
		/*
		*  check if state is a valid state for me, otherwise discard (could be simply checking if the state is not part of my environment piece)
		*  get all my states that can reach the given state (must be 2 or 3)
		*  for each state put into the qMatrix the given value for the transition
		* */
	}

	override def getNeighboringAgents: Iterable[MASAgent] = neighboringAgents.values

	override def setNeighboringAgents(neighbors: Map[(Int, Int), MASAgent]): Unit = neighboringAgents = neighbors

	override def getAngleStatesAbsCoords: ((Int, Int), (Int, Int)) = maze.getAngleStatesAbsCoords

	override def isThisPartOfYourEnv(state: State): Boolean = maze.isPartOfThisEnvPiece(state)

	override def setStartingState(startingState: State): Unit = maze.setStartingState(startingState)

	override def getCoords: (Int, Int) = (envCoordY, envCoordX)

	override def getBestPathFromStartingState: Path = if (envCoordY == 0 && envCoordX == 0) Analyze.getBestPath(qMatrix, maze) else Analyze.getBestPathForEnvPiece(qMatrix, maze)

	def getStringId: String = Utils.coordsToLabel(envCoordY, envCoordX)


	def canEqual(other: Any): Boolean = other.isInstanceOf[MASAgent]

	override def equals(other: Any): Boolean = other match {
		case that: MASAgent =>
			(that canEqual this) &&
				envCoordY == that.envCoordY &&
				envCoordX == that.envCoordX
		case _ => false
	}

	override def hashCode(): Int = {
		val state = Seq(envCoordY, envCoordX)
		state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
	}

	override def toString: String = getStringId

}
