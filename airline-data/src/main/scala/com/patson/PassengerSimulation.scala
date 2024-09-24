

package com.patson

import java.util.{ArrayList, Collections}
import java.util.concurrent.atomic.AtomicInteger
import com.patson.data.{AirlineSource, AirportSource, AllianceSource, CountrySource, CycleSource, LinkSource}
import com.patson.model.AirlineBaseSpecialization.BrandSpecialization
import com.patson.model.FlightType.Value
import com.patson.model._
import FlightPreferenceType._

import java.util
import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Set}
import scala.util.Random
import scala.collection.parallel.CollectionConverters._
import scala.jdk.CollectionConverters._

object PassengerSimulation {

//  implicit val actorSystem = ActorSystem("rabbit-akka-stream")

//  import actorSystem.dispatcher

//  implicit val materializer = FlowMaterializer()
  
  val countryOpenness : Map[String, Int] = CountrySource.loadAllCountries().map( country => (country.countryCode, country.openness)).toMap
  
  def testFlow() = {

    //val airportGroups = getAirportGroups(airportData)
    //println("Using " + airportData.size + " airport data");
    
    //val demand = Await.result(DemandGenerator.computeDemand(), Duration.Inf)
    val demand = DemandGenerator.computeDemand(0)
    println("DONE with demand total demand: " + demand.foldLeft(0) {
      case(holder, (_, _, demandValue)) =>  
        holder + demandValue
    })

//    val airportData = AirportSource.loadAllAirports().filter( _.size >= 2)
//    val links = generateFlightLinks(airportData)
//    println("Generated " + links.size + " links")
    
    val links = LinkSource.loadAllLinks(LinkSource.FULL_LOAD)
    
    val consumptionResult = passengerConsume(demand, links)
    
    println("Consumption result: ")
    val soldLinks = links.filter{ link => link.getTotalSoldSeats > 0  }.map { link =>
      (link, link.getTotalSoldSeats)
      }.sortBy {
        case (_, soldSeats) => soldSeats 
      }
      
    soldLinks.foreach{ case(link, soldSeats) => println(link.airline.name + "($" + link.price + "; recommend $" + link.standardPrice(ECONOMY) + ") " + soldSeats  + " : " + link.from.name + " => " + link.to.name) }
    println("seats sold: " + soldLinks.foldLeft(0) {
      case (holder, (link, soldSeats)) => holder + soldSeats
    })
    
    
    //test
    //findShortestRoute(airportGroups(0)(0), airportGroups(4)(0), links.toList)
    //10 random
    //findRandomRoutes(airportGroups(0)(0), airportGroups(4)(0), links.toList, 10)
  }
  case class PassengerConsumptionResult(consumptionByRoutes : Map[(PassengerGroup, Airport, Route), Int], missedDemand : Map[(PassengerGroup, Airport), Int])

  def passengerConsume[T <: Transport](demand : List[(PassengerGroup, Airport, Int)], links : List[T]) : PassengerConsumptionResult = {
    val consumptionResult = Collections.synchronizedList(new ArrayList[(PassengerGroup, Airport, Int, Route)]())
    val missedDemandChunks = Collections.synchronizedList(new ArrayList[(PassengerGroup, Airport, Int)]())
    val consumptionCycleMax = 9; //try and rebuild routes 10 times
    var consumptionCycleCount = 0;
    //start consumption cycles

    //find all active Airports
    val activeAirportIds = Set[Int]()
    val activeAirlineIds = Set[Int]()
    links.foreach { link =>
      activeAirportIds.add(link.from.id)
      activeAirportIds.add(link.to.id)
      activeAirlineIds.add(link.airline.id)
    }
    println("Total active airports: " + activeAirportIds.size)

    println("Remove demand groups not covered by active airports, before " + demand.size);

    //randomize the demand chunks so later on it's consumed in a random (relatively even) manner
    var demandChunks = Random.shuffle(demand.filter { demandChunk =>
      val (passengerGroup, toAirport, chunkSize) = demandChunk
      val isConnected = activeAirportIds.contains(passengerGroup.fromAirport.id) && activeAirportIds.contains(toAirport.id)
      if (!isConnected) {
        missedDemandChunks.add(demandChunk)
      }
      isConnected
    }).sortWith {
      case ((pg1, _, demand1), (pg2, _, demand2)) =>
        pg1.preference.getPreferenceType.priority < pg2.preference.getPreferenceType.priority //sort by pax preference purchase order
    }

    println("After pruning : " + demandChunks.size);

    val establishedAlliances = AllianceSource.loadAllAlliances().filter(_.status == AllianceStatus.ESTABLISHED)
    val establishedAllianceIdByAirlineId : java.util.Map[Int, Int] = new java.util.HashMap[Int, Int]()

    establishedAlliances.foreach { alliance =>
      alliance.members.filter { member =>
        member.role != AllianceRole.APPLICANT && activeAirlineIds.contains(member.airline.id)
      }.foreach(member => establishedAllianceIdByAirlineId.put(member.airline.id, alliance.id))
    }

    val currentCycle = CycleSource.loadCycle()
    val airlineCostModifiers = AirlineSource.loadAirlineModifiers().filter {
      case (airlineId, modifier) => modifier.modifierType == AirlineModifierType.NERFED && activeAirlineIds.contains(airlineId)
    }.map {
      case (airlineId, modifier) => (airlineId, modifier.asInstanceOf[NerfedAirlineModifier].costMultiplier(currentCycle))
    }.toMap

    println(s"Simple Cost modifiers $airlineCostModifiers")

    val specializationCostModifiers : Map[(Int, Int), SpecializationModifier] = AirportSource.loadAllAirportBaseSpecializations.filter{
      case(airline, airport, specialization) =>
        activeAirlineIds.contains(airline.id) && specialization == BaseSpecializationType.BRANDING
    }.map {
      case (airline, airport, brandingSpecialization) => ((airline.id, airport.id), SpecializationModifier(brandingSpecialization.asInstanceOf[BrandSpecialization].linkCostDeltaByClass))
    }.toMap

    //specializationCostModifiers : Map[(Int, Int), Double] = Map.empty, //(airlineId , airportId) -> modifier

    val externalCostModifier = ExternalCostModifier(airlineCostModifiers, specializationCostModifiers)

    while (consumptionCycleCount <= consumptionCycleMax) {
      println(s"Run cycle $consumptionCycleCount for ${demandChunks.size} demand chunks")

      //using minSeats to have pax book together and decrease consumptions
      val minSeats: Int = (consumptionCycleMax - consumptionCycleCount) % 3 * 4
      //remove links without enough seats
      val availableLinks = links.filter {
        _.getTotalAvailableSeats > minSeats * 3
      }
      println(s"available links: ${availableLinks.size} of ${links.size}")
      
      val (filteredDemandChunks, demandChunksForLater) =
        if (consumptionCycleCount >= 4) { //don't ticket everyone to start
          demandChunks.partition {
            case (_, _, chunkSize) => chunkSize > minSeats
          }
        } else {
          demandChunks.partition {
            case (paxGroup, _, chunkSize) => chunkSize > minSeats && paxGroup.preference.getPreferenceType.priority <= 3
          }
        }
      val remainingDemandChunks = Collections.synchronizedList(new util.ArrayList[(PassengerGroup, Airport, Int)]())
      remainingDemandChunks.addAll(demandChunksForLater.asJava)
      println("Demand chunks saved for later: " + remainingDemandChunks.size)

      //find out required routes - which "to airports" does each passengerGroup has
      print("Find required routes...")
      val requiredRoutes = scala.collection.mutable.Map[PassengerGroup, Set[Airport]]()
      filteredDemandChunks.foreach {
        case (passengerGroup, toAirport, _) =>
          val toAirports: Set[Airport] = requiredRoutes.getOrElseUpdate(passengerGroup, scala.collection.mutable.Set[Airport]())
          toAirports.add(toAirport)
      }
      println("Done!")


      //og AC at 4, 5, 6
      val iterationCount =
        if (consumptionCycleCount < 5) 3
        else if (consumptionCycleCount < 7) 4
        else 5
      val allRoutesMap = mutable.HashMap[PassengerGroup, Map[Airport, Route]]()

      //start consuming routes
      //       println()
      //       print("Start to go through demand chunks and comsume...nom nom nom...")


      println("Total passenger groups: " + requiredRoutes.size)
      println(s"Hops: $iterationCount; calculating chunks >= $minSeats size")
      val counter = new AtomicInteger(0)
      val progressCount = new AtomicInteger(0)
      val progressChunk = requiredRoutes.size / 100

      filteredDemandChunks.par.foreach {
        case (passengerGroup, toAirport, chunkSize) =>
          var hasComputedRouteMap = false
          val toAirportRouteMap = allRoutesMap.getOrElseUpdate(passengerGroup, {
            hasComputedRouteMap = true
            findRoutesByPassengerGroup(passengerGroup, toAirports = requiredRoutes(passengerGroup), availableLinks, PassengerSimulation.countryOpenness, establishedAllianceIdByAirlineId, Some(externalCostModifier), iterationCount)
          })
          //allRoutesMap.get(passengerGroup).foreach { toAirportRouteMap =>
          //             if (!toAirportRouteMap.isEmpty) {
          //               println("to airport route map" + toAirportRouteMap)
          //             }
          toAirportRouteMap.get(toAirport) match {
            case Some(pickedRoute) =>
              //println("picked route info" + passengerGroup + " " + pickedRoute.links(0).airline)
              val fromAirport = passengerGroup.fromAirport
              val rejection = getRouteRejection(pickedRoute, fromAirport, toAirport, passengerGroup.preference.preferredLinkClass)
              rejection match {
                case None =>
                  synchronized {
                    val consumptionSize = pickedRoute.links.foldLeft(chunkSize) { (foldInt, linkConsideration) =>
                      val actualLinkClass = linkConsideration.linkClass
                      val availableSeats = linkConsideration.link.availableSeats(actualLinkClass)
                      if (availableSeats < foldInt) {
                        availableSeats
                      } else {
                        foldInt
                      }
                    }
                    //some capacity available on all the links, consume them NOMNOM NOM!
                    if (consumptionSize > 0) {
                      pickedRoute.links.foreach { linkConsideration =>
                        val actualLinkClass = linkConsideration.linkClass
                        //val newAvailableSeats = linkConsideration.link.availableSeats(actualLinkClass) - consumptionSize

                        linkConsideration.link.addSoldSeatsByClass(actualLinkClass, consumptionSize)
                        //linkConsideration.link.availableSeats = LinkClassValues(linkConsideration.link.availableSeats.map.+(actualLinkClass -> newAvailableSeats))
                        //                   if (link.availableSeats == 0) {
                        //                     println("EXHAUSED!! = " + link)
                        //                   }
                      }

                      consumptionResult.add((passengerGroup, toAirport, consumptionSize, pickedRoute))
                    }
                    //update the remaining demand chunk list
                    if (consumptionSize < chunkSize) { //not enough capacity to completely fill
                      //put a updated demand chunk
                      remainingDemandChunks.add((passengerGroup, toAirport, chunkSize - consumptionSize));
                    }
                  }
                case Some(rejection) =>
                  import RouteRejectionReason._
                  rejection match {
                    case TOTAL_COST => // do not retry
                      missedDemandChunks.add((passengerGroup, toAirport, chunkSize));
                    case DISTANCE => //try again to see if there's any route within reasonable route distance
                      remainingDemandChunks.add((passengerGroup, toAirport, chunkSize));
                    case LINK_COST => //try again to see if there's any route with better links
                      remainingDemandChunks.add((passengerGroup, toAirport, chunkSize));
                  }
              }
            case None => //no route
              missedDemandChunks.add((passengerGroup, toAirport, chunkSize));
          }
          //           }
          if (hasComputedRouteMap) {
            if (progressChunk == 0 || counter.incrementAndGet() % progressChunk == 0) {
              print(".")
              if (progressCount.incrementAndGet() % 10 == 0) {
                print(progressCount.get + "% ")
              }
            }
          }
      }
      println("Done!")

      //now process the remainingDemandChunks in next cycle
      demandChunks = remainingDemandChunks.asScala.toList
      consumptionCycleCount += 1
    }

    println("Total chunks that consume something " + consumptionResult.size)
    println("Total missed chunks " + missedDemandChunks.size)

    println("Total transported pax " + consumptionResult.asScala.map(_._3).sum)
    println("Total missed pax " + missedDemandChunks.asScala.map(_._3).sum)

    //collapse it now
    val collapsedMap = consumptionResult.asScala.groupBy {
      case(passengerGroup, toAirport, passengerCount, route) => (passengerGroup, toAirport, route)
    }.mapValues { consumptions => consumptions.map(_._3).sum }

    println("Collapsed consumption map size: " + collapsedMap.size)


    val missedMap = missedDemandChunks.asScala.groupBy {
      case(passengerGroup, toAirport, passengerCount) => (passengerGroup, toAirport)
    }.view.mapValues( missedChunks => missedChunks.map(_._3).sum).toMap

//    val soldLinks = links.filter{ link => link.availableSeats < link.capacity  }.map { link =>
//      (link, link.capacity - link.availableSeats)
//      }.sortBy {
//        case (_, soldSeats) => soldSeats
//      }
//
//    soldLinks.foreach{ case(link, soldSeats) => println(link.airline.name + "($" + link.price + "; recommend $" + Pricing.computeStandardPrice(link.distance) + ") " + soldSeats  + " : " + link.from.name + " => " + link.to.name) }
//    println("seats sold: " + soldLinks.foldLeft(0) {
//      case (holder, (link, soldSeats)) => holder + soldSeats
//    })
//
//    LinkSource.saveLinkConsumptions(soldLinks)

    PassengerConsumptionResult(collapsedMap.toMap, missedMap)
  }

  val ROUTE_COST_TOLERANCE_FACTOR = 1.5
  val LINK_COST_TOLERANCE_FACTOR = 0.925
  val LINK_DISTANCE_TOLERANCE_FACTOR = 1.6
  val ROUTE_DISTANCE_TOLERANCE_FACTOR = 3.0


  object RouteRejectionReason extends Enumeration {
    type RouteRejectionReason = Value
    val TOTAL_COST, LINK_COST, DISTANCE = Value
  }

  def getRouteRejection(route: Route, fromAirport: Airport, toAirport: Airport, preferredLinkClass : LinkClass) : Option[RouteRejectionReason.Value] = {
    import RouteRejectionReason._
    val routeDisplacement = Computation.calculateDistance(fromAirport, toAirport)
    val routeDistance = route.links.foldLeft(0)(_ + _.link.distance)

    if (routeDistance > routeDisplacement * ROUTE_DISTANCE_TOLERANCE_FACTOR) {
      return Some(DISTANCE)
    }

    val routeAffordableCost = Pricing.computeStandardPrice(routeDisplacement, Computation.getFlightType(fromAirport, toAirport, routeDisplacement), preferredLinkClass) * ROUTE_COST_TOLERANCE_FACTOR
    if (route.totalCost > routeAffordableCost) {
      //println(s"rejected affordable: $routeAffordableCost, cost : , ${route.totalCost}  $route" )
      return Some(TOTAL_COST)
    }

    //now check individual link
    val unaffordableLink = route.links.find { linkConsideration => //find links that are too expensive
      val link = linkConsideration.link
      val linkAffordableCost = link.standardPrice(preferredLinkClass) * LINK_COST_TOLERANCE_FACTOR * PassengerType.priceAdjust(linkConsideration.passengerGroup.passengerType)
      linkConsideration.cost > linkAffordableCost
    }

    if (unaffordableLink.isDefined) {
      return Some(LINK_COST)
    }
    return None

  }




//  def findAllRoutes(requiredRoutes : Map[PassengerGroup, Set[Airport]], linksList : List[Link], activeAirportIds : Set[Int],  countryOpenness : Map[String, Int] = PassengerSimulation.countryOpenness) : Future[Map[PassengerGroup, Map[Airport, Route]]] = {
//    val totalRequiredRoutes = requiredRoutes.foldLeft(0){ case (currentCount, (fromAirport, toAirports)) => currentCount + toAirports.size }
//
//    println("Total routes to compute : " + totalRequiredRoutes)
//    println("Total passenger groups : " + requiredRoutes.size)
//
//    val links = linksList.toArray
//
//    val demandSource = Source(requiredRoutes.iterator)
//    val computeFlow: Flow[(PassengerGroup, Set[Airport]), (PassengerGroup, Map[Airport, Route])] = Flow[(PassengerGroup, Set[Airport])].map {
//      case(passengerGroup, toAirports) =>
//        val linkClass = passengerGroup.preference.linkClass
//        //remove links that's unknown to this airport then compute cost for each link. Cost is adjusted by the PassengerGroup's preference
//        val linkConsiderations = ArrayBuffer[LinkConsideration]()
//
//        var walker = 0
//        while (walker < links.length) {
//          val link = links(walker)
//          walker += 1
//
//          //see if there are any seats for that class (or lower) left
//          link.availableSeatsAtOrBelowClass(linkClass).foreach {
//            case(matchingLinkClass, seatsLeft) =>
//              //from the perspective of the passenger group, how well does it know each link
//              val airlineAwarenessFromCity = passengerGroup.fromAirport.getAirlineAwareness(link.airline.id)
//              val airlineAwarenessFromReputation = link.airline.getReputation() / 2
//              //println("Awareness from reputation " + airlineAwarenessFromReputation)
//              val airlineAwareness = Math.max(airlineAwarenessFromCity, airlineAwarenessFromReputation)
//
//              if (airlineAwareness > Random.nextInt(AirlineAppeal.MAX_AWARENESS)) {
//                var cost = passengerGroup.preference.computeCost(link) //cost should NOT be lower if seats available are lower than requested class, this reflect the unwillingness to downgrade
//                //2 instance of the link, one for each direction. Take note that the underlying link is the same, hence capacity and other params is shared properly!
//                val linkConsideration1 = LinkConsideration(link, cost, matchingLinkClass, false)
//                val linkConsideration2 = LinkConsideration(link, cost, matchingLinkClass, true)
//                if (hasFreedom(linkConsideration1, passengerGroup.fromAirport, countryOpenness)) {
//                  linkConsiderations += linkConsideration1
//                }
//                if (hasFreedom(linkConsideration2, passengerGroup.fromAirport, countryOpenness)) {
//                  linkConsiderations += linkConsideration2
//                }
//              }
//          }
//
//        }
//
//        //then find the shortest route based on the cost
//
//        val routeMap = findShortestRoute(passengerGroup.fromAirport, toAirports, activeAirportIds, linkConsiderations, 4)
//        //if (!routeMap.isEmpty) { println(routeMap) }
//        (passengerGroup, routeMap)
//    }
//    //val resultSink = Sink.foreach { demandInfo : (Airport, Map[Airport, Int]) => println() }
//    var counter = 0
//    var progressCount = 0
//    val progressChunk = requiredRoutes.size / 100
//
//    val resultSink = Sink.fold(Map[PassengerGroup, Map[Airport, Route]]()) {
//      (map, demandInfo : (PassengerGroup, Map[Airport, Route])) =>
//         counter += 1
//          if (progressChunk == 0 || counter % progressChunk == 0) {
//            progressCount += 1;
//            print(".")
//            if (progressCount % 10 == 0) {
//              print(progressCount + "% ")
//            }
//          }
//        map + demandInfo
//    }
//
//    val completeFlow = demandSource.via(computeFlow).to(resultSink)
//    val materializedFlow = completeFlow.run()
//    materializedFlow.get(resultSink)
//  }


  case class SpecializationModifier(deltaByLinkClass : Map[LinkClass, Double]) {
    val value = (preferredLinkClass : LinkClass) => {
      1.0 + deltaByLinkClass.getOrElse(preferredLinkClass, 0.0)
    }
  }

  case class ExternalCostModifier(airlineCostModifiers : Map[Int, Double] = Map.empty,
                                  specializationCostModifiers : Map[(Int, Int), SpecializationModifier] = Map.empty) extends CostModifier { //(airlineId , airportId) -> modifier)
    override def value(link : Transport, linkClass : LinkClass) : Double = {
      var modifier = 1.0
      if (airlineCostModifiers.contains(link.airline.id)) {
        modifier *= airlineCostModifiers(link.airline.id)
      }

      val airlineFromAirportTuple = (link.airline.id, link.from.id)
      if (specializationCostModifiers.contains(airlineFromAirportTuple)) {
        modifier *= specializationCostModifiers(airlineFromAirportTuple).value(linkClass)
      }
      val airlineToAirportTuple = (link.airline.id, link.to.id)
      if (specializationCostModifiers.contains(airlineToAirportTuple)) {
        modifier *= specializationCostModifiers(airlineToAirportTuple).value(linkClass)
      }
      modifier
    }
  }

  
   /**
   * Return all routes if available, with destination defined in the input Map's value, the Input map key indicates various Passenger Group
   * 
   * Returned value is in form of Future[Map[PassengerGroup, Map[Airport, Route]]], which the Map key should always present even if no valid route is found at all
   * for that particular PassengerGroup, in such a case the map value will just me an empty map.
   * 
   * Take note that when finding routes, in order to be considered as a valid route, this method takes into consideration of:
   * 1. whether the link has available capacity left for the PassengerGroup's link Class, all the links in between 2 points should have capacity for the correct class
   *
   */
  def findFurthestAirportDistance(fromAirport: Airport, toAirports: Set[Airport]): Int = {
    val distances = toAirports.map(toAirport => Computation.calculateDistance(fromAirport, toAirport))
    distances.maxOption.getOrElse(0)
  }


  def findRoutesByPassengerGroup(passengerGroup: PassengerGroup,
                                  toAirports : Set[Airport],
                    linksList : List[Transport],
                    countryOpenness : Map[String, Int] = PassengerSimulation.countryOpenness,
                    establishedAllianceIdByAirlineId : java.util.Map[Int, Int] = Collections.emptyMap[Int, Int](),
                    externalCostModifier : Option[CostModifier] = None,
                    iterationCount : Int = 4) : Map[Airport, Route] = {

    val preferredLinkClass = passengerGroup.preference.preferredLinkClass
    //remove links that's unknown to this airport then compute cost for each link. Cost is adjusted by the PassengerGroup's preference
    val linkConsiderations = new ArrayList[LinkConsideration]()
    val activeAirports = Set[Int]()
    val furthestDistance = LINK_DISTANCE_TOLERANCE_FACTOR * findFurthestAirportDistance(passengerGroup.fromAirport, toAirports)

//    linksList.foreach { link =>
      linksList.filter(_.distance <= furthestDistance).foreach { link =>

      //see if there are any seats for that class (or lower) left
      link.availableSeatsAtOrBelowClass(preferredLinkClass).foreach {
        case (matchingLinkClass, seatsLeft) =>
          //2 instance of the link, one for each direction. Take note that the underlying link is the same, hence capacity and other params is shared properly!
          val costProvider = CostStoreProvider() //use same instance of costProvider so this is only computed once
          val linkConsideration1 = LinkConsideration(link, matchingLinkClass, false, passengerGroup, externalCostModifier, costProvider)
          val linkConsideration2 = LinkConsideration(link, matchingLinkClass, true, passengerGroup, externalCostModifier, costProvider)
          if (hasFreedom(linkConsideration1, passengerGroup.fromAirport, countryOpenness, link.from.size)) {
            linkConsiderations.add(linkConsideration1)
            activeAirports.add(link.from.id)
            activeAirports.add(link.to.id)
          }
          if (hasFreedom(linkConsideration2, passengerGroup.fromAirport, countryOpenness, link.to.size)) {
            linkConsiderations.add(linkConsideration2)
            activeAirports.add(link.from.id)
            activeAirports.add(link.to.id)
          }
      }
    }
    //val links = linksList.toArray
    findShortestRoute(passengerGroup, toAirports, allVertices = activeAirports, linkConsiderations, establishedAllianceIdByAirlineId, iterationCount)
  }

  
  
  
  def hasFreedom(linkConsideration : LinkConsideration, originatingAirport : Airport, countryOpenness : Map[String, Int], airportSize : Int) : Boolean = {
    if (linkConsideration.from.countryCode == linkConsideration.to.countryCode) { //domestic flight is always ok
      true
    } else if (linkConsideration.from.countryCode == originatingAirport.countryCode) { //always ok if link flying out from same country as the originate airport
      true
    } else if (airportSize >= 7) { //large airports can handle transfers
      true
    } else { //international to international, decide base on openness
      countryOpenness(linkConsideration.from.countryCode) >= Country.SIXTH_FREEDOM_MIN_OPENNESS
    }
  }


  /**
   * Find the shortest routes from the fromAirport to ALL the toAirport
   * Returns a map with valid route in format of
   * Map[toAiport, Route]
   */
  def findShortestRoute(passengerGroup : PassengerGroup, toAirports : Set[Airport], allVertices : Set[Int], linkConsiderations : java.util.List[LinkConsideration], allianceIdByAirlineId : java.util.Map[Int, Int], maxIteration : Int) : Map[Airport, Route] = {
    val from = passengerGroup.fromAirport

    //     // Step 1: initialize graph
//   for each vertex v in vertices:
//       if v is source then distance[v] := 0
//       else distance[v] := inf
//       predecessor[v] := null
    //val allVertices = allVerticesSource.map { _.id }
    
    val distanceMap = new java.util.HashMap[Int, Double]()
    var predecessorMap = new java.util.HashMap[Int, LinkConsideration]()
    var activeVertices = new java.util.HashSet[Int]()
    val assetDiscountByAirportId = mutable.HashMap[Int, Option[(Double, List[AirportAsset])]]() //key is airport
    activeVertices.add(from.id)
    allVertices.foreach { vertex => 
      if (vertex == from.id) {
        distanceMap.put(vertex, 0)
      } else {
        distanceMap.put(vertex, 10000000)
      }
    }

   // Step 2: relax edges repeatedly
//   for i from 1 to size(vertices)-1:
//       for each edge (u, v) with weight w in edges:
//           if distance[u] + w < distance[v]:
//               distance[v] := distance[u] + w
//               predecessor[v] := u
    for (i <- 0 until maxIteration) {
      //val updatingLinks = ArrayBuffer[LinkConsideration]()
      //val linkConsiderationsIterator = linkConsiderations.iterator()
      val newPredecessorMap = new java.util.HashMap[Int, LinkConsideration](predecessorMap)
      val newActiveVertices = new java.util.HashSet[Int]()
      //create a clone of last run, we update this map, but for lookup we use the previous one
      //this is necessary to avoid "previous leg replacement problem"
      //for example on first iteration, there is F0, T1 and T2. If there are links:
      // Link Consideration 1 : Airline A, F0 -> T1, cost 50
      // Link Consideration 2 : Airline A, T1 -> T2, cost 50
      // Link Consideration 3 : Airline B, F0 -> T1, cost 40
      //If we do NOT use a clone and lookup the current predecessorMap, from F0
      // It will be:
      // 1. Process Link Consideration 1, predecessorMap: T1 -> (Link 1, 50)
      // 2. Process Link Consideration 2, predecessorMap: T1 -> (Link 1, 50), T2 -> (Link 2, 50 + 50 + Connection cost of SAME airline)
      // 3. Process Link Consideration 3, predecessorMap: T1 -> (Link 3, 40), T2 -> (Link 2, 50 + 50 + Connection cost of SAME airline)
      // At the end it will be wrong as solution for route from F0 to T2, will be Link 3 and Link 2 while the final cost is incorrect
      // This also create the shuttle from other alliance problem
      //The fix for this is never use the current predecessorMap for lookup, instead, use the previous map

      val linkConsiderationsIterator = linkConsiderations.iterator()
      while (linkConsiderationsIterator.hasNext) {
        val linkConsideration = linkConsiderationsIterator.next
        if (activeVertices.contains(linkConsideration.from.id)) { //optimization - only need to re-run if the vertex was update in last iteration
          val predecessorLinkConsideration = predecessorMap.get(linkConsideration.from.id)

          var connectionCost = 10.0
          var isValid : Boolean = true
          val fromCost = distanceMap.get(linkConsideration.from.id)
          var flightTransit = false
          if (predecessorLinkConsideration != null) { //then it should be a connection flight
            val predecessorLink = predecessorLinkConsideration.link
            val previousLinkAirlineId = predecessorLink.airline.id
            val currentLinkAirlineId = linkConsideration.link.airline.id


            if (linkConsideration.link.id == predecessorLink.id) { //going back and forth on the same link
              isValid = false
            } else if (predecessorLink.transportType == TransportType.GENERIC_TRANSIT && linkConsideration.link.transportType == TransportType.GENERIC_TRANSIT) {
              isValid = false //don't allow transit 2 transit connections
            } else if (predecessorLink.transportType == TransportType.GENERIC_TRANSIT && predecessorLink.from.id != passengerGroup.fromAirport.id) {
              connectionCost += 25 //middle "leave the airport" transit connections more expensive
            } else if (predecessorLink.transportType == TransportType.GENERIC_TRANSIT || linkConsideration.link.transportType == TransportType.GENERIC_TRANSIT) {
              connectionCost = 0
            } else {

              //now look at the frequency of the link arriving at this FromAirport and the link (current link) leaving this FromAirport. check frequency
              val frequency = Math.max(predecessorLink.frequencyByClass(predecessorLinkConsideration.linkClass), linkConsideration.link.frequencyByClass(linkConsideration.linkClass))
              //if the bigger of the 2 is less than 21, impose extra layover time (if either one is frequent enough, then consider that as ok)
              if (frequency < 7) {
                connectionCost += (7 * 24 * 4.5) / frequency //possible overnight connection; $126 at 6 freq
              } else if (frequency < 28) {
                connectionCost += (7 * 24 * 3) / frequency //$3.00 per hour wait; $36 @ 14 freq
              }

              if (previousLinkAirlineId ==  currentLinkAirlineId || allianceIdByAirlineId.get(previousLinkAirlineId) == allianceIdByAirlineId.get(currentLinkAirlineId))) { //same alliance or airline, 30 % less perceived cost
                 connectionCost -= fromCost*0.3
              }
              
              //make transfert impose extra cost
              connectionCost += 40
              
              flightTransit = true
            }

    
           if (flightTransit = false) { //if one airline (no transfer)) then should be treated as same airline or alliance.
              connectionCost -= fromCost*0.3
            }
          
            //connection cost should take into consideration of preferred link class too
            connectionCost *= passengerGroup.preference.preferredLinkClass.priceMultiplier
            connectionCost += passengerGroup.preference.preferredLinkClass.basePrice
            connectionCost *= passengerGroup.preference.connectionCostRatio
            
            if (flightTransit) {
              val waitTimeDiscount = Math.min(0.5, linkConsideration.from.computeTransitDiscount(
                predecessorLinkConsideration,
                linkConsideration,
                passengerGroup))
              connectionCost *= (1 - waitTimeDiscount)
            }

          }

          if (isValid) {
            val cost = Math.max(0, linkConsideration.cost + connectionCost) //just to avoid loop in graph

            var newCost = fromCost + cost

            assetDiscountByAirportId.getOrElseUpdate(linkConsideration.to.id, linkConsideration.to.computePassengerCostAssetDiscount(linkConsideration, passengerGroup)).foreach {
              case(discount, _) =>
                //discount scale by cost travelled so far ie bigger impact for pax from far away
                //however the discount should never be more than half the new cost, otherwise it COULD potentially get cheaper the further it goes
                val costDiscount = Math.min(cost * 0.5, newCost * discount)
                newCost -= costDiscount
            }

            if (newCost < distanceMap.get(linkConsideration.to.id)) {
              distanceMap.put(linkConsideration.to.id, newCost)
              newPredecessorMap.put(linkConsideration.to.id, linkConsideration.copyWithCost(cost)) //clone it, do not modify the existing linkWithCost
              newActiveVertices.add(linkConsideration.to.id)
            }
          }
        }
      }
      predecessorMap = newPredecessorMap
      activeVertices = newActiveVertices
    }

    val resultMap : scala.collection.mutable.Map[Airport, Route] = scala.collection.mutable.Map[Airport, Route]()

    toAirports.foreach{ to =>
      var walker = to.id
      var noSolution = false;
      var foundSolution = false
      var hasFlight = false
      var route = ListBuffer[LinkConsideration]()
      val visitedAssetsListBuffer = ListBuffer[AirportAsset]()
      var hopCounter = 0
      while (!foundSolution && !noSolution) {
        val link = predecessorMap.get(walker)
        if (link != null) {
          route.prepend(link)
          if (link.link.transportType == TransportType.FLIGHT) {
            hasFlight = true
          }
          assetDiscountByAirportId.get(walker).foreach { _.foreach {
              case(_, visitedAssetsOfThisAirport) => visitedAssetsListBuffer.addAll(visitedAssetsOfThisAirport)
            }
          }
          walker = link.from.id
          if (walker == from.id && hasFlight) { //at least 1 leg has to be a flight. We don't want route with no flights
            foundSolution = true
          }
        } else { 
            noSolution = true
        }
        hopCounter += 1        
      }
      if (foundSolution) {
        if (visitedAssetsListBuffer.isEmpty) {
          resultMap.put(to, Route(route.toList, distanceMap.get(to.id)))
        } else {
          resultMap.put(to, Route(route.toList, distanceMap.get(to.id), visitedAssetsListBuffer.toList))
        }

      }  
    }
    
    resultMap.toMap
  }


//  def findRandomRoutes(from : Airport, to : Airport, links : List[Link], routeCount : Int) = {
//    val linkMap = scala.collection.mutable.Map[Airport, ListBuffer[Link]]()
//    
//    links.foreach { link =>
//      var linksFromThisAirport : ListBuffer[Link] = null
//      if (!linkMap.contains(link.from)) {
//        linksFromThisAirport = ListBuffer[Link]()
//        linkMap.put(link.from, linksFromThisAirport)
//      } else {
//        linksFromThisAirport = linkMap(link.from)
//      }
//      linksFromThisAirport.append(link)
//    }
//    
//    val random = new Random()
//    println
//    for (i <- 0 until routeCount) {
//      var walker = from
//      var cost = 0.0
//      for (j <- 0 until 3) { //from group 0 => .. => group 3
//        print(walker.name + " => ")
//        val nextLinks = linkMap(walker)
//        val nextLink = nextLinks(random.nextInt(nextLinks.length))
//        walker = nextLink.to
//        cost += nextLink.cost
//      }
//      
//      //last step, has to goto "to"
//      for (link <- linkMap(walker)) {
//        if (link.to == to) {
//          print(link.to.name + " COST " + (cost + link.cost))
//        }
//      }
//      println
//    }
//  }


}
