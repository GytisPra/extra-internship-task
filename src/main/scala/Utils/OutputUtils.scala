package Utils

import os.Path
import java.io.File
import Models.Station

object OutputUtils {
  private val outputDir = File((os.pwd / "output").toString)
  private val errorsDir = File((os.pwd / "errors").toString)

  private val outputDirPath = Path(outputDir.getPath())
  private val errorsDirPath = Path(errorsDir.getPath())

  def writeAppendErrors(fileName: String, error: String): Unit =
    os.write.append(errorsDirPath / fileName, error)

  def writeErrors(fileName: String, error: String): Unit =
    val path = errorsDirPath / fileName

    if os.exists(path) then os.remove(path)

    os.write(path, error)

  def clearOutputDir(): Unit =
    deleteRecursively(outputDir)
    if !os.exists(outputDirPath) then os.makeDir(outputDirPath)

  def clearErrorsDir(): Unit =
    deleteRecursively(errorsDir)
    if !os.exists(errorsDirPath) then os.makeDir(errorsDirPath)

  private def deleteRecursively(file: File): Unit =
    if file.isDirectory then file.listFiles.foreach(deleteRecursively)
    else if file.exists && !file.delete then
      throw new Exception(s"Unable to delete ${file.getAbsolutePath}")

  def writeResults(results: List[Station], fileName: String): Unit =
    val path = outputDirPath / fileName
    if os.exists(path) then os.remove(path)

    path.ext match {
      case "json" =>
        os.write(path, upickle.default.write[List[Station]](results))
      case "txt"  =>
        results.foreach(station => os.write.append(path, station.toResultsString() + "\n"))
      case other  =>
        writeErrors("results-writing-error.txt", s"Unknown filename extension: ${path.last}")
    }
}
