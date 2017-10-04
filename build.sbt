name := "user"
//version := "0.1"
scalaVersion := "2.12.3"

version := conf.getString("app.version")

libraryDependencies ++= {
  val akkaV = "2.4.19"
  val akkaHttpV = "10.0.10"
  Seq(
    "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
    "com.softwaremill.macwire" %% "util" % "2.3.0",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe" % "config" % "1.3.1",
    "com.iheart" %% "ficus" % "1.4.2",
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.datastax.cassandra" % "cassandra-driver-core" % "3.3.0",
    "com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % "0.13"
  )
}
