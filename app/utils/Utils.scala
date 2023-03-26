package utils

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {

  def formatTimestamp(date : String) = {
    val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val localDate : LocalDate = LocalDate.parse(date,formatter)
    localDate
  }

  lazy val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false)
}
