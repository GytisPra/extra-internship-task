package Models

import java.io.File
import os.Path
import scala.xml.{XML, Node}
import scala.compiletime.ops.int
import Models.Train

case class Station(
    val id: String,
    val name: String,
    val version: Int
):
  override def toString(): String =
    s"ID:$id, Name:$name, Version:$version"

object Station {
  def apply(xml: Node): Either[String, Station] =
    val id      = (xml \ "id").text
    val name    = (xml \ "name").text
    val version = (xml \ "@version").text

    if version.isBlank then Left(s"Station has a missing version attribute.")
    else if id.isBlank then Left(s"Station has a blank id.")
    else if name.isBlank then Left(s"Station has a blank name.")
    else Right(Station(id, name, version.toInt))
}

class Stations private (private val stations: List[Station]):
  def getStation(stationId: String, version: Int): Either[String, Station] =
    stations.find(station => station.id == stationId && station.version == version) match {
      case None        => Left(s"station (ID: $stationId; version: $version) not found")
      case Some(value) => Right(value)
    }

  def getTop15Stations(trips: Trips): List[Result] =
    val results = stations.map(station =>
      val trains          = trips.getStationTrains(station)
      val numOfPassengers = trains.map(_.seats).sum

      Result(station, numOfPassengers, trains)
    )

    results.sortBy(-_.passengers).take(15)

object Stations {
  def apply(xmlFiles: IndexedSeq[File]): (String, Stations) =
    val results = xmlFiles
      .map(xmlFile =>
        val xml         = XML.loadFile(xmlFile)
        val stationXmls = (xml \\ "station")

        val parsingResults = stationXmls.map(Station(_) match
          case Left(error)    => Left(s"Error in file ${xmlFile.getName}: $error")
          case Right(station) => Right(station)
        )

        val (errors, stations) = parsingResults.partitionMap(identity)

        if errors.nonEmpty then Left(errors)
        else Right(stations)
      )
      .toList

    val (errors, stations) = results.partitionMap(identity)

    (errors.flatten.mkString("\n"), new Stations(stations.flatten))
}
