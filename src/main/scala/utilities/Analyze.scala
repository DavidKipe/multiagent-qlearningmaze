package utilities

import environment.Environment
import environment.action.Action
import environment.path.{Path, PathLabels}
import environment.state.State
import exception.NoSuchPathFound
import learning.QMatrix
import policy.BestDeterministic

object Analyze { // this static methods analyze the paths to printing information about the process

	def getBestPathFrom(qMatrix: QMatrix, maze: Environment, startingState: State): Path = {
		val policy = new BestDeterministic()
		val path = new Path()

		var currState: State = startingState

		path -> currState

		do {
			val selected_a: Action = policy.nextAction(currState, qMatrix)
			currState = selected_a.act.newState

			path -> currState
		} while (!maze.isGoal(currState))

		path
	}

	def printBestPathStats(qMatrix: QMatrix, maze: Environment): Unit = { // given a maze it follows the best path using the best deterministic policy
		val bestPathIterator = getBestPathFrom(qMatrix, maze, maze.getStartingState).iterator

		var currState: State = bestPathIterator.next()
		var oldState: State = currState

		var nStep = 0
		var qSum = 0.0

		print("Best path: ")
		do {
			print(currState + " -> ")

			oldState = currState
			currState = bestPathIterator.next()

			val opt_q = qMatrix.get(oldState, currState)
			if (opt_q.isEmpty)
				throw new NoSuchPathFound((oldState.getLabel, currState.getLabel), "No Q-value found for such action in the given Q-matrix")

			qSum += opt_q.get

			nStep += 1
		} while (!maze.isGoal(currState))
		println(currState)

		val (x, y) = maze.gridSize
		println("Minimum number of steps: " + (x + y - 2))
		println("Number of steps: " + nStep)
		println("Average of reward bonus: " + (qSum / nStep))
	}

	def printPathStats(qMatrix: QMatrix, path: PathLabels): Unit = { // simply follows the path given on the q-matrix (no maze is )
		var qSum = 0.0

		println("Path: " + path)

		val pathIterator = path.iterator
		var fromLabel = pathIterator.next()

		for (label <- pathIterator) {
			val opt_q = qMatrix.getByLabel(fromLabel, label)
			if (opt_q.isEmpty)
				throw new NoSuchPathFound((fromLabel, label), "No Q-value found for such action in the given Q-matrix")

			val q = opt_q.get
			qSum += q

			fromLabel = label
			print(f" -> $q%.2f")
		}
		println()

		val nStep = path.numberOfSteps
		println("Number of steps: " + nStep)
		println("Average of reward bonus: " + (qSum / nStep))
	}

}
