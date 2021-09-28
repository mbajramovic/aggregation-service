name := "aggregation-service-scala"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(

  "com.typesafe.akka" %% "akka-http" % "10.2.6",
  "com.typesafe.akka" %% "akka-stream" % "2.6.8",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.8",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.6",

  "log4j" % "log4j" % "1.2.14",

  "org.apache.kafka" %% "kafka" % "3.0.0",

  "org.scalatest" %% "scalatest" % "3.0.8" % "test",

  "org.json4s" %% "json4s-native" % "4.0.3",
  "org.json4s" %% "json4s-jackson" % "4.0.3"
)