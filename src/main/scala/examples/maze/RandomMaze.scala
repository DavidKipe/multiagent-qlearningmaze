package examples.maze
import environment.Environment
import environment.maze.MazeGridBuilder

import scala.util.Random

class RandomMaze(val height: Int, val width: Int, val posRewrdProp: Double, val negRewrdProp: Double) extends MazeDirector {

	require(posRewrdProp >= 0.0 && negRewrdProp >= 0.0 && posRewrdProp <= 1.0 && negRewrdProp <= 1.0, "Reward probability must be between 0.0 and 1.0")
	require(posRewrdProp + negRewrdProp <= 1.0, "Positive and negative probabilities can not exceed 1.0")

	val random = new Random

	override def construct(rewardBonus: Int = MazeGridBuilder.VERY_WEAK_REWARD): Environment = {
		val builder = new MazeGridBuilder(height, width, rewardBonus)

		for (i <- 0 until height; j <- 0 until width) {
			val randomValue = random.nextDouble()

			if (j == 0)
				print(i + " ")

			if (i == 0 && j == 0) {
				builder.setGoalState(i, j)
				print("@")
			}
			else if (i == height - 1 && j == width - 1) {
				builder.setStartingState(i, j)
				print("#")
			}
			else if (randomValue <= posRewrdProp) {
				builder.setPosReward(i, j)
				print("+")
			}
			else if (randomValue <= posRewrdProp + negRewrdProp) {
				builder.setNegReward(i, j)
				print("-")
			}
			else
				print("·")

			if (j == width - 1)
				println()
		}

		builder.build()
	}

	override def showMaze(): Unit = ???

}
