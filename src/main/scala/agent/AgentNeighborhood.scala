package agent

trait AgentNeighborhood {

	def setNeighboringAgents(neighbors: Map[(Int, Int), MASAgent]): Unit

	def getNeighboringAgents: Iterable[MASAgent]

}
