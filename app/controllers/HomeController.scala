package controllers

import MySqlDao.{FullMatchDetailsDao, MatchDetailsDao}
import akka.stream.scaladsl.{FileIO, Framing, Keep, Sink}
import akka.stream.{ActorAttributes, Materializer}
import akka.util.ByteString
import models.IplDetails
import modules.flows.AppFlows.{decider, mappingFlow}
import modules.flows.TableGenerator
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
class HomeController @Inject()(val fullMatchDetailsDao: FullMatchDetailsDao,
                               val matchDetailsDao: MatchDetailsDao,
                               val tableGenerator: TableGenerator,
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

  def processCsv() = Action.async{implicit request : Request[AnyContent] =>
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

  def getMatchDetails() = Action.async{implicit request =>
    fullMatchDetailsDao.getAll().map(x => Ok(Json.toJson(x)))
  }

  def fetchTeamWinCounts() = Action.async{ implicit request =>
    fullMatchDetailsDao.fetchTeamWinCounts().map(x => Ok(Json.toJson(x)))
  }

  def fetchMatchHistoryByTeam(teamName : String) = Action.async{ implicit
                                                              request =>
    matchDetailsDao.getMatchDetailsByTeam(teamName).map(x => Ok(Json.toJson(x)))
  }
}
