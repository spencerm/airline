package controllers

import com.patson.data.{AirplaneSource, LinkSource}
import com.patson.model.{Airline, Computation, Link, LinkClass, LinkClassValues}
import com.patson.util.AirportCache

import scala.collection.mutable.ListBuffer

object LinkUtil {
  def adjustLinksAfterConfigurationChanges(configurationId : Int) = {

    val affectedLinkIds = AirplaneSource.loadAirplanesCriteria(List(("configuration", configurationId))).flatMap { airplane =>
      val linkIdsFlownByThisAirplane = AirplaneSource.loadAirplaneLinkAssignmentsByAirplaneId(airplane.id).assignments.keys
      linkIdsFlownByThisAirplane.toList
    }.toSet

    val affectedLinks = ListBuffer[Link]()
    affectedLinkIds.foreach { linkId =>
      LinkSource.loadFlightLinkById(linkId).foreach { link =>
        affectedLinks.append(link)
      }
    }

    LinkSource.updateLinks(affectedLinks.toList)
  }

  def adjustLinksAfterAirplaneConfigurationChange(airplaneId : Int) = {
    val affectedLinkIds = AirplaneSource.loadAirplaneLinkAssignmentsByAirplaneId(airplaneId).assignedLinkIds

    val affectedLinks = ListBuffer[Link]()
    affectedLinkIds.foreach { linkId =>
      LinkSource.loadFlightLinkById(linkId).foreach { link =>
        affectedLinks.append(link)
      }
    }

    LinkSource.updateLinks(affectedLinks.toList)
  }

  def getFlightCode(airline : Airline, flightNumber : Int) = {
    airline.getAirlineCode + " " + (1000 + flightNumber).toString.substring(1, 4)
  }

  def findExpectedQuality(fromAirportId: Int, toAirportId: Int, queryAirportId: Int): Option[LinkClassValues] = {
    AirportCache.getAirport(fromAirportId) match {
      case Some(fromAirport) =>
        AirportCache.getAirport(toAirportId) match {
          case Some(toAirport) =>
            val flightType = Computation.getFlightType(fromAirport, toAirport)
            val airport = if (fromAirportId == queryAirportId) fromAirport else toAirport
            val classMap: List[(LinkClass, Int)] = LinkClass.values.map { linkClass =>
              linkClass -> airport.expectedQuality(flightType, linkClass)
            }
            Some(LinkClassValues.getInstanceByMap(classMap.toMap))
          case None =>
            None
        }
      case None =>
        None
    }
  }
}
