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
