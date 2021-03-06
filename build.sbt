import sbtcrossproject.CrossPlugin.autoImport.crossProject
import sbtcrossproject.CrossType

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / organization := "ninja.grimrose.sandbox"
ThisBuild / version := "0.1"

ThisBuild / scalafmtOnCompile := true

val scalaJsNodeJsV10Version = "0.9.1"

val airframeVersion  = "19.12.4"
val hammockVersion   = "0.10.0"
val circeVersion     = "0.12.3"
val catsVersion      = "2.0.0"
val catsRetryVersion = "0.3.2"

val circeGenericExtrasVersion = "0.12.2"

lazy val baseSettings = Def.settings(
  scalacOptions ~= filterConsoleScalacOptions,
  scalacOptions += "-P:scalajs:sjsDefinedByDefault",
  parallelExecution in Test := false
)

lazy val airframeSettings = Def.settings(
  libraryDependencies ++= (
    "org.scala-lang.modules" %%% "scala-collection-compat" % "2.1.3" ::
      "org.wvlet.airframe"   %%% "airframe"                % airframeVersion ::
      "org.wvlet.airframe"   %%% "airframe-log"            % airframeVersion ::
      Nil
  )
)

lazy val circeSettings = Def.settings(
  libraryDependencies ++= (
    "io.circe"   %%% "circe-core"           % circeVersion ::
      "io.circe" %%% "circe-generic"        % circeVersion ::
      "io.circe" %%% "circe-generic-extras" % circeGenericExtrasVersion ::
      "io.circe" %%% "circe-parser"         % circeVersion ::
      "io.circe" %%% "circe-scalajs"        % circeVersion ::
      Nil
  )
)

lazy val effectSettings = Def.settings(
  libraryDependencies ++= (
    "com.github.cb372"   %%% "cats-retry-core"        % catsRetryVersion ::
      "com.github.cb372" %%% "cats-retry-cats-effect" % catsRetryVersion ::
      Nil
  )
)

lazy val httpClientSettings = Def.settings(
  libraryDependencies ++= (
    "com.pepegar"   %%% "hammock-core"  % hammockVersion ::
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
    "org.scalatest" %%% "scalatest" % "3.1.0" % Test ::
      Nil
  )
)

lazy val coreSettings = baseSettings ++ airframeSettings ++ circeSettings ++ testSettings ++ Def.settings(
  scalaJSLinkerConfig ~= {
    _.withModuleKind(ModuleKind.CommonJSModule) /*.withOptimizer(false)*/
  },
  libraryDependencies ++= (
    "net.exoego" %%% "scala-js-nodejs-v10" % scalaJsNodeJsV10Version ::
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

lazy val coreJs  = core.js
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
  .dependsOn(infrastructure % "test->test;compile->compile")

lazy val `hello-world` = (project in file("functions/hello-world"))
  .enablePlugins(ScalaJSPlugin)
  .settings(coreSettings)
  .settings(httpClientSettings)
  .settings(
    libraryDependencies ++= (
      ScalablyTyped.E.`express-serve-static-core` ::
        Nil
    )
  )
  .dependsOn(coreJs)

lazy val root = (project in file("."))
  .settings(baseSettings)
  .settings(
    name := "cloud-build-slack-notifications",
    noPublish
  )
  .aggregate(coreJs, coreJvm, application, infrastructure, `entry-point`, `hello-world`)

addCommandAlias("fmt", "scalafmtAll;scalafmtSbt")
addCommandAlias("lint", "scalafmtCheckAll;scalafmtSbtCheck")
addCommandAlias("cov", "clean;coverage;test;coverageReport:coverageAggregate")
