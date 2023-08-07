package controllers

import com.patson.data.{AirlineSource, AirlineStrategySource, CycleSource, DelegateSource}
import com.patson.model._
import controllers.AuthenticationObject.AuthenticatedAirline
import javax.inject.Inject
import play.api.libs.json.{Json, _}
import play.api.mvc._

class AirlineStrategyApplication @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  private def delegatesRequired(selections : Int): Int = {
    ((selections * (selections - 1)) / 2).floor.toInt
  }

  implicit object AirlineStrategyFormat extends Format[AirlineStrategy] {
    def reads(json : JsValue): JsResult[AirlineStrategy] = {
      val airlineId = json.\("airlineId").as[Int]
      val hasEconomy = json.\("hasEconomy").as[Boolean]
      val hasBusiness = json.\("hasBusiness").as[Boolean]
      val hasFirst = json.\("hasFirst").as[Boolean]
      val hasVacationPackages = json.\("hasVacationPackages").as[Boolean]
      val hasFlexibleTicketing = json.\("hasFlexibleTicketing").as[Boolean]
      val strat = AirlineStrategy(airlineId, hasEconomy, hasBusiness, hasFirst, hasVacationPackages, hasFlexibleTicketing)
      JsSuccess(strat)
    }

    def writes(entry: AirlineStrategy): JsValue = JsObject(List(
      "airlineId" -> JsNumber(entry.airlineId),
      "hasEconomy" -> JsBoolean(entry.hasEconomy),
      "hasBusiness" -> JsBoolean(entry.hasBusiness),
      "hasFirst" -> JsBoolean(entry.hasFirst),
      "hasVacationPackages" -> JsBoolean(entry.hasVacationPackages),
      "hasFlexibleTicketing" -> JsBoolean(entry.hasFlexibleTicketing)
    ))
  }

  def getAirlineStrategy(airlineId : Int) = AuthenticatedAirline(airlineId) { implicit request =>
    val strategy = AirlineStrategySource.loadAirlineStrategy(airlineId)
    Ok(Json.toJson(strategy))
  }

  def postAirlineStrategy(airlineId : Int) = AuthenticatedAirline(airlineId) { request =>
    if (request.body.isInstanceOf[AnyContentAsJson]) {
      val strategy = request.body.asInstanceOf[AnyContentAsJson].json.as[AirlineStrategy]
      AirlineStrategySource.saveAirlineStrategy(airlineId, strategy)
      Created(Json.toJson(strategy)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
    } else {
      BadRequest("Cannot update strategy with non json body").withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
    }
  }

}
