package MySqlDao

import models.MatchDetails
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MatchDetailsDao @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                                    implicit val ec : ExecutionContext,
                                    cc : ControllerComponents
                                   ) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._

  private val matchDetails: TableQuery[MatchDetailsTable] = TableQuery[MatchDetailsTable]
//  private val venue: TableQuery[VenueTable] = TableQuery[VenueTable]
//  private val team: TableQuery[TeamTable] = TableQuery[TeamTable]
//
//
//  class VenueTable(tag : Tag) extends Table[Venue](tag,
//  "venues") {
//    def venueId = column[String]("venue_id", O.PrimaryKey)
//    def venueName = column[String]("venue_name")
//    def * = (venueId,venueName) <> ((Venue.apply _).tupled,Venue.unapply)
//  }
//
//  class TeamTable(tag : Tag) extends Table[Team](tag,
//    "teams") {
//    def teamId = column[String]("team_id", O.PrimaryKey)
//    def teamName = column[String]("team_name")
//    def * = (teamId,teamName) <> ((Team.apply _).tupled,Team.unapply)
//  }


  class MatchDetailsTable(tag: Tag) extends Table[MatchDetails](tag,
  "match_details") {
    def matchId = column[Long]("match_id",O.PrimaryKey)
    def city = column[String]("city")
    def date = column[LocalDate]("date")
    def mom = column[String]("man_of_match")
    def venueName = column[String]("venue")
    def team1 = column[String]("first_team")
    def team2 = column[String]("second_team")
    def winner = column[String]("winner")
    def result = column[String]("result")
    def * = (matchId,city,date,mom,venueName,team1,team2,winner,result) <> (
      (MatchDetails.apply _).tupled,MatchDetails.unapply)

//    def venueFk = foreignKey("venue_fk", venueName, venue)(_.venueId, onDelete = ForeignKeyAction.Cascade)
//    def team1Fk = foreignKey("team_fk",team1,team)(_.teamId,onDelete = ForeignKeyAction.Cascade)
//    def team2Fk = foreignKey("team_fk",team1,team)(_.teamId,onDelete = ForeignKeyAction.Cascade)

  }

  def insert(mDetails : MatchDetails) = {
    db.run(DBIO.seq(matchDetails.schema.createIfNotExists, matchDetails += mDetails))
  }

  def getAllMatches() : Future[Seq[MatchDetails]] = {
    db.run(matchDetails.result)
  }
  def getMatchDetailsByTeam(teamName : String) = {
    db.run(matchDetails.filter(_.team1 === teamName).result)
  }

}
