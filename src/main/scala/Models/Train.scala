package Models

import os.Path
import scala.xml.{XML, Node}
import upickle.default.Writer
import java.io.File

import scala.io.Source

case class Train(val id: String, val seats: Int, val version: Int) derives Writer:
  override def toString(): String = s"$id, $seats, $version"

object Train {
  def apply(xml: Node): Either[String, Train] =
    val id      = (xml \ "id").text
    val seats   = (xml \ "seats").text
    val version = (xml \ "@version").text

    if id.isBlank then Left(s"train has a blank id.")
    else if seats.isBlank then Left(s"train has blank seats.")
    else if version.isBlank then Left(s"train has a missing version attribute.")
    else Right(Train(id, seats.toInt, version.toInt))
}

class Trains private (private val trains: List[Train]):
  def getTrain(trainId: String, version: Int): Either[String, Train] =
    trains.find(train => train.id == trainId && train.version == version) match
      case None        => Left(s"train (ID:$trainId; version:$version) not found")
      case Some(train) => Right(train)

object Trains {
  def apply(xmlFiles: IndexedSeq[File]): (String, Trains) =
    val results = xmlFiles
      .map(xmlFile =>
        val xml            = XML.loadFile(xmlFile)
        val trainXmls      = (xml \\ "train")
        val parsingResults = trainXmls.map(Train(_) match
          case Left(error)  =>
            Left(s"Error in file ${xmlFile.getName}: $error")
          case Right(train) => Right(train)
        )

        val (errors, stations) = parsingResults.partitionMap(identity)

        if errors.nonEmpty then Left(errors)
        else Right(stations)
      )
      .toList

    val (errors, trains) = results.partitionMap(identity)

    (errors.flatten.mkString("\n"), new Trains(trains.flatten))
}
