ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "zio2-deadlock-2023-04"
  )

libraryDependencies += "dev.zio" %% "zio" % "2.0.12"
