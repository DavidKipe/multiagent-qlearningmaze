package agent

import environment.EnvironmentPiece
import environment.path.Path
import environment.state.{BasicState, State}
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy
import utilities.{Analyze, Utils}

class MASAgent(qMatrix: QMatrix, maze: EnvironmentPiece, qFunction: QFunction, protected var neighboringAgents: Map[(Int, Int), MASAgent], eGreedyPolicy: EpsilonGreedy, numberOfEpisodes: Int = 0)
		extends SingleAgent(qMatrix, maze, qFunction, eGreedyPolicy, numberOfEpisodes) with AgentCommunication with AgentNeighborhood {

	val ((firstY, firstX), (lastY, lastX)) = maze.getAngleStatesAbsCoords // delimiters coordinates of this environment
	val (envCoordY, envCoordX) = maze.getPieceCoords

	import jade.core.behaviours.CyclicBehaviour

	override def setup(): Unit = {
		super.setup()
		addBehaviour(new CyclicBehaviour(this) {
			override def action(): Unit = {
				val msg = receive
				if (msg != null) {
					System.out.println(" - " + myAgent.getLocalName + " <- " + msg.getContent)
					val s =  new BasicState(msg.getContent.charAt(1).asDigit, msg.getContent.charAt(3).asDigit)
					updateQValue(s, msg.getContent.substring(8).toDouble)
				}
				block()
			}
		})
	}

	override protected def runOneEpisode(eGreedyPolicy: EpsilonGreedy, isLaunchedFromBehaviour: Boolean = false): Unit = {
		super.runOneEpisode(eGreedyPolicy, isLaunchedFromBehaviour)

		val updatingStates = maze.getBorderState // get the border states to update for this environment piece

		for (updatingState <- updatingStates) { // for each of state to update
			val maxActionValue = qMatrix.getMax(updatingState) // get the max value action
			val (y, x) = updatingState.getCoord // get the coords

			val sendTo = (coordY: Int, coordX: Int) => sendValueToAgent(coordY, coordX, updatingState, maxActionValue, isLaunchedFromBehaviour)

			// choose the neighbor agent which can have an action to this state
			if (x == firstX)
				sendTo(envCoordY, envCoordX - 1)
			if (y == firstY)
				sendTo(envCoordY - 1, envCoordX)
			if (x == lastX)
				sendTo(envCoordY, envCoordX + 1)
			if (y == lastY)
				sendTo(envCoordY + 1, envCoordX)
		}
	}

	// function to send the value to the given agent in the correct way (with Jade msg or without)
	protected def sendValueToAgent(coordY: Int, coordX: Int, toState: State, maxActionValue: Double, withJadeMessage: Boolean = false): Unit = {
		val neighborOpt = neighboringAgents get (coordY, coordX)

		if (neighborOpt.isDefined)
			if (withJadeMessage) {
				//draft code for sending message
				import jade.lang.acl.ACLMessage
				val msg = new ACLMessage(ACLMessage.INFORM)
				msg.addReceiver(neighborOpt.get.getAID)
				//msg.setContentObject((toState, maxActionValue))
				msg.setContent(toState.getLabel + " = " + maxActionValue)
				send(msg)
			}
			else
				neighborOpt.get.updateQValue(toState, maxActionValue)

	}

	override def updateQValue(toState: State, maxValueAction: Double): Unit = synchronized {
		// this procedure receive the max value action for the given state in another environment and calculate the new q-value for this environment

		for (s <- maze.getBorderState) { // get all states in the border
			val optAction = s.getActionTo(toState) // get the action to the given state if exists

			if (optAction.isDefined) { // if exists  calculate the new value q-value and put it inside the q-matrix
				val newValue = qFunction.valueGivenMaxFutureAction(qMatrix, maxValueAction, s, optAction.get)
				qMatrix.put(s, toState, newValue)
			}
		}
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
