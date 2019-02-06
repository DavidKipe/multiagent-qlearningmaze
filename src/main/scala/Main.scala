import agent.SingleAgent
import environment.maze.MazeGridBuilder
import environment.path.PathLabels
import examples.maze.Maze5x6
import learning.QFunction
import policy.EpsilonGreedy
import utilities.Analyze

object Main {

	/* parameters for the q-function */
	private val lRate = .8      // learning rate

	private val dFactor = .9    // discount factor
	/*  */

	private val epsilon = .2    // the percentage of random actions taken for the e-greedy exploration policy


	def main(args: Array[String]): Unit = {
		var c: Char = '?'
		while (c != 'w' && c != 's') { // input request for reward bonus
			print("Choose the intermediate reward bonus value [w = weak, s = strong]: ")
			c = scala.io.StdIn.readChar()
		}
		val rewardType = if (c == 'w') MazeGridBuilder.WEAK_REWARD else MazeGridBuilder.STRONG_REWARD

		var n: Int = 0
		while (n < 2) { // input request for the number of episodes
			print("Choose the number of episodes that will be performed: ")
			n = scala.io.StdIn.readInt()
		}

		val mazeDir = Maze5x6

		val maze = mazeDir.construct(rewardType) // create the example maze

		// initialize q-matrix, q-function and the exploration policy
		val qFunction = new QFunction(lRate, dFactor)
		val epsilonGreedy = new EpsilonGreedy(epsilon)

		val mouse = new SingleAgent(maze, qFunction)

		mouse.runEpisodes(epsilonGreedy, n)

		mazeDir.showMaze()

		Analyze.bestPath(mouse.qMatrix, maze)

		println("\n -- 1 -- ")
		val path9_1 = new PathLabels(9) -> (4,5) -> (4,4) -> (4,3) -> (4,2) -> (3,2) -> (2,2) -> (2,1) -> (2,0) -> (1,0) -> (0,0)
		Analyze.path(mouse.qMatrix, path9_1)
		println("\n -- 2 -- ")
		val path9_2 = new PathLabels(9) -> (4,5) -> (4,4) -> (4,3) -> (3,3) -> (2,3) -> (1,3) -> (1,2) -> (1,1) -> (0,1) -> (0,0)
		Analyze.path(mouse.qMatrix, path9_2)
		println("\n -- 3 -- ")
		val path11 = new PathLabels(11) -> (4,5) -> (4,4) -> (4,3) -> (4,2) -> (3,2) -> (2,2) -> (2,3) -> (1,3) -> (1,2) -> (0,2) -> (0,1) -> (0,0)
		Analyze.path(mouse.qMatrix, path11)
	}

}
