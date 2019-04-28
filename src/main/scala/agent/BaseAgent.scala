package agent

import environment.Environment
import environment.path.Path
import jade.core.behaviours.SimpleBehaviour
import learning.{QFunction, QMatrix}
import policy.EpsilonGreedy
import utilities.{Analyze, Exploration}

// single agent
class BaseAgent(protected val qMatrix: QMatrix, protected val maze: Environment, val qFunction: QFunction, val eGreedyPolicy: EpsilonGreedy, var numberOfEpisodes: Int = 0) extends Agent {

	override def setup(): Unit = addBehaviour(new RunEpisodesBehavior)

	class RunEpisodesBehavior extends SimpleBehaviour {

		protected var count: Int = _

		override def onStart(): Unit = count = 0

		override def action(): Unit = {
			runOneEpisode(eGreedyPolicy, isLaunchedFromBehaviour = true)
			count += 1
		}

		override def done(): Boolean = {
			val done = count >= numberOfEpisodes
			if (done) TerminationControl.agentDone()
			done
		}
	}

	override def runEpisode(epsilonGreedyPolicy: EpsilonGreedy = this.eGreedyPolicy): Unit = runOneEpisode(epsilonGreedyPolicy)

	override def runEpisodes(epsilonGreedyPolicy: EpsilonGreedy = this.eGreedyPolicy, numberOfEpisodes: Int = this.numberOfEpisodes): Unit = for (_ <- 0 until numberOfEpisodes) runOneEpisode(epsilonGreedyPolicy)

	protected def runOneEpisode(eGreedyPlcy: EpsilonGreedy, isLaunchedFromBehaviour: Boolean = false): Unit = Exploration.episode(qMatrix, qFunction, maze, eGreedyPlcy)

	override def getBestPathFromStartingState: Path = Analyze.getBestPath(qMatrix, maze)

}
