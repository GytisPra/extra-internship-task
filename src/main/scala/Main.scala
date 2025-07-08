import scala.xml._
import os.Path
import javax.xml.validation.SchemaFactory
import javax.xml.XMLConstants
import java.io.File
import javax.xml.transform.stream.StreamSource
import Models.*

@main
def main(): Unit =
  val xmlFolder = os.list(os.pwd / "src/main/xmls")
  val xsdFolder = os.list(os.pwd / "src/main/xsd")

  val stationXsdFile = XmlUtils.findXsdFile("station", xsdFolder)
  val trainXsdFile   = XmlUtils.findXsdFile("train", xsdFolder)
  val tripXsdFile    = XmlUtils.findXsdFile("trip", xsdFolder)

  val stationXmlPaths = XmlUtils.getXmlPaths("stations", xmlFolder, stationXsdFile)
  val trainsXmlPaths  = XmlUtils.getXmlPaths("trains", xmlFolder, trainXsdFile)
  val tripsXmlPaths   = XmlUtils.getXmlPaths("trips", xmlFolder, tripXsdFile)

  val allStations = parseStationsFromXmlPaths(stationXmlPaths)
  val allTrains   = parseTrainsFromXmlPaths(trainsXmlPaths)
  val allTrips    = parseTripsFromXmlPaths(tripsXmlPaths, allTrains, allStations)

  val top15Stations   = allStations.getTop15Stations()
  val resultsPathTxt  = os.pwd / "output" / "top15stations.txt"
  val resultsPathJson = os.pwd / "output" / "top15stations.json"

  if os.exists(resultsPathTxt) then os.remove(resultsPathTxt)

  if os.exists(resultsPathJson) then os.remove(resultsPathJson)

  top15Stations.foreach(station =>
    os.write.append(resultsPathTxt, station.toResultsString() + "\n")
  )
  os.write.append(resultsPathJson, upickle.default.write[List[Station]](top15Stations))
