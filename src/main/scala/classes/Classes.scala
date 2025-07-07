package classes

class Station(val id: String, val name: String, val version: Int):
    override def toString(): String = s"$id, $name, $version"

class Train(val id: String, val seats: Int, val version: Int):
    override def toString(): String = s"$id, $seats, $version"

class Trip(val id: String, train: Train, val stations: List[Station]):
    override def toString(): String = s"$id, \n$train, \n${stations.mkString("\n")}"
