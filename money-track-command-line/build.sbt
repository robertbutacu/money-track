name := "money-track-command-line"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= {
  Seq(
    "org.scalactic" %% "scalactic" % "3.0.4",
    "org.scalatest" %% "scalatest" % "3.0.4" % "test",
    "org.scalamock" %% "scalamock" % "4.0.0" % Test,
    "com.typesafe" % "config" % "1.3.2",
    "org.rogach" %% "scallop" % "3.1.3",
    "org.scalaj" %% "scalaj-http" % "2.4.1"
  )
}