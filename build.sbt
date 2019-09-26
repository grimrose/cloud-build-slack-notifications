import sbtcrossproject.CrossPlugin.autoImport.crossProject
import sbtcrossproject.CrossType

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / organization := "ninja.grimrose.sandbox"
ThisBuild / version := "0.1"

ThisBuild / scalafmtOnCompile := true

val scalaJsnodeJsV8Version = "0.8.0"

val airframeVersion = "19.9.7"
val hammockVersion = "0.9.2"
val circeVersion = "0.12.1"
val catsVersion = "2.0.0"
val catsRetryVersion = "0.3.0"

lazy val baseSettings = Def.settings(
  scalacOptions ~= filterConsoleScalacOptions,
  scalacOptions += "-P:scalajs:sjsDefinedByDefault",
  parallelExecution in Test := false
)

lazy val airframeSettings = Def.settings(
  libraryDependencies ++= (
    "org.scala-lang.modules" %%% "scala-collection-compat" % "2.1.2" ::
      "org.wvlet.airframe" %%% "airframe" % airframeVersion ::
      "org.wvlet.airframe" %%% "airframe-log" % airframeVersion ::
      Nil
    )
)

lazy val circeSettings = Def.settings(
  libraryDependencies ++= (
    "io.circe" %%% "circe-core" % circeVersion ::
      "io.circe" %%% "circe-generic" % circeVersion ::
      "io.circe" %%% "circe-generic-extras" % circeVersion ::
      "io.circe" %%% "circe-parser" % circeVersion ::
      "io.circe" %%% "circe-scalajs" % circeVersion ::
      Nil
    )
)

lazy val effectSettings = Def.settings(
  libraryDependencies ++= (
    "com.github.cb372" %%% "cats-retry-core" % catsRetryVersion ::
      "com.github.cb372" %%% "cats-retry-cats-effect" % catsRetryVersion ::
      Nil
    )
)

lazy val httpClientSettings = Def.settings(
  libraryDependencies ++= (
    "com.pepegar" %%% "hammock-core" % hammockVersion ::
      "com.pepegar" %%% "hammock-circe" % hammockVersion ::
      Nil
    )
)

lazy val gcpSettings = Def.settings(
  libraryDependencies ++= (
    ScalablyTyped.G.`google-cloud__kms` ::
      ScalablyTyped.G.`google-auth-library` ::
      Nil
    )
)

lazy val testSettings = Def.settings(
  libraryDependencies ++= (
    "org.scalatest" %%% "scalatest" % "3.0.8" % Test ::
      Nil
    )
)

lazy val coreSettings = baseSettings ++ airframeSettings ++ circeSettings ++ testSettings ++ Def.settings(
  scalaJSLinkerConfig ~= {
    _.withModuleKind(ModuleKind.CommonJSModule)
  },
  libraryDependencies ++= (
    "net.exoego" %%% "scala-js-nodejs-v8" % scalaJsnodeJsV8Version ::
      Nil
    )
)

lazy val noPublish = Seq(
  publishLocal := {},
  publishArtifact in Compile := false,
  publish := {}
)

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("functions/core"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(BuildInfoPlugin, GitPlugin)
  .settings(coreSettings)
  .settings(
    buildInfoKeys ++= Seq[BuildInfoKey]("HEAD" -> git.gitHeadCommit.value.getOrElse("")),
    buildInfoOptions ++= (
      BuildInfoOption.BuildTime ::
        BuildInfoOption.ToJson ::
        Nil
      )
  )

lazy val coreJs = core.js
lazy val coreJvm = core.jvm

lazy val application = (project in file("functions/application"))
  .enablePlugins(ScalaJSPlugin)
  .settings(coreSettings)
  .settings(effectSettings)
  .dependsOn(coreJs % "test->test;compile->compile")

lazy val infrastructure = (project in file("functions/infrastructure"))
  .enablePlugins(ScalaJSPlugin)
  .settings(coreSettings)
  .settings(httpClientSettings)
  .settings(gcpSettings)
  .dependsOn(application % "test->test;compile->compile")

lazy val `entry-point` = (project in file("functions/entry-point"))
  .enablePlugins(ScalaJSPlugin)
  .settings(coreSettings)
  .settings(
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.CommonJSModule)
    },
    libraryDependencies ++= (
      ScalablyTyped.E.`express-serve-static-core` ::
        Nil
      )
  )
  .dependsOn(infrastructure % "test->test;compile->compile")

lazy val root = (project in file("."))
  .settings(baseSettings)
  .settings(
    name := "cloud-build-slack-notifications",
    noPublish
  )
  .aggregate(coreJs, coreJvm, application, infrastructure, `entry-point`)

addCommandAlias("fmt", ";scalafmtAll")
addCommandAlias("cov", ";clean;coverage;test;coverageReport:coverageAggregate")
