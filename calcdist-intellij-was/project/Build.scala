import sbt._
import Keys._

object BuildSettings {

  lazy val buildSettings =
    Defaults.coreDefaultSettings ++
      Seq(
        version := "1.0",
        scalaVersion in ThisBuild := "2.11.7",
        parallelExecution in ThisBuild := false,
        scalacOptions := Seq(
          "-feature",
          "-language:implicitConversions",
          "-language:postfixOps",
          "-deprecation",
          "-encoding", "utf8",
          "-Ywarn-adapted-args"))

}

object Build extends Build {
  import BuildSettings._
    lazy val root = Project(id = "parent",
                            base = file("."),settings = buildSettings) aggregate(cbserver,cbclient)

    lazy val cbserver = Project(id = "cb-server",
                           base = file("cb-server"),settings = buildSettings)

  lazy val cbclient = Project(id = "cb-client",
    base = file("cb-client"),settings = buildSettings)

}