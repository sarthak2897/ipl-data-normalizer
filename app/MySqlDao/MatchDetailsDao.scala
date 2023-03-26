package MySqlDao

import models.IplDetails
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

  private val teamWinCountDetails : TableQuery[TeamWinCountDetailsTable] = TableQuery[TeamWinCountDetailsTable]

  private class TeamWinCountDetailsTable(tag : Tag) extends Table[(String,Int)](tag, "team_win_counts") {
    def teamName = column[String]("team_name", O.PrimaryKey)
    def totalWins = column[Int]("total_wins")
    def * = (teamName,totalWins)
  }

  private class MatchDetailsTable(tag: Tag) extends Table[IplDetails](tag,
    "match_details") {
    def id = column[Long]("id",O.PrimaryKey)
    def city = column[String]("city")
    def date = column[LocalDate]("date")
    def mom = column[String]("man_of_match")
    def venue = column[String]("venue")
    def team1 = column[String]("first_team")
    def team2 = column[String]("second_team")
    def tossWinner = column[String]("toss_winner")
    def tossDecision = column[String]("toss_decision")
    def winner = column[String]("winner")
    def result = column[String]("result")
    def eliminator = column[String]("eliminator")
    def firstUmpire = column[String]("first_umpire")
    def secondUmpire = column[String]("second_umpire")
    def * = (id,city,date,mom,venue,team1,team2,tossWinner,
      tossDecision,winner,result,eliminator,firstUmpire,secondUmpire)<>
      ((IplDetails.apply _).tupled,IplDetails.unapply)
  }

  def insert(iplDetails :IplDetails) = {
    db.run(DBIO.seq(matchDetails.schema.createIfNotExists, matchDetails += (iplDetails)))
  }

  def getAll() : Future[Seq[IplDetails]] = {
    db.run(matchDetails.result)
  }

  def insertTeamWinCounts(winCountList : List[(String,Int)]) = {
    db.run(DBIO.seq(teamWinCountDetails.schema.createIfNotExists, teamWinCountDetails ++= (winCountList)))
  }

  def fetchTeamWinCounts(): Future[Seq[(String, Int)]] = {
    db.run(teamWinCountDetails.result)
  }

}
