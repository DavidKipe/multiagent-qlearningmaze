package agent

import environment.path.Path
import policy.EpsilonGreedy

trait Agent {

	def runEpisode(eGreedyPolicy: EpsilonGreedy): Unit

	def runEpisodes(eGreedyPolicy: EpsilonGreedy, numberOfEpisodes: Int): Unit

	def getBestPathFromStartingState: Path

}
