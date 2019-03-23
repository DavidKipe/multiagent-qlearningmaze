package environment.state

import environment.action.Action

import scala.collection.mutable.ArrayBuffer


class BasicState(val coordY: Int, val coordX: Int, protected var actions: Seq[Action]) extends State { // the coordinates are the identifier of a BasicState

	protected var label: String = "(" + coordY + "," + coordX + ")"

	def this(coordY: Int, coordX: Int) = this(coordY, coordX, Array.empty[Action])

	def this(label: String) = {
		this(-1, -1)
		this.label = label
	}

	override private[environment] def setActions(actions: Seq[Action]): Unit = this.actions = actions

	override def getActions: Seq[Action] = actions

	override def getActions(anglesBoundaries: ((Int, Int), (Int, Int))): Seq[Action] = {
		val ((firstY, firstX), (lastY, lastX)) = anglesBoundaries
		val resActions = ArrayBuffer[Action]()

		for (action <- actions) {
			val (y, x) = action.act.newState.getCoord
			if (y >= firstY && y <= lastY && x >= firstX && x <= lastX)
				resActions.append(action)
		}

		resActions
	}

	override def hasActionTo(state: State): Boolean = actions exists ((a: Action) => {/*print("DEBUG: " + a.act.newState.getLabel + " =? " + state.getLabel + " -> "); println(a.act.newState == state);*/ a.act.newState == state})

	override def getActionTo(state: State): Option[Action] = {
		for (action <- actions)
			if (action.act.newState == state)
				return Some(action)

		None
	}

	override def getLabel: String = label

	override def getCoord: (Int, Int) = (coordY, coordX)

	override def toString: String = label


	def canEqual(other: Any): Boolean = other.isInstanceOf[BasicState]

	override def equals(other: Any): Boolean = other match {
		case that: BasicState =>
			(that canEqual this) &&
				coordY == that.coordY &&
				coordX == that.coordX
		case _ => false
	}

	override def hashCode(): Int = {
		val state = Seq(coordY, coordX)
		state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
	}
}
