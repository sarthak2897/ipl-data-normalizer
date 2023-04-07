package modules.flows

import MySqlDao.{FullMatchDetailsDao, MatchDetailsDao, TeamDao, VenueDao}
import akka.Done
import akka.stream.Materializer
import models.IplDetails
import modules.flows.AppFlows.{toMatchDetails, toTeam, toTotalTeamWinCounts, toVenue}
import org.slf4j.{Logger, LoggerFactory}
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TableGenerator @Inject()(val venueDao: VenueDao,
                               val fullMatchDetailsDao: FullMatchDetailsDao,
                               val teamDao: TeamDao,
                               val matchDetailsDao : MatchDetailsDao,
                               val controllerComponents: ControllerComponents)
                              (implicit mat: Materializer,ec : ExecutionContext)
  extends BaseController {

  val log : Logger = LoggerFactory.getLogger(classOf[TableGenerator])

  var teamsList = List()
  var venuesList = List()

  def insertDataToTables(iplDetails: IplDetails) = {
    for {
      //Creating and inserting match details in denormalized manner in match_details table
      _ <- fullMatchDetailsDao.insert(iplDetails)
      //Creating and inserting venue data into venues table
      _ <- createInsertVenue(iplDetails)
      //Creating and inserting team names data into teams table
      _ <- createInsertTeam(iplDetails)
      //Creating and inserting match details data into match_details
      _ <- createInsertMatchDetails(iplDetails)
    } yield iplDetails
  }

  def performAggregations(iplDetailsList : Seq[IplDetails]) = {
    for {
      _ <- totalTeamWinCounts(iplDetailsList)
      _ <- winnersBySeason(iplDetailsList)
    } yield Done
  }

  def totalTeamWinCounts(iplDetailsList: Seq[IplDetails]) = {
    fullMatchDetailsDao.insertTeamWinCounts(toTotalTeamWinCounts(iplDetailsList))
  }

  def winnersBySeason(iplDetailsList : Seq[IplDetails]) = {
    val winnersBySeason = iplDetailsList.groupBy(ipl => (ipl.date.getYear,ipl
      .team1)).map(x => (x._1._1,x._1._2,x._2.filter(i => i.team1 == i
      .winner).toList.size)).toList
    fullMatchDetailsDao.insertWinnersByYear(winnersBySeason)
  }

  def createInsertVenue(iplDetails: IplDetails) = {
    if(!venuesList.contains(iplDetails.venue)){
      val venue = toVenue(iplDetails)
      venueDao.insert(venue)
    }
    else venuesList :+ iplDetails.venue
    Future(Done.done())
  }

  def createInsertTeam(iplDetails: IplDetails) = {
    if(!teamsList.contains(iplDetails.team1)){
      val team = toTeam(iplDetails)
      teamDao.insert(team)
    }else teamsList :+ iplDetails.team1
    Future(Done.done())
  }

  def createInsertMatchDetails(iplDetails: IplDetails) ={
    val matches = toMatchDetails(iplDetails)
    matchDetailsDao.insert(matches)
  }


}
