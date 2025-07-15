package Utils

import breeze.linalg._
import breeze.plot._

import Models.Result

object PlottingUtils {
  def drawTop15StationsHistogram(data: List[Result], title: String, xlable: String): Unit =
    val f = Figure()
    val p = f.subplot(0)

    p.title = title
    p.xlabel = xlable

    val sortedData = data.sortBy(_.station.name).map(_.passengers)

    val range     = sortedData.max - sortedData.min
    val numOfRows = sortedData.length
    val std       = standardDeviation(sortedData)

    val optimalBinNum = math.ceil(range * ((math.pow(numOfRows, 0.3)) / (3.49 * std))).toInt

    p += hist(sortedData, bins = optimalBinNum)

    f.saveas("./output/histogram.pdf")

  private def standardDeviation(data: List[Int]): Double =
    val length = data.length
    val sum    = data.sum

    val mean      = sum / length
    val squareSum = data.map(x => math.pow((x - mean), 2)).sum

    math.sqrt(squareSum / (length - 1))
}
