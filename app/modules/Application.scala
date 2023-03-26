package modules

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{FileIO, Framing, Sink}
import akka.util.ByteString
import modules.flows.AppFlows.mappingFlow
import org.slf4j.{Logger, LoggerFactory}
import play.api.mvc.{BaseController, ControllerComponents}

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@Singleton
class Application @Inject()(val ac : ActorSystem,
                             implicit val mat: Materializer,
                             implicit val ec : ExecutionContext,
                             val controllerComponents: ControllerComponents) extends BaseController {

  val logger : Logger = LoggerFactory.getLogger(classOf[Application])

  logger.info("Starting the stream.....")

  FileIO.fromPath(Paths.get("C:\\Play_Framework\\play_basics\\conf\\IPL.csv"))
    .via(Framing.delimiter(ByteString("\n"),4096)
      .map(_.utf8String))
   // .filter(x => x.contains())
    .via(mappingFlow)
  //  .mapAsync(10)(ipl => insertToMySql())
    .runWith(Sink.ignore).onComplete{
    case Success(_) => println("Done")
    case Failure(exception) => exception.getStackTrace()
  }

}

