package Models

import os.Path
import scala.xml.XML
import scala.xml.Node
import os.stat

import Utils.OutputUtils
import java.io.File

class Trip(
    val id: String,
    val train: Train,
    val stations: List[Station]
):
  override def toString(): String =
    s"TripID: $id, TrainID:${train.id}, Stations:${stations.map(_.id).mkString("[", ",", "]")}"

class Trips private (private val trips: List[Trip])

object Trips {
  def apply(
    xmlFiles: IndexedSeq[File],
    allTrains: Trains,
    allStations: Stations
  ): (String, Trips) =
    val results = xmlFiles
      .map(xmlFile =>
        val xml  = XML.loadFile(xmlFile)
        val tripXmls = (xml \\ "trip")

        tripXmls
          .map(tripXml => parseTripFromXml(tripXml, allTrains, allStations, xmlFile.getName()))
      )
      .flatten

    val (errors, trips) = results.partitionMap(identity)

    (errors.mkString, new Trips(trips.toList))
}


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
): Either[String, Trip] =
  val id      = (tripXml \ "id").text             // Trip id
  val version = (tripXml \ "@version").text.toInt // Trip version
  val trainId = (tripXml \ "train").text          // Train id

  allTrains.getTrain(trainId, version) match
    case Left(error)      => Left(error + s" for trip with ID: $id in file ${xmlFileName}\n")
    case Right(tripTrain) =>
      val tripStationsResults =
        (tripXml \\ "station").map(sId => allStations.getStation(sId.text, version)) map {
          case Left(error) => Left(error + s" for trip with ID: $id in file ${xmlFileName}\n")
          case Right(trip) => Right(trip)
        }

      val (errors, tripStations) = tripStationsResults.partitionMap(identity)

      if errors.nonEmpty then Left(errors.mkString)
      else
        tripStations.foreach(station =>
          station.passengers += tripTrain.seats
          station.appendTrain(tripTrain)
        )

        Right(Trip(id, tripTrain, tripStations.toList))
