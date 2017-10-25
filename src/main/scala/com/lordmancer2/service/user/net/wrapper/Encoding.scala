package com.lordmancer2.service.user.net.wrapper

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class Encoding extends Wrapper {

  def wrap(route: Route): Route = {
    decodeRequest {
      encodeResponse {
        route
      }
    }
  }

}
