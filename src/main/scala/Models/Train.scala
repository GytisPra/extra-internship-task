package Models

import os.Path
import scala.xml.XML
import upickle.default.Writer
import java.io.File

case class Train(val id: String, val seats: Int, val version: Int) derives Writer:
  override def toString(): String = s"$id, $seats, $version"

class Trains private (private val trains: List[Train]):
  def getTrain(trainId: String, version: Int): Either[String, Train] =
    trains.find(train => train.id == trainId && train.version == version) match
      case None        => Left(s"train (ID:$trainId; version:$version) not found")
      case Some(train) => Right(train)

object Trains {
  def apply(xmlFiles: IndexedSeq[File]): Trains =
    new Trains(
      xmlFiles
        .flatMap(xmlFile =>
          val xml = XML.loadFile(xmlFile)

          (xml \ "train" \\ "seats") zip (xml \ "train" \\ "id") zip (xml \ "train" \\ "@version") map {
            case ((seats, id), version) =>
              Train(id.text, seats.text.toInt, version.text.toInt)
          }
        )
        .toList
    )
}
