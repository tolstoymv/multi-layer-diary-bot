import Dependencies._

ThisBuild / scalaVersion     := "2.13.7"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "multi-layer-diary-bot",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "com.bot4s" %% "telegram-core" % "5.3.0",
      "com.bot4s" %% "telegram-akka" % "5.3.0",
      "com.softwaremill.sttp.client3" %% "core" % "3.4.1",
      "com.softwaremill.sttp.client3" %% "okhttp-backend" % "3.4.1"
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
