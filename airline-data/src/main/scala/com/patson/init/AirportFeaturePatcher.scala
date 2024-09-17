package com.patson.init

import com.patson.model._
import com.patson.data.AirportSource
import com.patson.data.DestinationSource

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object AirportFeaturePatcher extends App {

  import AirportFeatureType._

  lazy val featureList = Map(

    INTERNATIONAL_HUB -> Map[String, Int](
      /**
       * international vacation destinations
       */
"DPS" -> 84, //Denpasar-Bali Island
"CDG" -> 80, //Paris
"IST" -> 80, //Istanbul
"AYT" -> 75, //Antalya
"JED" -> 70, //Jeddah
"CNX" -> 70, //Chiang Mai
"PQC" -> 70, //Phu Quoc Island
"HKT" -> 69, //Phuket
"HRG" -> 67, //Hurghada
"PUJ" -> 65, //Punta Cana
"DXB" -> 65, //Dubai
"CUN" -> 64, //Cancún
"HKG" -> 63, //Hong Kong
"BKK" -> 62, //Bangkok
"CUZ" -> 61, //Cusco
"LGK" -> 61, //Langkawi
"SSH" -> 60, //Sharm el-Sheikh
"LHR" -> 60, //London
"USM" -> 60, //Na Thon (Ko Samui Island)
"PNH" -> 59, //Phnom Penh
"RAK" -> 58, //Marrakech
"UTP" -> 58, //Rayong
"KBV" -> 57, //Krabi
"CXR" -> 56, //Nha Trang
"MLE" -> 55, //Malé Maldives
"JFK" -> 54, //New York
"NRT" -> 53, //Tokyo / Narita
"DAD" -> 50, //Da Nang
"BCN" -> 48, //Barcelona
"CMB" -> 48, //Colombo
"FCO" -> 46, //Rome
"DMK" -> 45, //Bangkok
"MBJ" -> 45, //Montego Bay
"HAV" -> 45, //Havana
"KIX" -> 45, //Osaka
"ASR" -> 44, //Kayseri
"CZM" -> 44, //Cozumel
"RMF" -> 44, //Marsa Alam
"KUL" -> 43, //Kuala Lumpur
"MIA" -> 42, //Miami
"SIN" -> 42, //Singapore
"VCE" -> 42, //Venice
"AMS" -> 41, //Amsterdam
"LAX" -> 41, //Los Angeles
"CAI" -> 40, //Cairo Egypt
"GRU" -> 39, //São Paulo
"GIG" -> 39, //Rio De Janeiro
"ATH" -> 39, //Athens
"VIE" -> 39, //Vienna
"CEB" -> 38, //Lapu-Lapu City
"EWR" -> 38, //New York City USA
"GZP" -> 38, //Gazipaşa
"HND" -> 38, //Tokyo / Haneda
"LGW" -> 38, //London United Kingdom
"AGP" -> 35, //Málaga
"CPT" -> 35, //Cape Town
"SPX" -> 35, //Cairo
"RHO" -> 34, //Rodes Island
"SCL" -> 34, //Santiago
"BJV" -> 34, //Bodrum
"ICN" -> 34, //Seoul
"YYZ" -> 34, //Toronto Canada
"BKI" -> 33, //Kota Kinabalu
"LIS" -> 32, //Lisbon
"BER" -> 32, //Berlin
"NAN" -> 32, //Nadi
"SAI" -> 31, //Siem Reap
"AGA" -> 31, //Agadir
"PEK" -> 31, //Beijing
"CTS" -> 30, //Chitose / Tomakomai
"SYD" -> 30, //Sydney Australia
"HNL" -> 30, //Honolulu
"AEP" -> 30, //Buenos Aires
"RUN" -> 30, //St Denis
"ARN" -> 30, //Stockholm
"DUB" -> 30, //Dublin Ireland
"LOP" -> 30, //Mataram
"MEX" -> 29, //Mexico City
"MUC" -> 29, //Munich
"MXP" -> 29, //Milan
"TLV" -> 29, //Tel Aviv
"MCO" -> 28, //Orlando
"GOX" -> 28, //Goa IN
"MPH" -> 28, //Malay
"FUE" -> 28, //Fuerteventura Island
"PEN" -> 28, //Penang
"DJE" -> 28, //Djerba
"PPT" -> 28, //Papeete
"PRG" -> 28, //Prague
"BOM" -> 27, //Mumbai
"LPB" -> 27, //La Paz / El Alto
"VRA" -> 27, //Varadero
"BUD" -> 26, //Budapest
"KEF" -> 26, //Reykjavík
"LAS" -> 25, //Las Vegas
"BAH" -> 25, //Manama
"GOI" -> 25, //Vasco da Gama
"YVR" -> 25, //Vancouver
"CMN" -> 25, //Casablanca
"ZQN" -> 25, //Queenstown
"MAD" -> 24, //Madrid
"LAP" -> 24, //La Paz
"NOU" -> 24, //Nouméa
"POP" -> 24, //Puerto Plata Dominican Republic
"PPS" -> 24, //Puerto Princesa City
"CPH" -> 23, //Copenhagen
"GUM" -> 23, //Hagåtña Guam International Airport
"KTM" -> 23, //Kathmandu
"POA" -> 22, //Porto Alegre
"SJU" -> 22, //San Juan
"EZE" -> 22, //Buenos Aires
"MRU" -> 22, //Port Louis
"BNE" -> 21, //Brisbane
"NAP" -> 21, //Nápoli
"IAD" -> 21, //Washington
"LIM" -> 21,
"MQP" -> 21, //Mpumalanga
"PDL" -> 21, //Azores
"SEZ" -> 21, //Mahe Island
"SFO" -> 21, //San Francisco
"PMI" -> 20, //Palma De Mallorca
"LPA" -> 20, //Gran Canaria Island
"TFS" -> 20, //Tenerife Island
"VAR" -> 20, //Varna
"HER" -> 20, //Heraklion
"SSA" -> 20, //Salvador
"FNC" -> 20, //Funchal
"LPQ" -> 20, //Luang Phabang
"OSL" -> 20, //Oslo
"MEL" -> 19, //Melbourne
"IBZ" -> 19, //Ibiza
"JTR" -> 19, //Santorini Island
"SVO" -> 19, //Moscow
"BOJ" -> 19, //Burgas
"TRV" -> 19, //Thiruvananthapuram
"MNL" -> 19,
"ZRH" -> 19, //Zurich
"CTG" -> 18, //Cartagena
"CNS" -> 18, //Cairns
"AKL" -> 18, //Auckland
"PSA" -> 18, //Pisa
"KGS" -> 18, //Kos Island
"AUA" -> 18, //Oranjestad
"NLU" -> 18, //Mexico City
"LXA" -> 18, //Lhasa
"BVC" -> 18, //Rabil
"DRW" -> 18, //Darwin
"MCT" -> 18, //Muscat
"MIR" -> 18, //Monastir
"MVD" -> 18, //Montevideo
"ORD" -> 18, //Chicago
"PVG" -> 18, //Shanghai
"SGN" -> 18,
"TIA" -> 18, //Triana
"OGG" -> 17, //Kahului
"CFU" -> 17, //Kerkyra Island
"ATL" -> 17,
"GPS" -> 17, //Baltra Galapagos
"JRO" -> 17, //Arusha
"NBE" -> 17, //Enfidha
"RTB" -> 17, //Roatan Island
"JMK" -> 16, //Mykonos Island
"CUR" -> 16, //Willemstad
"MUB" -> 16, //Maun
"BGI" -> 16, //Bridgetown
"BGY" -> 16, //Milan
"BOG" -> 16,
"BSL" -> 16, //Mulhouse French/Swiss Alps
"DOH" -> 16,
"HUI" -> 16, //Hue Phu Bai VN
"KLO" -> 16, //Boracay
"SJO" -> 16, //San Jose
"VCS" -> 16, //Con Dao VN
"WAW" -> 16, //Warsaw
"AER" -> 15, //Sochi
"CAG" -> 15, //Cagliari
"COK" -> 15, //Kochi
"IKA" -> 15, //Tehran
"VKO" -> 15, //Moscow
"FLR" -> 15, //Firenze
"SKD" -> 15, //Samarkand
"STT" -> 15, //Charlotte Amalie
"ACE" -> 15, //Lanzarote Island
"ZTH" -> 15, //Zakynthos Island
"KIN" -> 15, //Kingston
"TIV" -> 15, //Tivat
"EDI" -> 15, //Edinburgh
"HEL" -> 15, //Helsinki
"ADB" -> 15, //Izmir
"BOD" -> 15, //prehistoric caves France
"EBB" -> 15, //Kampala
"FLG" -> 15, //Flagstaff Grand Canyon
"OKA" -> 14, //Naha
"SJD" -> 14, //San José del Cabo
"PVR" -> 14, //Puerto Vallarta
"HAN" -> 14, //Hanoi
"PER" -> 14, //Perth
"KRK" -> 14, //Kraków
"LCA" -> 14, //Larnarca
"DLM" -> 14, //Dalaman
"DME" -> 14, //Moscow
"SID" -> 14, //Espargos
"YQB" -> 14, //Quebec
"MED" -> 13, //Medina
"LED" -> 13, //St. Petersburg
"KOS" -> 12, //Sihanukville
"CTA" -> 12, //Catania
"PMO" -> 12, //Palermo
"PTP" -> 12, //Pointe-Ã -Pitre
"THR" -> 12, //Tehran
"TFN" -> 12, //Tenerife Island
"NAS" -> 12, //Nassau
"SLL" -> 12, //Salalah
"HUX" -> 12, //Huatulco
"PLZ" -> 12, //Addo Elephant National Park South Africa
"HDS" -> 12, //Kruger National Park South Africa
"GAN" -> 12, //Maldives
"JNU" -> 12, //Juneau
"LBJ" -> 12, //Komodo National Park Indonesia
"MAO" -> 12, //Manaus
"PDP" -> 12, //Punta del Este
"PTY" -> 12,
"QSR" -> 12, //Amalfi coast
"RAI" -> 12, //Praia
"TBS" -> 12, //Tbilisi
"TNG" -> 12, //Tangiers
"TNM" -> 12, //AQ
"TPE" -> 12,
"TQO" -> 12, //Tulum
"VTE" -> 12, //Luang Prabang Laos
"XIY" -> 12, //Xi'an
"ZNZ" -> 12, //Zanzibar
"BTH" -> 11, //Batam Island
"DBV" -> 11, //Dubrovnik
"LVI" -> 11, //Livingstone
"VFA" -> 11, //Victoria Falls
"JAI" -> 11, //Jaipur
"LIR" -> 11, //Liberia Costa Rica
"AMM" -> 11, //Amman
"YZF" -> 11, //Yellowknife
"ANC" -> 11, //Anchorage
"TER" -> 11, //Azores Lajes
"VDO" -> 11, //Van Don VN
"LRM" -> 11, //La Romana DR
"OPO" -> 11,
"SAW" -> 10, //Istanbul
"FAO" -> 10, //Faro
"DEL" -> 10,
"IGU" -> 10, //Foz Do IguaÃ§u
"CJC" -> 10, //Calama
"CHQ" -> 10, //Heraklion
"CIA" -> 10, //Ostia Antica Italy
"GYD" -> 10, //Baku
"MAH" -> 10, //Menorca Island
"YUL" -> 10, //Montreal
"SPU" -> 10, //Split
"TGZ" -> 10, //Tuxtla Gutiérrez
"AUH" -> 10,
"CCC" -> 10, //Cayo Coco
"CGK" -> 10,
"DEN" -> 10,
"PFO" -> 10, //Paphos
"SEA" -> 10,
"SRQ" -> 10, //Sarasota/Bradenton
"VER" -> 10, //Pico de Orizaba National Park Mexico
"MSY" -> 9, //New Orleans
"REC" -> 9, //Recife
"IGR" -> 9, //Puerto Iguazu
"BRC" -> 9, //San Carlos de Bariloche
"NBO" -> 9, //Nairobi
"JNB" -> 9, //Johannesburg
"ECN" -> 9, //Nicosia
"GND" -> 9,
"GUA" -> 9, //Tikal Guatemala
"LIF" -> 9, //Lifou
"LJU" -> 9, //Triglav National Park Slovenia
"SPC" -> 9,
"TGD" -> 9,
"TUN" -> 9, //Tunis
"TOS" -> 8, //Tromsø
"CCJ" -> 8, //Calicut
"BWN" -> 8, //Bandar Seri Begawan
"SMR" -> 8, //Santa Marta
"SXM" -> 8, //Saint Martin
"UVF" -> 8, //Vieux Fort
"STX" -> 8, //Christiansted
"SZG" -> 8, //Salzburg Austrian Alps
"CGB" -> 8, //Cuiabá Ecotourism
"CYO" -> 8, //Cayo Largo del Sur Cuba
"FLW" -> 8, //Azores Flores
"PTF" -> 8, //Mamanuca Islands
"SMA" -> 8, //Azores
"UPN" -> 8, //Kgalagadi Transfrontier Park South Africa/Botswana
"USH" -> 8, //Ushuahia
"VDE" -> 8, //Canary Islands
"PLS" -> 7, //Providenciales Turks and Caicos
"SZG" -> 7, //Berchtesgaden National Park Germany
"GCN" -> 7, //Grand Canyon
"GDT" -> 7, //Cockburn Town
"EVN" -> 7,
"BEY" -> 7,
"BZE" -> 7, //Chiquibul National Park Belize
"SLC" -> 7, //Salt Lake City
"FDF" -> 6, //Fort-de-France
"FAT" -> 6, //Yosemite National Park USA
"GMZ" -> 6, //Canary Islands
"BTS" -> 6, //Devin Castle Slovakia
"FPO" -> 6, //Bahamas
"LXR" -> 6, //Luxor
"PNT" -> 6, //Torres del Paine National Park Chile
"SJZ" -> 6, //Azores São Jorge
"WVB" -> 6,
"XIY" -> 5, //Terracotta Army China
"ASP" -> 5, //Alice Springs
"AYQ" -> 5, //Ayers Rock
"FAI" -> 5, //Fairbanks
"ASW" -> 5, //Abu Simbel Egypt
"CYB" -> 5, //West End
"FPO" -> 5,
"HAL" -> 5,
"HBE" -> 5, //Alexandria
"HOR" -> 5, //Azores Horta
"MFU" -> 5,
"MRE" -> 5, //Maasai Mara National Reserve Kenya
"PUQ" -> 5, //Punta Arenas
"SEU" -> 5,
"SKB" -> 5,
"TAB" -> 5,
"ZSA" -> 5,
"ANU" -> 4, //St. John's
"BON" -> 4, //Kralendijk Bonaire
"UNA" -> 4, //Transamérica Resort Comandatuba Island
"MFA" -> 4, //Mafia Island TZ
"SZG" -> 4, //Salzburg
"ALG" -> 4, //Algiers
"BBK" -> 4,
"BOB" -> 4, //Bora Bora French Polynesia
"FCA" -> 4, //Glacier National Park
"MMY" -> 4, //Miyako JP
"JNX" -> 4, //GR
"MMY" -> 4, //Miyako JP
"SMI" -> 4, //GR
"TMR" -> 4, //Ahaggar National Park
"WDH" -> 4,
"YAS" -> 4, //Fiji
"YXY" -> 4, //Whitehorse
"EIS" -> 4, //BVI
"ZAG" -> 4,
"MCZ" -> 3,
"MHH" -> 3, //Marsh Harbour Bahammas
"GGT" -> 3, //Bahamas
"GHB" -> 3, //Governor's Harbour Bahamas
"ORN" -> 3, //Oran
"TNJ" -> 3, //Bintan Island, ID
"RAR" -> 3, //Cook Islands
"STM" -> 2, //Amazon
"AEY" -> 2, //Thingvellir National Park Iceland
"DCF" -> 2, //Dominica
"GOH" -> 2,
"LIO" -> 2,
"PJM" -> 2, //Costa rica
"PRI" -> 2, //Seychelles
"PTF" -> 2,
"LED" -> 2, //Andorra
"KVG" -> 2, //PG
"AXA" -> 2,
"BLJ" -> 1, //Timgad & Batna
"GBJ" -> 1, //Guadaloupe
"MNF" -> 1, //Fiji
"MQS" -> 1,
"PTF" -> 1, //Fiji
"SAB" -> 1,
"SPR" -> 1, //Belize
"YFB" -> 1, //Iqaluit
"SCT" -> 1, //Socotra Islands
"HLE" -> 1, //St Helena
    ),
    VACATION_HUB -> Map[String, Int](
"CJU" -> 190, //Jeju City
"CTS" -> 150, //Chitose / Tomakomai
"MCO" -> 99, //Orlando
"MEL" -> 94, //Melbourne
"SYD" -> 88, //Sydney Australia
"YIA" -> 86, //Yogyakarta
"JED" -> 80, //Jeddah
"HNL" -> 80, //Honolulu
"LAS" -> 80, //Las Vegas
"OKA" -> 80, //Naha
"TRD" -> 78, //Trondheim
"CUN" -> 75, //Cancún
"HAK" -> 75, //Haikou
"SYX" -> 75, //Sanya
"PMI" -> 70, //Palma De Mallorca
"CTG" -> 68, //Cartagena
"FLN" -> 67, //Florianópolis
"SXR" -> 67, //Srinagar
"OGG" -> 66, //Kahului
"VNS" -> 66, //Varanasi
"AGP" -> 65, //Málaga
"AER" -> 60, //Sochi
"SJD" -> 60, //San José del Cabo
"MFM" -> 60, //Macau
"PKX" -> 60, //Beijing China
"PMC" -> 60, //Puerto Montt
"PMV" -> 60, //Isla Margarita
"PVR" -> 59, //Puerto Vallarta
"OOL" -> 57, //Gold Coast
"CGH" -> 55, //São Paulo
"ITM" -> 54, //Osaka Japan
"SAW" -> 52, //Istanbul
"BAH" -> 51, //Manama
"MHD" -> 51, //Mashhad
"AYT" -> 50, //Antalya
"BKI" -> 50, //Kota Kinabalu
"LPA" -> 50, //Gran Canaria Island
"TFS" -> 50, //Tenerife Island
"FAO" -> 50, //Faro
"BGO" -> 50, //Bergen
"GRU" -> 49, //São Paulo
"BNE" -> 49, //Brisbane
"LGA" -> 49, //New York
"ORY" -> 49, //Paris
"KOS" -> 48, //Sihanukville
"BNA" -> 48, //Nashville
"MLA" -> 48, //Valletta
"MSY" -> 47, //New Orleans
"OLB" -> 47, //Olbia (SS)
"GOX" -> 46, //Goa IN
"PUJ" -> 45, //Punta Cana
"DMK" -> 45, //Bangkok
"KUL" -> 45, //Kuala Lumpur
"BAR" -> 45, //Qionghai
"HBA" -> 45, //Hobart
"KIH" -> 45, //Kish Island IR
"GIG" -> 44, //Rio De Janeiro
"GOI" -> 44, //Vasco da Gama
"AEP" -> 42, //Buenos Aires
"CAG" -> 42, //Cagliari
"CTA" -> 42, //Catania
"TOS" -> 42, //Tromsø
"KRR" -> 42, //Krasnodar
"GRO" -> 41, //Girona
"RHO" -> 40, //Rodes Island
"POA" -> 40, //Porto Alegre
"CNS" -> 40, //Cairns
"PMO" -> 40, //Palermo
"DEL" -> 40,
"KZN" -> 40, //Kazan
"OKD" -> 40, //Sapporo
"SVG" -> 40,
"BTH" -> 39, //Batam Island
"REC" -> 39, //Recife
"SHA" -> 39, //Shanghai China
"VIX" -> 39, //Vitória
"MAD" -> 38, //Madrid
"VAR" -> 38, //Varna
"MRS" -> 38, //Marseille
"ALC" -> 38, //Alicante
"RSW" -> 37, //Fort Myers
"LYS" -> 37, //Lyon
"CPT" -> 36, //Cape Town
"AKL" -> 36, //Auckland
"BPS" -> 36, //Porto Seguro
"TSV" -> 36, //Townsville
"BKK" -> 35, //Bangkok
"YVR" -> 35, //Vancouver
"HAN" -> 35, //Hanoi
"PER" -> 35, //Perth
"MED" -> 35, //Medina
"PTP" -> 35, //Pointe-Ã -Pitre
"DBV" -> 35, //Dubrovnik
"LVI" -> 35, //Livingstone
"CCJ" -> 35, //Calicut
"CNF" -> 35, //Belo Horizonte
"KWL" -> 35, //Guilin City
"GDN" -> 34, //GdaÅ„sk
"HGH" -> 34, //Hangzhou
"NQN" -> 34, //Neuquen
"TAO" -> 34, //Qingdao
"MBJ" -> 32, //Montego Bay
"IGU" -> 32, //Foz Do IguaÃ§u
"BOS" -> 32,
"FLL" -> 31, //Miami
"HRG" -> 30, //Hurghada
"SSH" -> 30, //Sharm el-Sheikh
"RUN" -> 30, //St Denis
"CEB" -> 30, //Lapu-Lapu City
"MPH" -> 30, //Malay
"SJU" -> 30, //San Juan
"PSA" -> 30, //Pisa
"CFU" -> 30, //Kerkyra Island
"COK" -> 30, //Kochi
"IKA" -> 30, //Tehran
"VKO" -> 30, //Moscow
"THR" -> 30, //Tehran
"CJC" -> 30, //Calama
"FDF" -> 30, //Fort-de-France
"ADZ" -> 30, //San Andrés
"CTU" -> 30, //Chengdu
"DCA" -> 30, //Washington
"VLC" -> 30, //Valencia
"RAK" -> 29, //Marrakech
"IGR" -> 29, //Puerto Iguazu
"FOR" -> 29, //Fortaleza
"RNO" -> 29, //Reno
"FLR" -> 28, //Firenze
"KOA" -> 28, //Kailua-Kona
"MAA" -> 28, //Chennai
"SDU" -> 28, //Rio De Janeiro
"REU" -> 27, //Reus
"HER" -> 26, //Heraklion
"SKD" -> 26, //Samarkand
"PXO" -> 26, //Peneda-Gerês National Park Portugal
"LIS" -> 25, //Lisbon
"IBZ" -> 25, //Ibiza
"STT" -> 25, //Charlotte Amalie
"LED" -> 25, //St. Petersburg
"TFN" -> 25, //Tenerife Island
"VFA" -> 25, //Victoria Falls
"BWI" -> 25, //Washington
"HIJ" -> 25, //Hiroshima
"KMQ" -> 25, //Kumamoto
"LIN" -> 25, //Milan Italian Alps
"KGS" -> 24, //Kos Island
"ACE" -> 24, //Lanzarote Island
"NAS" -> 24, //Nassau
"CHQ" -> 24, //Heraklion
"CIA" -> 24, //Ostia Antica Italy
"BWN" -> 24, //Bandar Seri Begawan
"AJA" -> 24, //Ajaccio/NapolÃ©on Bonaparte
"BIA" -> 24, //Bastia-Poretta
"HAM" -> 24, //Hamburg
"NVT" -> 24, //Navegantes
"STI" -> 24, //Santiago
"TPA" -> 24, //Tampa
"SDQ" -> 23, //Santo Domingo
"JAI" -> 22, //Jaipur
"BRC" -> 22, //San Carlos de Bariloche
"SMR" -> 22, //Santa Marta
"SXM" -> 22, //Saint Martin
"IKT" -> 22, //Irkutsk
"SIP" -> 22, //Simferopol
"TFU" -> 22, //Chengdu
"SSA" -> 21, //Salvador
"JMK" -> 21, //Mykonos Island
"BUF" -> 21, //Buffalo
"CTM" -> 21, //Chetumal
"HTI" -> 21, //Hamilton Island Resort
"IXB" -> 21, //Bagdogra Darjeeling
"MID" -> 21, //Mérida
"ENO" -> 21, //Encarnación
"DAD" -> 20, //Da Nang
"BCN" -> 20, //Barcelona
"NCE" -> 20, //Nice
"SCL" -> 20, //Santiago
"BER" -> 20, //Berlin
"SAI" -> 20, //Siem Reap
"FUE" -> 20, //Fuerteventura Island
"NAP" -> 20, //Nápoli
"JTR" -> 20, //Santorini Island
"SVO" -> 20, //Moscow
"AUA" -> 20, //Oranjestad
"CUR" -> 20, //Willemstad
"KRK" -> 20, //Kraków
"LIR" -> 20, //Liberia Costa Rica
"GYD" -> 20, //Baku
"UVF" -> 20, //Vieux Fort
"XIY" -> 20, //Terracotta Army China
"BRI" -> 20, //Bari
"CCK" -> 20,
"KNH" -> 20, //Kinmen
"LIH" -> 20, //Lihue
"NKG" -> 20, //Nanjing
"RUH" -> 20,
"XCH" -> 20,
"FNC" -> 19, //Funchal
"AMM" -> 19, //Amman
"PNQ" -> 19, //Pune
"AMD" -> 19, //Ahmedabad
"ITO" -> 19, //Hilo
"PBI" -> 19,
"ZTH" -> 18, //Zakynthos Island
"NBO" -> 18, //Nairobi
"ANU" -> 18, //St. John's
"DLC" -> 18, //Dalian
"INN" -> 18, //Innsbruck
"IOS" -> 18, //IlhÃ©us
"MDQ" -> 18,
"PHL" -> 18,
"RVN" -> 18, //Rovaniemi
"YYC" -> 18, //Calgary
"KIN" -> 17, //Kingston
"SLL" -> 17, //Salalah
"EYW" -> 17, //Key West
"FOC" -> 17, //Fuzhou
"FTE" -> 17, //El Calafate
"IXC" -> 17, //Chandigarh
"SHJ" -> 17, //Dubai
"HUX" -> 16, //Huatulco
"MAH" -> 16, //Menorca Island
"YUL" -> 16, //Montreal
"DYG" -> 16,
"GCM" -> 16, //Georgetown
"LLA" -> 16, //LuleÃ¥
"PPP" -> 16, //Whitsunday Coast Airport
"BJV" -> 15, //Bodrum
"PEN" -> 15, //Penang
"IAD" -> 15, //Washington
"NLU" -> 15, //Mexico City
"TIV" -> 15, //Tivat
"LCA" -> 15, //Larnarca
"SPU" -> 15, //Split
"TGZ" -> 15, //Tuxtla Gutiérrez
"JNB" -> 15, //Johannesburg
"PLS" -> 15, //Providenciales Turks and Caicos
"ASP" -> 15, //Alice Springs
"BLQ" -> 15, //Bologna
"ISG" -> 15, //Ishigaki JP
"SNA" -> 15, //Santa Ana
"XMN" -> 15, //Xiamen
"ZIA" -> 15, //Moscow
"BOJ" -> 14, //Burgas
"EDI" -> 14, //Edinburgh
"BON" -> 14, //Kralendijk Bonaire
"BME" -> 14, //Broome
"CWB" -> 14, //Curitiba
"OTP" -> 14, //Bucharest
"VBY" -> 14, //Visby, SE
"YHZ" -> 14, //Halifax
"EFL" -> 13, //Kefallinia Island
"BSB" -> 13, //Brasília
"YYT" -> 13, //St John
"LPQ" -> 12, //Luang Phabang
"MCZ" -> 12,
"CGB" -> 12, //Cuiabá
"AGX" -> 12, //Agatti
"ATQ" -> 12, //Amritsar
"BAQ" -> 12, //Barranquilla
"BDS" -> 12, //Brindisi
"FEN" -> 12, //Fernando De Noronha
"KTN" -> 12, //Ketchikan
"LKO" -> 12, //Lucknow
"NGO" -> 12, //Tokoname
"SBZ" -> 12, //Sibiu
"SHE" -> 12, //Shenyang
"TSN" -> 12, //Tianjin
"VCP" -> 12, //Campinas
"YLW" -> 12, //Jasper National Park Canada
"YOW" -> 12, //Ottawa
"CAT" -> 12, //Lisbon
"BZN" -> 11, //Bozeman
"FUK" -> 11, //Fukuoka
"GYN" -> 11, //Goiânia
"VOG" -> 11, //Volgograd
"AMS" -> 10, //Amsterdam
"LAP" -> 10, //La Paz
"MUB" -> 10, //Maun
"PLZ" -> 10, //Addo Elephant National Park South Africa
"STX" -> 10, //Christiansted
"AYQ" -> 10, //Ayers Rock
"UNA" -> 10, //Transamérica Resort Comandatuba Island
"MYR" -> 10, //Myrtle Beach
"BJL" -> 10, //Banjul
"CSX" -> 10, //Changsha
"FSC" -> 10, //Figari Sud-Corse
"FSZ" -> 10, //Fuji-Hakone-Izu National Park Japan
"GRQ" -> 10, //Grenoble French Alps
"HRB" -> 10, //Harbin
"ISG" -> 10, //Ishigaki
"KNO" -> 10, //North Sumatra
"BTV" -> 9, //Burlington Stowe/Sugarbush Vermont USA
"CHC" -> 9, //Christchurch
"IXZ" -> 9, //Port Blair
"KTA" -> 9, //Blue Mountains National Park Australia
"SAN" -> 9, //San Diego USA
"TRN" -> 9, //Turin Italian Alps
"YYJ" -> 9,
"ZAD" -> 9, //Zemunik (Zadar)
"SZG" -> 8, //Salzburg Austrian Alps
"SZG" -> 8, //Berchtesgaden National Park Germany
"FAT" -> 8, //Yosemite National Park USA
"MTJ" -> 8, //Montrose (Ski resort)
"CLY" -> 8, //Calvi-Sainte-Catherine
"JAC" -> 8, //Jackson
"JER" -> 8, //Guernsey
"KTT" -> 8, //Kittilä FI
"LMP" -> 8, //Italy
"RVN" -> 8, //Rovaniemi FI
"SLZ" -> 8, //São Luís
"THE" -> 8, //Teresina
"YXC" -> 8, //Banff National Park Canada
"GCI" -> 7, //Jersey
"MUH" -> 7, //El Alamein EG
"YDF" -> 7, //Gros Morne National Park Canada
"GCN" -> 6, //Grand Canyon
"ASE" -> 6, //Aspen
"ECP" -> 6, //Panama City Beach
"MZG" -> 6, //TW
"PNL" -> 6, //Italy
"YKS" -> 6, //Serbia
"TSN" -> 6, //Tainan TW
"STS" -> 6,
"IOM" -> 6, //Isle of Man
"HDS" -> 5, //Kruger National Park South Africa
"FAI" -> 5, //Fairbanks
"EGE" -> 5, //Vail/Beaver Creek Colorado USA
"VQS" -> 5, //Vieques PR
"SUN" -> 5, //Hailey Sun Valley Idaho USA
"ACV" -> 5, //Eureka
"AO1" -> 5, //Aogashima JP
"CLQ" -> 5, //Nevado de Colima National Park Mexico
"GPT" -> 5, //Gulf port
"IPC" -> 5, //Isla De Pascua
"LDH" -> 5,
"NLK" -> 5,
"SUV" -> 5,
"YTY" -> 5, //Yangzhou
"ZUH" -> 5, //Zhuhai
"PKU" -> 5, //Pekanbaru ID
"SGU" -> 4, //Zion National Park
"CHS" -> 4,
"CMF" -> 4, //Chambéry
"CNY" -> 4, //Arches National Park USA
"CUK" -> 4, //Belize
"DBB" -> 4, //EG
"HDN" -> 4, //Hayden Steamboat Springs Colorado USA
"HHH" -> 4, //Hilton Head Island
"KUM" -> 4,
"LSI" -> 4, //Shetland
"SLK" -> 4,
"DLU" -> 4, //Dali CN
"GMZ" -> 3, //Canary Islands
"HAC" -> 3,
"HGL" -> 3,
"HYA" -> 3, //Cape Cod
"MFR" -> 3, //Crater lake
"MFR" -> 3,
"OTH" -> 3, //North Bend
"TVC" -> 3, //Traverse City
"YYB" -> 3, //North Bay
"CPX" -> 2, //Culebra PR
"ACK" -> 2, //Nantucket
"BRW" -> 2,
"BHB" -> 2, //Acadia NP
"GRB" -> 2, //Door County WI
"YQA" -> 2, //Muskoka CA
 ),
    FINANCIAL_HUB -> Map[String, Int](
"SIN" -> 85, //Singapore
"JFK" -> 80, //New York
"HND" -> 75, //Tokyo
"FRA" -> 70, //Frankfurt
"LHR" -> 70, //London
"MUC" -> 65, //Munich
"CDG" -> 60, //Paris
"HKG" -> 60, //Hong Kong
"NRT" -> 60, //
"YYZ" -> 60, //Toronto
"ICN" -> 58, //Seoul
"PEK" -> 58, //Beijing
"DXB" -> 55, //Dubai
"EWR" -> 55, //New York
"JNB" -> 50, //Johannesburg
"ORD" -> 50, //Chicago
"SZX" -> 46, //Shenzhen
"AMS" -> 45, //Amsterdam
"BRU" -> 45, //Brussels
"PVG" -> 45, //Shanghai
"STR" -> 45, //Stuttgart
"TPE" -> 45, //Taipei
"DUB" -> 44, //Dublin
"GVA" -> 44, //Geneva
"KUL" -> 44, //Kuala Lumpur
"LAX" -> 44, //Los Angeles
"CAN" -> 43, //Guangzhou
"AUH" -> 42, //Abu Dhabi
"DFW" -> 42, //Dallas Fort Worth
"ZRH" -> 42, //Zurich
"MAD" -> 41, //Madrid
"BER" -> 40, //Berlin
"GRU" -> 40, //Sao Paulo
"LGW" -> 40, //London
"SYD" -> 39, //Sydney
"SFO" -> 38, //San Francisco
"SCL" -> 37, //Santiago
"DEN" -> 36, //Denver
"BOM" -> 35, //Mumbai
"LGA" -> 35, //New York
"YVR" -> 35, //Vancouver
"ARN" -> 34, //Stockholm
"CPH" -> 34, //Copenhagen
"DOH" -> 34, //Doha
"HAM" -> 34, //Hamburg
"MEL" -> 34, //Melbourne
"OSL" -> 34, //Oslo
"KIX" -> 33, //Osaka
"KWI" -> 33, //Kuwait City
"TLV" -> 33, //Tel Aviv
"BOS" -> 32, //Boston
"ITM" -> 32, //Osaka
"VIE" -> 32, //Vienna
"YUL" -> 32, //Montreal
"DME" -> 31, //Moscow
"PKX" -> 31, //Beijing
"ATL" -> 30, //Atlanta
"LUX" -> 30, //Luxembourg
"AKL" -> 29, //Auckland
"EDI" -> 29, //Edinburgh
"SVO" -> 29, //Moscow
"DEL" -> 28, //New Delhi
"IST" -> 28, //Istanbul
"MXP" -> 28, //Milan
"PUS" -> 28, //Busan
"CLT" -> 27, //Charlotte
"EZE" -> 27, //Buenos Aires
"SEA" -> 27, //Seattle
"SHA" -> 27, //Shanghai
"BOG" -> 26, //Bogota
"IAH" -> 26, //Houston
"BAH" -> 25, //Bahrain
"CPT" -> 25, //Cape Town
"ORY" -> 25, //Paris
"YYC" -> 25, //Calgary
"GMP" -> 24, //Seoul
"MAN" -> 24, //Manchester
"MEX" -> 24, //Mexico City
"MIA" -> 23, //Miami
"FCO" -> 22, //Rome
"GIG" -> 22, //Rio de Janeiro
"LIM" -> 21, //Lima
"LOS" -> 21, //Lagos
"NGO" -> 21, //Nagoya
"RUH" -> 21, //Riyadh
"BKK" -> 20, //Bangkok
"BUD" -> 20, //Budapest
"FUK" -> 20, //Fukuoka
"HAJ" -> 20, //Hanover
"IAD" -> 20, //Washington DC
"LCY" -> 20, //London
"LIN" -> 20, //Milan
"DCA" -> 19, //Washington DC
"BLR" -> 19, //Bangalore
"JED" -> 19, //Jeddah
"PRG" -> 19, //Prague
"WAW" -> 19, //Warsaw
"TAS" -> 19, //Tashkent
"BCN" -> 18, //Barcelona
"BLQ" -> 18, //Bologna
"BSB" -> 18, //Brasilia
"CGK" -> 18, //Jakarta
"CGN" -> 18, //Cologne
"DUS" -> 18, //Dusseldorf
"MSP" -> 18, //Minneapolis
"PHL" -> 18, //Philadelphia
"PHX" -> 18, //Phoenix
"SGN" -> 18, //Ho Chi Minh City
"DTW" -> 17, //Detroit
"MNL" -> 17, //Manila
"ALG" -> 16, //Algiers
"HYD" -> 16, //Hyderabad
"TLL" -> 16, //Tallinn
"SLC" -> 16, //Salt Lake City
"CGH" -> 15, //Sao Paulo
"HEL" -> 15, //Helsinki
"MDW" -> 15, //Chicago
"RMO" -> 15, //Chisinau
"RTM" -> 15, //The Hague
"AMD" -> 14, //GIFT City-Gujarat
"BNE" -> 14, //Brisbane
"IKA" -> 14, //Tehran
"KHH" -> 14, //Kaohsiung
"PER" -> 14, //Perth
"SJC" -> 14, //San Francisco
"TRN" -> 14, //Turin
"ALA" -> 13, //Almaty
"CMN" -> 13, //Casablanca
"DAL" -> 13, //Dallas
"TLS" -> 13, //Toulouse
"TSN" -> 13, //Tianjin
"CBR" -> 12, //Canberra
"LEJ" -> 12, //Leipzig
"TAO" -> 12, //Qingdao
"VNO" -> 12, //Vilnius
"YQB" -> 12, //Quebec City
"BWI" -> 11, //Baltimore
"DAC" -> 11, //Dhaka
"FLL" -> 11, //
"HAN" -> 11, //Hanoi
"KUN" -> 11, //Kaunas
"LED" -> 11, //St Petersburg
"AEP" -> 10, //Buenos Aires
"BNA" -> 10, //Nashville
"DMK" -> 10, //Bangkok
"MAA" -> 10, //Chennai
"RIX" -> 10, //Riga
"SAN" -> 10, //San Diego
"SDU" -> 10, //Rio de Janeiro
"TSA" -> 10, //Taipei
"YEG" -> 10, //Edmonton
"ATH" -> 9, //Athens
"AUS" -> 9, //Austin
"BEG" -> 9, //Belgrade
"BGO" -> 9, //Bergen
"MDE" -> 9, //Medellin
"MLA" -> 9, //Malta
"NBO" -> 9, //Nairobi
"PTY" -> 9, //Panama City
"TFU" -> 9, //Chengdu
"TPA" -> 9, //Tampa
"YOW" -> 9, ////Ottawa
"ADL" -> 8, //Adelaide
"BDA" -> 8, //Bermuda
"BTS" -> 8, //Bratislava
"CCU" -> 8, //Kolkata
"CKG" -> 8, //Jakarta
"CTU" -> 8, //Chengdu
"DUR" -> 8, //Durban
"GOT" -> 8, //Gothenburg
"KHI" -> 8, //Karachi
"LYS" -> 8, //Grenoble
"NCL" -> 8, //Newcastle
"NKG" -> 8, //Nanjing
"NLU" -> 8, //Mexico City
"OTP" -> 8, //Bucharest
"POS" -> 8, //Port of Spain
"ANC" -> 7, //Anchorage
"DLC" -> 7, //Dalian
"GYD" -> 7, //Baku
"HGH" -> 7, //Hangzhou
"NQZ" -> 7, //Nur-Sultan
"PDX" -> 7, //Portland
"PNQ" -> 7, //Pune
"STL" -> 7, //
"AAL" -> 6, //Aalborg
"NAS" -> 6, //Nassau
"OAK" -> 6, //San Francisco
"PIT" -> 6, //Pittsburgh
"SMF" -> 6, //Sacramento
"SOF" -> 6, //Sofia
"ADD" -> 5, //Addis Ababa
"AHB" -> 5, //
"BHX" -> 5, //Birmingham
"CLO" -> 5, //Cali
"DTM" -> 5, //Dortmund
"GLA" -> 5, //Glasgow
"IOM" -> 5, //Castletown
"KGL" -> 5, //Kigali
"MTY" -> 5, //Monterrey
"TRD" -> 5, //Trondheim
"WLG" -> 5, //Wellington
"XIY" -> 5, //Xi'an
"YTZ" -> 5, //Toronto
"CZX" -> 5, //Changzhou
"BGI" -> 4, //Bridgetown
"DMM" -> 4, //
"ESB" -> 4, //Ankara
"HOU" -> 4, //Houston
"KEF" -> 4, //Reykjavik
"SNA" -> 4, //
"YXE" -> 4, //Saskatoon
"HAV" -> 4, //Havanna
"ABV" -> 3, //
"JNU" -> 3, //Juneau
"MBA" -> 3, //Mombasa
"MCI" -> 3, //
"YWG" -> 3, //Winnipeg
"YQR" -> 3, //Regina
"LAD" -> 3, ////Luanda
"SJJ" -> 3, //Sarajevo
"LCA" -> 3, //Nicosia
"WUH" -> 3, //Wuhan
"RDU" -> 3, //
"ABQ" -> 2, //
"BOI" -> 2, //
"CLE" -> 2, //
"EBL" -> 2, //Arbil
"JAX" -> 2, //
"MKE" -> 2, //
"YWG" -> 2, //
"PZU" -> 2, ////Port Sudan
"JIB" -> 2, ////Djibouti
"CAY" -> 2, //
"CB7" -> 1, //Vancouver Heliport
"JRA" -> 1, //NYC Heliport
"JRA" -> 1, //NYC Heliport
"JRB" -> 1, //NYC Heliport
"GR0" -> 1, //Sao Paulo
"HHP" -> 1, //Hong Kong
"SW1" -> 1, //Rio Heliport
    ), 
    DOMESTIC_AIRPORT -> Map(
      "LGA" -> 0,
      "DCA" -> 0,
      "MDW" -> 0,
      "SNA" -> 0,
      "BUR" -> 0,
      "OAK" -> 0,
      "DAL" -> 0,
      "HOU" -> 0,
      "AZA" -> 0,
      "COS" -> 0,
      "PAE" -> 0,
      "PIE" -> 0,
      "SFB" -> 0,
      "USA" -> 0,
      "PGD" -> 0,
      "LIH" -> 0,
      "OGG" -> 0,
      "ORH" -> 0,
      "SIG" -> 0,
      //canada
      "YTZ" -> 0,
      "YHU" -> 0,
      //mexico
      "TLC" -> 0,
      "CJS" -> 0,
      //EU
      "EIN" -> 0,
      "CRL" -> 0,
      "ANR" -> 0,
      "BVA" -> 0,
      "HHN" -> 0,
      "BRE" -> 0,
      "DTM" -> 0,
      "FMM" -> 0,
      "FAO" -> 0,
      "REU" -> 0,
      "GRO" -> 0,
      "LIN" -> 0,
      "CIA" -> 0,
      "TSF" -> 0,
      "NYO" -> 0,
      "BMA" -> 0,
      "TRF" -> 0,
      "WMI" -> 0,
      "CAT" -> 0,
      //GB
      "BHD" -> 0,
      //iceland
      "RKV" -> 0,
      //china
      "CTU" -> 0,
      "PKX" -> 0,
      "SHA" -> 0,
      "ZUH" -> 0,
      "LXA" -> 0,
      //japan
      "ITM" -> 0,
      "UKB" -> 0,
      "IBR" -> 0,
      "OKD" -> 0,
      //argentina
      "AEP" -> 0,
      //brazil
      "CGH" -> 0,
      "SDU" -> 0,
      //colombia
      "EOH" -> 0,
      "FLA" -> 0,
      //chile
      "LSC" -> 0,
      //dominican-republic
      "JBQ" -> 0,
      //iran
      "THR" -> 0,
      "PGU" -> 0,
      "ABD" -> 0,
      "KIH" -> 0,
      "AWZ" -> 0,
      //india
      "HDO" -> 0,
      "DHM" -> 0,
      "BDQ" -> 0,
      "PNY" -> 0,
      "AIP" -> 0,
      "STV" -> 0,
      "KNU" -> 0,
      //russia
      "CEK" -> 0,
      "KEJ" -> 0,
      "BTK" -> 0,
      "YKS" -> 0,
      "UUS" -> 0,
      //southern africa
      "HLA" -> 0,
      "ERS" -> 0,
      //indonesia
      "HLP" -> 0,
      //Australia
      "AVV" -> 0,
      "MCY" -> 0,
      "LST" -> 0
    )
  ) + (GATEWAY_AIRPORT -> getGatewayAirports().map(iata => (iata, 0)).toMap) + (ELITE_CHARM -> getEliteDestinations())

  patchFeatures()

  def patchFeatures() = {
    val airportFeatures = scala.collection.mutable.Map[String, ListBuffer[AirportFeature]]()
    featureList.foreach {
      case (featureType, airportMap) =>
        airportMap.foreach {
          case (airportIata, featureStrength) =>
            val featuresForThisAirport = airportFeatures.getOrElseUpdate(airportIata, ListBuffer[AirportFeature]())
            featuresForThisAirport += AirportFeature(featureType, featureStrength)
        }
    }


    airportFeatures.toList.foreach {
        case (iata, features) =>
          AirportSource.loadAirportByIata(iata) match {
            case Some(airport) =>
              AirportSource.updateAirportFeatures(airport.id, features.toList)
            case None =>
              println(s"Cannot find airport with iata $iata to patch $features")
          }
      }
      IsolatedAirportPatcher.patchIsolatedAirports()
  }

    def getEliteDestinations() : Map[String, Int] = {
      val destinations = DestinationSource.loadAllDestinations()
      val iataMap = destinations.groupBy(_.airport.iata).view.mapValues(_.length).toMap
      println("inserting elite destinations to features...")
      println(iataMap)
      iataMap
    }


  def getGatewayAirports() : List[String] = {
    //The most powerful airport of every country
    val airportsByCountry = AirportSource.loadAllAirports().groupBy(_.countryCode).filter(_._2.length > 0)
    val topAirportByCountry = airportsByCountry.view.mapValues(_.sortBy(_.power).last)

    val baseList = topAirportByCountry.values.map(_.iata).toList

    val list: mutable.ListBuffer[String] = collection.mutable.ListBuffer(baseList:_*)

    list -= "HND"
    list -= "CGO" //China
    list -= "OSS" //Uzbekistan
    list += "FRU"
    list -= "LHE" //Pakistan
    list += "ISB"
    list -= "GYE" //Ecuador
    list += "UIO"
    list -= "THR" //Iran
    list += "IKA"
    list -= "RUH" //Saudi
    list += "JED"
    list -= "OND" //Nambia
    list += "WDH"
    list -= "ZND" //Mali
    list += "NIM"
    list -= "BYK" //Ivory Coast
    list += "ABJ"
    list -= "DLA" //Cameroon
    list += "NSI"
    list -= "MQQ" //Chad
    list += "NDJ"
    list -= "BLZ" //Malawi
    list += "LLW"
    list -= "KGA" //DRC
    list -= "MJM"
    list += "FIH"
    list -= "KAN" //Nigeria
    list += "LOS"
    list -= "APL" //Mozambique
    list += "MPM"
    list -= "MWZ" //Tanzania
    list += "DAR"
    list -= "HGU" //Tanzania
    list += "POM"
    list -= "STX" //US VI
    list += "STT"
    list -= "XSC" //
    list += "PLS"
    list += "AQ0" //AQ
    list += "PPT"
    list += "NOU"


    //now add extra ones for bigger countries
    //from top to bottom by pop coverage, so we wouldnt miss any
    list.appendAll(List(
      "SP1", //South Pole station
      "CAN", //China
      "PVG",
      "PEK",
      "JFK", //US
      "LAX",
      "SFO",
      "MIA",
      "BOM", //India
      "RUH", //Saudi
      "AUH", //UAE
      "AYT", //Turkey
      "CPT", //South Africa
      "GIG", //Brazil
      "GRU",
      "NRT", //Japan
      "KIX",
      "SVO", //Russia
      "LED",
      "FCO", //Italy
      "MXP",
      "MAD", //Spain
      "BCN",
      "FRA", //Germany
      "MUC",
      "SYD", //Australia
      "MEL",
      "YVR", //Canada
      "YUL",
      "YYZ"))
    list.toList
  }
}
