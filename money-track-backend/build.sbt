name := "money-track-backend"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= {
  val akkaActorVersion = "2.5.17"

  Seq(
    "org.typelevel" %% "cats-core" % "1.4.0",
  "org.scalactic" %% "scalactic" % "3.0.4",
    "org.scalatest" %% "scalatest" % "3.0.4" % "test",
    "org.scalamock" %% "scalamock" % "4.0.0" % Test,
    "com.typesafe" % "config" % "1.3.2",
    "org.rogach" %% "scallop" % "3.1.3",
    "com.typesafe.akka" %% "akka-actor" % akkaActorVersion,
    "com.typesafe.akka" %% "akka-http" % "10.1.5",
    "com.typesafe.akka" %% "akka-stream" % "2.5.12",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5" % Test,
    "org.mongodb" %% "casbah" % "3.1.1",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.5",
    "io.spray" %% "spray-json" % "1.3.3",
    "org.slf4j" % "slf4j-api" % "1.7.5",
    "org.slf4j" % "slf4j-simple" % "1.7.5",
    "org.clapper" %% "grizzled-slf4j" % "1.3.2"
  )
}