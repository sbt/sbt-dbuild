package sbtdbuild

import sbt._
import Keys._

/**
 * A plugin which provides a `dbuildRunner` key that can be used to
 * run dbuild within sbt.
 */
object DbuildPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin

  object autoImport extends DbuildKeys

  import autoImport._

  override def buildSettings = Seq(
    dbuildLaunchConfig := baseDirectory.value / "project" / "dbuild.properties",
    dbuildDebug := false,
    dbuildRunner := DbuildRunner(dbuildSbtLauncher.value, dbuildLaunchConfig.value, dbuildDebug.value),
    dbuildSbtLauncher := {
      val dest = dbuildSbtLauncherPath.value
      if (!dest.exists) {
        IO.download(url("https://repo1.maven.org/maven2/org/scala-sbt/launcher/1.0.0/launcher-1.0.0.jar"), dest)
      }
      dest
    },
    dbuildSbtLauncherPath := baseDirectory.value / "target" / "sbt" / "sbt-launch.jar",
    dbuild := {
      val r = dbuildRunner.value
      val t = (target in dbuild).value
      r.run(t, dbuildConfiguration.value, sLog.value)
    },
    target in dbuild := {
      baseDirectory.value / "dbuild" / "target"
    },
    dbuildConfiguration := baseDirectory.value / "dbuild.conf"
  )
}
