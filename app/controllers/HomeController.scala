package controllers

import MySqlDao.MatchDetailsDao
import akka.stream.scaladsl.{FileIO, Framing, Keep, Sink}
import akka.stream.{ActorAttributes, Materializer}
import akka.util.ByteString
import models.IplDetails
import modules.flows.AppFlows.{ decider, mappingFlow, teamWinCountFlow}
import play.api.libs.json.Json
import play.api.mvc._

import java.nio.file.Paths
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val matchDetailsDao: MatchDetailsDao,
                               val controllerComponents: ControllerComponents)
                              (implicit mat: Materializer,ec : ExecutionContext)
  extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def helloPlay() = Action.async{implicit request : Request[AnyContent] =>
    Future{
      Ok("Hello World!")
    }
  }

  def readCsv() = Action.async{implicit request : Request[AnyContent] =>
    FileIO.fromPath(Paths.get("C:\\Play_Framework\\play_basics\\conf\\IPL.csv"))
      .via(Framing.delimiter(ByteString("\n"),4096)
        .filterNot(x => x.contains("id,city"))
        .map(_.utf8String))
      .via(mappingFlow)
      .mapAsync(10)(ipl => matchDetailsDao.insert(ipl).map(_ => ipl))
      .fold(Seq.empty[IplDetails])((acc,n) => acc :+ n)
      .via(teamWinCountFlow)
      .mapAsync(10)(aggResults => matchDetailsDao.insertTeamWinCounts(aggResults))
      .toMat(Sink.ignore)(Keep.right)
      .withAttributes(ActorAttributes.supervisionStrategy(decider))
      .run().map(_ => Ok("Done"))
}

  def getMatchDetails() = Action.async{implicit request =>
      matchDetailsDao.getAll().map(x => Ok(Json.toJson(x)))
  }

  def fetchTeamWinCounts() = Action.async{ implicit request =>
    matchDetailsDao.fetchTeamWinCounts().map(x => Ok(Json.toJson(x)))
  }

  }
