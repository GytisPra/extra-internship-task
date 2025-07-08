package Models

import os.Path
import scala.xml.XML
import upickle.default.*

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

class Stations(private val stations: List[Station]):
  /** Finds a station by its Id and version
    *
    * @param stationId
    *   The id of the station to find
    * @param version
    *   The version of the station to find
    * @return
    *   None if station not found; Some(value) otherwise
    */
  def getStation(stationId: String, version: Int): Option[Station] =
    stations.find(station => station.id == stationId && station.version == version)

  /**
    * Sorts stations by the passanger count and gets the top 15 stations
    *
    * @return Top 15 stations by passenger count 
    */
  def getTop15Stations(): List[Station] =
    stations.sortBy(-_.passengers).take(15)

/** Parses and returns stations from given xml file paths
  *
  * @param xmlPaths
  *   the filepath of the xml file
  * @return
  *   class Stations that contains a list of stations
  */
def parseStationsFromXmlPaths(xmlPaths: IndexedSeq[Path]): Stations =
  Stations(
    xmlPaths
      .map(path =>
        val xmlFile = XML.loadFile(path.toString)

        (xmlFile \ "station" \\ "id") zip (xmlFile \ "station" \\ "id") zip (xmlFile \ "station" \\ "@version") map {
          case ((id, name), version) =>
            Station(id.text, name.text, version.text.toInt, List(), 0)
        }
      )
      .flatten
      .toList
  )
