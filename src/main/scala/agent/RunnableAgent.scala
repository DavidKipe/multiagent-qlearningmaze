package agent

import policy.EpsilonGreedy

class RunnableAgent(agent: Agent, epsilonGreedy: EpsilonGreedy, numberOfEpisodesToRun: Int) extends Runnable {

	require(numberOfEpisodesToRun >= 0, "It needs a positive or zero number of episodes to run")

	override def run(): Unit = agent.runEpisodes(epsilonGreedy, numberOfEpisodesToRun)

}
