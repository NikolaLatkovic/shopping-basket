import scala.io.Source

// Version configuration
val scalaVersionValue = "2.13.8"      // Compatible with Java 11+, widely available
val sbtVersionValue = "1.8.0"         // Stable version, widely available
val scalatestVersionValue = "3.2.15"  // Compatible with Scala 2.13.8

// Load project version from VERSION file
val versionFile = file("VERSION")
val projectVersionValue = Source.fromFile(versionFile).getLines().next().trim

ThisBuild / organization := "com.shoppingbasket"
ThisBuild / scalaVersion := scalaVersionValue

lazy val root = (project in file("."))
  .settings(
    name := "shopping-basket",
    version := projectVersionValue,
    
    // Compiler settings
    scalacOptions ++= Seq(
      "-deprecation",        // Warn about deprecated APIs to help maintain code quality
      "-feature",            // Warn about language features that require explicit import
      "-unchecked",          // Warn about unchecked type operations (e.g., type erasure)
      "-Xlint",              // Enable additional compiler warnings for common issues
      "-Ywarn-dead-code",    // Warn about dead code that can never be executed
      "-Ywarn-unused"        // Warn about unused imports, parameters, and variables
    ),
    
    // Library dependencies
    libraryDependencies ++= Seq(
      // Testing framework
      "org.scalatest" %% "scalatest" % scalatestVersionValue % Test,
      "org.scalatest" %% "scalatest-flatspec" % scalatestVersionValue % Test,
      "org.scalatest" %% "scalatest-shouldmatchers" % scalatestVersionValue % Test
    ),
    
    // Test configuration
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),
    
    // Main class for running the application
    Compile / mainClass := Some("com.shoppingbasket.PriceBasket"),
    
    // Assembly plugin configuration for creating JAR files
    assembly / mainClass := Some("com.shoppingbasket.PriceBasket"),
    assembly / assemblyJarName := s"shopping-basket-${projectVersionValue}.jar",
    
    // Scoverage configuration - exclude main entry points and error handling that calls sys.exit
    coverageExcludedPackages := "com\\.shoppingbasket\\.PriceBasket",
    coverageMinimumStmtTotal := 70,
    coverageMinimumBranchTotal := 70,
    coverageFailOnMinimum := true
  )

// Enable parallel execution of tests
Test / parallelExecution := true

// Fork tests in separate JVM
Test / fork := true
