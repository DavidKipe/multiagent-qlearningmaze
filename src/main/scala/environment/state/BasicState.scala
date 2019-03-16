package environment.state

import environment.action.Action

import scala.collection.mutable.ArrayBuffer


class BasicState(val coordY: Int, val coordX: Int, protected var actions: Seq[Action]) extends State { // the only type of state in this project (the label [String] is the identifier of a BasicState)

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

	override def getLabel: String = label

	override def getCoord: (Int, Int) = (coordY, coordY)

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
