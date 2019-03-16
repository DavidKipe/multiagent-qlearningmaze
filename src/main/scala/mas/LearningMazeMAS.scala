package mas

import agent.MASAgent
import environment.EnvironmentPiece
import learning.QFunction

class LearningMazeMAS(val environmentPieces: Array[Array[EnvironmentPiece]], val qFunction: QFunction) {

	val gridVertHeight: Int = environmentPieces.length
	val gridHorizWidth: Int = if (gridVertHeight == 0) 0 else environmentPieces(0).length

	val gridOfAgents: Array[Array[MASAgent]] = Array.ofDim(gridVertHeight, gridHorizWidth) // TODO to hide in final version

	for (posY <- 0 until gridVertHeight) // create and initialize all the agents on the grid
		for (posX <- 0 until gridHorizWidth)
			gridOfAgents(posY)(posX) = new MASAgent(environmentPieces(posY)(posX), qFunction, Set.empty[MASAgent])

	for (posY <- 0 until gridVertHeight) // set all the neighbors for each agent
		for (posX <- 0 until gridHorizWidth) {
			var neighbors: Set[MASAgent] = Set.empty[MASAgent]

			for {
				(y, x) <- Seq((posY, posX-1), (posY+1, posX), (posY, posX+1), (posY-1, posX)) // sequence of the four possible agents
				if y >= 0
				if x >= 0
				if y < gridVertHeight
				if x < gridHorizWidth
			} neighbors += gridOfAgents(y)(x)

			gridOfAgents(posY)(posX).setNeighborhoodAgents(neighbors)
		}

	def startSimulation(): Unit = {
		// TODO create ThreadPool of 'grid.height * grid.width' size with all agents running
	}

}
