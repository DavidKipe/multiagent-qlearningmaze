package agent

trait AgentNeighborhood {

	def getNeighboringAgents: Map[(Int, Int), MASAgent]

	def setNeighboringAgents(neighbors: Map[(Int, Int), MASAgent]): Unit

}
