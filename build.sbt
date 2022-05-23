
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "arrow-flight-scala-poc"
  )

libraryDependencies ++= Seq(
  "io.netty" % "netty-transport-native-unix-common" % "4.1.72.Final" % "compile" classifier osDetectorClassifier.value,
  "org.apache.arrow" % "flight-core" % "8.0.0"
)

enablePlugins(OsDetectorPlugin)
