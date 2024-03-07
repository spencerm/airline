package com.patson.model

import com.patson.model.airplane.Model
import com.patson.model.airplane.Model.Type
import FlightType._
import com.patson.model.AirportFeatureType.{AirportFeatureType, DOMESTIC_AIRPORT, FINANCIAL_HUB, GATEWAY_AIRPORT, INTERNATIONAL_HUB, ISOLATED_TOWN, OLYMPICS_IN_PROGRESS, OLYMPICS_PREPARATIONS, UNKNOWN, VACATION_HUB}
import com.patson.model.IsolatedTownFeature.HUB_RANGE_BRACKETS


abstract class AirportFeature {
  val MAX_STRENGTH = 100
  def strength : Int
  //def airportId : Int
  def featureType : AirportFeatureType.Value
  val strengthFactor : Double = strength.toDouble / MAX_STRENGTH
  
  def demandAdjustment(rawDemand : Double, passengerType : PassengerType.Value, airportId : Int, fromAirport : Airport, toAirport : Airport, flightType : FlightType.Value, relationship : Int) : Double

  lazy val getDescription = {
    featureType match {
      case INTERNATIONAL_HUB => "International Hub - More international passengers especially premium."
      case VACATION_HUB => "Vacation Hub - Attracts more tourists"
      case FINANCIAL_HUB => "Financial Hub - Attracts more business passengers"
      case DOMESTIC_AIRPORT => "Domestic Airport – Only accepts small aircraft if flight is international"
      case ISOLATED_TOWN => s"Isolated Airport - Increased demand within ${this.asInstanceOf[IsolatedTownFeature].boostRange}km."
      case GATEWAY_AIRPORT => "Gateway - More passengers with other gateway airports"
      case OLYMPICS_PREPARATIONS => "Preparing the Olympic Games"
      case OLYMPICS_IN_PROGRESS => "Year of the Olympic Games"
      case UNKNOWN => "Unknown"
    }
  }
}

object AirportFeature {
  import AirportFeatureType._
  def apply(featureType : AirportFeatureType, strength : Int) : AirportFeature = {
    featureType match {
      case INTERNATIONAL_HUB => InternationalHubFeature(strength)  
      case VACATION_HUB => VacationHubFeature(strength)
      case FINANCIAL_HUB => FinancialHubFeature(strength)
      case DOMESTIC_AIRPORT => DomesticAirportFeature()
      case GATEWAY_AIRPORT => GatewayAirportFeature()
      case ISOLATED_TOWN => IsolatedTownFeature(strength)
      case OLYMPICS_PREPARATIONS => OlympicsPreparationsFeature(strength)
      case OLYMPICS_IN_PROGRESS => OlympicsInProgressFeature(strength)
    }
  }
}

sealed case class InternationalHubFeature(baseStrength : Int, boosts : List[AirportBoost] = List.empty) extends AirportFeature {
  val featureType = AirportFeatureType.INTERNATIONAL_HUB
  override def demandAdjustment(rawDemand : Double, passengerType : PassengerType.Value, airportId : Int, fromAirport : Airport, toAirport : Airport, flightType : FlightType, relationship : Int) : Double = {
    if (airportId == toAirport.id) { //only affect if as a destination
      val multiplier =
        if (passengerType == PassengerType.BUSINESS) { //more obvious for business travelers
          2  
        } else {
          0.5
        }
      if (flightType == SHORT_HAUL_INTERNATIONAL || flightType == MEDIUM_HAUL_INTERNATIONAL || flightType == SHORT_HAUL_INTERCONTINENTAL || flightType == MEDIUM_HAUL_INTERCONTINENTAL) {
        (rawDemand * (strengthFactor * 0.5) * multiplier).toInt //at MAX_STREGTH, add 1x for business traveler, 0.2x for tourists (short haul)   
      } else if (flightType == LONG_HAUL_INTERNATIONAL || flightType == LONG_HAUL_INTERCONTINENTAL || flightType == ULTRA_LONG_HAUL_INTERCONTINENTAL) {
        (rawDemand * (strengthFactor * 1) * multiplier).toInt //at MAX_STREGTH, add 2x for business traveler, 0.4x for tourists (long haul)
      } else {
        0
      }
    } else {
      0
    }
  }

  override lazy val strength = baseStrength + boosts.filter(_.boostType == AirportBoostType.INTERNATIONAL_HUB).map(_.value).sum.toInt
}

sealed case class VacationHubFeature(baseStrength : Int, boosts : List[AirportBoost] = List.empty) extends AirportFeature {
  val featureType = AirportFeatureType.VACATION_HUB
  override def demandAdjustment(rawDemand : Double, passengerType : PassengerType.Value, airportId : Int, fromAirport : Airport, toAirport : Airport, flightType : FlightType.Value, relationship : Int) : Double = {
    if (toAirport.id == airportId && passengerType == PassengerType.TOURIST) { //only affect if as a destination and tourists
      val goFactor = { //out of how many people, will there be 1 going to this spot per year
        if (flightType == SHORT_HAUL_DOMESTIC) {
          50
        } else if (flightType == MEDIUM_HAUL_DOMESTIC) {
          75
        } else if (flightType == LONG_HAUL_DOMESTIC || flightType == SHORT_HAUL_INTERNATIONAL || flightType == SHORT_HAUL_INTERCONTINENTAL) {
          100
        } else if (flightType == MEDIUM_HAUL_INTERNATIONAL || flightType == MEDIUM_HAUL_INTERCONTINENTAL) {
          150
        } else if (flightType == LONG_HAUL_INTERNATIONAL || flightType == LONG_HAUL_INTERCONTINENTAL) {
          500
        } else {
          // i.e. ULTRA_LONG_HAUL_INTERCONTINENTAL
          700
        }
      }
      (fromAirport.population / goFactor / 52 * fromAirport.income / 50000  * strengthFactor).toInt //assume in a city of 50k income out of goFactor people, 1 will visit this spot at full strength (10)
    } else {
      0
    }
  }

  override lazy val strength = baseStrength + boosts.filter(_.boostType == AirportBoostType.VACATION_HUB).map(_.value).sum.toInt
}

sealed case class FinancialHubFeature(baseStrength : Int, boosts : List[AirportBoost] = List.empty) extends AirportFeature {
  val featureType = AirportFeatureType.FINANCIAL_HUB
  override def demandAdjustment(rawDemand : Double, passengerType : PassengerType.Value, airportId : Int, fromAirport : Airport, toAirport : Airport, flightType : FlightType.Value, relationship : Int) : Double = {
    if (toAirport.id == airportId && passengerType == PassengerType.BUSINESS) { //only affect if as a destination and tourists
      val goFactor = 500 //out of how many people, will there be 1 going to this spot per year

      (fromAirport.population / goFactor / 52 * fromAirport.income / 50000  * strengthFactor).toInt //assume in a city of 50k income out of goFactor people, 1 will visit this spot
    } else {
      0
    }
  }
  override lazy val strength = baseStrength + boosts.filter(_.boostType == AirportBoostType.FINANCIAL_HUB).map(_.value).sum.toInt
}

sealed case class DomesticAirportFeature() extends AirportFeature {
  val featureType = AirportFeatureType.DOMESTIC_AIRPORT
  def strength = 0
  override def demandAdjustment(rawDemand : Double, passengerType : PassengerType.Value, airportId : Int, fromAirport : Airport, toAirport : Airport, flightType : FlightType.Value, relationship : Int) : Double = {
    if (FlightType.getCategory(flightType) == FlightCategory.DOMESTIC) {
      rawDemand / 2
    } else {
       (-1 * rawDemand / 2).toInt
    }
  }
}

sealed case class GatewayAirportFeature() extends AirportFeature {
  val featureType = AirportFeatureType.GATEWAY_AIRPORT
  def strength = 0
  override def demandAdjustment(rawDemand : Double, passengerType : PassengerType.Value, airportId : Int, fromAirport : Airport, toAirport : Airport, flightType : FlightType.Value, relationship : Int) : Double = {
    if (airportId != fromAirport.id) {
      0
    } else if (relationship < 0) {
      0
    } else if (FlightType.getCategory(flightType) == FlightCategory.DOMESTIC) {
      0
    } else {
      if (
        fromAirport.getFeatures().map(_.featureType).contains(AirportFeatureType.GATEWAY_AIRPORT) &&
          toAirport.getFeatures().map(_.featureType).contains(AirportFeatureType.GATEWAY_AIRPORT)
      ) { //extra demand if both airports are gateway, mostly for tiny island nations
        val distance = Computation.calculateDistance(fromAirport, toAirport)
        val base = (fromAirport.power + toAirport.power) / 20000

        if (base >= 1) {
          val distanceMultiplier = {
            if (flightType ==  FlightType.SHORT_HAUL_INTERCONTINENTAL
              || flightType == FlightType.SHORT_HAUL_INTERNATIONAL) {
              4
            } else if (flightType ==  FlightType.MEDIUM_HAUL_INTERNATIONAL) {
              2.5
            } else {
              0.25
            }
          }
          Math.log(base) * distanceMultiplier
        } else {
          0
        }
      } else {
        0
      }
    }
  }
}

object IsolatedTownFeature {
  val HUB_RANGE_BRACKETS = Array(300, 600, 1200, 2400) //if pop not within X km
  val ISOLATED_COUNTRIES = Array("FO", "BS", "KY", "TC", "VC", "GD", "DM", "AG", "MS", "BQ", "BL", "MF", "SX", "AI", "VI", "VG") //always add 1 level, because island countries
}

sealed case class IsolatedTownFeature(strength : Int) extends AirportFeature {
  val featureType = AirportFeatureType.ISOLATED_TOWN
  val boostRange =
    if (strength < HUB_RANGE_BRACKETS.size) { //up to 4
      HUB_RANGE_BRACKETS(strength)
    } else {
      HUB_RANGE_BRACKETS.last + 1000
    }

  import IsolatedTownFeature._
  override def demandAdjustment(rawDemand : Double, passengerType : PassengerType.Value, airportId : Int, fromAirport : Airport, toAirport : Airport, flightType : FlightType.Value, relationship : Int) : Double = {
    val distance = Computation.calculateDistance(fromAirport, toAirport)
    if (distance <= boostRange && relationship >= 3) {
      if (rawDemand < 0.01) { //up to 30
        5 + rawDemand / 0.01 * 25
      } else if (rawDemand <= 0.1) { //up to 60
        30 + rawDemand / 0.1 * 30
      } else { //up to 100
        60 + rawDemand / 0.5 * 40
      }
    } else {
      0
    }
  }
}

sealed case class OlympicsPreparationsFeature(strength : Int) extends AirportFeature {
  val featureType = AirportFeatureType.OLYMPICS_PREPARATIONS
  override def demandAdjustment(rawDemand : Double, passengerType : PassengerType.Value, airportId : Int, fromAirport : Airport, toAirport : Airport, flightType : FlightType.Value, relationship : Int) : Double = {
    0
  }
}

sealed case class OlympicsInProgressFeature(strength : Int) extends AirportFeature {
  val featureType = AirportFeatureType.OLYMPICS_IN_PROGRESS
  override def demandAdjustment(rawDemand : Double, passengerType : PassengerType.Value, airportId : Int, fromAirport : Airport, toAirport : Airport, flightType : FlightType.Value, relationship : Int) : Double = {
    0
  }
}


object AirportFeatureType extends Enumeration {
    type AirportFeatureType = Value
    val INTERNATIONAL_HUB, VACATION_HUB, FINANCIAL_HUB, DOMESTIC_AIRPORT, ISOLATED_TOWN, GATEWAY_AIRPORT, OLYMPICS_PREPARATIONS, OLYMPICS_IN_PROGRESS, UNKNOWN = Value
}