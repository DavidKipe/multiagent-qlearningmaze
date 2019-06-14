import agent.BaseAgent
import environment.maze.MazeGridBuilder
import examples.maze.MASMaze8x8
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy

object Main {

	/* parameters for the q-function */
	val lRate = .8      // learning rate

	val dFactor = .9    // discount factor
	/*  */

	val epsilon = .4    // the percentage of random actions taken for the e-greedy exploration policy


	def main(args: Array[String]): Unit = {
		var x: Int = 0
		while (x < 1 || x > 4) { // input request for reward bonus
			print("Choose the intermediate reward bonus value [1 = very weak, 2 = weak, 3 = medium, 4 = strong]: ")
			x = scala.io.StdIn.readInt()
		}

		val rewardType = x match {
			case 1 => MazeGridBuilder.VERY_WEAK_REWARD
			case 2 => MazeGridBuilder.WEAK_REWARD
			case 3 => MazeGridBuilder.MEDIUM_REWARD
			case 4 => MazeGridBuilder.STRONG_REWARD
		}

		var n: Int = 0
		while (n < 2) { // input request for the number of episodes
			print("Choose the number of episodes that will be performed: ")
			n = scala.io.StdIn.readInt()
		}

		val mazeDir = MASMaze8x8
		val maze = mazeDir.construct(rewardType) // create the example maze

		//val n = 200
		//val rewardType = MazeGridBuilder.MEDIUM_REWARD
		//val maze = new RandomMaze(10, 15, .1, .05).construct()

		// initialize q-function and the exploration policy
		val qFunction = new QFunction(lRate, dFactor)
		val epsilonGreedy = new EpsilonGreedy(0.5, 0.2, n*10)
		val qMatrix = new QMatrix

		val mouseAgent = new BaseAgent(qMatrix, maze, qFunction, epsilonGreedy, n) // init the agent

		val tStart = System.nanoTime()
		mouseAgent.runEpisodes()
		val tEnd = System.nanoTime()
		println("SIMULATION ENDED in: " + (tEnd - tStart)/1e06 + " ms")

		//mazeDir.showMaze()

		val bestPath = mouseAgent.getBestPathFromStartingState
		print("Best Path: "); println(bestPath)

		/*println("\n -- 1 -- ")
		val path9_1 = new PathLabels(9) -> (4,5) -> (4,4) -> (4,3) -> (4,2) -> (3,2) -> (2,2) -> (2,1) -> (2,0) -> (1,0) -> (0,0)
		Analyze.printPathStats(qMatrix, path9_1)
		println("\n -- 2 -- ")
		val path9_2 = new PathLabels(9) -> (4,5) -> (4,4) -> (4,3) -> (3,3) -> (2,3) -> (1,3) -> (1,2) -> (1,1) -> (0,1) -> (0,0)
		Analyze.printPathStats(qMatrix, path9_2)
		println("\n -- 3 -- ")
		val path11 = new PathLabels(11) -> (4,5) -> (4,4) -> (4,3) -> (4,2) -> (3,2) -> (2,2) -> (2,3) -> (1,3) -> (1,2) -> (0,2) -> (0,1) -> (0,0)
		Analyze.printPathStats(qMatrix, path11)*/
	}

}
