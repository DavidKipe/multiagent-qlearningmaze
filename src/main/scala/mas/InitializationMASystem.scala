package mas {

	import environment.Environment
	import environment.maze.Maze
	import environment.state.State

	object InitializationMASystem {

		def splitEnvironment(environment: Environment, horizontalPiecesNumber: Int, verticalPiecesNumber: Int): Seq[Seq[Environment]] = {
			val (envHeight, envWidth): (Int, Int) = environment.gridSize

			if (envWidth % horizontalPiecesNumber != 0 || envHeight % verticalPiecesNumber != 0)
				throw new IllegalArgumentException

			val pieceWidth: Int = envWidth / horizontalPiecesNumber
			val pieceHeight: Int = envHeight / verticalPiecesNumber

			val envGrid = environment.getGrid

			val horizontalPieces = envGrid.sliding(pieceHeight, pieceHeight) // split the grid horizontally

			for (horizontalStripe <- horizontalPieces) { // for each horizontal stripe
				// split each array (of this h. stripe) into parts of pieceWidth size
				val splitting: Array[Array[Array[State]]] = Array.ofDim(pieceHeight, horizontalPiecesNumber, pieceWidth)
				for (i <- 0 until pieceHeight) {
					val parts = horizontalStripe(i).sliding(pieceWidth, pieceWidth)
					var k = 0
					for (arrayPart <- parts) {
						splitting(i)(k) = arrayPart
						k += 1
					}
				}

				// gather all part into the new grids
				for (i <- 0 until horizontalPiecesNumber) {
					val newEnvGrid: Array[Array[State]] = Array.ofDim(pieceHeight, pieceWidth)
					for (j <- 0 until pieceHeight)
						newEnvGrid(j) = splitting(j)(i)
					println(new Maze(newEnvGrid, null, null))
				}
			}

			null
		}

	}

}
