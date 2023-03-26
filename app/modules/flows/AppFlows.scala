package modules.flows

import akka.NotUsed
import akka.stream.Supervision
import akka.stream.scaladsl.Flow
import models.IplDetails
import utils.Utils.formatTimestamp

object AppFlows {

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
  val mappingFlow: Flow[String, IplDetails, NotUsed] = Flow[String].map(line => {
    toIPLDetails(line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)").toList)
  })

  val teamWinCountFlow: Flow[Seq[IplDetails], List[(String,Int)], NotUsed] =
    Flow[Seq[IplDetails]].map(matchDetailsList => {
      matchDetailsList.groupBy(_.winner).map(z => (z._1,z._2.size)).toList
  })

  val decider : Supervision.Decider = {
    case _ : NumberFormatException => Supervision.Resume
    case _ : Exception => Supervision.Stop
  }
}
