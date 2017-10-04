package com.lordmancer2.user.net

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.RoutingSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.lordmancer2.user.net.wrapper.Wrapper

import scala.concurrent.ExecutionContext

class Api(routes: Seq[route.Route], wrappers: Seq[Wrapper])
         (implicit system: ActorSystem, dispatcher: ExecutionContext, materializer: ActorMaterializer) {

  implicit private val routingSettings: RoutingSettings = RoutingSettings.default

  private val allWrappers = {
    wrappers.foldLeft[Route => Route](Route.apply) { (builder, wrapper) =>
      builder.compose(wrapper.wrap)
    }
  }

  private val allRoutes = {
    routes.foldRight[Route](reject) { (partial, builder) =>
      partial.route ~ builder
    }
  }

  val route: Route = allWrappers(allRoutes)

  val routeFlow: Flow[HttpRequest, HttpResponse, NotUsed] = Route.handlerFlow(route)

}
