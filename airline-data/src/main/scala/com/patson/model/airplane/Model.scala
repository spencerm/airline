package com.patson.model.airplane

import com.patson.model.IdObject
import com.patson.model.Airline
import com.patson.model.airplane.Model.Category
import com.patson.util.AirplaneModelCache

case class Model(name : String, family : String = "", capacity : Int, maxSeats : Int, quality : Double, fuelBurn : Int, speed : Int, range : Int, price : Int, lifespan : Int, constructionTime : Int, manufacturer: Manufacturer, runwayRequirement : Int, imageUrl : String = "", var id : Int = 0) extends IdObject {
  import Model.Type._

  val countryCode = manufacturer.countryCode
  val SUPERSONIC_SPEED_THRESHOLD = 1236
  val SIZE_SMALL_THRESHOLD = 80

  val airplaneType : Type = {
    if (speed > SUPERSONIC_SPEED_THRESHOLD) {
      SUPERSONIC
    } else if (speed < 180) {
      AIRSHIP
    } else if (speed <= 300) {
      HELICOPTER
    } else if (speed <= 680) {
      if (capacity <= SIZE_SMALL_THRESHOLD) {
        PROPELLER_SMALL
      } else {
        PROPELLER_MEDIUM
      }
    } else if (capacity <= SIZE_SMALL_THRESHOLD) {
      SMALL
    } else if (capacity <= 131 || family == "Embraer E-Jet E2") {
      REGIONAL
    } else if (capacity <= 215) {
      MEDIUM
    } else if (family == "Boeing 757" || family == "Boeing 737"  || family == "Airbus A320" || family == "DC-8" || family == "Yakovlev MC-21") {
      MEDIUM_XL
    } else if (capacity <= 375) {
      LARGE
    } else if (capacity <= 475) {
      EXTRA_LARGE
    } else if (capacity <= 800) {
      JUMBO
    } else {
      JUMBO_XL
    }
  }

  val category: Model.Category.Value = Category.fromType(airplaneType)

  private[this]val BASE_TURNAROUND_TIME = 30
  val turnaroundTime : Int = {
    BASE_TURNAROUND_TIME +
      (airplaneType match {
        case HELICOPTER => 0 //buffing for short distances
        case AIRSHIP => 0
        case SMALL =>  capacity / 3 //70
        case REGIONAL => capacity / 3
        case PROPELLER_SMALL => capacity / 3
        case PROPELLER_MEDIUM => capacity / 3
        case _ => capacity / 3
      }).toInt
  }

  val airplaneTypeLabel : String = label(airplaneType)

  val airplaneTypeSize : Double = size(airplaneType)

  //weekly fixed cost
  val baseMaintenanceCost : Int = {
    (capacity * 155).toInt
  }

  def applyDiscount(discounts : List[ModelDiscount]) = {
    var discountedModel = this
    discounts.groupBy(_.discountType).foreach {
      case (discountType, discounts) => discountType match {
        case DiscountType.PRICE =>
          val totalDiscount = discounts.map(_.discount).sum
          discountedModel = discountedModel.copy(price = (price * (1 - totalDiscount)).toInt)
        case DiscountType.CONSTRUCTION_TIME =>
          var totalDiscount = discounts.map(_.discount).sum
          totalDiscount = Math.min(1, totalDiscount)
          discountedModel = discountedModel.copy(constructionTime = (constructionTime * (1 - totalDiscount)).toInt)
      }
    }
    discountedModel
  }

  val purchasableWithRelationship = (relationship : Int) => {
    relationship >= Model.BUY_RELATIONSHIP_THRESHOLD
  }
}

object Model {
  val BUY_RELATIONSHIP_THRESHOLD = 0

  def fromId(id : Int) = {
    val modelWithJustId = Model("Unknown", "Unknown", 0, 0, 0, 0, 0, 0, 0, 0, 0, Manufacturer("Unknown", countryCode = ""), runwayRequirement = 0)
    modelWithJustId.id = id
    modelWithJustId
  }

  object Type extends Enumeration {
    type Type = Value
    val AIRSHIP, HELICOPTER, PROPELLER_SMALL, PROPELLER_MEDIUM, SMALL, REGIONAL, MEDIUM, MEDIUM_XL, LARGE, EXTRA_LARGE, JUMBO, JUMBO_XL, SUPERSONIC = Value

    val label: Type => String = {
      case AIRSHIP => "Airship"
      case HELICOPTER => "Helicopter"
      case PROPELLER_SMALL => "Small Prop"
      case PROPELLER_MEDIUM => "Regional Prop"
      case SMALL => "Small Jet"
      case REGIONAL => "Regional Jet"
      case MEDIUM => "Narrow-body"
      case MEDIUM_XL => "Narrow-body XL"
      case LARGE => "Wide-body"
      case EXTRA_LARGE => "Wide-body XL"
      case JUMBO => "Jumbo Jet"
      case JUMBO_XL => "Jumbo XL"
      case SUPERSONIC => "Supersonic"
    }

    val size: Type => Double = {
      case HELICOPTER => 0.03
      case PROPELLER_SMALL => 0.055
      case PROPELLER_MEDIUM => 0.095
      case SMALL => 0.055
      case REGIONAL => 0.095
      case MEDIUM => 0.14
      case MEDIUM_XL => 0.18
      case AIRSHIP => 0.2
      case SUPERSONIC => 0.24
      case LARGE => 0.20
      case EXTRA_LARGE => 0.25
      case JUMBO => 0.3
      case JUMBO_XL => 0.33
    }
  }

  object Category extends Enumeration {
    type Category = Value
    val SPECIAL, SMALL, REGIONAL, MEDIUM, LARGE, EXTRAORDINARY = Value
    val grouping: Map[Model.Category.Value, List[Type.Value]] = Map(
      SMALL -> List(Type.SMALL, Type.PROPELLER_SMALL),
      REGIONAL -> List(Type.REGIONAL, Type.PROPELLER_MEDIUM),
      MEDIUM -> List(Type.MEDIUM, Type.MEDIUM_XL),
      LARGE -> List(Type.LARGE, Type.EXTRA_LARGE),
      EXTRAORDINARY -> List(Type.JUMBO, Type.JUMBO_XL, Type.SUPERSONIC),
      SPECIAL -> List(Type.AIRSHIP, Type.HELICOPTER),
    )

    val fromType: Type.Value => Model.Category.Value = (airplaneType : Type.Value) => {
      grouping.find(_._2.contains(airplaneType)).map(_._1).getOrElse(Model.Category.EXTRAORDINARY)
    }

    val capacityRange : Map[Category.Value, (Int, Int)]= {
      AirplaneModelCache.allModels.values.groupBy(_.category).view.mapValues { models =>
        val sortedByCapacity = models.toList.sortBy(_.capacity)
        (sortedByCapacity.head.capacity, sortedByCapacity.last.capacity)
      }.toMap
    }

    val speedRange: Map[Category.Value, (Int, Int)] = {
      AirplaneModelCache.allModels.values.groupBy(_.category).view.mapValues { models =>
        val sortedBySpeed = models.toList.sortBy(_.speed)
        (sortedBySpeed.head.speed, sortedBySpeed.last.speed)
      }.toMap
    }

    def getCapacityRange(category: Category.Value): (Int, Int) = {
      capacityRange.getOrElse(category, (0, 0))
    }

    def getSpeedRange(category: Category.Value): (Int, Int) = {
      speedRange.getOrElse(category, (0, 0))
    }

  }

  val models = List(
Model("Boeing 2707",	"Boeing 2707",	554,	277,	7,	12210,	3300,	5900,	1160235000,	1664,	63,	Manufacturer("Boeing",	countryCode="US"),	3800,	imageUrl =""),
Model("Boeing 307 Stratoliner",	"Post-War Props",	60,	60,	1,	126,	357,	3850,	2945000,	1508,	0,	Manufacturer("Boeing",	countryCode="US"),	620,	imageUrl =""),
Model("Boeing 377 Stratocruiser",	"Post-War Props",	117,	117,	2,	377,	480,	6760,	19665000,	1456,	6,	Manufacturer("Boeing",	countryCode="US"),	1000,	imageUrl =""),
Model("Boeing 707",	"Boeing 707",	194,	194,	3,	960,	990,	6900,	41705000,	2496,	0,	Manufacturer("Boeing",	countryCode="US"),	2700,	imageUrl ="https://www.norebbo.com/boeing-707-320c-blank-illustration-templates/"),
Model("Boeing 717-200",	"DC-9",	134,	134,	5,	415,	811,	2645,	44365000,	1820,	6,	Manufacturer("Boeing",	countryCode="US"),	2050,	imageUrl ="https://www.norebbo.com/2017/06/boeing-717-200-blank-illustration-templates/"),
Model("Boeing 720B",	"Boeing 707",	156,	156,	3,	749,	972,	5900,	24605000,	1976,	0,	Manufacturer("Boeing",	countryCode="US"),	2000,	imageUrl =""),
Model("Boeing 727-100",	"Boeing 727",	131,	131,	4,	595,	960,	4170,	36100000,	2184,	18,	Manufacturer("Boeing",	countryCode="US"),	1750,	imageUrl =""),
Model("Boeing 727-200",	"Boeing 727",	189,	189,	5,	831,	811,	4020,	62985000,	2184,	36,	Manufacturer("Boeing",	countryCode="US"),	1800,	imageUrl ="https://www.norebbo.com/2018/03/boeing-727-200-blank-illustration-templates/"),
Model("Boeing 737 MAX 10",	"Boeing 737",	230,	204,	7,	750,	830,	6500,	192280000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2700,	imageUrl ="https://www.norebbo.com/2019/01/737-10-max-side-view/"),
Model("Boeing 737 MAX 7",	"Boeing 737",	172,	153,	7,	550,	830,	6500,	130910000,	1820,	36,	Manufacturer("Boeing",	countryCode="US"),	2050,	imageUrl ="https://www.norebbo.com/2016/07/boeing-737-max-7-blank-illustration-templates/"),
Model("Boeing 737 MAX 8",	"Boeing 737",	210,	178,	7,	664,	830,	6500,	164730000,	1820,	36,	Manufacturer("Boeing",	countryCode="US"),	2500,	imageUrl ="https://www.norebbo.com/2016/07/boeing-737-max-8-blank-illustration-templates/"),
Model("Boeing 737 MAX 8-200",	"Boeing 737",	200,	200,	6,	685,	839,	5200,	143545000,	1820,	36,	Manufacturer("Boeing",	countryCode="US"),	2500,	imageUrl ="https://www.norebbo.com/2016/07/boeing-737-max-8-blank-illustration-templates/"),
Model("Boeing 737 MAX 9",	"Boeing 737",	220,	193,	7,	722,	839,	6570,	175750000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2600,	imageUrl ="https://www.norebbo.com/2018/05/boeing-737-9-max-blank-illustration-templates/"),
Model("Boeing 737-100",	"Boeing 737",	124,	124,	3,	505,	780,	3440,	22895000,	1976,	12,	Manufacturer("Boeing",	countryCode="US"),	1800,	imageUrl ="https://www.norebbo.com/2018/10/boeing-737-100-blank-illustration-templates/"),
Model("Boeing 737-200",	"Boeing 737",	136,	136,	4,	535,	780,	4200,	35245000,	1976,	18,	Manufacturer("Boeing",	countryCode="US"),	1859,	imageUrl ="https://www.norebbo.com/2018/09/boeing-737-200-blank-illustration-templates/"),
Model("Boeing 737-300",	"Boeing 737",	144,	144,	4,	570,	800,	4400,	47120000,	1976,	24,	Manufacturer("Boeing",	countryCode="US"),	1940,	imageUrl ="https://www.norebbo.com/2018/09/boeing-737-300-blank-illustration-templates/"),
Model("Boeing 737-400",	"Boeing 737",	168,	168,	4,	670,	800,	5000,	65740000,	1976,	24,	Manufacturer("Boeing",	countryCode="US"),	2540,	imageUrl ="https://www.norebbo.com/2018/09/boeing-737-400-blank-illustration-templates/"),
Model("Boeing 737-500",	"Boeing 737",	132,	132,	5,	510,	800,	5200,	57285000,	1976,	18,	Manufacturer("Boeing",	countryCode="US"),	1830,	imageUrl ="https://www.norebbo.com/2018/09/boeing-737-500-blank-illustration-templates-with-and-without-blended-winglets/"),
Model("Boeing 737-600",	"Boeing 737",	130,	130,	5,	479,	834,	7200,	55195000,	1976,	24,	Manufacturer("Boeing",	countryCode="US"),	1878,	imageUrl ="https://www.norebbo.com/2018/09/boeing-737-600-blank-illustration-templates/"),
Model("Boeing 737-700",	"Boeing 737",	149,	149,	5,	592,	834,	7630,	66215000,	1820,	24,	Manufacturer("Boeing",	countryCode="US"),	2042,	imageUrl ="https://www.norebbo.com/2014/04/boeing-737-700-blank-illustration-templates/"),
Model("Boeing 737-700ER",	"Boeing 737",	149,	88,	6,	525,	834,	10695,	83885000,	1820,	24,	Manufacturer("Boeing",	countryCode="US"),	2042,	imageUrl ="https://www.norebbo.com/2014/04/boeing-737-700-blank-illustration-templates/"),
Model("Boeing 737-800",	"Boeing 737",	180,	180,	6,	738,	842,	5436,	96045000,	1820,	30,	Manufacturer("Boeing",	countryCode="US"),	2316,	imageUrl ="https://www.norebbo.com/2012/11/boeing-737-800-blank-illustration-templates/"),
Model("Boeing 737-900",	"Boeing 737",	215,	215,	6,	875,	842,	5436,	117610000,	1820,	30,	Manufacturer("Boeing",	countryCode="US"),	3000,	imageUrl ="https://www.norebbo.com/2014/08/boeing-737-900-blank-illustration-templates/"),
Model("Boeing 737-900ER",	"Boeing 737",	220,	220,	6,	868,	844,	5460,	128820000,	1820,	36,	Manufacturer("Boeing",	countryCode="US"),	3000,	imageUrl ="https://www.norebbo.com/2016/07/boeing-737-900er-with-split-scimitar-winglets-blank-illustration-templates/"),
Model("Boeing 747-100",	"Boeing 747",	550,	550,	5,	3105,	907,	8530,	258210000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	3250,	imageUrl ="https://www.norebbo.com/2019/07/boeing-747-100-side-view/"),
Model("Boeing 747-200",	"Boeing 747",	550,	550,	5,	3025,	907,	12150,	272840000,	1820,	60,	Manufacturer("Boeing",	countryCode="US"),	3300,	imageUrl ="https://www.norebbo.com/2019/08/boeing-747-200-side-view/"),
Model("Boeing 747-300",	"Boeing 747",	660,	400,	6,	3602,	939,	11720,	376675000,	1820,	72,	Manufacturer("Boeing",	countryCode="US"),	2955,	imageUrl ="https://www.norebbo.com/boeing-747-300-side-view/"),
Model("Boeing 747-400",	"Boeing 747",	660,	416,	6,	3615,	943,	13490,	396245000,	1820,	72,	Manufacturer("Boeing",	countryCode="US"),	3260,	imageUrl =""),
Model("Boeing 747-400D",	"Boeing 747",	660,	660,	5,	3845,	943,	10490,	307895000,	1820,	72,	Manufacturer("Boeing",	countryCode="US"),	3260,	imageUrl =""),
Model("Boeing 747-400ER",	"Boeing 747",	660,	416,	6,	3625,	943,	14045,	413820000,	1820,	72,	Manufacturer("Boeing",	countryCode="US"),	3300,	imageUrl ="https://www.norebbo.com/2013/09/boeing-747-400-blank-illustration-templates/"),
Model("Boeing 747-8i",	"Boeing 747",	718,	605,	7,	3690,	933,	14320,	580070000,	1820,	72,	Manufacturer("Boeing",	countryCode="US"),	3100,	imageUrl ="https://www.norebbo.com/2015/12/boeing-747-8i-blank-illustration-templates/"),
Model("Boeing 747SP",	"Boeing 747",	400,	248,	6,	2090,	994,	10800,	258685000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2820,	imageUrl ="https://www.norebbo.com/2019/08/boeing-747sp-side-view/"),
Model("Boeing 757-100",	"Boeing 757",	160,	160,	5,	660,	854,	8250,	67450000,	1976,	54,	Manufacturer("Boeing",	countryCode="US"),	1910,	imageUrl =""),
Model("Boeing 757-200",	"Boeing 757",	239,	239,	5,	1023,	854,	7250,	86735000,	1976,	54,	Manufacturer("Boeing",	countryCode="US"),	2070,	imageUrl ="https://www.norebbo.com/2015/01/boeing-757-200-blank-illustration-templates/"),
Model("Boeing 757-200ER",	"Boeing 757",	239,	239,	5,	950,	850,	9170,	109250000,	1976,	54,	Manufacturer("Boeing",	countryCode="US"),	2070,	imageUrl ="https://www.norebbo.com/2015/01/boeing-757-200-blank-illustration-templates/"),
Model("Boeing 757-300",	"Boeing 757",	295,	295,	5,	1426,	850,	6421,	121125000,	1976,	54,	Manufacturer("Boeing",	countryCode="US"),	2377,	imageUrl ="https://www.norebbo.com/2017/03/boeing-757-300-blank-illustration-templates/"),
Model("Boeing 767-200",	"Boeing 767",	265,	265,	5,	1285,	851,	7200,	84075000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	1900,	imageUrl ="https://www.norebbo.com/2014/07/boeing-767-200-blank-illustration-templates/"),
Model("Boeing 767-200ER",	"Boeing 767",	265,	216,	5,	1165,	896,	12200,	116375000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2480,	imageUrl ="https://www.norebbo.com/2014/07/boeing-767-200-blank-illustration-templates/"),
Model("Boeing 767-300",	"Boeing 767",	299,	299,	5,	1385,	851,	7200,	153330000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2800,	imageUrl ="https://www.norebbo.com/2014/07/boeing-767-200-blank-illustration-templates/"),
Model("Boeing 767-300ER",	"Boeing 767",	299,	261,	6,	1315,	896,	11093,	185345000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2650,	imageUrl ="https://www.norebbo.com/2014/07/boeing-767-200-blank-illustration-templates/"),
Model("Boeing 767-400ER",	"Boeing 767",	409,	272,	6,	1830,	896,	10415,	235125000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	3290,	imageUrl ="https://www.norebbo.com/2014/07/boeing-767-400-blank-illustration-templates/"),
Model("Boeing 777-200",	"Boeing 777",	440,	440,	6,	2050,	896,	9700,	311410000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2440,	imageUrl ="https://www.norebbo.com/2012/12/boeing-777-200-blank-illustration-templates/"),
Model("Boeing 777-200ER",	"Boeing 777",	440,	301,	7,	1896,	896,	13080,	358530000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	3280,	imageUrl ="https://www.norebbo.com/2012/12/boeing-777-200-blank-illustration-templates/"),
Model("Boeing 777-200LR",	"Boeing 777",	440,	301,	7,	1925,	896,	15843,	371260000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2800,	imageUrl ="https://www.norebbo.com/2012/12/boeing-777-200-blank-illustration-templates/"),
Model("Boeing 777-300",	"Boeing 777",	550,	550,	6,	2925,	945,	11121,	361190000,	1820,	66,	Manufacturer("Boeing",	countryCode="US"),	3230,	imageUrl ="https://www.norebbo.com/2014/03/boeing-777-300-blank-illustration-templates/"),
Model("Boeing 777-300ER",	"Boeing 777",	550,	365,	7,	2895,	945,	13649,	441085000,	1820,	66,	Manufacturer("Boeing",	countryCode="US"),	3050,	imageUrl ="https://www.norebbo.com/2014/03/boeing-777-300-blank-illustration-templates/"),
Model("Boeing 777-8",	"Boeing 777",	440,	395,	8,	1778,	896,	16090,	491910000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	3050,	imageUrl ="https://www.norebbo.com/2019/12/boeing-777-8-side-view/"),
Model("Boeing 777-9",	"Boeing 777",	520,	426,	8,	2069,	896,	13940,	622535000,	1820,	66,	Manufacturer("Boeing",	countryCode="US"),	3050,	imageUrl ="https://www.norebbo.com/2019/12/boeing-777-9-side-view/"),
Model("Boeing 787-10 Dreamliner",	"Boeing 787",	440,	440,	7,	1903,	903,	11750,	378100000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2800,	imageUrl ="https://www.norebbo.com/2017/06/boeing-787-10-blank-illustration-templates/"),
Model("Boeing 787-10ER",	"Boeing 787",	440,	280,	7,	1860,	903,	13750,	402515000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2900,	imageUrl ="https://www.norebbo.com/2017/06/boeing-787-10-blank-illustration-templates/"),
Model("Boeing 787-8 Dreamliner",	"Boeing 787",	359,	359,	7,	1489,	907,	13621,	294975000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2600,	imageUrl ="https://www.norebbo.com/2013/02/boeing-787-8-blank-illustration-templates/"),
Model("Boeing 787-9 Dreamliner",	"Boeing 787",	420,	420,	7,	1853,	903,	14010,	325565000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2800,	imageUrl ="https://www.norebbo.com/2014/04/boeing-787-9-blank-illustration-templates/"),
Model("Boeing 787-9ER",	"Boeing 787",	420,	265,	7,	1762,	903,	15400,	364705000,	1820,	54,	Manufacturer("Boeing",	countryCode="US"),	2900,	imageUrl ="https://www.norebbo.com/2014/04/boeing-787-9-blank-illustration-templates/"),
Model("Boeing 797-6",	"Boeing 797",	225,	225,	8,	759,	890,	9260,	245860000,	1820,	60,	Manufacturer("Boeing",	countryCode="US"),	2600,	imageUrl ="https://www.norebbo.com/boeing-797-side-view/"),
Model("Boeing 797-7",	"Boeing 797",	275,	275,	8,	952,	890,	8330,	308560000,	1820,	60,	Manufacturer("Boeing",	countryCode="US"),	2600,	imageUrl ="https://www.norebbo.com/boeing-797-side-view/"),
Model("Boeing Vertol 107-II",	"Boeing Vertol",	28,	28,	4,	47,	265,	1020,	3895000,	1300,	6,	Manufacturer("Boeing",	countryCode="US"),	1,	imageUrl =""),
Model("Boeing Vertol 234",	"Boeing Vertol",	44,	44,	4,	70,	269,	1010,	8265000,	1300,	6,	Manufacturer("Boeing",	countryCode="US"),	1,	imageUrl =""),
Model("Bombardier CRJ100",	"Bombardier CRJ",	50,	50,	7,	92,	830,	2250,	32775000,	1820,	0,	Manufacturer("Bombardier",	countryCode="CA"),	1920,	imageUrl ="https://www.norebbo.com/2015/04/bombardier-canadair-regional-jet-200-blank-illustration-templates/"),
Model("Bombardier CRJ1000",	"Bombardier CRJ",	104,	104,	7,	299,	870,	3004,	64885000,	1820,	12,	Manufacturer("Bombardier",	countryCode="CA"),	2120,	imageUrl ="https://www.norebbo.com/2019/06/bombardier-crj-1000-side-view/"),
Model("Bombardier CRJ200",	"Bombardier CRJ",	50,	50,	7,	92,	830,	3150,	34770000,	1820,	0,	Manufacturer("Bombardier",	countryCode="CA"),	1920,	imageUrl ="https://www.norebbo.com/2015/04/bombardier-canadair-regional-jet-200-blank-illustration-templates/"),
Model("Bombardier CRJ700",	"Bombardier CRJ",	78,	78,	7,	172,	828,	3045,	54625000,	1820,	6,	Manufacturer("Bombardier",	countryCode="CA"),	1605,	imageUrl ="https://www.norebbo.com/2015/05/bombardier-canadair-regional-jet-700-blank-illustration-templates/"),
Model("Bombardier CRJ900",	"Bombardier CRJ",	90,	90,	7,	211,	870,	2876,	59945000,	1820,	12,	Manufacturer("Bombardier",	countryCode="CA"),	1939,	imageUrl ="https://www.norebbo.com/2016/07/bombardier-canadair-regional-jet-900-blank-illustration-templates/"),
Model("Bombardier Global 5000",	"Modern Business Jet",	40,	16,	10,	50,	934,	9630,	38665000,	1820,	12,	Manufacturer("Bombardier",	countryCode="CA"),	1689,	imageUrl ="https://www.norebbo.com/bombardier-global-5000-blank-illustration-templates/"),
Model("Bombardier Global 7500",	"Modern Business Jet",	48,	19,	10,	67,	1080,	14260,	54435000,	1820,	12,	Manufacturer("Bombardier",	countryCode="CA"),	1768,	imageUrl ="https://www.norebbo.com/bombardier-global-7500-side-view/"),
Model("Boom Overture",	"Boom Overture",	160,	80,	9,	1996,	1800,	7870,	377435000,	1664,	150,	Manufacturer("Boom Technology",	countryCode="US"),	3048,	imageUrl =""),
Model("Bristol Brittania",	"Post-War Props",	139,	139,	1,	540,	575,	7130,	25460000,	1820,	3,	Manufacturer("BAe",	countryCode="GB"),	1200,	imageUrl =""),
Model("CASA C-212 Aviocar",	"CASA",	26,	26,	4,	32,	354,	2680,	3610000,	1560,	0,	Manufacturer("CASA",	countryCode="ES"),	600,	imageUrl =""),
Model("CASA CN-235",	"CASA",	40,	40,	4,	56,	460,	3658,	8360000,	1560,	0,	Manufacturer("CASA",	countryCode="ES"),	1204,	imageUrl =""),
Model("Cessna Caravan",	"Cessna",	16,	16,	4,	15,	355,	2400,	3230000,	1820,	0,	Manufacturer("Cessna",	countryCode="US"),	762,	imageUrl ="https://www.norebbo.com/2017/06/cessna-208-grand-caravan-blank-illustration-templates/"),
Model("Cessna Citation X",	"Cessna",	30,	12,	10,	34,	900,	6050,	29355000,	1820,	12,	Manufacturer("Cessna",	countryCode="US"),	1600,	imageUrl ="https://www.norebbo.com/cessna-citation-x-template/"),
Model("Comac ARJ21",	"Comac ARJ21",	90,	90,	4,	235,	828,	2200,	32870000,	1300,	12,	Manufacturer("COMAC",	countryCode="CN"),	1700,	imageUrl =""),
Model("Comac C919",	"Comac C919",	168,	168,	5,	634,	834,	4075,	72390000,	1300,	24,	Manufacturer("COMAC",	countryCode="CN"),	2000,	imageUrl ="https://www.norebbo.com/comac-c919-side-view/"),
Model("Comac C929-500",	"Comac C929",	348,	348,	6,	1608,	908,	14000,	189240000,	1560,	36,	Manufacturer("COMAC",	countryCode="CN"),	2700,	imageUrl =""),
Model("Comac C929-600",	"Comac C929",	405,	405,	6,	1950,	908,	12000,	219355000,	1560,	36,	Manufacturer("COMAC",	countryCode="CN"),	2800,	imageUrl =""),
Model("Comac C929-700",	"Comac C929",	440,	440,	6,	2110,	908,	10000,	263340000,	1560,	36,	Manufacturer("COMAC",	countryCode="CN"),	2900,	imageUrl =""),
Model("Comac C939",	"Comac C939",	460,	460,	6,	2188,	908,	14000,	267900000,	1560,	45,	Manufacturer("COMAC",	countryCode="CN"),	2800,	imageUrl =""),
Model("Concorde",	"Concorde",	192,	128,	8,	3559,	2158,	7223,	346370000,	1820,	90,	Manufacturer("BAe",	countryCode="GB"),	3600,	imageUrl ="https://www.norebbo.com/aerospatiale-bac-concorde-blank-illustration-templates/"),
Model("Dassault Falcon 50",	"60s Business Jet",	28,	14,	10,	57,	883,	5660,	17480000,	1456,	12,	Manufacturer("Dassault",	countryCode="FR"),	1524,	imageUrl ="https://www.norebbo.com/dassault-falcon-50/"),
Model("Dassault Mercure",	"Dassault",	162,	162,	2,	540,	926,	2084,	62890000,	1820,	18,	Manufacturer("Dassault Aviation",	countryCode="FR"),	2100,	imageUrl =""),
Model("De Havilland Canada DHC-7-100",	"De Havilland Canada DHC",	50,	50,	4,	66,	428,	1300,	18335000,	1820,	6,	Manufacturer("De Havilland Canada",	countryCode="CA"),	620,	imageUrl =""),
Model("De Havilland Canada DHC-8-100",	"De Havilland Canada DHC",	39,	39,	5,	55,	448,	1889,	15295000,	1820,	3,	Manufacturer("De Havilland Canada",	countryCode="CA"),	950,	imageUrl ="https://www.norebbo.com/2018/01/de-havilland-dhc-8-200-dash-8-blank-illustration-templates/"),
Model("De Havilland Canada DHC-8-200",	"De Havilland Canada DHC",	39,	39,	5,	55,	448,	2084,	16910000,	1820,	3,	Manufacturer("De Havilland Canada",	countryCode="CA"),	1000,	imageUrl ="https://www.norebbo.com/2018/01/de-havilland-dhc-8-200-dash-8-blank-illustration-templates/"),
Model("De Havilland Canada DHC-8-300",	"De Havilland Canada DHC",	50,	50,	5,	74,	450,	1711,	20805000,	1820,	6,	Manufacturer("De Havilland Canada",	countryCode="CA"),	1085,	imageUrl ="https://www.norebbo.com/2018/05/de-havilland-dhc-8-300-blank-illustration-templates/"),
Model("De Havilland Canada DHC-8-400",	"De Havilland Canada DHC",	68,	68,	5,	158,	667,	1980,	31635000,	1560,	6,	Manufacturer("De Havilland Canada",	countryCode="CA"),	1085,	imageUrl ="https://www.norebbo.com/2018/05/de-havilland-dhc-8-300-blank-illustration-templates/"),
Model("De Havilland Canada Q400",	"De Havilland Canada DHC",	82,	82,	5,	185,	562,	2040,	33250000,	1560,	12,	Manufacturer("De Havilland Canada",	countryCode="CA"),	1885,	imageUrl ="https://www.norebbo.com/2015/08/bombardier-dhc-8-402-q400-blank-illustration-templates/"),
Model("Dornier 1128",	"Dornier",	130,	130,	8,	464,	923,	3800,	81225000,	1300,	18,	Manufacturer("Dornier",	countryCode="DE"),	1550,	imageUrl =""),
Model("Dornier 328-110",	"Dornier",	33,	33,	8,	60,	620,	1310,	19855000,	1820,	9,	Manufacturer("Dornier",	countryCode="DE"),	1088,	imageUrl ="https://www.norebbo.com/2019/01/dornier-328-110-blank-illustration-templates/"),
Model("Dornier 328JET",	"Dornier",	44,	44,	8,	74,	740,	1665,	30020000,	1820,	9,	Manufacturer("Dornier",	countryCode="DE"),	1367,	imageUrl ="https://www.norebbo.com/2019/01/fairchild-dornier-328jet-illustrations/"),
Model("Dornier 528",	"Dornier",	65,	65,	8,	178,	1000,	3000,	42085000,	1820,	9,	Manufacturer("Dornier",	countryCode="DE"),	1363,	imageUrl =""),
Model("Dornier 728",	"Dornier",	80,	80,	8,	244,	1000,	3300,	49590000,	1820,	9,	Manufacturer("Dornier",	countryCode="DE"),	1463,	imageUrl =""),
Model("Dornier 928",	"Dornier",	112,	112,	8,	399,	951,	3600,	61370000,	1300,	12,	Manufacturer("Dornier",	countryCode="DE"),	1513,	imageUrl =""),
Model("McDonnell Douglas DC-10-10",	"DC-10",	410,	410,	3,	1835,	876,	6500,	135660000,	1820,	18,	Manufacturer("McDonnell Douglas",	countryCode="US"),	2700,	imageUrl ="https://www.norebbo.com/mcdonnell-douglas-dc-10-30-blank-templates/"),
Model("McDonnell Douglas DC-10-30",	"DC-10",	410,	410,	3,	1850,	886,	9400,	163970000,	1820,	24,	Manufacturer("McDonnell Douglas",	countryCode="US"),	3200,	imageUrl ="https://www.norebbo.com/mcdonnell-douglas-dc-10-30-blank-templates/"),
Model("McDonnell Douglas DC-10-40",	"DC-10",	410,	410,	4,	1900,	886,	12392,	165680000,	1820,	30,	Manufacturer("McDonnell Douglas",	countryCode="US"),	2900,	imageUrl ="https://www.norebbo.com/mcdonnell-douglas-dc-10-30-blank-templates/"),
Model("Douglas DC-3",	"Post-War Props",	32,	32,	0,	51,	333,	1800,	285000,	3224,	0,	Manufacturer("Douglas Aircraft Company",	countryCode="US"),	701,	imageUrl =""),
Model("McDonnell Douglas DC-8-10",	"DC-8",	177,	177,	2,	929,	895,	6960,	32205000,	2496,	6,	Manufacturer("Douglas Aircraft Company",	countryCode="US"),	2680,	imageUrl =""),
Model("McDonnell Douglas DC-8-72",	"DC-8",	189,	189,	3,	969,	895,	9800,	41325000,	2496,	12,	Manufacturer("Douglas Aircraft Company",	countryCode="US"),	2680,	imageUrl =""),
Model("McDonnell Douglas DC-8-73",	"DC-8",	259,	259,	3,	1340,	895,	8300,	67545000,	2496,	12,	Manufacturer("Douglas Aircraft Company",	countryCode="US"),	2680,	imageUrl ="https://www.norebbo.com/douglas-dc-8-73-and-dc-8-73cf-blank-illustration-templates/"),
Model("Embraer E175-E2",	"Embraer E-Jet E2",	88,	88,	6,	200,	870,	3735,	53295000,	1560,	9,	Manufacturer("Embraer",	countryCode="BR"),	1800,	imageUrl ="https://www.norebbo.com/2019/03/e175-e2-side-view/"),
Model("Embraer E190-E2",	"Embraer E-Jet E2",	106,	106,	6,	260,	870,	5278,	65835000,	1560,	12,	Manufacturer("Embraer",	countryCode="BR"),	1450,	imageUrl ="https://www.norebbo.com/2019/03/e190-e2-blank-side-view/"),
Model("Embraer E195-E2",	"Embraer E-Jet E2",	146,	146,	6,	421,	833,	4800,	83410000,	1560,	18,	Manufacturer("Embraer",	countryCode="BR"),	1970,	imageUrl ="https://www.norebbo.com/2019/03/embraer-e195-e2-side-view/"),
Model("Embraer ERJ135",	"Embraer ERJ",	37,	37,	5,	79,	850,	3241,	10830000,	1560,	3,	Manufacturer("Embraer",	countryCode="BR"),	1580,	imageUrl ="https://www.norebbo.com/2018/05/embraer-erj-135-blank-illustration-templates/"),
Model("Embraer ERJ145",	"Embraer ERJ",	50,	50,	5,	126,	850,	2800,	19950000,	1560,	3,	Manufacturer("Embraer",	countryCode="BR"),	1410,	imageUrl ="https://www.norebbo.com/2018/04/embraer-erj-145-blank-illustration-templates/"),
Model("Embraer ERJ145XR",	"Embraer ERJ",	50,	50,	6,	124,	850,	3700,	22895000,	1560,	0,	Manufacturer("Embraer",	countryCode="BR"),	1720,	imageUrl ="https://www.norebbo.com/2018/04/embraer-erj-145xr-blank-illustration-templates/"),
Model("Embraer ERJ170",	"Embraer ERJ",	72,	72,	5,	212,	870,	3982,	24890000,	1560,	3,	Manufacturer("Embraer",	countryCode="BR"),	1644,	imageUrl ="https://www.norebbo.com/embraer-erj-175-templates-with-the-new-style-winglets/"),
Model("Embraer ERJ175",	"Embraer ERJ",	78,	78,	5,	232,	870,	4074,	28690000,	1560,	6,	Manufacturer("Embraer",	countryCode="BR"),	2144,	imageUrl ="https://www.norebbo.com/2015/10/embraer-erj-175-templates-with-the-new-style-winglets/"),
Model("Embraer ERJ190",	"Embraer ERJ",	100,	100,	5,	310,	823,	4537,	35150000,	1560,	12,	Manufacturer("Embraer",	countryCode="BR"),	2054,	imageUrl ="https://www.norebbo.com/2015/06/embraer-190-blank-illustration-templates/"),
Model("F27-100",	"Fokker",	44,	44,	3,	61,	460,	1468,	9975000,	1560,	0,	Manufacturer("Fokker",	countryCode="NL"),	1550,	imageUrl =""),
Model("Fokker 100",	"Fokker",	109,	109,	4,	340,	845,	1686,	41990000,	1820,	3,	Manufacturer("Fokker",	countryCode="NL"),	1550,	imageUrl ="https://www.norebbo.com/2018/07/fokker-100-f-28-0100-blank-illustration-templates/"),
Model("Fokker 50",	"Fokker",	56,	56,	3,	86,	500,	1700,	13870000,	1560,	3,	Manufacturer("Fokker",	countryCode="NL"),	1550,	imageUrl =""),
Model("Fokker 60",	"Fokker",	64,	64,	3,	115,	515,	1550,	17385000,	1560,	3,	Manufacturer("Fokker",	countryCode="NL"),	1550,	imageUrl =""),
Model("Fokker 60 Jet",	"Fokker",	64,	64,	3,	160,	845,	1400,	23370000,	1560,	6,	Manufacturer("Fokker",	countryCode="NL"),	1550,	imageUrl =""),
Model("Gulfstream G650ER",	"Modern Business Jet",	48,	19,	10,	51,	966,	13890,	62605000,	2080,	12,	Manufacturer("Gulfstream",	countryCode="US"),	1920,	imageUrl ="https://www.norebbo.com/gulfstream-g650er-template/"),
Model("Handley Page Dart Herald 200",	"Post-War Props",	56,	56,	0,	108,	443,	1126,	665000,	1560,	0,	Manufacturer("Handley Page",	countryCode="GB"),	820,	imageUrl =""),
Model("Ilyushin Il-18",	"Ilyushin Il",	120,	120,	0,	474,	625,	6500,	9405000,	1300,	6,	Manufacturer("Ilyushin",	countryCode="RU"),	1350,	imageUrl =""),
Model("Ilyushin Il-62",	"Ilyushin Il",	186,	186,	2,	949,	900,	10000,	29260000,	1456,	18,	Manufacturer("Ilyushin",	countryCode="RU"),	2300,	imageUrl =""),
Model("Ilyushin Il-96-300",	"Ilyushin Il-96",	300,	300,	4,	1500,	900,	13500,	71820000,	1456,	27,	Manufacturer("Ilyushin",	countryCode="RU"),	3200,	imageUrl =""),
Model("Ilyushin Il-96-400",	"Ilyushin Il-96",	436,	436,	4,	2198,	900,	14500,	129390000,	1456,	36,	Manufacturer("Ilyushin",	countryCode="RU"),	2600,	imageUrl =""),
Model("Lockheed Constellation L-749",	"Post-War Props",	81,	81,	1,	230,	555,	8039,	12730000,	1820,	6,	Manufacturer("Lockheed",	countryCode="US"),	1050,	imageUrl =""),
Model("Lockheed JetStar",	"60s Business Jet",	26,	11,	9,	52,	920,	4820,	15295000,	1820,	6,	Manufacturer("Lockheed",	countryCode="US"),	1100,	imageUrl =""),
Model("Lockheed L-1011-100",	"Lockheed TriStar",	400,	400,	5,	1882,	962,	4963,	197885000,	2080,	24,	Manufacturer("Lockheed",	countryCode="US"),	2560,	imageUrl =""),
Model("Lockheed L-1011-200",	"Lockheed TriStar",	400,	400,	5,	1892,	969,	6500,	222965000,	2080,	27,	Manufacturer("Lockheed",	countryCode="US"),	2560,	imageUrl =""),
Model("Lockheed L-1011-500",	"Lockheed TriStar",	400,	250,	6,	1868,	986,	9899,	298775000,	1560,	36,	Manufacturer("Lockheed",	countryCode="US"),	2865,	imageUrl ="https://www.norebbo.com/2015/03/lockheed-l-1011-500-blank-illustration-templates/"),
Model("McDonnell Douglas DC-9-10",	"DC-9",	92,	92,	3,	270,	965,	2367,	24130000,	1040,	6,	Manufacturer("McDonnell Douglas",	countryCode="US"),	1816,	imageUrl =""),
Model("McDonnell Douglas DC-9-30",	"DC-9",	115,	115,	4,	345,	804,	2778,	30590000,	1040,	6,	Manufacturer("McDonnell Douglas",	countryCode="US"),	1900,	imageUrl ="https://www.norebbo.com/mcdonnell-douglas-dc-9-30-templates/"),
Model("McDonnell Douglas DC-9-50",	"DC-9",	139,	139,	4,	464,	804,	3030,	40185000,	1040,	6,	Manufacturer("McDonnell Douglas",	countryCode="US"),	2100,	imageUrl ="https://www.norebbo.com/dc-9-50-side-view/"),
Model("McDonnell Douglas MD-11",	"DC-10",	410,	410,	5,	1910,	886,	12455,	217740000,	1820,	36,	Manufacturer("McDonnell Douglas",	countryCode="US"),	3050,	imageUrl =""),
Model("McDonnell Douglas MD-220",	"60s Business Jet",	29,	29,	8,	70,	1020,	4100,	17195000,	1820,	12,	Manufacturer("McDonnell Douglas",	countryCode="US"),	1200,	imageUrl =""),
Model("McDonnell Douglas MD-81",	"DC-9",	146,	146,	4,	425,	811,	4635,	71440000,	1560,	18,	Manufacturer("McDonnell Douglas",	countryCode="US"),	2070,	imageUrl ="https://www.norebbo.com/2015/02/mcdonnell-douglas-md-80-blank-illustration-templates/"),
Model("McDonnell Douglas MD-90",	"DC-9",	160,	160,	5,	530,	811,	3787,	80940000,	1560,	30,	Manufacturer("McDonnell Douglas",	countryCode="US"),	2134,	imageUrl ="https://www.norebbo.com/2018/02/mcdonnell-douglas-md-90-blank-illustration-templates/"),
Model("McDonnell Douglas MD-90ER",	"DC-9",	160,	160,	5,	510,	811,	4143,	87685000,	1560,	30,	Manufacturer("McDonnell Douglas",	countryCode="US"),	2134,	imageUrl ="https://www.norebbo.com/2018/02/mcdonnell-douglas-md-90-blank-illustration-templates/"),
Model("McDonnell Douglas MD-XX",	"DC-10",	440,	380,	5,	1950,	906,	10455,	239780000,	1820,	36,	Manufacturer("McDonnell Douglas",	countryCode="US"),	2964,	imageUrl =""),
Model("Mil Mi-26",	"Mil",	90,	90,	0,	155,	255,	970,	12350000,	1300,	0,	Manufacturer("Mil",	countryCode="RU"),	1,	imageUrl =""),
Model("Mitsubishi MRJ-100",	"Mitsubishi SpaceJet",	84,	84,	7,	195,	900,	3540,	67450000,	2080,	9,	Manufacturer("Mitsubishi",	countryCode="JP"),	1760,	imageUrl =""),
Model("Mitsubishi MRJ-90",	"Mitsubishi SpaceJet",	88,	88,	7,	204,	900,	3770,	72390000,	2080,	12,	Manufacturer("Mitsubishi",	countryCode="JP"),	1740,	imageUrl =""),
Model("Pilatus PC-12",	"Pilatus",	9,	9,	5,	8,	528,	3417,	1995000,	1456,	0,	Manufacturer("Pilatus",	countryCode="CH"),	748,	imageUrl =""),
Model("Saab 2000",	"Saab Regional",	58,	58,	5,	88,	608,	2868,	27170000,	1820,	3,	Manufacturer("Saab",	countryCode="SE"),	1252,	imageUrl =""),
Model("Saab 90 Scandia",	"Saab Regional",	32,	32,	6,	36,	340,	2650,	7220000,	1820,	0,	Manufacturer("Saab",	countryCode="SE"),	850,	imageUrl =""),
Model("Shaanxi Y-10",	"Shaanxi Y",	178,	178,	4,	755,	917,	5560,	38950000,	1040,	12,	Manufacturer("Shaanxi Aircraft Corporation",	countryCode="CN"),	2700,	imageUrl =""),
Model("Sikorsky S-76",	"Sikorsky",	12,	12,	7,	17,	287,	761,	3515000,	1560,	3,	Manufacturer("Sikorsky",	countryCode="US"),	1,	imageUrl =""),
Model("Sikorsky S-92",	"Sikorsky",	25,	19,	8,	35,	290,	998,	9405000,	1560,	3,	Manufacturer("Sikorsky",	countryCode="US"),	1,	imageUrl =""),
Model("Sud Aviation Caravelle 12",	"Sud Aviation Caravelle",	140,	140,	2,	560,	810,	3200,	28310000,	1300,	6,	Manufacturer("Sud Aviation",	countryCode="FR"),	1310,	imageUrl =""),
Model("Sud Aviation Caravelle III",	"Sud Aviation Caravelle",	80,	80,	2,	238,	845,	2100,	23275000,	1300,	6,	Manufacturer("Sud Aviation",	countryCode="FR"),	1270,	imageUrl =""),
Model("Sukhoi KR860",	"Sukhoi",	860,	860,	5,	4770,	907,	9400,	334590000,	1300,	87,	Manufacturer("JSC Sukhoi",	countryCode="RU"),	3400,	imageUrl =""),
Model("Sukhoi Su-80",	"Sukhoi",	32,	32,	5,	44,	520,	1600,	6745000,	1300,	12,	Manufacturer("JSC Sukhoi",	countryCode="RU"),	650,	imageUrl =""),
Model("Sukhoi Superjet 100",	"Sukhoi",	108,	108,	6,	300,	828,	4578,	53010000,	1560,	24,	Manufacturer("JSC Sukhoi",	countryCode="RU"),	1731,	imageUrl ="https://www.norebbo.com/2016/02/sukhoi-ssj-100-blank-illustration-templates/"),
Model("Sukhoi Superjet 130NG",	"Sukhoi",	130,	130,	6,	449,	871,	4008,	67830000,	1560,	24,	Manufacturer("JSC Sukhoi",	countryCode="RU"),	1731,	imageUrl =""),
Model("Tupolev Tu-124",	"Tupolev Tu",	56,	56,	1,	185,	970,	2300,	9025000,	1300,	12,	Manufacturer("Tupolev",	countryCode="RU"),	1550,	imageUrl =""),
Model("Tupolev Tu-204",	"Tupolev Tu",	210,	210,	4,	965,	810,	4300,	41325000,	1300,	24,	Manufacturer("Tupolev",	countryCode="RU"),	1870,	imageUrl ="https://www.norebbo.com/tupolev-tu-204-100-blank-illustration-templates/"),
Model("Tupolev Tu-334-100",	"Tupolev Tu",	102,	102,	4,	288,	820,	4100,	31255000,	1300,	12,	Manufacturer("Tupolev",	countryCode="RU"),	1940,	imageUrl =""),
Model("Tupolev Tu-334-200",	"Tupolev Tu",	126,	126,	4,	465,	820,	3150,	34295000,	1300,	6,	Manufacturer("Tupolev",	countryCode="RU"),	1820,	imageUrl =""),
Model("Vickers VC10",	"Vickers",	150,	150,	3,	565,	930,	9410,	60230000,	1560,	18,	Manufacturer("Vickers-Armstrongs",	countryCode="GB"),	2520,	imageUrl =""),
Model("Xi'an MA600",	"Xi'an Turboprops",	60,	60,	4,	99,	514,	1600,	23180000,	1300,	6,	Manufacturer("Xi'an Aircraft Industrial Corporation",	countryCode="CN"),	750,	imageUrl =""),
Model("Xi'an MA700",	"Xi'an Turboprops",	86,	86,	4,	190,	637,	1500,	34105000,	1300,	12,	Manufacturer("Xi'an Aircraft Industrial Corporation",	countryCode="CN"),	630,	imageUrl =""),
Model("Yakovlev MC-21-100",	"Yakovlev MC-21",	132,	132,	5,	455,	870,	6140,	53770000,	1300,	30,	Manufacturer("Irkut",	countryCode="RU"),	1322,	imageUrl =""),
Model("Yakovlev MC-21-100",	"Yakovlev MC-21",	132,	132,	5,	455,	870,	6140,	53770000,	1300,	30,	Manufacturer("Irkut",	countryCode="RU"),	1322,	imageUrl =""),
Model("Yakovlev MC-21-200",	"Yakovlev MC-21",	165,	165,	5,	615,	870,	6400,	63080000,	1300,	36,	Manufacturer("Irkut",	countryCode="RU"),	1350,	imageUrl =""),
Model("Yakovlev MC-21-300",	"Yakovlev MC-21",	211,	211,	5,	865,	870,	6000,	82080000,	1300,	45,	Manufacturer("Irkut",	countryCode="RU"),	1644,	imageUrl ="https://www.norebbo.com/irkut-mc-21-300/"),
Model("Yakovlev MC-21-400",	"Yakovlev MC-21",	230,	230,	5,	937,	870,	5500,	97470000,	1300,	54,	Manufacturer("Irkut",	countryCode="RU"),	1500,	imageUrl =""),
Model("Zeppelin",	"Zeppelin",	432,	72,	10,	22,	148,	8000,	123880000,	624,	24,	Manufacturer("Zeppelin Luftschifftechnik GmbH",	countryCode="DE"),	100,	imageUrl =""),
Model("Zeppelin NT",	"Zeppelin",	50,	20,	7,	3,	138,	900,	10545000,	936,	18,	Manufacturer("Zeppelin Luftschifftechnik GmbH",	countryCode="DE"),	100,	imageUrl =""),
  )
  val modelByName = models.map { model => (model.name, model) }.toMap
}