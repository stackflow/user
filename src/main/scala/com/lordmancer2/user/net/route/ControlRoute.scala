package com.lordmancer2.user.net.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server

class ControlRoute extends Route {

  def statusRoute: server.Route = {
    path("status") {
      get {
        complete(StatusCodes.OK, "Ok")
      }
    }
  }

  val route: server.Route = pathPrefix("control") {
    statusRoute
  }

}
