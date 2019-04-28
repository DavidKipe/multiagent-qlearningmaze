package agent

import environment.EnvironmentPiece
import environment.path.Path
import environment.state.{BasicState, State}
import jade.core.behaviours.CyclicBehaviour
import jade.lang.acl.ACLMessage
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy
import utilities.{Analyze, Utils}

class MASAgent(qMatrix: QMatrix, maze: EnvironmentPiece, qFunction: QFunction, protected var neighboringAgents: Map[(Int, Int), MASAgent], eGreedyPolicy: EpsilonGreedy, numberOfEpisodes: Int = 0)
		extends BaseAgent(qMatrix, maze, qFunction, eGreedyPolicy, numberOfEpisodes) with AgentCommunication with AgentNeighborhood {

	val ((firstY, firstX), (lastY, lastX)) = maze.getAngleStatesAbsCoords // delimiters coordinates of this environment
	val (envCoordY, envCoordX) = maze.getPieceCoords

	class RecvQValueBehaviour extends CyclicBehaviour {

		override def action(): Unit = {
			val msg = receive
			if (Option(msg).isDefined) {
				//System.out.println(" - " + myAgent.getLocalName + " <- " + msg.getContent)
				val splitStrArr = msg.getContent.split('=')
				val (toStateLabel, maxActionValue) = (splitStrArr(0), splitStrArr(1))

				val s =  new BasicState(Utils.labelToCoords(toStateLabel))
				updateQValue(s, maxActionValue.toDouble)
			}
		}
	}

	override def setup(): Unit = {
		super.setup()
		addBehaviour(new RecvQValueBehaviour)
	}

	override protected def runOneEpisode(eGreedyPolicy: EpsilonGreedy, isLaunchedFromBehaviour: Boolean = false): Unit = {
		super.runOneEpisode(eGreedyPolicy, isLaunchedFromBehaviour)
		maze.countNewEpisode() // tell the environment that one more episode has been run on it (needs for final states rotation)

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
				val msg = new ACLMessage(ACLMessage.INFORM)
				msg.addReceiver(neighborOpt.get.getAID)
				msg.setContent(toState.getLabel + "=" + maxActionValue)
				send(msg) // TODO synchronize this send (maybe wait for all the sends at the end of the 'runOneEpisode')
			}
			else
				neighborOpt.get.updateQValue(toState, maxActionValue)
	}

	// this procedure receive the max value action for the given state in another environment and calculate the new q-value for this environment
	override def updateQValue(toState: State, maxValueAction: Double): Unit = this.synchronized {
		//MASAgent.count += 1
		//println("count: " + MASAgent.count) // TODO possible issue, the agents terminate early, thus many updates of q-value occur after and don't take effect

		for (s <- maze.getBorderState) { // get all states in the border
			val optAction = s.getActionTo(toState) // get the action to the given state if exists

			if (optAction.isDefined) { // if exists  calculate the new value q-value and put it inside the q-matrix
				val newValue = qFunction.value(qMatrix, s, optAction.get.act, Some(maxValueAction))
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

object MASAgent {
	var count = 0
}
