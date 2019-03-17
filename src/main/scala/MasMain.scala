import environment.maze.MazeGridBuilder
import examples.maze.Simple4x4
import learning.{QFunction, QMatrix}
import mas.{InitializationMASystem, LearningMazeMAS}

object MasMain {

	/* parameters for the q-function */
	private val lRate = .8      // learning rate

	private val dFactor = .9    // discount factor
	/*  */

	private val epsilon = .2    // the percentage of random actions taken for the e-greedy exploration policy


	def main(args: Array[String]): Unit = {
		val qFunction = new QFunction(lRate, dFactor)
		val qMatrix = new QMatrix
		val maze4x4 = Simple4x4.construct(MazeGridBuilder.WEAK_REWARD)

		//Simple4x4.showMaze()
		println(maze4x4)
		val pieces = InitializationMASystem.splitEnvironment(maze4x4,2,2)
		val agents = new LearningMazeMAS(pieces, qFunction)
		agents.epsilonGreedyValue = epsilon
		agents.numberOfEpisodesToRun = 1000

		agents.startSimulation()
		//val eGreedy = new EpsilonGreedyBounds(epsilon, pieces(0)(1).getPieceAngleAbsCoords)
		//agents.gridOfAgents(0)(1).runEpisodes(eGreedy, 50)

		/*
		 TODO
		 - DONE create a new "bestAction" in qMatrix class whose take also the boundaries of the environment piece, therefore excludes the states outside the environment boundaries
		 - run all agents in parallel
		 - a solution to find the best path (the goal state should be all the edges but the edge in which the path entered in the env piece)
		 */

	}
}
