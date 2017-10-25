package com.lordmancer2.service.user.config

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

  lazy val serverHostConfig: String @@ ServerHost = {
    akkaHttpConfig.as[String]("server.host").taggedWith[ServerHost]
  }

  lazy val serverPortConfig: Int @@ ServerPort = {
    akkaHttpConfig.as[Int]("server.port").taggedWith[ServerPort]
  }

}
