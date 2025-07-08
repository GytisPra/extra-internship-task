package Models

import os.Path
import scala.xml.XML

class Train(val id: String, val seats: Int, val version: Int):
  override def toString(): String = s"$id, $seats, $version"

class Trains(private val trains: List[Train]):
  /**
    * Gets a train by its ID and version
    *
    * @param trainId The ID of the train to find
    * @param version The version of the train to find
    * @return Train if found; None otherwise
    */
  def getTrain(trainId: String, version: Int): Option[Train] =
    trains.find(train => train.id == trainId && train.version == version)

/** Parses and returns trains from given xml file paths
  *
  * @param xmlPaths
  *   the filepath of the xml file
  * @return
  *   class Trains that contains a list of trains
  */
def parseTrainsFromXmlPaths(xmlPaths: IndexedSeq[Path]): Trains =
  Trains(
    xmlPaths
      .map(path =>
        val xmlFile = XML.loadFile(path.toString)

        (xmlFile \ "train" \\ "seats") zip (xmlFile \ "train" \\ "id") zip (xmlFile \ "train" \\ "@version") map {
          case ((seats, id), version) =>
            Train(id.text, seats.text.toInt, version.text.toInt)
        }
      )
      .flatten
      .toList
  )