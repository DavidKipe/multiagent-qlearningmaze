package agent

import environment.Environment
import environment.path.Path
import jade.core.behaviours.SimpleBehaviour
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy
import utilities.{Analyze, Exploration}

class SingleAgent(val qMatrix: QMatrix, val maze: Environment, val qFunction: QFunction, val eGreedyPolicy: EpsilonGreedy = None.orNull, val numberOfEpisodes: Int = 0) extends Agent {

	override def setup(): Unit = addBehaviour(new RunEpisodesBehavior)

	protected class RunEpisodesBehavior extends SimpleBehaviour {

		protected var count: Int = _

		override def onStart(): Unit = count = 0

		override def action(): Unit = { runOneEpisode(eGreedyPolicy); count += 1 }

		override def done(): Boolean = count == numberOfEpisodes

	}

	override def runEpisode(epsilonGreedyPolicy: EpsilonGreedy): Unit = runOneEpisode(epsilonGreedyPolicy)

	override def runEpisodes(epsilonGreedyPolicy: EpsilonGreedy, numberOfEpisodes: Int): Unit = 0 until numberOfEpisodes foreach (_ => runOneEpisode(epsilonGreedyPolicy))

	protected def runOneEpisode(eGreedyPlcy: EpsilonGreedy): Unit = {
		Exploration.episode(qMatrix, qFunction, maze, eGreedyPlcy)
		maze.countNewEpisode() // tell the environment that one more episode has been run on it
	}

	override def getBestPathFromStartingState: Path = Analyze.getBestPath(qMatrix, maze)

}
