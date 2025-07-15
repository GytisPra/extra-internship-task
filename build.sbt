val scala3Version = "3.7.1"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "internship-extra-task",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.lihaoyi"            %% "os-lib"     % "0.11.4",
      "com.lihaoyi"            %% "upickle"    % "4.1.0",
      "org.scala-lang.modules" %% "scala-xml"  % "2.2.0",
      "org.scalanlp"           %% "breeze-viz" % "2.1.0"
    )
  )
