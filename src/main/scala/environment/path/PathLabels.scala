package environment.path

import utilities.Utils

import scala.collection.mutable.ArrayBuffer

class PathLabels(val startingSize: Int = 0) extends Iterable[String] { // this class stores a path labels and provides easy methods to build a path labels or iterate it

	private var labelArray: ArrayBuffer[(Int, Int)] = _

	if (startingSize > 0)
		labelArray = new ArrayBuffer[(Int, Int)](startingSize + 1)
	else
		labelArray = new ArrayBuffer[(Int, Int)]

	def ->(i: Int, j: Int): PathLabels = {
		labelArray += ((i, j))
		this
	}

	def numberOfSteps: Int = labelArray.length - 1

	def numberOfStates: Int = labelArray.length

	def intPairIterator: Iterator[(Int, Int)] = labelArray.iterator

	override def iterator: Iterator[String] = labelArray.map(Utils.coordsToLabel).iterator

	override def toString: String = {
		val strPath: StringBuilder = new StringBuilder()
		var i = 0
		val steps = numberOfSteps
		for (label <- this) {
			i += 1
			strPath ++= label
			if (i <= steps)
				strPath ++= " -> "
		}
		strPath.toString
	}

}
