package examples.maze
import environment.Environment
import environment.maze.{GridBuilder, MazeGridBuilder}

object MASMaze8x8 extends MazeDirector {

	override def construct(rewardBonus: Int = MazeGridBuilder.WEAK_REWARD): Environment = {
		val builder: GridBuilder = new MazeGridBuilder(8, 8, rewardBonus)

		builder
			.setWall(1, 2, 1, 3)
			.setWall(2, 2, 2, 3)
			.setWall(2, 1, 3, 1)
			.setWall(2, 3, 3, 3)
			.setWall(4, 1, 5, 1)
			.setWall(4, 2, 5, 2)
			.setWall(4, 3, 5, 3)
			.setWall(5, 4, 6, 4)
			.setWall(5, 5, 6, 5)
			.setWall(5, 6, 6, 6)

    		//.setWall(2, 2, 3, 2) // this wall changes the best path

			.setPosReward(0, 2)
			.setPosReward(0, 3)
			.setPosReward(0, 4)
			.setPosReward(1, 3)
			.setPosReward(1, 5)
			.setPosReward(1, 6)
			.setPosReward(1, 7)
			.setPosReward(2, 6)
    		.setPosReward(4, 2)
			.setPosReward(4, 3)
			.setPosReward(5, 3)
			.setPosReward(6, 3)
			.setPosReward(6, 4)
			.setPosReward(7, 4)

			.setNegReward(1, 4)
			.setNegReward(2, 0)
			.setNegReward(2, 1)
			.setNegReward(2, 2)
			.setNegReward(2, 4)
			.setNegReward(2, 5)
			.setNegReward(3, 0)
			.setNegReward(3, 4)
			.setNegReward(5, 2)
			.setNegReward(5, 6)
			.setNegReward(5, 7)
			.setNegReward(6, 2)
			.setNegReward(7, 2)

			.setGoalState(0, 0)
			.setStartingState(7, 7)

			.build()
	}

	override def showMaze(): Unit = ???

}
