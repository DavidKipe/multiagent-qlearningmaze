package exception

import utilities.Utils

class NoSuchPathFound(protected val stepNotFound: (String, String), protected val extraInfo: String = "", override protected val cause: Throwable = None.orNull)
	extends MazeException(NoSuchPathFound.getMessage(stepNotFound._1, stepNotFound._2, extraInfo), cause) {

	def this(fromCoords: (Int, Int), toCoords: (Int, Int), extraInfo: String) {
		this((Utils.coordsToLabel(fromCoords), Utils.coordsToLabel(toCoords)), extraInfo)
	}

}

object NoSuchPathFound {

	protected def getMessage(fromLabel: String, toLabel: String, extraInfo: String): String = {
		val msg = new StringBuilder("Error occurred when analyzing path: Step '")
		msg.append(fromLabel).append(" -> ").append(toLabel).append("' not found")
		if (extraInfo.nonEmpty)
			msg.append(" - Extra info: ").append(extraInfo)
		msg.toString()
	}

}
