package modules

import com.google.inject.AbstractModule
import play.api.Logger
//import org.slf4j.{Logger, LoggerFactory}

class AppModule extends AbstractModule{
  final val logger : Logger = Logger(this.getClass)

  override def configure(): Unit = {
    logger.info("Inside module")
    bind(classOf[Application]).asEagerSingleton()
  }
}
