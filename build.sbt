import com.typesafe.sbt.SbtGit.git
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

name := "activityHub"
organization in ThisBuild := "ru.activity.hub.api"

version := "2.0.0"

scalaVersion := "2.13.4"

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
//  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
//  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
//  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
//  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
//  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
//  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
//  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
//  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
//  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
//  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
//  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
//  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
//  "-Ywarn-value-discard",              // Warn when non-Unit expression results are unused.
//  "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
//  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
//  "-Ywarn-unused:locals",              // Warn if a local definition is unused.
//  "-Ywarn-unused:params",              // Warn if a value parameter is unused.
//  "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
//  "-Ywarn-unused:privates",            // Warn if a private member is unused.
//  "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
//  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
//  "-Yrangepos"                         // required by SemanticDB compiler plugin
)

scalacOptions in ThisBuild ++= scalacFlags


val versions = new {
  val cats = "2.3.0"
  val slf4j = "1.7.26"
  val logback = "1.2.3"
  val typedSchema = "0.14.1"
  val tethys = "0.21.0"
  val scalaLogging = "3.9.2"
  val doobie = "0.10.0"
  val newType = "0.4.4"

  val zio = new {
    val main = "1.0.4-2"
    val nio = "0.1.1"
    val cats = "2.3.1.0"
    val twitter = "20.10.0.0"
  }
  val scalajHttp = "2.4.2"
}

val exclusions = new {
  val findBugs = ExclusionRule(organization = "com.google.code.findbugs")
  val nettyHandler =
    ExclusionRule(organization = "io.netty", name = "netty-handler")
  val zioCore = ExclusionRule(organization = "dev.zio", name = "zio_2.12")
  val zioInteropCats =
    ExclusionRule(organization = "dev.zio", name = "zio-interop-cats_2.12")
  val typedSchemaSwagger = ExclusionRule(organization = "ru.tinkoff",
    name = "typed-schema-swagger_2.12")
  val typedSchemaParam =
    ExclusionRule(organization = "ru.tinkoff", name = "typed-schema-param_2.12")
  val catsCore =
    ExclusionRule(organization = "org.typelevel", name = "cats-core_2.12")

}

val dependencies = Seq(
  //httpclient
  "org.scalaj" %% "scalaj-http" % versions.scalajHttp,

  // zio
  "dev.zio" %% "zio"              % versions.zio.main,
  "dev.zio" %% "zio-interop-cats" % versions.zio.cats,
  "dev.zio" %% "zio-interop-twitter" % versions.zio.twitter excludeAll exclusions.zioCore,

  //cats
  "org.typelevel" %% "cats-core"   % versions.cats,
  "org.typelevel" %% "cats-effect" % versions.cats,

  //newtypes
  "io.estatico" %% "newtype" % versions.newType,

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
  "com.lihaoyi"                %% "sourcecode"              % "0.2.2" withSources(),

  "com.beachape"   %% "enumeratum"   % "1.5.13",
  "io.scalaland"   %% "chimney"      % "0.3.2",

  //tethys
  "com.tethys-json" %% "tethys-core"       % versions.tethys,
  "com.tethys-json" %% "tethys-jackson"    % versions.tethys,
  "com.tethys-json" %% "tethys-derivation" % versions.tethys,
  "com.tethys-json" %% "tethys-enumeratum" % versions.tethys,
  //typedSchema
  "ru.tinkoff" %% "typed-schema-finagle" % versions.typedSchema excludeAll exclusions.findBugs,
  "ru.tinkoff" %% "typed-schema-finagle-zio" % versions.typedSchema excludeAll (
    exclusions.findBugs,
    exclusions.zioCore,
    exclusions.zioInteropCats),

  "ru.tinkoff" %% "typed-schema-finagle-tethys" % versions.typedSchema excludeAll exclusions.findBugs,
  "ru.tinkoff" %% "typed-schema-finagle-common" % versions.typedSchema excludeAll (exclusions.findBugs, exclusions.catsCore),

  "org.typelevel" %% "mouse" % "0.24",

  compilerPlugin("org.typelevel" %% "kind-projector" % "0.11.3" cross CrossVersion.full),
  compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0"),
//  compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

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


lazy val activityHub = (project in file("."))
  .enablePlugins(
    JavaAppPackaging,
    GitVersioning,
    BuildInfoPlugin
  )
  .settings(name := "activityHub")
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      sbtVersion,
      git.branch,
      git.gitHeadCommit,
      git.gitHeadCommitDate,
      git.gitCurrentBranch,
      BuildInfoKey.action("buildTime") {
        ZonedDateTime
          .now()
          .format(DateTimeFormatter.ofPattern("YYYYMMdd_hhmmss"))
      }
    ),
    version := "0.1.0",
    libraryDependencies := dependencies
  )
