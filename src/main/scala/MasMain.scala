import agent.TerminationControl
import environment.maze.MazeGridBuilder
import examples.maze.Simple4x4
import learning.QFunction
import mas.{InitializationMASystem, LearningMazeMAS}

object MasMain {

	/* parameters for the q-function */
	val lRate = .8      // learning rate

	val dFactor = .8    // discount factor
	/*  */

	val epsilon = .2    // the percentage of random actions taken for the e-greedy exploration policy

	val n = 1000


	def main(args: Array[String]): Unit = {
		val qFunction = new QFunction(lRate, dFactor)
		val maze4x4 = Simple4x4.construct(MazeGridBuilder.WEAK_REWARD)

		println(maze4x4)
		val pieces = InitializationMASystem.splitEnvironment(maze4x4,2,2)
		val mas = new LearningMazeMAS(pieces, qFunction, epsilon, n)

		var t0 = System.nanoTime() // start time
		//mas.startSimulationWithThreads()
		TerminationControl.getInstance.setEndCallBack(_ => result(mas))
		mas.startSimulation()

	    var t1 = System.nanoTime() // end time
	    println("Time for MAS: " + (t1 - t0)/1e06 + " ms")

		// compare with single agent
		/*val agent = new SingleAgent(maze4x4, qFunction)
		val epsilonGreedy = new EpsilonGreedy(epsilon)

		t0 = System.nanoTime() // start time
		agent.runEpisodes(epsilonGreedy, n)
		val bestPathSA = agent.getBestPathFromStartingState
		t1 = System.nanoTime() // end time
		println("Time for SA: " + (t1 - t0)/1e06 + " ms")*/

		//Simple4x4.showMaze()
		//println("MAS bestpath: " + bestPath)
		//println("SA  bestpath: " + bestPathSA)

		val dummy = 0 // breakpoint
	}

	private def result(mas: LearningMazeMAS): Unit = {
		val bestPath = mas.generateTheBestPath()
		Simple4x4.showMaze()
		println("MAS bestpath: " + bestPath)
	}

}
