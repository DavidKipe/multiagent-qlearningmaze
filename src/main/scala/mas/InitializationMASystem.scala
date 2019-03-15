package mas

import environment.maze.MazePiece
import environment.state.State
import environment.{Environment, EnvironmentPiece}

object InitializationMASystem {

	/*
		limitations:
		- the original environment size must be divisible by the number of horizontal and vertical pieces
		- the assumption is that start stat is on the down right state, and the goal state is on the top left state,
		according to that the goal states of each piece are set
	 */
	def splitEnvironment(environment: Environment, numberOfHorizontalPieces: Int, numberOfVerticalPieces: Int): Array[Array[EnvironmentPiece]] = {
		val envGrid = environment.getGrid // get the grid of states
		val (envHeight, envWidth): (Int, Int) = environment.gridSize // get the grid size

		if (envWidth % numberOfHorizontalPieces != 0 || envHeight % numberOfVerticalPieces != 0) // check if splitting is perfectly divisible
			throw new IllegalArgumentException

		val pieceWidth: Int = envWidth / numberOfHorizontalPieces // width of a single piece
		val pieceHeight: Int = envHeight / numberOfVerticalPieces // height of a single piece

		val matrixOfEnvPieces: Array[Array[EnvironmentPiece]] = Array.ofDim(numberOfVerticalPieces, numberOfHorizontalPieces) // init output matrix of environment pieces

		val horizontalPieces = envGrid.sliding(pieceHeight, pieceHeight) // split the grid horizontally in 'numberOfVerticalPieces' parts

		var currHStripeInd: Int = 0
		for (horizontalStripe <- horizontalPieces) { // for each horizontal stripe, split each array into parts of pieceWidth size
			val tmpSplitting: Array[Array[Array[State]]] = Array.ofDim(pieceHeight, numberOfHorizontalPieces, pieceWidth) // this stores all the single array parts of this stripe
			for (i <- 0 until pieceHeight) {
				val hParts = horizontalStripe(i).sliding(pieceWidth, pieceWidth) // split the horizontal array into 'numberOfHorizontalPieces' parts
				var j = 0
				for (hPart <- hParts) { // this needs to convert Iterator[Array] <hParts> to Array[Array[State]] <tmpSplitting(i)>
					tmpSplitting(i)(j) = hPart
					j += 1
				}
			}

			// gather all the parts into the new grid pieces
			for (i <- 0 until numberOfHorizontalPieces) { // there are 'numberOfHorizontalPieces' grid pieces inside this stripe
				val newEnvGrid: Array[Array[State]] = Array.ofDim(pieceHeight, pieceWidth) // init the new grid
				for (j <- 0 until pieceHeight) // collect all the horizontal array parts (i -> i-th grid piece; j -> j-th horizontal array)
					newEnvGrid(j) = tmpSplitting(j)(i)

				//val goalStates: Set[State] = createGoalStatesSet(newEnvGrid, currHStripeInd, i)
				matrixOfEnvPieces(currHStripeInd)(i) = new MazePiece(newEnvGrid, currHStripeInd, i) // create and set the new piece of maze

				if (currHStripeInd == numberOfVerticalPieces-1 && i == numberOfHorizontalPieces-1) { // set the orig starting state in this piece
					val startingMazePiece = matrixOfEnvPieces(currHStripeInd)(i).asInstanceOf[MazePiece]
					val (dimX, dimY) = startingMazePiece.gridSize
					startingMazePiece.setStartingState(startingMazePiece.getGrid(dimX-1)(dimY-1))
				}
			}

			currHStripeInd += 1
		}

		matrixOfEnvPieces
	}

	/*private def createGoalStatesSet(grid: Array[Array[State]], posY: Int, posX: Int): Set[State] = { // calculation of goalStates
		if (posX == 0 && posY == 0) // for the ending piece it is the original goal state
			return Set[State](grid(0)(0))

		val goalStatesSet: mutable.Set[State] = new mutable.HashSet[State] // init the set

		val gridHorizIterator = grid.iterator // iterator over the horizontal lines
		val currHorizArray = gridHorizIterator.next()

		if (posY != 0) // if the position of this environment piece is not on the top edge of the entire environment
			goalStatesSet ++= currHorizArray // set as final state the entire first line
		else
			goalStatesSet += currHorizArray(0) // otherwise only the first one will be a final state

		if (posX != 0) // if the position of this environment piece is not on the left edge of the entire environment
			for (currHorizArray <- gridHorizIterator) // set the first state of each line
					goalStatesSet += currHorizArray(0)

		Set.empty[State] ++ goalStatesSet
	}*/

}

