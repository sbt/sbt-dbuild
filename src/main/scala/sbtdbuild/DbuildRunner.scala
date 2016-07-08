package sbtdbuild

import sbt._
import Keys._

/**
 * A trait that can run dbuild.   This allows us
 * to script larger builds from within sbt.
 */
trait DbuildRunner {
  /**
   * Runs dbuild
   *
   * @param cwd  The working directory for dbuild.
   * @param buildFile  The build file to pass into dbuild.
   * @param log   A logger to ouptut dbuild System.out/err.
   * @param jvmProps Extra properties to pass to the JVM when booting.
   */
  def run(cwd: File, buildFile: File, log: Logger, jvmProps: (String, String)*): Unit
  /**
   * Runs dbuild
   *
   * @param cwd  The working directory for dbuild.
   * @param args The arguments to pass to dbuild.
   * @param log  A logger to ouptut dbuild System.out/err.
   * @param jvmProps Extra properties to pass to the JVM when booting.
   */
  def run(cwd: File, args: Seq[String], log: Logger, env: (String, String)*): Unit
}

object DbuildRunner {
  def apply(launcherJar: File, propsFile: File, debug: Boolean): DbuildRunner =
    new DbuildRunner {
      def run(cwd: File, buildFile: File, log: Logger, env: (String, String)*): Unit =
        run(cwd, Seq(buildFile.getCanonicalPath), log, env:_*)
      def run(cwd: File, args: Seq[String], log: Logger, env: (String, String)*): Unit = {
        IO.createDirectory(cwd)  // Ensure directory exists.
        // TODO - use Fork.java here or some other better JVM handler.
        val cmd = Seq("java") ++
        Seq("-jar", launcherJar.getAbsolutePath,
          s"@${propsFile.toURI.toURL.toString}") ++
          (if(debug) Seq("--debug") else Nil) ++
          args

        log.info(s"Running dbuild: ${cmd.mkString(" ")}")
        Process(cmd, Some(cwd), env:_*) ! log match {
          case 0 => ()
          case n =>
            sys.error(s"Failed to run command: $cmd, error: $n")
        }
      }
      override def toString = s"DbuildRunner($launcherJar, $propsFile)"
    }
}
