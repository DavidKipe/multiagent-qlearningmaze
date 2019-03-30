package mas

import agent.{MASAgent, RunnableAgent}
import environment.EnvironmentPiece
import environment.path.Path
import learning.QFunction
import policy.EpsilonGreedyBounds

import scala.collection.mutable

class LearningMazeMAS(val environmentPieces: Array[Array[EnvironmentPiece]], val qFunction: QFunction) {

	val gridVertHeight: Int = environmentPieces.length
	val gridHorizWidth: Int = if (gridVertHeight == 0) 0 else environmentPieces.head.length

	private val gridOfAgents: Array[Array[MASAgent]] = Array.ofDim(gridVertHeight, gridHorizWidth)

	/* Constructor */
	forAllGridPositions((posY: Int, posX: Int) => gridOfAgents(posY)(posX) = new MASAgent(environmentPieces(posY)(posX), qFunction, Map.empty[(Int, Int), MASAgent])) // create and initialize all the agents on the grid

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

	/* Variables with getters and setters */
	private var _epsilonGreedyValue: Double = _

	private var _numberOfEpisodesToRun: Int = _

	def epsilonGreedyValue: Double = _epsilonGreedyValue

	def epsilonGreedyValue_=(value: Double): Unit = _epsilonGreedyValue = value

	def numberOfEpisodesToRun: Int = _numberOfEpisodesToRun

	def numberOfEpisodesToRun_=(value: Int): Unit = _numberOfEpisodesToRun = value
	/*  */

	def startSimulation(): Unit = { // starts the simulation with the current variables
		require(Option(_epsilonGreedyValue).isDefined, "epsilon greedy policy not defined yet")
		require(Option(_numberOfEpisodesToRun).isDefined, "the number of episodes to run not defined yet")

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

	private def forAllGridPositions(p: (Int, Int) => Unit): Unit = { // run a procedure for each position coordinate in the grid
		for (posY <- 0 until gridVertHeight)
			for (posX <- 0 until gridHorizWidth)
				p(posY, posX)
	}

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

}
