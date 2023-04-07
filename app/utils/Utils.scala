package utils

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {

  def formatTimestamp(date: String) = {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val localDate: LocalDate = LocalDate.parse(date, formatter)
    localDate
  }

  lazy val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def generateVenueId(venueName: String) = {
    venueName.split(" ").flatMap(_.headOption).mkString
  }

  def generateTeamId(teamName : String) = {
    teamName match {
      case "Royal Challengers Bangalore" => "RCB"
      case "Kings XI Punjab" => "KXIP"
      case "Delhi Daredevils" => "DD"
      case "Mumbai Indians" => "MI"
      case "Kolkata Knight Riders" => "KKR"
      case "Rajasthan Royals" => "RR"
      case "Deccan Chargers" => "DCH"
      case "Chennai Super Kings" => "CSK"
      case "Kochi Tuskers Kerala" => "KT"
      case "Pune Warriors" => "PW"
      case "Sunrisers Hyderabad" => "SRH"
      case "Gujarat Lions" => "GL"
      case "Rising Pune Supergiants" | "Rising Pune Supergiant" => "RPS"
      case "Delhi Capitals" => "DC"
      case _ => teamName.split(" ").flatMap(_.headOption).mkString
    }
  }
}
