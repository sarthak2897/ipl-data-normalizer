package MySqlDao

import models.Venue
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueDao @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                         implicit val ec : ExecutionContext,
                         cc : ControllerComponents
                               ) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._

  private val venue: TableQuery[VenueTable] = TableQuery[VenueTable]

  class VenueTable(tag : Tag) extends Table[Venue](tag,
    "venues") {
    def venueId = column[String]("venue_id", O.PrimaryKey)
    def venueName = column[String]("venue_name")
    def * = (venueId,venueName) <> ((Venue.apply _).tupled,Venue.unapply)
  }

  def insert(venueDetails : Venue) = {
    db.run(DBIO.seq(venue.schema.createIfNotExists, venue += (venueDetails)))
  }

  def getAllVenues() : Future[Seq[Venue]] = {
    db.run(venue.result)
  }
}
