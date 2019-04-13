package mas

import agent.{MASAgent, RunnableAgent, TerminationControl}
import environment.EnvironmentPiece
import environment.path.Path
import jade.core.ProfileImpl
import jade.wrapper.AgentController
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedyBounds

import scala.collection.mutable

class LearningMazeMAS(val environmentPieces: Array[Array[EnvironmentPiece]], val qFunction: QFunction, val epsilonGreedyValue: Double, val numberOfEpisodesToRun: Int) {

	val gridVertHeight: Int = environmentPieces.length
	val gridHorizWidth: Int = if (gridVertHeight == 0) 0 else environmentPieces.head.length

	private val gridOfAgents: Array[Array[MASAgent]] = Array.ofDim(gridVertHeight, gridHorizWidth)

	/* Constructor */
	forAllGridPositions((posY: Int, posX: Int) =>
		gridOfAgents(posY)(posX) = new MASAgent(new QMatrix, environmentPieces(posY)(posX), qFunction, Map.empty[(Int, Int), MASAgent],
			new EpsilonGreedyBounds(epsilonGreedyValue, environmentPieces(posY)(posX).getAngleStatesAbsCoords), numberOfEpisodesToRun)
	) // create and initialize all the agents on the grid

	forAllGridPositions((posY: Int, posX: Int) => { // set all the neighbors for each agent
			val neighbors: mutable.Map[(Int, Int), MASAgent] = new mutable.HashMap[(Int, Int), MASAgent]()

			for {
				(y, x) <- Seq((posY, posX-1), (posY+1, posX), (posY, posX+1), (posY-1, posX)) // sequence of the four possible agents
				if y >= 0
				if x >= 0
				if y < gridVertHeight
				if x < gridHorizWidth
			} neighbors += ((y,x) -> gridOfAgents(y)(x))

			gridOfAgents(posY)(posX).setNeighboringAgents(neighbors.toMap)
		})
	/*  */

	def startSimulation(): Unit = { // starts the simulation with Jade
		// init the Jade container
		val runtime = jade.core.Runtime.instance
		val container = runtime.createMainContainer(new ProfileImpl)

		val gridOfAgentCtrls: Array[Array[AgentController]] = Array.ofDim[AgentController](gridVertHeight, gridHorizWidth)

		// create and initialize all agent controllers
		forAllGridPositions((posY: Int, posX: Int) => gridOfAgentCtrls(posY)(posX) = container.acceptNewAgent(gridOfAgents(posY)(posX).getStringId, gridOfAgents(posY)(posX)))
		// set the termination control
		TerminationControl.getInstance.setNumberOfAgents(gridHorizWidth * gridVertHeight)
		// start all agents
		forAllGridPositions((posY: Int, posX: Int) => gridOfAgentCtrls(posY)(posX).start())
	}

	def startSimulationWithThreads(): Unit = { // start simulation with Java threads
		val gridOfThreads: Array[Array[Thread]] = Array.ofDim[Thread](gridVertHeight, gridHorizWidth)

		// create and initialize all threads
		forAllGridPositions((posY: Int, posX: Int) =>
			gridOfThreads(posY)(posX) = new Thread(
				new RunnableAgent(gridOfAgents(posY)(posX),
				new EpsilonGreedyBounds(epsilonGreedyValue, gridOfAgents(posY)(posX).getAngleStatesAbsCoords),
				numberOfEpisodesToRun)
			)
		)

		/*forAllGridPositions((posY: Int, posX: Int) => { // one agent a time for testing with debug print
			println("*** START agent (" + posY + ", " + posX + ") ***")
			Simple4x4.showMaze()
			gridOfThreads(posY)(posX).start()
			gridOfThreads(posY)(posX).join()
		})*/
		// start all threads
		forAllGridPositions((posY: Int, posX: Int) => gridOfThreads(posY)(posX).start())
		// waiting for all threads to finish computation
		forAllGridPositions((posY: Int, posX: Int) => gridOfThreads(posY)(posX).join())
	}

	def waitSimulationToEnd(): Unit = forAllGridPositions((posY: Int, posX: Int) => gridOfAgents(posY)(posX).join()) // waiting for all agents to finish computation

	def generateTheBestPath(): Path = {
		val bestPath = new Path()

		val finalAgent = gridOfAgents.head.head
		var currAgent = gridOfAgents.last.last // the bottom right corner agent/environment piece is the starting one
		var nextAgentFound = false

		do {
			nextAgentFound = false
			val currPath = currAgent.getBestPathFromStartingState // get the best path for this env piece

			val nextStartingState = currPath.last // get the last state which is the starting state for the next env piece
			bestPath --> currPath // append the current path to the best path

			if (currAgent != finalAgent) { // if don't reach the last agent
				bestPath -= nextStartingState // delete the last state from the best path because will be append again in next iteration

				val currNeighbors = currAgent.getNeighboringAgents // get all the neighbors
				for (neighbor <- currNeighbors) // iterate over them and find the next one
					if (neighbor.isThisPartOfYourEnv(nextStartingState)) {
						currAgent = neighbor
						currAgent.setStartingState(nextStartingState)
						nextAgentFound = true
					}
			}

			if (!nextAgentFound && currAgent != finalAgent)
				throw new Exception("Could not find a path ending in the goal state")

		} while(nextAgentFound)

		bestPath
	}

	private def forAllGridPositions(p: (Int, Int) => Unit): Unit = { // run a procedure for each position coordinate in the grid
		for (posY <- 0 until gridVertHeight)
			for (posX <- 0 until gridHorizWidth)
				p(posY, posX)
	}

}
