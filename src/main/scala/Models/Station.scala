package Models

import os.Path
import scala.xml.XML
import upickle.default.Writer
import java.io.File

case class Station(
    val id: String,
    val name: String,
    val version: Int
) derives Writer:
  override def toString(): String =
    s"ID:$id, Name:$name, Version:$version"

  def getStationTrains(trips: List[Trip]): List[Train] =
    for
      trip <- trips
      if trip.stations.exists(tripStation => tripStation.id == id && tripStation.version == version)
    yield (trip.train)

class Stations private (private val stations: List[Station]):
  def getStation(stationId: String, version: Int): Either[String, Station] =
    stations.find(station => station.id == stationId && station.version == version) match {
      case None        => Left(s"station (ID: $stationId; version: $version) not found")
      case Some(value) => Right(value)
    }

object Stations {
  def apply(xmlFiles: IndexedSeq[File]): Stations =
    new Stations(
      xmlFiles
        .flatMap(xmlFile =>
          val xml = XML.loadFile(xmlFile)

          (xml \ "station" \\ "id") zip (xml \ "station" \\ "id") zip (xml \ "station" \\ "@version") map {
            case ((id, name), version) =>
              Station(id.text, name.text, version.text.toInt)
          }
        )
        .toList
    )
}
