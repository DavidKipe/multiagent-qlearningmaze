package environment.state

import environment.action.Action

trait State {

	private[environment] def setActions(actions: Seq[Action]): Unit

	def getActions: Seq[Action]

	def getActions(anglesBoundaries: ((Int, Int), (Int, Int))): Seq[Action]

	def hasActionTo(state: State): Boolean

	def getActionTo(state: State): Option[Action]

	def getLabel: String

	def getCoord: (Int, Int)

}
