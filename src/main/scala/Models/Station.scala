package Models

import os.Path
import scala.xml.XML
import upickle.default.*
import java.io.File

case class Station(
    val id: String,
    val name: String,
    val version: Int,
    var trains: List[Train],
    var passengers: Int
) derives Writer:
  override def toString(): String =
    s"ID:$id, Name:$name, Version:$version, Passengers:${passengers}"

  def toResultsString(): String =
    s"$name is visited by ${trains.length} trains with ${trains.map(train => train.seats).mkString(",")} respectively - it can recieve $passengers passangers"

  def appendTrain(train: Train): Unit =
    trains = trains :+ train

class Stations private (private val stations: List[Station]):
  def getStation(stationId: String, version: Int): Either[String, Station] =
    stations.find(station => station.id == stationId && station.version == version) match {
      case None        => Left(s"station (ID: $stationId; version: $version) not found")
      case Some(value) => Right(value)
    }

  def getTop15Stations(): List[Station] =
    stations.sortBy(-_.passengers).take(15)

object Stations {
  def apply(xmlFiles: IndexedSeq[File]): Stations =
    new Stations(
      xmlFiles
        .map(xmlFile =>
          val xml = XML.loadFile(xmlFile)

          (xml \ "station" \\ "id") zip (xml \ "station" \\ "id") zip (xml \ "station" \\ "@version") map {
            case ((id, name), version) =>
              Station(id.text, name.text, version.text.toInt, List(), 0)
          }
        )
        .flatten
        .toList
    )
}
