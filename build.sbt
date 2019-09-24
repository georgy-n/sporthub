name := "sporthub"

version := "0.1"

scalaVersion := "2.12.8"

val scalacFlags = Seq(
  //  "-Xlog-implicits",
  "-Xlog-reflective-calls",
  "-opt:_",
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-value-discard",              // Warn when non-Unit expression results are unused.
  "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals",              // Warn if a local definition is unused.
  "-Ywarn-unused:params",              // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",            // Warn if a private member is unused.
  "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
  "-Yrangepos"                         // required by SemanticDB compiler plugin
)

scalacOptions in ThisBuild ++= scalacFlags

val globalExclusions = new {
  def ExclusionRuleVersioned(org: String, moduleName: String): ExclusionRule =
    ExclusionRule(org, s"${moduleName}_2.12")

  val akka = ExclusionRule(organization = "com.typesafe.akka")

  val cats = List("cats-core", "cats-effect", "cats-free", "cats-macros", "alleycats-core")
    .map(ExclusionRuleVersioned("org.typelevel", _))

  val slf4j = ExclusionRule(organization = "org.slf4j")
}

val versions = new {
  val cats = "2.0.0-RC1"
  val magnolia = "0.11.0"
  val slf4j = "1.7.26"
  val logback = "1.2.3"
  val prometheus = "0.6.0"
  val tofu = "0.4.0"
  val typedSchema = "0.11.0-RC2"
  val scalaLogging = "3.9.2"
  val scalaCache = "0.28.0"

  val zio = new {
    val core = "1.0.0-RC11-1"
    val nio = "0.1.1"
    val cats = "2.0.0.0-RC2"
  }

  val scalajHttp = "2.4.2"
  val doobie = "0.8.2"
  val newType = "0.4.3"

  val tethys = "0.10.0"
  val akka = new {
    val core = "2.5.24"
    val http = "10.1.9"
    val cors = "0.4.1"
  }
}

val dependencies = Seq(
  //httpclient
  "org.scalaj" %% "scalaj-http" % versions.scalajHttp,

  //akka
  "com.typesafe.akka" %% "akka-stream"    % versions.akka.core,
  "com.typesafe.akka" %% "akka-actor"     % versions.akka.core,
  "com.typesafe.akka" %% "akka-http"      % versions.akka.http,
  "ch.megard"         %% "akka-http-cors" % versions.akka.cors,

  // zio
  "org.scalaz" %% "scalaz-zio"              % versions.zio.core,
  "org.scalaz" %% "scalaz-zio-interop-cats" % versions.zio.cats,

  //cats
  "org.typelevel" %% "cats-core"   % versions.cats,
  "org.typelevel" %% "cats-effect" % versions.cats,

  //newtypes
  "io.estatico" %% "newtype" % "0.4.2",

  //config
  "com.typesafe" % "config" % "1.3.4",

  //database
  "org.tpolecat" %% "doobie-core"      % versions.doobie,
  "org.tpolecat" %% "doobie-hikari"    % versions.doobie,
  "org.tpolecat" %% "doobie-postgres"  % versions.doobie,

  // logging && debugging
  "org.slf4j"                   % "slf4j-api"               % versions.slf4j,
  "ch.qos.logback"              % "logback-classic"         % versions.logback,
  "com.typesafe.scala-logging" %% "scala-logging"           % versions.scalaLogging,
  "com.lihaoyi"                %% "sourcecode"              % "0.1.6" withSources(),
  "ru.tinkoff"                 %% "tofu-logging-structured" % versions.tofu, //excludeAll globalExclusions.slf4j,
  "ru.tinkoff"                 %% "tofu-logging-derivation" % versions.tofu,// excludeAll globalExclusions.slf4j,
  "ru.tinkoff"                 %% "tofu-logging-layout"     % versions.tofu excludeAll (globalExclusions.cats:_*) excludeAll(globalExclusions.slf4j),

  //cache
  "com.github.cb372" %% "scalacache-core"        % versions.scalaCache,
  "com.github.cb372" %% "scalacache-cats-effect" % versions.scalaCache excludeAll (globalExclusions.cats: _*),
  "com.github.cb372" %% "scalacache-caffeine"    % versions.scalaCache,

  //metrics
  "io.prometheus" % "simpleclient"            % versions.prometheus,
  "io.prometheus" % "simpleclient_hotspot"    % versions.prometheus,
  "io.prometheus" % "simpleclient_httpserver" % versions.prometheus,

  "org.scalactic" %% "scalactic" % "3.0.7",

  //magnolia
  "com.propensive" %% "magnolia"   % "0.11.0",

  "com.beachape"   %% "enumeratum"   % "1.5.13",
  "io.scalaland"   %% "chimney"      % "0.3.2",
  "io.monix"       %% "monix-catnap" % "3.0.0-RC2",

  //tethys
  "com.tethys-json" %% "tethys-core"       % versions.tethys,
  "com.tethys-json" %% "tethys-jackson"    % versions.tethys,
  "com.tethys-json" %% "tethys-derivation" % versions.tethys,

  //typedSchema
  "ru.tinkoff" %% "typed-schema-akka-http" % versions.typedSchema excludeAll globalExclusions.akka,
  "ru.tinkoff" %% "typed-schema-swagger"   % versions.typedSchema,

  compilerPlugin("org.typelevel" %% "kind-projector" % "0.10.1"),
  compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0"),
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
)

val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.7",
  "org.scalamock" %% "scalamock" % "4.2.0",
  "org.scalacheck" %% "scalacheck" % "1.14.0",
  "org.tpolecat" %% "doobie-scalatest" % versions.doobie,
).map(_ % "test")

resolvers in ThisBuild ++= Seq(
  "Sbt repo" at "https://repo.scala-sbt.org/scalasbt/simple/repo1-cache",
  "Confluent Maven Repo" at "https://packages.confluent.io/maven/",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("public"),
  Resolver.sonatypeRepo("snapshots")
)


