import examples.maze.RandomMaze
import learning.QFunction
import mas.{InitializationMASystem, LearningMazeMAS}

object MasMain {

	/* parameters for the q-function */
	val lRate = .8      // learning rate

	val dFactor = .9    // discount factor
	/*  */

	val epsilon = .4    // the percentage of random actions taken for the e-greedy exploration policy

	val n = 100


	private var tStart: Long = _
	private var tEnd: Long = _

	def main(args: Array[String]): Unit = {
		val qFunction = new QFunction(lRate, dFactor)

		//val maze4x4 = Simple4x4.construct(MazeGridBuilder.WEAK_REWARD)
		val maze8x8 = new RandomMaze(10, 15, .1, .05).construct()//MASMaze8x8.construct(MazeGridBuilder.MEDIUM_REWARD)

		//println(maze4x4)
		//val pieces = InitializationMASystem.splitEnvironment(maze4x4,2,2)
		val pieces = InitializationMASystem.splitEnvironment(maze8x8, 3, 2)
		val mas = new LearningMazeMAS(pieces, qFunction, 0.5, 0.2, n)

		tStart = System.nanoTime() // start time
		mas.startSimulationWithThreads(); result(mas)
		//TerminationControl.setEndCallBack(_ => result(mas))
		//mas.startSimulation()

		val dummy = 0 // breakpoint
	}

	private def result(mas: LearningMazeMAS): Unit = {
		tEnd = System.nanoTime()
		println("SIMULATION ENDED in: " + (tEnd - tStart)/1e06 + " ms")
		val bestPath = mas.generateTheBestPath()
		//Simple4x4.showMaze()
		println("MAS bestpath: " + bestPath)
	}

}
