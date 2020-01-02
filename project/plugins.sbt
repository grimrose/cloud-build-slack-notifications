// for scala.js
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.1")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "0.6.31")

resolvers += Resolver.bintrayRepo("oyvindberg", "ScalablyTyped")
addSbtPlugin("org.scalablytyped" % "sbt-scalablytyped" % "201912290430")

// for build info
addSbtPlugin("com.typesafe.sbt" % "sbt-git"       % "1.0.0")
addSbtPlugin("com.eed3si9n"     % "sbt-buildinfo" % "0.9.0")

// for project
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"  % "2.3.0")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"  % "0.1.10")
addSbtPlugin("org.scoverage"             % "sbt-scoverage" % "1.6.1")

// for package
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.5.2")

fullResolvers ~= { _.filterNot(_.name == "jcenter") }

scalacOptions ++= ("-deprecation" :: "-feature" :: Nil)
