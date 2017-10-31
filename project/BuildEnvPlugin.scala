import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

/** sets the build environment */
object BuildEnvPlugin extends AutoPlugin {

  // make sure it triggers automatically
  override def trigger = AllRequirements

  override def requires = JvmPlugin

  object autoImport {

    object BuildEnv extends Enumeration {
      val Master, Beta, Dev, Local = Value
    }

    val buildEnv = settingKey[BuildEnv.Value]("the current build environment")

  }

  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    buildEnv := {
      sys.props.get("env")
        .orElse(sys.env.get("BUILD_ENV"))
        .flatMap {
          case "master" => Some(BuildEnv.Master)
          case "beta" => Some(BuildEnv.Beta)
          case "dev" => Some(BuildEnv.Dev)
          case "local" => Some(BuildEnv.Local)
          case unkown => None
        }
        .getOrElse(BuildEnv.Local)
    },
    // give feed back
    onLoadMessage := {
      // depend on the old message as well
      val defaultMessage = onLoadMessage.value
      val env = buildEnv.value
      s"""|$defaultMessage
          |Running in build environment: $env""".stripMargin
    }
  )

}
