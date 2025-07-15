package Main

import os.Path
import java.io.File

import Models.{Stations, Trains, Trips}
import Utils.{XmlUtils, OutputUtils, PlottingUtils}
import Extensions.IndexedSeqExtensions.{getXsdOrExit, getXmlsOrExit}

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
  val trainXmlFiles   = xmlDir.getXmlsOrExit("trains", trainXsdFile)
  val tripXmlFiles    = xmlDir.getXmlsOrExit("trips", tripXsdFile)

  val allStations        = Stations(stationXmlFiles)
  val allTrains          = Trains(trainXmlFiles)
  val (errors, allTrips) = Trips(tripXmlFiles, allTrains, allStations)

  if errors.nonEmpty then OutputUtils.writeErrors("trips-parsing-errors.txt", errors)

  val top15Stations = allTrips.getTop15Stations()

  OutputUtils.writeResults(top15Stations, "top15stations.txt")
  OutputUtils.writeResults(top15Stations, "top15stations.json")

  PlottingUtils.drawTop15StationsHistogram(
    data = top15Stations,
    title = "Top 15 stations distribution graph (sorted by name)",
    xlable = "Passengers Count"
  )
