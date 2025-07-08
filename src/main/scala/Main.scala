import scala.xml._
import os.Path
import javax.xml.validation.SchemaFactory
import javax.xml.XMLConstants
import java.io.File
import javax.xml.transform.stream.StreamSource
import Models.*

// TODO: 3-4 tasks

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
