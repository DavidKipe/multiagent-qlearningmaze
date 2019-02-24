package environment.state
import environment.action.Action

class OneActionState(val label: String, protected var optAction: Option[Action]) extends State {

	require(label != null && label.nonEmpty, "The label of a State can not be null or empty")

	def this(label: String) = this(label, None)

	private[environment] def setAction(optAction: Option[Action]): Unit = {
		this.optAction = optAction
	}

	override private[environment] def setActions(actions: Seq[Action]): Unit = {
		require(actions.length > 1, "In 'OneActionState' there is only space for one action")
		this.optAction = Some(actions.head)
	}

	def getAction: Option[Action] = optAction

	override def getActions: Seq[Action] = optAction match { case None => Array.empty[Action]; case Some(action) => List(action) }

	override def getLabel: String = label

	override def toString: String = label


	def canEqual(other: Any): Boolean = other.isInstanceOf[OneActionState]

	override def equals(other: Any): Boolean = other match {
		case that: OneActionState =>
			(that canEqual this) &&
				label == that.label
		case _ => false
	}

	override def hashCode(): Int = {
		val state = Seq(label)
		state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
	}
}
