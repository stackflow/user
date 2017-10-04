package com.lordmancer2.user.net

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.lordmancer2.user.config.{ServerHostConfig, ServerPortConfig}
import com.softwaremill.tagging._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

class Server(api: Api,
             host: String @@ ServerHostConfig,
             port: Int @@ ServerPortConfig)(implicit system: ActorSystem, materializer: ActorMaterializer, dispatcher: ExecutionContext) extends LazyLogging {

  val bind: Future[ServerBinding] = {
    Http(system).bindAndHandle(api.routeFlow, host, port)
  }

  def afterStart(binding: ServerBinding): Unit = {
    logger.info(s"Server started on ${binding.localAddress.toString}")
  }

  def beforeStop(binding: ServerBinding): Future[Unit] = {
    binding.unbind()
  }

}
