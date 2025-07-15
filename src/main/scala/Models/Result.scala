package Models

import upickle.default.{writer, write, Writer}
import ujson.{Obj, Value}

import Models.{Station, Train}

case class Result(val station: Station, val passengers: Int, val trains: List[Train]):
  override def toString(): String =
    s"${station.name} is visited by ${trains.length} trains with ${trains.map(train => train.seats).mkString(",")} seats - it can recieve $passengers passangers"

object Result {
  implicit val resultWriter: Writer[Result] =
    writer[Obj].comap(result =>
      Obj(
        "name"       -> result.station.name,
        "passengers" -> result.passengers,
        "trains"     -> Value(write[List[Train]](result.trains))
      )
    )
}
