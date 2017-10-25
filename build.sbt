import java.util.Date

organization := "com.lordmancer2"
name := "user"
version := "0.1.0"
scalaVersion := "2.12.4"

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

mappings in(Compile, packageBin) += {
  // logic like this belongs into an AutoPlugin
  val confFile = buildEnv.value match {
    case BuildEnv.Development => "development.conf"
    case BuildEnv.Test => "test.conf"
    case BuildEnv.Stage => "stage.conf"
    case BuildEnv.Production => "production.conf"
  }
  ((resourceDirectory in Compile).value / confFile) -> "application.conf"
}

enablePlugins(DockerPlugin, MarathonPlugin)

// Define a Dockerfile
dockerfile in docker := {
  val targetDir = "/app/"
  val jarFile = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"$targetDir${jarFile.getName}"
  new Dockerfile {
    // Base image
    from("openjdk:8-jre")
    // Add all files on the classpath
    add(classpath.files, targetDir)
    // Add the JAR file
    add(jarFile, jarTarget)
    // On launch run Java with the classpath and the main class
    entryPoint("java", "-cp", s".$targetDir*", mainclass)
  }
}

// specify the url of your Marathon service
marathonServiceUrl := "http://localhost:8080"

// specify the docker registry to which your images will be pushed
dockerRegistry := "registry.mobak.ru:5443"

val dockerImageName = settingKey[ImageName]("Docker image name")
dockerImageName := ImageName(
  registry = Some(dockerRegistry.value),
  namespace = Some(s"lordmancer2/${buildEnv.value.toString.toLowerCase}/service"),
  repository = "user",
  tag = Some("latest")
)

// Set names for the image
imageNames in docker += dockerImageName.value

marathonApplicationId := s"/${buildEnv.value.toString.toLowerCase}/${name.value}"

marathonServiceRequest := sbtmarathon.adt.Request.newBuilder()
  .withId(marathonApplicationId.value)
  .withContainer(
    sbtmarathon.adt.DockerContainer(
      image = dockerImageName.value.toString,
      network = "BRIDGE"
    )
      //    .addParameter("v", "/var/run/docker.sock:/var/run/docker.sock")
      .addPortMapping(8080, 0, None, "tcp")
  )
  .withCpus(.1)
  .withMem(0)
  .addEnv("timestamp", new Date().toString)
  .addFetch(sbtmarathon.adt.Fetchable(uri = "file:///root/.dockercfg"))
  .addHealthCheck(sbtmarathon.adt.HealthCheck.fromJsonString("    {\n      \"protocol\": \"HTTP\",\n      \"path\": \"/status\",\n      \"portIndex\": 0,\n      \"gracePeriodSeconds\": 300,\n      \"intervalSeconds\": 60,\n      \"timeoutSeconds\": 30,\n      \"maxConsecutiveFailures\": 3\n    }"))
  .build()