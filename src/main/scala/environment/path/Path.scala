package environment.path

import environment.action.BasicAction
import environment.state.{OneActionState, State}

import scala.collection.mutable.ArrayBuffer

class Path(val startingSize: Int = 0) extends Iterable[State] { // this class stores a path and provides easy methods to build a path or iterate a path

	private var statesArray: ArrayBuffer[State] = _

	if (startingSize > 0)
		statesArray = new ArrayBuffer[State](startingSize + 1) // plus 1 because there are one more elements than steps
	else
		statesArray = new ArrayBuffer[State]()

	def this(pathLabels: PathLabels) {
		this(pathLabels.numberOfStates)

		val pathLabelsIterator = pathLabels.iterator
		var fromLabel = pathLabelsIterator.next()

		var currState = new OneActionState(fromLabel)
		statesArray += currState

		for (label <- pathLabelsIterator) {
			val newState = new OneActionState(label)

			currState.setAction(Some(new BasicAction(newState)))

			currState = newState
			fromLabel = label
		}
	}

	def this(oldPath: Path) {
		this(oldPath.numberOfStates)
		oldPath.foreach(s => this.statesArray += s)
	}

	def ->(state: State): Path = {
		statesArray += state
		this
	}

	def -->(states: Iterable[State]): Path = {
		statesArray ++= states
		this
	}

	def -->(otherPath: Path): Path = {
		statesArray ++= otherPath
		this
	}

	def -=(state: State): Unit = statesArray -= state

	def numberOfSteps: Int = statesArray.length - 1

	def numberOfStates: Int = statesArray.length

	override def iterator: Iterator[State] = statesArray.iterator

	override def toString: String = {
		val strPath: StringBuilder = new StringBuilder()
		var i = 0
		val steps = numberOfSteps
		for (state <- this) {
			i += 1
			strPath ++= state.getLabel
			if (i <= steps)
				strPath ++= " -> "
		}
		strPath.toString
	}
}
