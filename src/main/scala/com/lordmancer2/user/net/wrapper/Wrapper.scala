package com.lordmancer2.user.net.wrapper

import akka.http.scaladsl.server.Route

trait Wrapper {

  def wrap(route: Route): Route

}
