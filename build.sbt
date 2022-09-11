
ThisBuild / version := "1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.7"
ThisBuild / organization  := "com.iamsmkr"

lazy val root = (project in file("."))
  .enablePlugins(OsDetectorPlugin)
  .configs(IntegrationTest)
  .settings(
    name := "arrow-messaging",
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "io.netty"                    % "netty-transport-native-unix-common" % "4.1.72.Final" % "compile" classifier osDetectorClassifier.value,
      "org.apache.arrow"            % "flight-core"                        % "8.0.0" exclude("io.netty", "netty-transport-native-unix-common"),
      "org.apache.logging.log4j"    % "log4j-api"                          % "2.17.2",
      "org.apache.logging.log4j"    % "log4j-core"                         % "2.17.2",
      "org.scala-lang"              % "scala-reflect"                      % "2.13.8",
    )
  )
