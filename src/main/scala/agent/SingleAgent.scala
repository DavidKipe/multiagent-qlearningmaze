package agent

import environment.Environment
import environment.path.Path
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy
import utilities.{Analyze, Exploration}

class SingleAgent(protected val maze: Environment, protected val qFunction: QFunction) extends Agent {

	val qMatrix: QMatrix = new QMatrix()

	override def runEpisode(eGreedyPolicy: EpsilonGreedy): Unit = _runEpisode(eGreedyPolicy)

	override def runEpisodes(eGreedyPolicy: EpsilonGreedy, numberOfEpisodes: Int): Unit = {
		for (_ <- 1 to numberOfEpisodes)
			_runEpisode(eGreedyPolicy)
	}

	private def _runEpisode(eGreedyPolicy: EpsilonGreedy): Unit = {
		Exploration.episode(qMatrix, qFunction, maze, eGreedyPolicy)
		maze.countNewEpisode() // tell the environment that one more episode has been run on it
	}

	override def getBestPathFromStartingState: Path = Analyze.getBestPath(qMatrix, maze)

}
