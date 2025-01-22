package com.patson.model.airplane

import com.patson.model.{AbstractLinkClassValues, Airline, BUSINESS, FIRST, Link}

case class AirplaneConfiguration(economyVal : Int, businessVal : Int, firstVal : Int, airline : Airline, model : Model, isDefault : Boolean, var id : Int = 0) extends AbstractLinkClassValues(economyVal, businessVal, firstVal) {
  lazy val minimized : AirplaneConfiguration = { //config that has least capacity
    val minimizedFirst = (model.capacity / FIRST.spaceMultiplier).toInt
    val minimizedBusiness = ((model.capacity - minimizedFirst * FIRST.spaceMultiplier) / BUSINESS.spaceMultiplier).toInt
    //no eco as user can lock econ to zero
    AirplaneConfiguration(0, minimizedBusiness, minimizedFirst, airline, model, isDefault)
  }
}

object AirplaneConfiguration {
  val empty: AirplaneConfiguration = AirplaneConfiguration(0, 0, 0, Airline.fromId(0), Model.fromId(0), true)
  val default: (Airline, Model) => AirplaneConfiguration = (airline, model) => {
    if (model.maxSeats == model.capacity) {
      AirplaneConfiguration(economyVal = model.capacity, 0, 0, airline, model, isDefault = true)
    } else {
      val ratio =  model.capacity.toDouble / model.maxSeats
      val firstVal = if (ratio > BUSINESS.spaceMultiplier) {
        Math.floor(Math.ceil(ratio / FIRST.spaceMultiplier * 10) / 10 * model.capacity / FIRST.spaceMultiplier).toInt
      } else 0
      var businessVal = if (ratio > 1.0 && ratio <= BUSINESS.spaceMultiplier) {
        Math.floor(Math.ceil(ratio / BUSINESS.spaceMultiplier * 10) / 10 * model.capacity / BUSINESS.spaceMultiplier).toInt
      } else 0
      var economyVal = (model.capacity - (businessVal * BUSINESS.spaceMultiplier + firstVal * FIRST.spaceMultiplier)).toInt
      if (model.quality > 3.5 && economyVal > 10) {
        businessVal = businessVal + (economyVal / BUSINESS.spaceMultiplier).toInt
        economyVal = (model.capacity - businessVal * BUSINESS.spaceMultiplier + firstVal * FIRST.spaceMultiplier).toInt
      }
      AirplaneConfiguration(economyVal, businessVal, firstVal, airline, model, isDefault = true)
    }
  }
  val MAX_CONFIGURATION_TEMPLATE_COUNT = 5 //per model and airline
}
