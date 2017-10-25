package com.lordmancer2.service.user.net

import com.lordmancer2.service.user.net.wrapper.{Encoding, Wrapper}
import com.softwaremill.macwire.wire

trait WrapperModule {

  lazy val wrappers: Seq[Wrapper] = {
    Seq(
      wire[Encoding]
    )
  }

}
