package modules

import com.google.inject.AbstractModule
import org.slf4j.{Logger, LoggerFactory}

class AppModule extends AbstractModule{
  val logger : Logger = LoggerFactory.getLogger(classOf[AppModule])

  override def configure(): Unit = {
    logger.info("Inside module")
    bind(classOf[Application]).asEagerSingleton()
  }
}
