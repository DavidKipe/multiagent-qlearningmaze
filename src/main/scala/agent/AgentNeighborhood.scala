package agent

trait AgentNeighborhood {

	def getNeighborhoodAgents: Set[MASAgent]

	def setNeighborhoodAgents(neighbors: Set[MASAgent]): Unit

}
