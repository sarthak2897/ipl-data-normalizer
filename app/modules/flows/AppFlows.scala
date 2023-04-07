package modules.flows

import akka.NotUsed
import akka.stream.Supervision
import akka.stream.scaladsl.Flow
import models.{IplDetails, MatchDetails, Team, Venue}
import org.slf4j.{Logger, LoggerFactory}
import utils.Utils.{formatTimestamp, generateTeamId, generateVenueId}

import java.sql.SQLIntegrityConstraintViolationException

object AppFlows {

  val log : Logger = LoggerFactory.getLogger(AppFlows.getClass)

def toIPLDetails(matchList : List[String]): IplDetails = {
    IplDetails(id = matchList.headOption.map(_.toLong).getOrElse(0L),
      city = matchList(1), date = formatTimestamp(matchList(2)),
      mom = matchList(3), venue = matchList(4).replace("\"",""),
      team1 = matchList(6), team2 = matchList(7), tossWinner = matchList(8),
      tossDecision = matchList(9), winner = matchList(10),
      result = matchList(12)+" "+matchList(11),
      eliminator = matchList(13),
      umpire1 = matchList(15), umpire2 = matchList(16))
}

  def toVenue(iplDetails: IplDetails) : Venue = {
    Venue(venueId = generateVenueId(iplDetails.venue),venueName = iplDetails.venue)
  }

  def toTeam(iplDetails: IplDetails) = {
    Team(teamId = generateTeamId(iplDetails.team1), teamName = iplDetails.team1)
  }

  def toMatchDetails(iplDetails: IplDetails) ={
    MatchDetails(matchId = iplDetails.id,city = iplDetails.city, date =
      iplDetails.date,venue = generateVenueId(iplDetails.venue),
      mom = iplDetails.mom,team1 = generateTeamId(iplDetails.team1),
      team2 = generateTeamId(iplDetails.team2),winner = generateTeamId(iplDetails.winner),
      result = iplDetails.result)
  }

  val mappingFlow: Flow[String, IplDetails, NotUsed] = Flow[String].map(line => {
    toIPLDetails(line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").toList)
  })

def toTotalTeamWinCounts(iplDetailsList : Seq[IplDetails]) = {
  iplDetailsList.groupBy(_.winner).map(z => (z._1,z._2.size)).toList
}

//  val teamWinCountFlow: Flow[Seq[IplDetails], List[(String,Int)], NotUsed] =
//    Flow[Seq[IplDetails]].map(matchDetailsList => {
//      matchDetailsList.groupBy(_.winner).map(z => (z._1,z._2.size)).toList
//  })

  val decider : Supervision.Decider = {
    case e : NumberFormatException =>
      e.printStackTrace();
      Supervision.Resume
    case e : SQLIntegrityConstraintViolationException => Supervision.Resume
    case e : Exception =>
      e.printStackTrace();
      Supervision.Stop
  }
}
