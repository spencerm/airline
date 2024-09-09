package com.patson

import java.util.{ArrayList, Collections}
import com.patson.data.{AirportSource, CountrySource, CycleSource, DestinationSource, EventSource}
import com.patson.model.event.{EventType, Olympics}
import com.patson.model.{PassengerType, _}
import com.patson.model.AirportFeatureType.{AirportFeatureType, DOMESTIC_AIRPORT, FINANCIAL_HUB, GATEWAY_AIRPORT, INTERNATIONAL_HUB, ISOLATED_TOWN, OLYMPICS_IN_PROGRESS, OLYMPICS_PREPARATIONS, UNKNOWN, VACATION_HUB}

import java.util.concurrent.ThreadLocalRandom
import scala.collection.immutable.Map
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.collection.parallel.CollectionConverters._
import scala.util.Random


object DemandGenerator {

  val FIRST_CLASS_INCOME_MAX = 125_000
  val FIRST_CLASS_PERCENTAGE_MAX: Map[PassengerType.Value, Double] = Map(PassengerType.TRAVELER -> 0, PassengerType.BUSINESS -> 0.12, PassengerType.TOURIST -> 0, PassengerType.ELITE -> 1, PassengerType.OLYMPICS -> 0)
  val BUSINESS_CLASS_INCOME_MAX = 125_000
  val BUSINESS_CLASS_PERCENTAGE_MAX: Map[PassengerType.Value, Double] = Map(PassengerType.TRAVELER -> 0.16, PassengerType.BUSINESS -> 0.49, PassengerType.TOURIST -> 0.1, PassengerType.ELITE -> 0, PassengerType.OLYMPICS -> 0.25)
  val DISCOUNT_CLASS_PERCENTAGE_MAX: Map[PassengerType.Value, Double] = Map(PassengerType.TRAVELER -> 0.38, PassengerType.BUSINESS -> 0.09, PassengerType.TOURIST -> 0.6, PassengerType.ELITE -> 0, PassengerType.OLYMPICS -> 0)
  val MIN_DISTANCE = 50
//  val launchDemandFactor : Double = Math.min(1, (45 + CycleSource.loadCycle().toDouble / 24) / 100)
  val launchDemandFactor : Double = 1.0
  val demandRandomizer: Int = CycleSource.loadCycle() % 3

  import scala.collection.JavaConverters._



  def computeDemand(cycle: Int) = {
    println("Loading airports")
    //val allAirports = AirportSource.loadAllAirports(true)
    val airports: List[Airport] = AirportSource.loadAllAirports(true).filter { airport => (airport.iata != "" || airport.icao != "") && airport.power > 0 }
    println("Loaded " + airports.size + " airports")
    
    val allDemands = new ArrayList[(Airport, List[(Airport, (PassengerType.Value, LinkClassValues))])]()
	  
	  val countryRelationships = CountrySource.getCountryMutualRelationships()
    val destinationList = DestinationSource.loadAllEliteDestinations()
	  airports.foreach {  fromAirport =>
	    val demandList = Collections.synchronizedList(new ArrayList[(Airport, (PassengerType.Value, LinkClassValues))]())

      airports.par.foreach { toAirport =>
        val distance = Computation.calculateDistance(fromAirport, toAirport)
        if (fromAirport != toAirport && fromAirport.population != 0 && toAirport.population != 0 && distance >= MIN_DISTANCE) {
          val relationship = countryRelationships.getOrElse((fromAirport.countryCode, toAirport.countryCode), 0)
          val affinity = Computation.calculateAffinityValue(fromAirport.zone, toAirport.zone, relationship)

          val demand = computeBaseDemandBetweenAirports(fromAirport, toAirport, affinity, relationship, distance)
          if (demand.travelerDemand.total > 0) {
            demandList.add((toAirport, (PassengerType.TRAVELER, demand.travelerDemand)))
          }
          if (demand.businessDemand.total > 0) {
            demandList.add((toAirport, (PassengerType.BUSINESS, demand.businessDemand)))
          }
          if (demand.touristDemand.total > 0) {
            demandList.add((toAirport, (PassengerType.TOURIST, demand.touristDemand)))
          }
        }
      }
      val eliteDemand = generateEliteDemand(fromAirport, destinationList)
      val fromDemand = demandList.asScala.toList ++ eliteDemand.getOrElse(List.empty)

	    allDemands.add((fromAirport, fromDemand))
  }

    val allDemandsAsScala = allDemands.asScala
    println(s"generated ${allDemandsAsScala.length} base demand groups; min feature size = $demandRandomizer")

    val eventDemand = generateEventDemand(cycle, airports)
    allDemandsAsScala.appendAll(eventDemand)
    println(s"generated ${eventDemand.length} event demand groups")

//    println("generating elite demand...")
//    val eliteDemand = generateEliteDemand(airports)
//    allDemandsAsScala.appendAll(eliteDemand)
//    println(s"generated ${eliteDemand.length} elite demand groups")

	  val baseDemandChunkSize = 10
	  
	  val allDemandChunks = ListBuffer[(PassengerGroup, Airport, Int)]()
    var oneCount = 0
	  allDemandsAsScala.foreach {
	    case (fromAirport, toAirportsWithDemand) =>
        //for each city generate different preferences
        val flightPreferencesPool = getFlightPreferencePoolOnAirport(fromAirport)

        toAirportsWithDemand.foreach {
          case (toAirport, (passengerType, demand)) =>
            LinkClass.values.foreach { linkClass =>
              if (demand(linkClass) > 0) {
                var remainingDemand = demand(linkClass)
                var demandChunkSize = baseDemandChunkSize + ThreadLocalRandom.current().nextInt(baseDemandChunkSize)
                while (remainingDemand > demandChunkSize) {
                  allDemandChunks.append((PassengerGroup(fromAirport, flightPreferencesPool.draw(passengerType, linkClass, fromAirport, toAirport), passengerType), toAirport, demandChunkSize))
                  remainingDemand -= demandChunkSize
                  demandChunkSize = baseDemandChunkSize + ThreadLocalRandom.current().nextInt(baseDemandChunkSize)
                }
                allDemandChunks.append((PassengerGroup(fromAirport, flightPreferencesPool.draw(passengerType, linkClass, fromAirport, toAirport), passengerType), toAirport, remainingDemand)) // don't forget the last chunk
              }
            }
        }

	  }

    allDemandChunks.toList
  }

  def computeDemandBetweenAirports(fromAirport : Airport, toAirport : Airport, affinity : Int, relationship : Int, distance : Int) : Demand = {
    val demand = if (fromAirport != toAirport && fromAirport.population != 0 && toAirport.population != 0 && distance >= MIN_DISTANCE) {
      computeBaseDemandBetweenAirports(fromAirport: Airport, toAirport: Airport, affinity: Int, relationship: Int, distance: Int): Demand
    } else {
      Demand(
        LinkClassValues(0, 0, 0),
        LinkClassValues(0, 0, 0),
        LinkClassValues(0, 0, 0)
      )
    }
    demand
  }

  def computeBaseDemandBetweenAirports(fromAirport : Airport, toAirport : Airport, affinity : Int, relationship : Int, distance : Int) : Demand = {
    import FlightType._
    val flightType = Computation.getFlightType(fromAirport, toAirport, distance, relationship)
    val hasFirstClass = (flightType == ULTRA_LONG_HAUL_INTERCONTINENTAL || flightType == ULTRA_LONG_HAUL_DOMESTIC || flightType == LONG_HAUL_INTERNATIONAL || flightType == LONG_HAUL_DOMESTIC || flightType == MEDIUM_HAUL_INTERNATIONAL)
    val fromPopIncomeAdjusted = if (fromAirport.popMiddleIncome > 0) fromAirport.popMiddleIncome else 1
    val demand = computeRawDemandBetweenAirports(fromAirport : Airport, toAirport : Airport, affinity : Int, distance : Int)

    //modeling provincial travel dynamics where folks go from small city to big city, but not for tourists
    val maxBonus = 1.5
    val travelerProvincialBonus = if (distance < 2500 && affinity > 1 && toAirport.population > fromPopIncomeAdjusted) {
      math.min(maxBonus, math.pow(toAirport.population / fromPopIncomeAdjusted.toDouble, .1))
    } else {
      1.0
    }
    //lower demand to (boosted) poor places, but not applied on tourists
    val toIncomeAdjust = Math.min(1.0, (toAirport.income.toDouble + 12_000) / 54_000)

    val percentTraveler = Math.min(0.7, fromAirport.income.toDouble / 40_000)

    val demands = Map(PassengerType.TRAVELER -> demand * percentTraveler * travelerProvincialBonus * toIncomeAdjust, PassengerType.BUSINESS -> (demand * (1 - percentTraveler - 0.1) * toIncomeAdjust), PassengerType.TOURIST -> demand * 0.1)

    val featureAdjustedDemands = demands.map { case (passengerType, demand) =>
      val fromAdjustments = fromAirport.getFeatures().map(feature => feature.demandAdjustment(demand, passengerType, fromAirport.id, fromAirport, toAirport, flightType, affinity, distance))
      val toAdjustments = toAirport.getFeatures().map(feature => feature.demandAdjustment(demand, passengerType, toAirport.id, fromAirport, toAirport, flightType, affinity, distance))
      (passengerType, fromAdjustments.sum + toAdjustments.sum + demand)
    }

    //for each trade affinity, add base "trade demand" to biz demand, modded by distance
    val affinityTradeAdjust = if (distance > 400 && (fromAirport.population >= 30000 || toAirport.population >= 30000)) {
      val baseTradeDemand = 7 + (12 - fromAirport.size.toDouble) * 1.5
      val distanceMod = Math.min(1.0, 5000.0 / distance)
      val matchOnlyTradeAffinities = 5
      (baseTradeDemand * distanceMod * Computation.affinityToSet(fromAirport.zone, toAirport.zone, matchOnlyTradeAffinities).length).toInt
    } else {
      0
    }

    Demand(
      computeClassCompositionFromIncome(featureAdjustedDemands.getOrElse(PassengerType.TRAVELER, 0.0), fromAirport.income, PassengerType.TRAVELER, hasFirstClass),
      computeClassCompositionFromIncome(featureAdjustedDemands.getOrElse(PassengerType.BUSINESS, 0.0) + affinityTradeAdjust, fromAirport.income, PassengerType.BUSINESS, hasFirstClass),
      computeClassCompositionFromIncome(featureAdjustedDemands.getOrElse(PassengerType.TOURIST, 0.0), fromAirport.income, PassengerType.TOURIST, hasFirstClass)
    )
  }

  //adds more demand, up to 225
  private def addToVeryLowIncome(fromPop: Long): Int = {
    val minPop = 5e5
    val minDenominator = 15000

    val boost = if (fromPop <= minPop) {
      (fromPop / minDenominator).toInt
    } else {
      val logFactor = 1 + Math.log10(fromPop / minPop)
      val adjustedDenominator = (minDenominator * logFactor)
      (fromPop / adjustedDenominator).toInt + 8
    }
    Math.min(225, boost)
  }


  private def computeRawDemandBetweenAirports(fromAirport : Airport, toAirport : Airport, affinity : Int, distance : Int) : Int = {
    val fromPopIncomeAdjusted = if (fromAirport.popMiddleIncome > 0) fromAirport.popMiddleIncome else 1

    val distanceReducerExponent: Double =
      if (distance < 350 && !List("FO", "BS", "KY", "TC", "VC", "GD", "DM", "AG", "MS", "BQ", "BL", "MF", "SX", "AI", "VI", "VG", "VC", "VU", "WF", "MU", "MV", "CC", "CK", "CV", "ST", "NP").contains(fromAirport.countryCode)) {
        distance.toDouble / 350
      } else if (distance > 5000) {
        1.0 - distance.toDouble / 36000 * (1 - affinity.toDouble / 15.0) //affinity affects perceived distance
      } else if (distance > 2000) { //bit less than medium-distance, with a 0.01 boost
        1.11 - distance.toDouble / 20000 * (1 - affinity.toDouble / 20.0) //affinity affects perceived distance
      } else 1

    //domestic/foreign/affinity relation multiplier
    val airportAffinityMutliplier: Double =
      if (affinity >= 5) (affinity - 5) * 0.05 + 1 //domestic+
      else if (affinity < 0) 0.025
      else affinity * 0.1 + 0.075

    val specialCountryModifier =
      if (fromAirport.countryCode == "AU" || fromAirport.countryCode == "NZ") {
        9.0 //they travel a lot; difficult to model
      } else if (fromAirport.countryCode == "NO" && toAirport.countryCode == "NO") {
        4.5 //very busy domestic routes
      } else if (List("NO", "IS", "FO", "GL", "GR", "CY", "FJ", "KR").contains(fromAirport.countryCode)) {
        2.25 //very high per capita flights https://ourworldindata.org/grapher/air-trips-per-capita
      } else if (List("SE", "GB", "CL", "BS", "AE", "DK").contains(fromAirport.countryCode)) {
        1.5 // high per capita flights
      } else if (List("CD", "CG", "CV", "CI", "GN", "GW", "LR", "ML", "MR", "NE", "SD", "SO", "SS", "TD", "TG").contains(fromAirport.countryCode)) {
        4.0 //very poor roads but unstable governance
      } else if (List("AO", "BI", "BJ", "BW", "CM", "CV", "DJ", "ET", "GA", "GH", "GM", "GQ", "KE", "KM", "LS", "MG", "MU", "MW", "MZ", "NA", "NG", "RW", "SC", "SL", "SN", "ST", "SZ", "TZ", "UG", "ZA", "ZM", "ZW").contains(fromAirport.countryCode)) {
        6.0 //very poor roads
      } else if (fromAirport.countryCode == "IN" && toAirport.countryCode == "IN") {
        0.67 //pops are just very large
      } else if (fromAirport.countryCode == "CN") {
        if(distance < 900) {
          0.53 //China has a very extensive highspeed rail network, pops are just very large
        } else {
          0.7
        }
      } else if (fromAirport.countryCode == "JP" && toAirport.countryCode == "JP" && distance < 500) {
        0.5 //also interconnected by HSR / intercity rail
      } else if (fromAirport.countryCode == "FR" && distance < 550 && toAirport.countryCode != "GB") {
        0.2
      } else if (fromAirport.countryCode == "IT" && distance < 500) {
        0.5
      } else if (distance < 260 && fromAirport.zone.contains("EU")) {
        0.6
      } else 1.0

    //set very low income floor, specifically traffic to/from central airports that is otherwise missing
    val buffLowIncomeAirports = if (fromAirport.income <= 5000 && toAirport.income <= 8000 && distance <= 3000 && (toAirport.size >= 4 || fromAirport.size >= 4)) addToVeryLowIncome(fromAirport.population) else 0

    val domesticDemandFloor = if (distance > 400 && distance < 1500 && affinity >= 5 &&
      ( toAirport.isGateway() || toAirport.size - fromAirport.size >= 6)) {
      20 + ThreadLocalRandom.current().nextInt(40)
    } else {
      0
    }

    val baseDemand : Double = Math.max(domesticDemandFloor, specialCountryModifier * airportAffinityMutliplier * fromPopIncomeAdjusted * toAirport.population.toDouble / 250_000 / 250_000) + buffLowIncomeAirports
    (Math.pow(baseDemand, distanceReducerExponent)).toInt
  }

  private def computeClassCompositionFromIncome(demand: Double, income: Int, passengerType: PassengerType.Value, hasFirstClass: Boolean) : LinkClassValues = {
    val firstClassDemand = if (hasFirstClass) {
        if (income > FIRST_CLASS_INCOME_MAX) {
          demand * FIRST_CLASS_PERCENTAGE_MAX(passengerType)
        } else {
          demand * FIRST_CLASS_PERCENTAGE_MAX(passengerType) * income.toDouble / FIRST_CLASS_INCOME_MAX
        }
      } else {
        0
      }
    val businessClassDemand = if (income > BUSINESS_CLASS_INCOME_MAX) {
        demand * BUSINESS_CLASS_PERCENTAGE_MAX(passengerType)
      } else {
        demand * BUSINESS_CLASS_PERCENTAGE_MAX(passengerType) * income.toDouble / BUSINESS_CLASS_INCOME_MAX
      }
    val discountClassDemand = demand * DISCOUNT_CLASS_PERCENTAGE_MAX(passengerType) * (1 - Math.min(income.toDouble / 30_000, 0.5))
    //adding cutoffs to reduce the tail and have fewer passenger groups to calculate
    val firstClassCutoff = if (firstClassDemand > 1) firstClassDemand else 0
    val businessClassCutoff = if (businessClassDemand > 2) businessClassDemand else 0
    val discountClassCutoff = if (discountClassDemand > 9) discountClassDemand else 0

    val economyClassDemand = Math.max(0, demand - firstClassDemand - businessClassCutoff - discountClassCutoff)
    LinkClassValues.getInstance(economyClassDemand.toInt, businessClassCutoff.toInt, firstClassCutoff.toInt, discountClassCutoff.toInt)
  }

  val ELITE_MIN_GROUP_SIZE = 5
  val ELITE_MAX_GROUP_SIZE = 9
  val CLOSE_DESTINATIONS_RADIUS = 1800

  private def generateEliteDemand(fromAirport : Airport, destinationList : List[Destination] ) : Option[List[(Airport, (PassengerType.Value, LinkClassValues))]] = {
//    val eliteDemands = new ArrayList[(Airport, List[(Airport, (PassengerType.Value, LinkClassValues))])]()

    if (fromAirport.popElite > 0) {
      val demandList = new java.util.ArrayList[(Airport, (PassengerType.Value, LinkClassValues))]()
      val groupSize = ThreadLocalRandom.current().nextInt(ELITE_MIN_GROUP_SIZE, ELITE_MAX_GROUP_SIZE)
      val closeDestinations = destinationList.filter { destination =>
        val distance = Computation.calculateDistance(fromAirport, destination.airport)
        distance >= 100 && distance <= CLOSE_DESTINATIONS_RADIUS
      }
      val farAwayDestinations = destinationList.filter { destination =>
        val distance = Computation.calculateDistance(fromAirport, destination.airport)
        distance > CLOSE_DESTINATIONS_RADIUS
      }

      var numberDestinations = Math.ceil(launchDemandFactor * 0.75 * fromAirport.popElite / groupSize.toDouble).toInt

      while (numberDestinations >= 0) {
        val destination = if (numberDestinations % 2 == 1 && closeDestinations.length > 5) {
          closeDestinations(ThreadLocalRandom.current().nextInt(closeDestinations.length))
        } else {
          farAwayDestinations(ThreadLocalRandom.current().nextInt(farAwayDestinations.length))
        }
        numberDestinations -= 1
        demandList.add((destination.airport, (PassengerType.ELITE, LinkClassValues(0, 0, groupSize))))
      }
      Some(demandList.asScala.toList)
    } else {
      None
    }
  }

  def generateEventDemand(cycle : Int, airports : List[Airport]) : List[(Airport, List[(Airport, (PassengerType.Value, LinkClassValues))])] = {
    val eventDemand = ListBuffer[(Airport, List[(Airport, (PassengerType.Value, LinkClassValues))])]()
    EventSource.loadEvents().filter(_.isActive(cycle)).foreach { event =>
      event match {
        case olympics : Olympics => eventDemand.appendAll(generateOlympicsDemand(cycle, olympics, airports))
        case _ => //
      }

    }
    eventDemand.toList
  }


  val OLYMPICS_DEMAND_BASE = 50000
  def generateOlympicsDemand(cycle: Int, olympics : Olympics, airports : List[Airport]) : List[(Airport, List[(Airport, (PassengerType.Value, LinkClassValues))])]  = {
    if (olympics.currentYear(cycle) == 4) { //only has special demand on 4th year
      val week = (cycle - olympics.startCycle) % Olympics.WEEKS_PER_YEAR //which week is this
      val demandMultiplier = Olympics.getDemandMultiplier(week)
      Olympics.getSelectedAirport(olympics.id) match {
        case Some(selectedAirport) => generateOlympicsDemand(cycle, demandMultiplier, Olympics.getAffectedAirport(olympics.id, selectedAirport), airports)
        case None => List.empty
      }
    } else {
      List.empty
    }
  }

  def generateOlympicsDemand(cycle: Int, demandMultiplier : Int, olympicsAirports : List[Airport], allAirports : List[Airport]) : List[(Airport, List[(Airport, (PassengerType.Value, LinkClassValues))])]  = {
    val totalDemand = OLYMPICS_DEMAND_BASE * demandMultiplier

    val countryRelationships = CountrySource.getCountryMutualRelationships()
    //use existing logic, just scale the total back to totalDemand at the end
    val unscaledDemands = ListBuffer[(Airport, List[(Airport, (PassengerType.Value, LinkClassValues))])]()
    val otherAirports = allAirports.filter(airport => !olympicsAirports.map(_.id).contains(airport.id))

    otherAirports.foreach { airport =>
      val unscaledDemandsOfThisFromAirport = ListBuffer[(Airport, (PassengerType.Value, LinkClassValues))]()
      val fromAirport = airport
      olympicsAirports.foreach {  olympicsAirport =>
        val toAirport = olympicsAirport
        val distance = Computation.calculateDistance(fromAirport, toAirport)
        val relationship = countryRelationships.getOrElse((fromAirport.countryCode, toAirport.countryCode), 0)
        val affinity = Computation.calculateAffinityValue(fromAirport.zone, toAirport.zone, relationship)
        val baseDemand = computeRawDemandBetweenAirports(fromAirport, toAirport, affinity, distance)
        val computedDemand = computeClassCompositionFromIncome(baseDemand, fromAirport.income, PassengerType.OLYMPICS, true)
          if (computedDemand.total > 1 + demandRandomizer) {
          unscaledDemandsOfThisFromAirport.append((toAirport, (PassengerType.OLYMPICS, computedDemand)))
        }
      }
      unscaledDemands.append((fromAirport, unscaledDemandsOfThisFromAirport.toList))
    }

    //now scale all the demands based on the totalDemand
    val unscaledTotalDemands = unscaledDemands.map {
      case (toAirport, unscaledDemandsOfThisToAirport) => unscaledDemandsOfThisToAirport.map {
        case (fromAirport, (passengerType, demand)) => demand.total
      }.sum
    }.sum
    val multiplier = totalDemand.toDouble / unscaledTotalDemands
    println(s"olympics scale multiplier is $multiplier")
    val scaledDemands = unscaledDemands.map {
      case (toAirport, unscaledDemandsOfThisToAirport) =>
        (toAirport, unscaledDemandsOfThisToAirport.map {
          case (fromAirport, (passengerType, unscaledDemand)) =>
            (fromAirport, (passengerType, unscaledDemand * multiplier))
        })
    }.toList

    scaledDemands
  }

  def getFlightPreferencePoolOnAirport(homeAirport : Airport) : FlightPreferencePool = {
    //price mods are also used on frontend for demand estimation
    //modding price by pax type
    val touristMod = PassengerType.priceAdjust(PassengerType.TOURIST)
    val travelerMod = PassengerType.priceAdjust(PassengerType.TRAVELER)
    val defaultMod = PassengerType.priceAdjust(PassengerType.BUSINESS)
    //modding price (sometimes) by class
    val discountPlus = 0.08
    val economyPlus = 0.04
    val businessPlus = 0.08
    val firstPlus = 0.08
    val flightPreferences = Map(
      PassengerType.BUSINESS -> List( //is default, i.e. also elite & olympic
        (DealPreference(homeAirport, DISCOUNT_ECONOMY, defaultMod), 3),
        (DealPreference(homeAirport, DISCOUNT_ECONOMY, defaultMod + discountPlus), 2),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, ECONOMY, defaultMod, loungeLevelRequired = 0), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, ECONOMY, defaultMod, loungeLevelRequired = 0, loyaltyRatio = 1.1), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, ECONOMY, defaultMod, loungeLevelRequired = 0, loyaltyRatio = 1.2), 1),
        (LastMinutePreference(homeAirport, ECONOMY, defaultMod + economyPlus + 0.05, loungeLevelRequired = 0), 1),
        (LastMinutePreference(homeAirport, ECONOMY, defaultMod + economyPlus + 0.1, loungeLevelRequired = 0), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, BUSINESS, defaultMod, loungeLevelRequired = 1), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, BUSINESS, defaultMod, loungeLevelRequired = 2, loyaltyRatio = 1.15), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, BUSINESS, defaultMod, loungeLevelRequired = 2, loyaltyRatio = 1.25), 1),
        (LastMinutePreference(homeAirport, BUSINESS, defaultMod + businessPlus, loungeLevelRequired = 0), 1),
        (LastMinutePreference(homeAirport, BUSINESS, defaultMod + businessPlus + 0.1, loungeLevelRequired = 0), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, FIRST, defaultMod, loungeLevelRequired = 2), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, FIRST, defaultMod + firstPlus, loungeLevelRequired = 4, loyaltyRatio = 1.15), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, FIRST, defaultMod, loungeLevelRequired = 3, loyaltyRatio = 1.25), 1),
        (LastMinutePreference(homeAirport, FIRST, defaultMod + firstPlus, loungeLevelRequired = 1), 1),
        (LastMinutePreference(homeAirport, FIRST, defaultMod + firstPlus + 0.2, loungeLevelRequired = 0), 1),
      ),
      PassengerType.TOURIST -> List(
        (DealPreference(homeAirport, DISCOUNT_ECONOMY, touristMod), 3),
        (DealPreference(homeAirport, DISCOUNT_ECONOMY, touristMod + discountPlus), 2),
        (DealPreference(homeAirport, ECONOMY, touristMod), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, ECONOMY, touristMod + economyPlus, loungeLevelRequired = 0), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, ECONOMY, touristMod + economyPlus, loungeLevelRequired = 0, loyaltyRatio = 1.1), 1),
        (LastMinutePreference(homeAirport, ECONOMY, touristMod - 0.05, loungeLevelRequired = 0), 2),
        (DealPreference(homeAirport, BUSINESS, touristMod), 2),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, BUSINESS, touristMod + businessPlus, loungeLevelRequired = 1), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, BUSINESS, touristMod + businessPlus + 0.16, loungeLevelRequired = 2, loyaltyRatio = 1.15), 1),
        (LastMinutePreference(homeAirport, BUSINESS, touristMod - 0.05, loungeLevelRequired = 1), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, FIRST, touristMod, loungeLevelRequired = 2, loyaltyRatio = 1.1), 1),
      ),
      PassengerType.TRAVELER -> List(
        (DealPreference(homeAirport, DISCOUNT_ECONOMY, travelerMod), 3),
        (DealPreference(homeAirport, DISCOUNT_ECONOMY, travelerMod + discountPlus), 2),
        (DealPreference(homeAirport, ECONOMY, travelerMod), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, ECONOMY, travelerMod, loungeLevelRequired = 0), 2),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, ECONOMY, travelerMod + economyPlus, loungeLevelRequired = 0, loyaltyRatio = 1.1), 1),
        (LastMinutePreference(homeAirport, ECONOMY, travelerMod + economyPlus + 0.06, loungeLevelRequired = 0), 1),
        (DealPreference(homeAirport, BUSINESS, travelerMod), 1),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, BUSINESS, travelerMod, loungeLevelRequired = 1), 2),
        (AppealPreference.getAppealPreferenceWithId(homeAirport, BUSINESS, travelerMod + businessPlus, loungeLevelRequired = 1, loyaltyRatio = 1.2), 1),
        (LastMinutePreference(homeAirport, BUSINESS, travelerMod + businessPlus + 0.16, loungeLevelRequired = 0), 1),
      )
    )

    new FlightPreferencePool(flightPreferences)
  }

  sealed case class Demand(travelerDemand: LinkClassValues, businessDemand : LinkClassValues, touristDemand : LinkClassValues)
}
