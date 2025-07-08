package Models

import os.Path
import scala.xml.XML
import scala.xml.Node
import os.stat

class Trip(
    val id: String,
    val train: Train,
    val stations: List[Station]
):
  override def toString(): String =
    s"TripID: $id, TrainID:${train.id}, Stations:${stations.map(_.id).mkString("[", ",", "]")}"

class Trips(private val trips: List[Trip])

/** Parses and returns trips from given xml file paths
  *
  * Will report if any trips are missing stations or a train at output/trips-parsing-errors.txt
  *
  * @param xmlPaths
  *   the filepath of the xml file
  * @param allTrains
  *   Trains class that contains all of the trains
  * @param allStations
  *   Stations class that contains all of the stations
  * @return
  *   class Trips that contains a list of trips
  */
def parseTripsFromXmlPaths(
    xmlPaths: IndexedSeq[Path],
    allTrains: Trains,
    allStations: Stations
): Trips =
  val errorsPath = os.pwd / "output" / "trips-parsing-errors.txt"
  if os.exists(errorsPath) then os.remove(errorsPath)

  Trips(
    xmlPaths
      .map(path =>
        val xmlFile  = XML.loadFile(path.toString)
        val tripXmls = (xmlFile \\ "trip")

        tripXmls
          .map(tripXml =>
            try
              parseTripFromXml(tripXml, allTrains, allStations, path.last)
            catch
              ex => os.write.append(errorsPath, ex.getMessage() + "\n")
          )
          .map({
            case trip: Trip => Option(trip)
            case unit: Unit => None
          })
          .flatten // flatten to get rid of the None
      )
      .toList
      .flatten
  )

/** Parses and returns a trip from an xml file
  *
  * Will throw error if the train for the trip was not found. Will throw error if a stations for the
  * trip was not found.
  *
  * @param tripXml
  *   The Node from which to parse the trip data
  * @param allTrains
  *   Trains class that contains all of the trains
  * @param allStations
  *   Stations class that contains all of the Stations
  * @param xmlFileName
  *   The file name from which the xml is comming from use for error printing
  * @return
  *   A trip
  */
def parseTripFromXml(
    tripXml: Node,
    allTrains: Trains,
    allStations: Stations,
    xmlFileName: String
): Trip =
  val id        = (tripXml \ "id")       // Trip id
  val version   = (tripXml \ "@version") // Trip version
  val tripTrain =
    allTrains.getTrain((tripXml \ "train")(0).text, version.text.toInt) match
      case None        => throw new Error(s"trip in file ${xmlFileName} has missing train")
      case Some(value) => value

  val tripStations =
    (tripXml \\ "station") map (sId => allStations.getStation(sId.text, version.text.toInt)) map {
      case None        => throw new Error(s"trip in file ${xmlFileName} has missing stations")
      case Some(value) => value
    }

  // For the stations we add the trip train seats and the train that goes to that station
  for station <- tripStations do 
    station.passengers += tripTrain.seats
    station.appendTrain(tripTrain)

  // create a trip if a train was found and there are no missing stations
  Trip(id.text, tripTrain, tripStations.toList)
