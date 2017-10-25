package com.lordmancer2.service.user

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.lordmancer2.service.user.config.ConfigModule
import com.lordmancer2.service.user.net.{Api, RouteModule, Server, WrapperModule}
import com.softwaremill.macwire.wire

import scala.concurrent.ExecutionContext

trait ServerModule extends RouteModule with WrapperModule with ConfigModule {

  implicit val system: ActorSystem

  implicit val dispatcher: ExecutionContext

  implicit val materializer: ActorMaterializer

  lazy val api: Api = wire[Api]

  lazy val server: Server = wire[Server]

}
