package environment

import environment.state.State

trait Environment {

	def gridSize: (Int, Int) // returns (height, width)

	def getGrid: Array[Array[State]]

	def isGoal(state: State): Boolean

	def getStartingState: State

	def getRandomState: State

	def countNewEpisode(): Unit

	def numberOfEpisodesRun: Int

}
