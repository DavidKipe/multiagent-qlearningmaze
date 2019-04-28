import agent.BaseAgent
import examples.maze.RandomMaze
import learning.{QFunction, QMatrix}
import mas.{InitializationMASystem, LearningMazeMAS}
import policy.EpsilonGreedy

object ComparisonMain {

	/* parameters for the q-function */
	val lRate = .8      // learning rate

	val dFactor = .9    // discount factor
	/*  */

	val startEpsilon = .5    // the percentage of random actions taken for the e-greedy exploration policy
	val finalEpsilon = .2


	/* Maze parameters */
	val height = 10
	val width = 15

	val horizPieces = 3
	val vertPieces = 2

	val nEpST = 180 // number of episodes for single thread execution

	val nEpMT: Int = (nEpST / Math.sqrt(horizPieces*vertPieces)).toInt
	/*  */

	private var tStart: Long = _
	private var tEnd: Long = _

	def main(args: Array[String]): Unit = {
		val qFunction = new QFunction(lRate, dFactor)

		val randomMaze = new RandomMaze(height, width, .1, .05).construct()


		// SingleThread - SingleAgent
		val epsilonGreedy = new EpsilonGreedy(startEpsilon, finalEpsilon, nEpST * Math.max(height, width))
		val mouseAgent = new BaseAgent(new QMatrix, randomMaze, qFunction, epsilonGreedy, nEpST)

		println("\nStart ST execution...")
		tStart = System.nanoTime() // start time
		mouseAgent.runEpisodes()
		tEnd = System.nanoTime() // end time
		println("Time for Single-Thread (Single Agent): " + (tEnd - tStart)/1e06 + " ms")
		val bestPathSA = mouseAgent.getBestPathFromStartingState
		//  //

		// MultiThread - MultiAgent
		val pieces = InitializationMASystem.splitEnvironment(randomMaze, horizPieces, vertPieces)
		val mas = new LearningMazeMAS(pieces, qFunction, startEpsilon, finalEpsilon, nEpMT)

		println("\nStart MT execution...")
		tStart = System.nanoTime() // start time
		mas.startSimulationWithThreads()
		tEnd = System.nanoTime()
		println("Time for Multi-Thread (Multi Agent): " + (tEnd - tStart)/1e06 + " ms")
		val bestPathMAS = mas.generateTheBestPath()
		//  //

		println("SA  bestpath: " + bestPathSA)
		println("MAS bestpath: " + bestPathMAS)
	}

	private def result(mas: LearningMazeMAS): Unit = {
		tEnd = System.nanoTime()
		println("SIMULATION ENDED in: " + (tEnd - tStart)/1e06 + " ms")
		val bestPath = mas.generateTheBestPath()
		println("MAS bestpath: " + bestPath)
	}

	// number_of_MT_episodes = number_of_ST_episodes / sqrt(number_of_pieces)

}
