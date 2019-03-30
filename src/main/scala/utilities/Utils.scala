package utilities

object Utils {

	def coordsToLabel(coordY: Int, coordX: Int): String = "(" + coordY + "," + coordX + ")"

	def coordsToLabel(coords: (Int, Int)): String = coordsToLabel(coords._1, coords._2)

}
