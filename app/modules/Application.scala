package modules

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Framing, Keep, Sink}
import akka.stream.{ActorAttributes, Materializer}
import akka.util.ByteString
import models.IplDetails
import modules.flows.AppFlows.{decider, mappingFlow}
import modules.flows.TableGenerator
import org.slf4j.{Logger, LoggerFactory}
import play.api.mvc.{BaseController, ControllerComponents}

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class Application @Inject()(val ac : ActorSystem,
                             implicit val mat: Materializer,
                             implicit val ec : ExecutionContext,
                             val tableGenerator: TableGenerator,
                             val controllerComponents: ControllerComponents) extends BaseController {

  val logger : Logger = LoggerFactory.getLogger(classOf[Application])

  logger.info("Starting the stream.....")

  FileIO.fromPath(Paths.get("C:\\Play_Framework\\play_basics\\conf\\IPL.csv"))
    .via(Framing.delimiter(ByteString("\n"),4096)
      .map(_.utf8String)).drop(1)
    .via(mappingFlow)
    .mapAsync(10)(ipl => tableGenerator.insertDataToTables(ipl))
    .fold(Seq.empty[IplDetails])((acc,n) => acc :+ n)
    .mapAsync(10)(matchesList => tableGenerator.performAggregations(matchesList))
    .toMat(Sink.ignore)(Keep.right)
    .withAttributes(ActorAttributes.supervisionStrategy(decider))
    .run().map(_ => Ok("Done"))

}

