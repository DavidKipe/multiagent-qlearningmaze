package environment.state

import environment.action.Action

trait State {

	private[environment] def setActions(actions: Seq[Action]): Unit

	def getActions: Seq[Action]

	def getLabel: String

	def getCoord: (Int, Int)

}
