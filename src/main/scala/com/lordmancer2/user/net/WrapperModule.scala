package com.lordmancer2.user.net

import com.lordmancer2.user.net.wrapper.{Encoding, Wrapper}
import com.softwaremill.macwire.wire

trait WrapperModule {

  lazy val wrappers: Seq[Wrapper] = {
    Seq(
      wire[Encoding]
    )
  }

}
