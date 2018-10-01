name := "money-track-backend"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= {
  val akkaVersion = "2.4.12"

  Seq(
    "org.scalactic" %% "scalactic" % "3.0.4",
    "org.scalatest" %% "scalatest" % "3.0.4" % "test",
    "org.scalamock" %% "scalamock" % "4.0.0" % Test,
    "com.typesafe" % "config" % "1.3.2",
    "org.rogach" %% "scallop" % "3.1.3",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % "10.1.5",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5" % Test,
    "org.mongodb" %% "casbah" % "3.1.1",
    "org.slf4j" % "slf4j-simple" % "1.6.4"
  )
}