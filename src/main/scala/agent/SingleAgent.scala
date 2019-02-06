package agent

import environment.Environment
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy
import utilities.Exploration

class SingleAgent(protected val maze: Environment, protected var qFunction: QFunction) extends Agent {

	val qMatrix: QMatrix = new QMatrix()
	protected var _numberOfEpisodes: Int = 0

	def numberOfEpisodes: Int = _numberOfEpisodes

	override def runEpisode(eGreedyPolicy: EpsilonGreedy): Unit = {
		Exploration.episode(qMatrix, qFunction, maze, eGreedyPolicy)
	}

	override def runEpisodes(eGreedyPolicy: EpsilonGreedy, numberOfIterations: Int): Unit = {
		for (_ <- 1 to numberOfIterations)
			Exploration.episode(qMatrix, qFunction, maze, eGreedyPolicy)
	}
}
