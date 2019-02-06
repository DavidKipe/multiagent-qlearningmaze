package agent

import policy.EpsilonGreedy

trait Agent {

	def runEpisode(eGreedyPolicy: EpsilonGreedy): Unit

	def runEpisodes(eGreedyPolicy: EpsilonGreedy, numberOfIterations: Int): Unit

}
