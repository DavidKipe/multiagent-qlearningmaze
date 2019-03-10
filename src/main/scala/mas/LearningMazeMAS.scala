package mas

import agent.MASAgent
import environment.EnvironmentPiece
import learning.QFunction

class LearningMazeMAS(environmentPieces: Array[Array[EnvironmentPiece]], qFunction: QFunction) {

	val gridVertHeight: Int = environmentPieces.length
	val gridHorizWidth: Int = if (gridVertHeight == 0) 0 else environmentPieces(0).length

	protected val gridOfAgents: Array[Array[MASAgent]] = Array.ofDim(gridVertHeight, gridHorizWidth)

	for (posY <- 0 until gridVertHeight)
		for (posX <- 0 until gridHorizWidth)
			gridOfAgents(posY)(posX) = new MASAgent(environmentPieces(posY)(posX), qFunction, Set.empty[MASAgent])

	// TODO setting the neighbor agents
}
