package agent

trait AgentNeighborhood {

	def getNeighborhoodAgents: Set[MASAgent]

	def setNeighborhoodAgents: Set[MASAgent]

}
