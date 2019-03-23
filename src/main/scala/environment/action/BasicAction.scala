package environment.action

import environment.Transition
import environment.state.State

class BasicAction(protected val toState: State, protected var reward: Int) extends Action { // the only type of action in this project (the toState and reward values are the identifier of a BasicAction)

	require(toState != null, "An Action require a target State")

	var label: String = createLabel()

	def this(toState: State) = this(toState, 0)

	private def createLabel(): String = "-> " + toState.getLabel + "[" + reward + "]"

	override private[environment] def setReward(reward: Int): Unit = { this.reward = reward; label = createLabel() }

	override def getLabel: String = label

	override def act: Transition = Transition(toState, reward)

	override def toString: String = label


	def canEqual(other: Any): Boolean = other.isInstanceOf[BasicAction]

	override def equals(other: Any): Boolean = other match {
		case that: BasicAction =>
			(that canEqual this) &&
				toState == that.toState &&
				reward == that.reward
		case _ => false
	}

	override def hashCode(): Int = {
		val state = Seq(toState, reward)
		state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
	}
}
