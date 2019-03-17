package agent

import environment.path.Path
import environment.state.State
import policy.EpsilonGreedy

trait Agent {

	def runEpisode(eGreedyPolicy: EpsilonGreedy): Unit

	def runEpisodes(eGreedyPolicy: EpsilonGreedy, numberOfEpisodes: Int): Unit

	def getBestPathFrom(state: State): Path

	def getBestPathFromStartingState: Path

}
