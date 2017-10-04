package com.lordmancer2.user.net

import com.lordmancer2.user.net.route.{ControlRoute, Route}
import com.softwaremill.macwire.wire

trait RouteModule {

  lazy val routes: Seq[Route] = {
    Seq(
      wire[ControlRoute]
    )
  }

}
