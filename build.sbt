organization := "com.lordmancer2"
name := "user"
version := "0.1.0"
scalaVersion := "2.12.4"

libraryDependencies ++= {
  val akkaV = "2.4.20"
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

mappings in(Compile, packageBin) += ((resourceDirectory in Compile).value / s"${buildEnv.value.toString.toLowerCase}.conf") -> "application.conf"
mappings in(Compile, packageBin) += ((resourceDirectory in Compile).value / s"logback-${buildEnv.value.toString.toLowerCase}.xml") -> "logback.xml"

enablePlugins(DockerPlugin, TemplatingPlugin)

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

// specify the docker registry to which your images will be pushed
dockerRegistry := sys.env.getOrElse("DOCKER_REGISTRY", "registry.mobak.ru:5443")

val dockerImageName = settingKey[ImageName]("Docker image name")
dockerImageName := ImageName(
  registry = Some(dockerRegistry.value),
  namespace = Some(s"${buildEnv.value.toString.toLowerCase}/lordmancer2/service"),
  repository = "user",
  tag = Some("latest")
)

// Set names for the image
imageNames in docker := Seq(dockerImageName.value)

// specify the url of your Marathon service
marathonServiceUrl := ""

marathonApplicationId := s"/${buildEnv.value.toString.toLowerCase}/${name.value}"

sourceDirectory in Templating := (sourceDirectory in Compile).value / "templates"
target in Templating := (sourceDirectory in Compile).value / "generated"

marathonTemplates := {
  val httpPort = sys.env.getOrElse("HTTP_PORT", "8080").toInt
  val cpu = 0.1
  val memory = 128

  val javaToolOptions = s"-Xms${memory}m -Xmx${memory}m -Dapplication.name=${name.value} -Dapplication.environment=${buildEnv.value.toString.toLowerCase}"
  val jenkinsBuildId = sys.env.getOrElse("JENKINS_BUILD_ID", "")
  val enviroments = Seq(
    "JAVA_TOOL_OPTIONS" -> javaToolOptions,
    "JENKINS_BUILD_NUMBER" -> jenkinsBuildId
  )

  Seq(Template(
    file = (sourceDirectory in Templating).value / "marathon.scala.json",
    driver = new {
      val appId = marathonApplicationId.value
      val cpus = cpu
      val mem = 2 * memory
      val env = "{\n" + enviroments.map(env => "\"" + env._1 + "\": \"" + env._2 + "\"").mkString(",\n") + "\n}"
      val containerDockerImage = dockerImageName.value.toString()
      val containerDockerPort = httpPort
      val uris = "file:///etc/docker.tar.gz"
    }
  ))
}
