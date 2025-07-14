package Models

import upickle.default.Writer

import Models.{Station, Train}

case class Result(val station: Station, val passengers: Int, val trains: List[Train])
    derives Writer:
  override def toString(): String =
    s"${station.name} is visited by ${trains.length} trains with ${trains.map(train => train.seats).mkString(",")} seats - it can recieve $passengers passangers"
