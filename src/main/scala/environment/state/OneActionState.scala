package environment.state
import environment.action.Action

class OneActionState(val coordY: Int, val coordX: Int, protected var optAction: Option[Action]) extends State {
	// TODO should extend BasicState

	protected var label: String = "(" + coordY + "," + coordX + ")"

	def this(coordY: Int, coordX: Int) = this(coordY, coordX, None)

	def this(label: String) = {
		this(-1, -1)
		this.label = label
	}

	private[environment] def setAction(optAction: Option[Action]): Unit = this.optAction = optAction

	override private[environment] def setActions(actions: Seq[Action]): Unit = {
		require(actions.length > 1, "In 'OneActionState' there is only space for one action")
		this.optAction = Some(actions.head)
	}

	def getAction: Option[Action] = optAction

	override def hasActionTo(state: State): Boolean = ???

	override def getActions: Seq[Action] = optAction match { case None => Array.empty[Action]; case Some(action) => Seq(action) }

	override def getActions(anglesBoundaries: ((Int, Int), (Int, Int))): Seq[Action] = ???

	override def getActionTo(state: State): Option[Action] = ???

	override def getLabel: String = label

	override def getCoord: (Int, Int) = (coordY, coordX)

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
