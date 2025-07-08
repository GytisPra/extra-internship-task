package Models

import os.Path
import scala.xml.XML

class Station(val id: String, val name: String, val version: Int):
  override def toString(): String = s"$id, $name, $version"

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
            Station(id.text, name.text, version.text.toInt)
        }
      )
      .flatten
      .toList
  )
