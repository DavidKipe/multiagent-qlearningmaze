package utilities

object Utils {

	def coordsToLabel(coordY: Int, coordX: Int): String = "(" + coordY + "," + coordX + ")"

	def coordsToLabel(coords: (Int, Int)): String = coordsToLabel(coords._1, coords._2)

	def labelToCoords(label: String): (Int, Int) = {
		val arrayCoords = label.substring(1, label.length-1).split(',')
		(arrayCoords(0).toInt, arrayCoords(1).toInt)
	}

}
