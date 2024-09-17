package com.patson.init

import com.patson.model._
import com.patson.data._
import com.patson.Util
import scala.collection.mutable.Set
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

object IsolatedAirportPatcher {
   

  import IsolatedTownFeature._
  
  def patchIsolatedAirports() = {
    val allAirports = AirportSource.loadAllAirports(true)
    val isolationByAirport = Map[Airport, Int]()
    val ISOLATED_ISLAND_AIRPORTS = Array(
      //carribean
      "PVA", "ADZ", "CYB", "RTB", "UII", "GJA", "CPX", "VQS", "SPR", "CYC", "CUK", "NAS",
      //europe
      "KGD", //RU
      "IDY", "ACI", "ISC", "OUI", //FR
      "PNL", "LMP", //IT
      "HGL", "BMK", "GWT", "BMR", //DE
      "EGH", "EOI", "FIE", "FOA", "LWK", "LSI", "ACI", "TRE", "BRR", "BEB", "SYY", "KOI", "ILY", "CAL", "ISC", "GCI", "JER", "GIB", "IOM", //GB
      "BYR", "RNN", //DK
      "MHQ", "KDL", "URE", "ENF", "KTT", //FI
      "IOR","INQ","IIA", //IE
      "PJA", //SE
      "EN9","EN1","EN2", "SKN", "SSJ", "BNN", "MOL", "OSY", "RVK", "SDN", "SOG", //NO
      "HZK", //IS
      "AOK", "JMK", "JNX", "JSI", "JTR", "KIT", "LKS", "MLO", "SMI", "JIK", "KGS", "RHO", "LXS", "MJT", "JKH", "ZTH", "EFL", //GR
      //americas
      "FRD", "ESD", "ACK", "MVY", "BID", "AVX", //US
      "YGR", "YPN", "YYB", //CA
      "FSP",
      "STT", "STX", "SAB", "EUX", "SXM", "SFG", "AXA", "SKB", "SBH", "NEV", "BBQ", "MNI", "GBJ", "NCA", "XSC", "GDT", "PTP", "FDF", //Caribbean
      "CAY",
      "ADZ",
      //oceania
      "WSZ", "WLS",
      //asia
      "KUM", "TNE", "MYE", "MK1", "OIM", "HAC", "AO1", "SDS", "OIR", "RIS", "OKI", "TSJ", "FUJ", "KKX", "TKN", "OKE", "RNJ", "UEO", "OKA", "MMY", "TRA", "ISG", "OGN", "IKI", "MMD", "KTD", //JP
      "BSO", "CGM", "JOL", "CYU", "TWT", "IAO", "MBT", "USU", "ENI", //PH
      "TNJ",
      "NAH", //ID
      //africa
      "MMO", "SSG"
    )
    val ISOLATED_COUNTRIES = Array("FO", "BS", "KY", "TC", "VC", "GD", "DM", "AG", "MS", "BQ", "BL", "MF", "SX", "AI", "VI", "VG", "VC", "VU", "WF", "MU", "MV", "CC", "CK", "CV", "ST") //always add 1 level, because island countries and islands are inherently isolated


    allAirports.foreach { airport =>
      var isolationLevel : Int = 0

      val boundaryLongitude = GeoDataGenerator.calculateLongitudeBoundary(airport.latitude, airport.longitude, HUB_RANGE_BRACKETS.last)
      for (i <- 0 until HUB_RANGE_BRACKETS.size) {
        val threshold = HUB_RANGE_BRACKETS(i)
        val populationWithinRange = allAirports.filter { targetAirport =>
          val distance = Util.calculateDistance(airport.latitude, airport.longitude, targetAirport.latitude, targetAirport.longitude)
          distance < threshold && targetAirport.longitude >= boundaryLongitude._1 && targetAirport.longitude <= boundaryLongitude._2
        }.map(_.population).sum
        if (populationWithinRange < 100000) { //very isolated
          isolationLevel += 3
        } else if (populationWithinRange < 500000) {
          isolationLevel += 2
        } else if (populationWithinRange < 3000000) { //kinda isolated
          isolationLevel += 1
        }
      }
      isolationLevel = (Math.floor( isolationLevel / 2 )).toInt
      if (ISOLATED_COUNTRIES.contains(airport.countryCode) && airport.size <= 4 || ISOLATED_ISLAND_AIRPORTS.contains(airport.iata)) {
        isolationLevel += 1
      }
      if (isolationLevel > 0) {
        isolationByAirport.put(airport, isolationLevel)
      }
    }

    isolationByAirport.foreach {
      case (airport,isolationLevel) =>
        val existingFeatures = airport.getFeatures().filter(_.featureType != AirportFeatureType.ISOLATED_TOWN)
        val newFeatures = existingFeatures :+ AirportFeature(AirportFeatureType.ISOLATED_TOWN, isolationLevel)
        //airport.initFeatures(newFeatures) //CANNOT init features here, features can only be init once.
        AirportSource.updateAirportFeatures(airport.id, newFeatures)
        println(s"$airport isolation level $isolationLevel features ${airport.getFeatures()}")
    }



  }
}  
  