package Main

import scala.xml._
import os.Path
import javax.xml.validation.SchemaFactory
import javax.xml.XMLConstants
import java.io.File
import javax.xml.transform.stream.StreamSource
import Models.*
import Utils.{XmlUtils, OutputUtils}
import Extensions.IndexedSeqExtensions.{getXsdOrExit, getXmlsOrExit}
import os.stat

@main
def main(): Unit =
  OutputUtils.clearOutputDir()
  OutputUtils.clearErrorsDir()

  val xmlDir = os.list(os.pwd / "src/main/scala/xmls")
  val xsdDir = os.list(os.pwd / "src/main/scala/xsd")

  val stationXsdFile = xsdDir.getXsdOrExit("station")
  val trainXsdFile   = xsdDir.getXsdOrExit("train")
  val tripXsdFile    = xsdDir.getXsdOrExit("trip")

  val stationXmlFiles = xmlDir.getXmlsOrExit("stations", stationXsdFile)
  val trainsXmlFiles  = xmlDir.getXmlsOrExit("trains", trainXsdFile)
  val tripsXmlFiles   = xmlDir.getXmlsOrExit("trips", tripXsdFile)

  val allStations        = Stations(stationXmlFiles)
  val allTrains          = Trains(trainsXmlFiles)
  val (errors, allTrips) = Trips(tripsXmlFiles, allTrains, allStations)

  if errors.nonEmpty then OutputUtils.writeErrors("trips-parsing-errors.txt", errors)

  val top15Stations = allStations.getTop15Stations()

  OutputUtils.writeResults(top15Stations, "top15stations.txt")
  OutputUtils.writeResults(top15Stations, "top15stations.json")
