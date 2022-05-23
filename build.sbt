
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "arrow-flight-scala-poc"
  )
resolvers ++= Seq (
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "org.apache.arrow" % "flight-core" % "8.0.0" classifier osDetectorClassifier.value
)
