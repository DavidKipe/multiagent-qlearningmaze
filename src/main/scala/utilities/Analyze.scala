package utilities

import environment.action.Action
import environment.path.{Path, PathLabels}
import environment.state.State
import environment.{Environment, EnvironmentPiece}
import exception.NoSuchPathFound
import learning.QMatrix
import policy.BestDeterministic

object Analyze { // this static methods analyze the paths to printing information about the process

	// best path for an entire env or for last piece
	def getBestPath(qMatrix: QMatrix, maze: Environment): Path = getBestPathPred(qMatrix, maze.getStartingState, s => maze.isGoal(s))

	// best path for an env piece
	def getBestPathForEnvPiece(qMatrix: QMatrix, mazePiece: EnvironmentPiece): Path = getBestPathPred(qMatrix, mazePiece.getStartingState, s => !mazePiece.isPartOfThisEnvPiece(s))

	private def getBestPathPred(qMatrix: QMatrix, startingState: State, isGoal: State => Boolean): Path = { // calculate the best deterministic path
		val policy = new BestDeterministic()
		val path = new Path()

		var currState: State = startingState
		path -> currState

		do {
			println(currState)
			val selected_a: Action = policy.nextAction(currState, qMatrix)
			currState = selected_a.act.newState

			path -> currState
		} while (!isGoal(currState))

		path
	}

	def printBestPathStats(qMatrix: QMatrix, maze: Environment): Unit = { // given a maze it follows the best path using the best deterministic policy
		val bestPathIterator = getBestPath(qMatrix, maze).iterator

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

		val pathIterator = path.intPairIterator
		var fromCoord = pathIterator.next()

		for (toCoords <- pathIterator) {
			val opt_q = qMatrix.getByCoords(fromCoord, toCoords)
			if (opt_q.isEmpty)
				throw new NoSuchPathFound(fromCoord, toCoords, "No Q-value found for such action in the given Q-matrix")

			val q = opt_q.get
			qSum += q

			fromCoord = toCoords
			print(f" -> $q%.2f")
		}
		println()

		val nStep = path.numberOfSteps
		println("Number of steps: " + nStep)
		println("Average of reward bonus: " + (qSum / nStep))
	}

}
