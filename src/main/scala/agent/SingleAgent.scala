package agent

import environment.Environment
import environment.path.Path
import environment.state.State
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy
import utilities.{Analyze, Exploration}

class SingleAgent(protected val maze: Environment, protected var qFunction: QFunction) extends Agent {

	val qMatrix: QMatrix = new QMatrix()
	protected var _episodesRun: Int = 0

	def episodesRun: Int = _episodesRun

	override def runEpisode(eGreedyPolicy: EpsilonGreedy): Unit = {
		runEpisodes(eGreedyPolicy, 1)
	}

	override def runEpisodes(eGreedyPolicy: EpsilonGreedy, numberOfIterations: Int): Unit = {
		for (_ <- 1 to numberOfIterations) {
			Exploration.episode(qMatrix, qFunction, maze, eGreedyPolicy)
			maze.countNewEpisode() // tell the environment that one more episode has been run on it
			_episodesRun += 1
		}
	}

	override def getBestPathFrom(startingState: State): Path = {
		Analyze.getBestPathFrom(qMatrix, maze, startingState)
	}

	override def getBestPathFromStartingState: Path = {
		getBestPathFrom(maze.getStartingState)
	}
}
