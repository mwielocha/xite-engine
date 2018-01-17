
// Generated with scalagen

lazy val root = (project in file(".")).
  settings(
    name := "xite-engine",
    version := "1.0",
    scalaVersion := "2.12.4"
  )

//mainClass in (Compile, run) := Some("...")

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.4" % "test"
  )

