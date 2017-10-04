package com.lordmancer2.user.net.route

import akka.http.scaladsl.marshalling.{ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future

trait Route extends Directives with StrictLogging {

  def route: server.Route

  def complete[T: ToResponseMarshaller](resource: Future[Option[T]]): server.Route = {
    onSuccess(resource) {
      case Some(t) => complete(ToResponseMarshallable(t))
      case None =>
        complete(StatusCodes.NotFound)
    }
  }

}
