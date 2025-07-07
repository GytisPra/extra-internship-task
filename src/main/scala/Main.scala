import scala.xml._
import os.Path
import javax.xml.validation.SchemaFactory
import javax.xml.XMLConstants
import java.io.File
import javax.xml.transform.stream.StreamSource
import classes.Station
import classes.Train
import classes.Trip

@main
def main(): Unit =
  val xmlPaths = os.list(os.pwd / "src/main/xmls")
  val xsdPaths = os.list(os.pwd / "src/main/xsd")

  val stationXsdPath = xsdPaths.find(_.last == "station.xsd") match {
    case None        => throw Error("stations xsd file not found")
    case Some(value) => value
  }
  val trainXsdPath   = xsdPaths.find(_.last == "train.xsd") match {
    case None        => throw Error("train xsd file not found")
    case Some(value) => value
  }
  val tripXsdPath    = xsdPaths.find(_.last == "trip.xsd") match {
    case None        => throw Error("trip xsd file not found")
    case Some(value) => value
  }

  val stationXmlPaths = for
    path <- xmlPaths
    if path.ext == "xml" && path.last.contains("stations")
    if validate(
      new File(path.toString),
      new File(stationXsdPath.toString)
    )
  yield (path)

  val trainsXmlPaths = for
    path <- xmlPaths
    if path.ext == "xml" && path.last.contains("trains")
    if validate(
      new File(path.toString),
      new File(trainXsdPath.toString)
    )
  yield (path)

  val tripsXmlPaths = for
    path <- xmlPaths
    if path.ext == "xml" && path.last.contains("trips")
    if validate(
      new File(path.toString),
      new File(tripXsdPath.toString)
    )
  yield (path)

  val stations = stationXmlPaths
    .map(path =>
      val xmlFile = XML.loadFile(path.toString)

      (xmlFile \ "station" \\ "name") zip (xmlFile \ "station" \\ "id") zip (xmlFile \ "station" \\ "@version") map {
        case ((name, id), version) =>
          Station(name.text, id.text, version.text.toInt)
      }
    )
    .flatten

  val trains = trainsXmlPaths
    .map(path =>
      val xmlFile = XML.loadFile(path.toString)

      (xmlFile \ "train" \\ "seats") zip (xmlFile \ "train" \\ "id") zip (xmlFile \ "train" \\ "@version") map {
        case ((seats, id), version) =>
          Train(id.text, seats.text.toInt, version.text.toInt)
      }
    )
    .flatten


  /* TODO: 
   * First read the trainIds then find the trains (there can be a scenarion where a non-existant train is assigned to a trip)
   * Then get stationIds find the stations associated with the id (some station ids can be to non-existant stations) 
   */
  val trips = tripsXmlPaths
    .map(path =>
      val xmlFile  = XML.loadFile(path.toString)
      val trainIds = (xmlFile \\ "train")
      val tripTrains = trainIds.map(tId => 
        val trainId = tId.text
        trains.find(_.id == trainId)
      )
    )

def validate(xmlFile: File, xsdFile: File): Boolean =
  val schemaFactory =
    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
  val schema        = schemaFactory.newSchema(xsdFile)

  val validator = schema.newValidator()

  try
    validator.validate(new StreamSource(xmlFile))
    true
  catch
    case pe: org.xml.sax.SAXParseException =>
      println(s"SAXParseException at line: ${pe
          .getLineNumber()} in file: ${xmlFile.getName()}: ${pe.getMessage()}")
      false
    case ex                                =>
      throw ex
