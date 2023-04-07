package models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class IplDetails(id : Long,
                      city : String,
                      date : LocalDate,
                      mom : String,
                      venue : String,
                      team1 : String,
                      team2 : String,
                      tossWinner : String,
                      tossDecision : String,
                      winner : String,
                      result : String,
                      eliminator : String,
                      umpire1 : String,
                      umpire2 : String)

object IplDetails {
  implicit val iplDetailsFormat: OFormat[IplDetails] = Json.format[IplDetails]
}

case class Venue(venueId : String, venueName : String)

case class Team(teamId : String,teamName : String)

case class MatchDetails(matchId : Long,
                        city : String,
                        date : LocalDate,
                        mom : String,
                        venue : String,
                        team1 : String,
                        team2 : String,
                        winner : String,
                        result : String
                       )

object MatchDetails{
  implicit val matchDetailsFormat: OFormat[MatchDetails] = Json.format[MatchDetails]
}
