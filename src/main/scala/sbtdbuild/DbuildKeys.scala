package sbtdbuild

import sbt._

trait DbuildKeys {
  val dbuildRunner          = taskKey[DbuildRunner]("A runner that can execute dbuild.")
  val dbuildDebug           = settingKey[Boolean]("Debug output from dbuild.")
  val dbuildSbtLauncher     = taskKey[File]("sbt launcher file")
  val dbuildSbtLauncherPath = taskKey[File]("sbt launcher file")

  /** The location where we look for dbuild's launcher properties.
    * This effectively determines the version of dbuild we'll use.
    */
  val dbuildLaunchConfig    = settingKey[File]("Location of dbuild launchconfig.")
  val dbuild                = taskKey[Unit]("Runs the dbuild")
  val dbuildConfiguration   = settingKey[File]("Location of dbuild configuration")
}
