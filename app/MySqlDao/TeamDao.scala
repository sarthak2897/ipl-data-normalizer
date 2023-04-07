package MySqlDao

import models.Team
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TeamDao @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                        implicit val ec : ExecutionContext,
                        cc : ControllerComponents
                               ) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._

  private val team: TableQuery[TeamTable] = TableQuery[TeamTable]

  private class TeamTable(tag : Tag) extends Table[Team](tag,
    "teams") {
    def teamId = column[String]("team_id", O.PrimaryKey)
    def teamName = column[String]("team_name")
    def * = (teamId,teamName) <> ((Team.apply _).tupled,Team.unapply)
  }

  def insert(teamDetails : Team) = {
    db.run(DBIO.seq(team.schema.createIfNotExists, team += (teamDetails)))
  }

  def getAllTeams() : Future[Seq[Team]] = {
    db.run(team.result)
  }
}

