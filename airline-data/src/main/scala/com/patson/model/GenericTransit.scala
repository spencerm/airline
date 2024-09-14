package com.patson.model

case class GenericTransit(from : Airport, to : Airport, distance : Int, var capacity: LinkClassValues, var id : Int = 0) extends Transport {
  override val transportType : TransportType.Value = TransportType.GENERIC_TRANSIT
  override val duration : Int = (distance.toDouble / 25 * 60).toInt
  override var frequency : Int = 24 * 7
  override def computedQuality() : Int = GenericTransit.QUALITY //constant quality
  override val price : LinkClassValues = LinkClassValues.getInstance()

  override val flightType : FlightType.Value = FlightType.SHORT_HAUL_DOMESTIC

  override val cost : LinkClassValues = LinkClassValues.getInstance(economy = Pricing.computeStandardPrice(distance, FlightType.SHORT_HAUL_DOMESTIC, ECONOMY)) * 0.3 //hidden cost of general transit

  val upkeep = 0
  override var minorDelayCount : Int = 0
  override var majorDelayCount : Int = 0
  override var cancellationCount : Int = 0

  override def toString() = {
    s"Generic transit $id; ${from.city}(${from.iata}) => ${to.city}(${to.iata}); distance $distance"
  }

  override val frequencyByClass  = (_ : LinkClass) =>  frequency
  override val airline : Airline = GenericTransit.TRANSIT_PROVIDER
}

object GenericTransit {
  val QUALITY = 35 //note, pax preference does not consider quality if transit
  val TRANSIT_PROVIDER = Airline.fromId(0).copy(name = "Local Transit")
}


