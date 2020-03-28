addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")

//addSbtPlugin("io.get-coursier" % "sbt-coursier" % "2.0.0-RC6")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
//addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.23")
//addSbtPlugin("org.scalaxb" % "sbt-scalaxb" % "1.7.1")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.7")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.4.1")
libraryDependencies += "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
