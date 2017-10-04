package com.lordmancer2.user.config

import com.softwaremill.tagging._
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import net.ceedubs.ficus.Ficus._

trait ConfigModule extends LazyLogging {

  val config: Config @@ GlobalConfig = {
    ConfigFactory.load().taggedWith[GlobalConfig]
  }

  lazy val akkaConfig: Config = {
    config.as[Config]("akka")
  }

  lazy val akkaHttpConfig: Config = {
    akkaConfig.as[Config]("http")
  }

  lazy val serverHostConfig: String @@ ServerHostConfig = {
    akkaHttpConfig.as[String]("server.host").taggedWith[ServerHostConfig]
  }

  lazy val serverPortConfig: Int @@ ServerPortConfig = {
    akkaHttpConfig.as[Int]("server.port").taggedWith[ServerPortConfig]
  }

}
