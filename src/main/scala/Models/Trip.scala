package Models

import os.Path
import scala.xml.XML
import scala.xml.Node
import java.io.File

import Models.Result

class Trip private (
    val id: String,
    val train: Train,
    val stations: List[Station]
):
  override def toString(): String =
    s"TripID: $id, TrainID:${train.id}, Stations:${stations.map(_.id).mkString("[", ",", "]")}"

object Trip {
  def apply(
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
        else Right(new Trip(id, tripTrain, tripStations.toList))
}

class Trips private (private val trips: List[Trip]):
  def getStationTrains(station: Station): List[Train] =
    for
      trip <- trips
      if trip.stations.exists(tripStation => tripStation.id == station.id && tripStation.version == station.version)
    yield (trip.train)

object Trips {
  def apply(
      xmlFiles: IndexedSeq[File],
      allTrains: Trains,
      allStations: Stations
  ): (String, Trips) =
    val results = xmlFiles.flatMap(xmlFile =>
      val xml      = XML.loadFile(xmlFile)
      val tripXmls = (xml \\ "trip")

      tripXmls.map(tripXml => Trip(tripXml, allTrains, allStations, xmlFile.getName()))
    )

    val (errors, trips) = results.partitionMap(identity)

    (errors.mkString, new Trips(trips.toList))
}
