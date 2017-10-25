package com.lordmancer2.service.user

import com.typesafe.scalalogging.LazyLogging

object App extends App with AkkaModule with ServerModule with LazyLogging {

  server.bind.foreach { binding =>
    server.afterStart(binding)
    sys.addShutdownHook {
      server.beforeStop(binding) onComplete { _ =>
        system.terminate()
        logger.info(s"Akka system has been terminated")
      }
    }
  }

}
