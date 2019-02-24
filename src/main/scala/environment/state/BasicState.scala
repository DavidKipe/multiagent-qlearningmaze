package environment.state

import environment.action.Action


class BasicState(val label: String, protected var actions: Seq[Action]) extends State { // the only type of state in this project (the label [String] is the identifier of a BasicState)

	require(label != null && label.nonEmpty, "The label of a State can not be null or empty")

	def this(label: String) = this(label, Array.empty[Action])

	override private[environment] def setActions(actions: Seq[Action]): Unit = {
		this.actions = actions
	}

	override def getActions: Seq[Action] = actions

	override def getLabel: String = label

	override def toString: String = label


	def canEqual(other: Any): Boolean = other.isInstanceOf[BasicState]

	override def equals(other: Any): Boolean = other match {
		case that: BasicState =>
			(that canEqual this) &&
				label == that.label
		case _ => false
	}

	override def hashCode(): Int = {
		val state = Seq(label)
		state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
	}
}
