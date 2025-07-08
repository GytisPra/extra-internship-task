## Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

## Task

There are 10 xml files for trains, stations and trips each containing current and historical data. Some files may contain syntax errors.

The xsd schemas for train, station and trip files are "train.xsd", "station.xsd" and "trip.xsd" respectively.

Quick explanation for elements:
#### `train`:
- **Attributes**:
  - `version`: integer — version number of the record.
- **Elements**:
  - `id`: string — a reference ID for the train. This may not be unique.
  - `seats`: integer — number of seats on the train.

#### `station`:
- **Attributes**:
  - `version`: integer — version number of the record.
- **Elements**:
  - `id`: string — a reference ID for the station. This may not be unique.
  - `name`: string — a "user-readable" name of the station.

#### `trip`:
- **Attributes**:
  - `version`: integer — version number of the record.
- **Elements**:
  - `id`: string — a unique ID of the element for the trip.
  - `train`: string — the ID for the train that goes on this trip.
  - `stations`: list
    - `station`: element containing the ID of a station visited by the train on this trip.
			
The `version` field denotes a key for some (train, station, trip) pairing and must be the same across all references -  i.e if a trip with a `version = 2` references a train with `version = 1` it is considered invalid and must be discarded.  The same is true for stations.


Tasks:

1. Parse xml files. Ignore files with errors and report their names. Bonus point: report the line number for the error.
2. Map trains and stations to their corresponding trips. Version numbers must match. Report all trips that have missing trains or stations and ignore the trips. Bonus point: report the file name which contains the trip with missing data.
3. Report the names of the top 15 stations that can receive the most passangers with the total of passangers. Example: 
station1 is visited by a train with 150 seats - it can receive 150 passangers.
station2 is visited by 3 trains with 50, 80 and 100 seats respectively - it can receive 230 passangers.
4. Bonus task: plot a distribution graph for the data in the 3rd task sorted by station names.

All output is to be written to files. Any language and library can be used. 
Demonstrating side effect free style of programming is a bonus (e.g constraining side effecting functions inside an IO monad or using other effect tracking mechanism of your preferred language).
