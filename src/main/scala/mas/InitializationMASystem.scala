package mas {

	import environment.Environment
	import environment.maze.{Maze, MazePiece}
	import environment.state.State

	object InitializationMASystem {

		def splitEnvironment(environment: Environment, numberOfHorizontalPieces: Int, numberOfVerticalPieces: Int): Array[Array[Environment]] = {
			val envGrid = environment.getGrid // get the grid of states
			val (envHeight, envWidth): (Int, Int) = environment.gridSize // get the grid size

			if (envWidth % numberOfHorizontalPieces != 0 || envHeight % numberOfVerticalPieces != 0) // check if splitting is perfectly divisible
				throw new IllegalArgumentException

			val pieceWidth: Int = envWidth / numberOfHorizontalPieces // width of a single piece
			val pieceHeight: Int = envHeight / numberOfVerticalPieces // height of a single piece

			val matrixOfEnvPieces: Array[Array[Environment]] = Array.ofDim(numberOfVerticalPieces, numberOfHorizontalPieces) // init output matrix of environment pieces

			val horizontalPieces = envGrid.sliding(pieceHeight, pieceHeight) // split the grid horizontally in 'numberOfVerticalPieces' parts

			var currHorizIndex: Int = 0
			for (horizontalStripe <- horizontalPieces) { // for each horizontal stripe, split each array into parts of pieceWidth size
				val tmpSplitting: Array[Array[Array[State]]] = Array.ofDim(pieceHeight, numberOfHorizontalPieces, pieceWidth) // this store all the single array parts of this stripe
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
					println(new Maze(newEnvGrid, null, null))
					matrixOfEnvPieces(currHorizIndex)(i) = new MazePiece(newEnvGrid, null) // TODO calculation of goalStates
				}

				currHorizIndex += 1
			}

			matrixOfEnvPieces
		}

	}

}
