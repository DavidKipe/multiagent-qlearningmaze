package agent

import environment.Environment
import environment.path.Path
import environment.state.State
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy
import utilities.{Analyze, Exploration}

class SingleAgent(protected val maze: Environment, protected var qFunction: QFunction) extends Agent {

	val qMatrix: QMatrix = new QMatrix()

	override def runEpisode(eGreedyPolicy: EpsilonGreedy): Unit = {
		_runEpisode(eGreedyPolicy)
	}

	override def runEpisodes(eGreedyPolicy: EpsilonGreedy, numberOfIterations: Int): Unit = {
		for (_ <- 1 to numberOfIterations)
			_runEpisode(eGreedyPolicy)
	}

	private def _runEpisode(eGreedyPolicy: EpsilonGreedy): Unit = {
		Exploration.episode(qMatrix, qFunction, maze, eGreedyPolicy)
		maze.countNewEpisode() // tell the environment that one more episode has been run on it
	}

	override def getBestPathFrom(startingState: State): Path = {
		Analyze.getBestPathFrom(qMatrix, maze, startingState)
	}

	override def getBestPathFromStartingState: Path = {
		getBestPathFrom(maze.getStartingState)
	}
}
