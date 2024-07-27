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
"IST" -> 80, //Istanbul
"CDG" -> 80, //Paris
"AYT" -> 75, //Antalya
"CNX" -> 70, //Chiang Mai
"PQC" -> 70, //Phu Quoc Island
"JED" -> 70, //Jeddah
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
"USM" -> 60, //Na Thon (Ko Samui Island)
"LHR" -> 60, //London
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
"RMF" -> 44, //Marsa Alam
"CZM" -> 44, //Cozumel
"KUL" -> 43, //Kuala Lumpur
"SIN" -> 42, //Singapore
"VCE" -> 42, //Venice
"MIA" -> 42, //Miami
"AMS" -> 41, //Amsterdam
"LAX" -> 41, //Los Angeles
"RUN" -> 40, //St Denis
"CAI" -> 40, //Cairo Egypt
"GRU" -> 39, //São Paulo
"GIG" -> 39, //Rio De Janeiro
"VIE" -> 39, //Vienna
"ATH" -> 39, //Athens
"CEB" -> 38, //Lapu-Lapu City
"GZP" -> 38, //Gazipaşa
"EWR" -> 38, //New York City USA
"LGW" -> 38, //London United Kingdom
"HND" -> 38, //Tokyo / Haneda
"AGP" -> 35, //Málaga
"CPT" -> 35, //Cape Town
"SPX" -> 35, //Cairo
"NCE" -> 34, //Nice
"BJV" -> 34, //Bodrum
"YYZ" -> 34, //Toronto Canada
"ICN" -> 34, //Seoul
"BKI" -> 33, //Kota Kinabalu
"SJU" -> 33, //San Juan
"LIS" -> 32, //Lisbon
"BER" -> 32, //Berlin
"NAN" -> 32, //Nadi
"SAI" -> 31, //Siem Reap
"AGA" -> 31, //Agadir
"SYD" -> 30, //Sydney Australia
"HNL" -> 30, //Honolulu
"AEP" -> 30, //Buenos Aires
"LOP" -> 30, //Mataram
"ARN" -> 30, //Stockholm
"DUB" -> 30, //Dublin Ireland
"POP" -> 29, //Puerto Plata Dominican Republic
"PEK" -> 31, //Beijing
"MXP" -> 29, //Milan
"MUC" -> 29, //Munich
"MEX" -> 29, //Mexico City
"MCO" -> 28, //Orlando
"GOX" -> 28, //Goa IN
"MPH" -> 28, //Malay
"FUE" -> 28, //Fuerteventura Island
"PEN" -> 28, //Penang
"DJE" -> 28, //Djerba
"PRG" -> 28, //Prague
"PPT" -> 28, //Papeete
"AUA" -> 28, //Oranjestad
"BOM" -> 27, //Mumbai
"LPB" -> 27, //La Paz / El Alto
"VRA" -> 27, //Varadero
"TLV" -> 26, //Tel Aviv
"BUD" -> 26, //Budapest
"KEF" -> 26, //Reykjavík
"BAH" -> 25, //Manama
"GOI" -> 25, //Vasco da Gama
"YVR" -> 25, //Vancouver
"ZQN" -> 25, //Queenstown
"CMN" -> 25, //Casablanca
"LAS" -> 25, //Las Vegas
"RHO" -> 24, //Rodes Island
"MAD" -> 24, //Madrid
"LAP" -> 24, //La Paz
"PPS" -> 24, //Puerto Princesa City
"NOU" -> 24, //Nouméa
"PTP" -> 24, //Pointe-Ã -Pitre
"GUM" -> 23, //Hagåtña Guam International Airport
"KTM" -> 23, //Kathmandu
"CPH" -> 23, //Copenhagen
"POA" -> 22, //Porto Alegre
"SXM" -> 22, //Saint Martin
"MRU" -> 22, //Port Louis
"EZE" -> 22, //Buenos Aires
"BNE" -> 21, //Brisbane
"NAP" -> 21, //Nápoli
"IAD" -> 21, //Washington
"TRV" -> 21, //Thiruvananthapuram
"LXA" -> 21, //Lhasa
"SFO" -> 21, //San Francisco
"MQP" -> 21, //Mpumalanga
"SEZ" -> 21, //Mahe Island
"LIM" -> 21,
"PDL" -> 21, //Azores
"PMI" -> 20, //Palma De Mallorca
"LPA" -> 20, //Gran Canaria Island
"TFS" -> 20, //Tenerife Island
"VAR" -> 20, //Varna
"FLR" -> 20, //Firenze
"HER" -> 20, //Heraklion
"JMK" -> 20, //Mykonos Island
"SSA" -> 20, //Salvador
"FNC" -> 20, //Funchal
"LPQ" -> 20, //Luang Phabang
"OSL" -> 20, //Oslo
"CUR" -> 20, //Willemstad
"GAN" -> 20, //Maldives
"MEL" -> 19, //Melbourne
"IBZ" -> 19, //Ibiza
"JTR" -> 19, //Santorini Island
"SVO" -> 19, //Moscow
"BOJ" -> 19, //Burgas
"SJO" -> 19, //San Jose
"ZRH" -> 19, //Zurich
"EBB" -> 19, //Kampala
"MNL" -> 19,
"CTG" -> 18, //Cartagena
"CNS" -> 18, //Cairns
"AKL" -> 18, //Auckland
"PSA" -> 18, //Pisa
"KGS" -> 18, //Kos Island
"NLU" -> 18, //Mexico City
"DRW" -> 18, //Darwin
"MVD" -> 18, //Montevideo
"BGI" -> 18, //Bridgetown
"BVC" -> 18, //Rabil
"HEL" -> 18, //Helsinki
"MIR" -> 18, //Monastir
"PVG" -> 18, //Shanghai
"ORD" -> 18, //Chicago
"SGN" -> 18,
"MCT" -> 18, //Muscat
"TIA" -> 18, //Triana
"OGG" -> 17, //Kahului
"CFU" -> 17, //Kerkyra Island
"LCA" -> 17, //Larnarca
"JRO" -> 17, //Arusha
"NBE" -> 17, //Enfidha
"RTB" -> 17, //Roatan Island
"ATL" -> 17,
"GPS" -> 17, //Baltra Galapagos
"PMO" -> 16, //Palermo
"MUB" -> 16, //Maun
"BGY" -> 16, //Milan
"FDF" -> 16, //Fort-de-France
"WAW" -> 16, //Warsaw
"BSL" -> 16, //Mulhouse French/Swiss Alps
"BOG" -> 16,
"DOH" -> 16,
"VCS" -> 16, //Con Dao VN
"HUI" -> 16, //Hue Phu Bai VN
"KLO" -> 16, //Boracay
"AER" -> 15, //Sochi
"CAG" -> 15, //Cagliari
"COK" -> 15, //Kochi
"IKA" -> 15, //Tehran
"VKO" -> 15, //Moscow
"SKD" -> 15, //Samarkand
"STT" -> 15, //Charlotte Amalie
"ACE" -> 15, //Lanzarote Island
"ZTH" -> 15, //Zakynthos Island
"KIN" -> 15, //Kingston
"TIV" -> 15, //Tivat
"EDI" -> 15, //Edinburgh
"ADB" -> 15, //Izmir
"BOD" -> 15, //prehistoric caves France
"FLG" -> 15, //Flagstaff Grand Canyon
"OKA" -> 14, //Naha
"SJD" -> 14, //San José del Cabo
"PVR" -> 14, //Puerto Vallarta
"PER" -> 14, //Perth
"HAN" -> 14, //Hanoi
"NAS" -> 14, //Nassau
"KRK" -> 14, //Kraków
"SID" -> 14, //Espargos
"YZF" -> 14, //Yellowknife
"DME" -> 14, //Moscow
"DLM" -> 14, //Dalaman
"MED" -> 13, //Medina
"LED" -> 13, //St. Petersburg
"ZNZ" -> 12, //Zanzibar
"KOS" -> 12, //Sihanukville
"CTA" -> 12, //Catania
"THR" -> 12, //Tehran
"TFN" -> 12, //Tenerife Island
"HUX" -> 12, //Huatulco
"PLZ" -> 12, //Addo Elephant National Park South Africa
"HDS" -> 12, //Kruger National Park South Africa
"PDP" -> 12, //Punta del Este
"MAO" -> 12, //Manaus
"TNM" -> 12, //AQ
"XIY" -> 12, //Xi'an
"TBS" -> 12, //Tbilisi
"RAI" -> 12, //Praia
"LBJ" -> 12, //Komodo National Park Indonesia
"VTE" -> 12, //Luang Prabang Laos
"PTY" -> 12,
"JNU" -> 12, //Juneau
"TQO" -> 12, //Tulum
"QSR" -> 12, //Amalfi coast
"TPE" -> 12,
"SLL" -> 12, //Salalah
"TNG" -> 12, //Tangiers
"AMM" -> 11, //Amman
"BTH" -> 11, //Batam Island
"DBV" -> 11, //Dubrovnik
"LVI" -> 11, //Livingstone
"VFA" -> 11, //Victoria Falls
"JAI" -> 11, //Jaipur
"LIR" -> 11, //Liberia Costa Rica
"ANC" -> 11, //Anchorage
"TER" -> 11, //Azores Lajes
"VDO" -> 11, //Van Don VN
"SAW" -> 10, //Istanbul
"FAO" -> 10, //Faro
"DEL" -> 10,
"IGU" -> 10, //Foz Do IguaÃ§u
"CJC" -> 10, //Calama
"CHQ" -> 10, //Heraklion
"CIA" -> 10, //Ostia Antica Italy
"GYD" -> 10, //Baku
"YUL" -> 10, //Montreal
"MAH" -> 10, //Menorca Island
"TGZ" -> 10, //Tuxtla Gutiérrez
"SPU" -> 10, //Split
"PLS" -> 10, //Providenciales Turks and Caicos
"VER" -> 10, //Pico de Orizaba National Park Mexico
"BON" -> 10, //Kralendijk Bonaire
"CCC" -> 10, //Cayo Coco
"PFO" -> 10, //Paphos
"SRQ" -> 10, //Sarasota/Bradenton
"DEN" -> 10,
"AUH" -> 10,
"CGK" -> 10,
"SEA" -> 10,
"REC" -> 9, //Recife
"IGR" -> 9, //Puerto Iguazu
"BRC" -> 9, //San Carlos de Bariloche
"NBO" -> 9, //Nairobi
"JNB" -> 9, //Johannesburg
"GND" -> 9,
"ECN" -> 9, //Nicosia
"LIF" -> 9, //Lifou
"GUA" -> 9, //Tikal Guatemala
"LJU" -> 9, //Triglav National Park Slovenia
"TGD" -> 9,
"SPC" -> 9,
"MSY" -> 9, //New Orleans
"TOS" -> 8, //Tromsø
"CCJ" -> 8, //Calicut
"BWN" -> 8, //Bandar Seri Begawan
"SMR" -> 8, //Santa Marta
"UVF" -> 8, //Vieux Fort
"GCN" -> 8, //Grand Canyon
"STX" -> 8, //Christiansted
"SZG" -> 8, //Salzburg Austrian Alps
"USH" -> 8, //Ushuahia
"UPN" -> 8, //Kgalagadi Transfrontier Park South Africa/Botswana
"GDT" -> 8, //Cockburn Town
"CYO" -> 8, //Cayo Largo del Sur Cuba
"SMA" -> 8, //Azores
"FLW" -> 8, //Azores Flores
"CGB" -> 8, //Cuiabá Ecotourism
"VDE" -> 8, //Canary Islands
"PTF" -> 8, //Mamanuca Islands
"SLC" -> 7, //Salt Lake City
"SZG" -> 7, //Berchtesgaden National Park Germany
"BEY" -> 7,
"BZE" -> 7, //Chiquibul National Park Belize
"FAT" -> 6, //Yosemite National Park USA
"BTS" -> 6, //Devin Castle Slovakia
"GMZ" -> 6, //Canary Islands
"LXR" -> 6, //Luxor
"PNT" -> 6, //Torres del Paine National Park Chile
"SJZ" -> 6, //Azores São Jorge
"FPO" -> 6, //Bahamas
"WVB" -> 6,
"TUN" -> 6, //Tunis
"XIY" -> 5, //Terracotta Army China
"ASP" -> 5, //Alice Springs
"AYQ" -> 5, //Ayers Rock
"FAI" -> 5, //Fairbanks
"MFA" -> 5, //Mafia Island TZ
"SZG" -> 5, //Salzburg
"PUQ" -> 5, //Punta Arenas
"SCR" -> 5, //Salzburg
"ASW" -> 5, //Abu Simbel Egypt
"MRE" -> 5, //Maasai Mara National Reserve Kenya
"SEU" -> 5,
"MFU" -> 5,
"ZSA" -> 5,
"CYB" -> 5, //West End
"HOR" -> 5, //Azores Horta
"HBE" -> 5, //Alexandria
"FPO" -> 5,
"HAL" -> 5,
"TAB" -> 5,
"UNA" -> 4, //Transamérica Resort Comandatuba Island
"BOB" -> 4, //Bora Bora French Polynesia
"YXY" -> 4, //Whitehorse
"FCA" -> 4, //Glacier National Park
"MHH" -> 4, //Marsh Harbour Bahammas
"WDH" -> 4,
"BBK" -> 4,
"YAS" -> 4, //Fiji
"MMY" -> 4, //Miyako JP
"JNX" -> 4, //GR
"SMI" -> 4, //GR
"TMR" -> 4, //Ahaggar National Park
"ALG" -> 4, //Algiers
"TMR" -> 4, //Ahaggar National Park
"MCZ" -> 3,
"GHB" -> 3, //Governor's Harbour Bahamas
"GGT" -> 3, //Bahamas
"TNJ" -> 3, //Bintan Island, ID
"ORN" -> 3, //Oran
"AEY" -> 2, //Thingvellir National Park Iceland
"PTF" -> 2,
"LIO" -> 2,
"PRI" -> 2, //Seychelles
"PJM" -> 2, //Costa rica
"GOH" -> 2,
"MNF" -> 1, //Fiji
"MQS" -> 1,
"GBJ" -> 1, //Guadaloupe
"PTF" -> 1, //Fiji
"SAB" -> 1,
"SPR" -> 1, //Belize
"BLJ" -> 1, //Timgad & Batna
    ),
    VACATION_HUB -> Map[String, Int](
"CJU" -> 190, //Jeju City
"CTS" -> 175, //Chitose / Tomakomai
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
"SYX" -> 75, //Sanya
"HAK" -> 75, //Haikou
"PMI" -> 70, //Palma De Mallorca
"KRR" -> 70, //Krasnodar
"CTG" -> 68, //Cartagena
"FLN" -> 67, //Florianópolis
"SXR" -> 67, //Srinagar
"OGG" -> 66, //Kahului
"VNS" -> 66, //Varanasi
"AGP" -> 65, //Málaga
"RHO" -> 60, //Rodes Island
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
"ORY" -> 49, //Paris
"LGA" -> 49, //New York
"KOS" -> 48, //Sihanukville
"BNA" -> 48, //Nashville
"MLA" -> 48, //Valletta
"MSY" -> 47, //New Orleans
"OLB" -> 47, //Olbia (SS)
"GOX" -> 46, //Goa IN
"PUJ" -> 45, //Punta Cana
"DMK" -> 45, //Bangkok
"KUL" -> 45, //Kuala Lumpur
"HBA" -> 45, //Hobart
"BAR" -> 45, //Qionghai
"KIH" -> 45, //Kish Island IR
"GOI" -> 44, //Vasco da Gama
"AEP" -> 42, //Buenos Aires
"CAG" -> 42, //Cagliari
"CTA" -> 42, //Catania
"TOS" -> 42, //Tromsø
"GRO" -> 41, //Girona
"POA" -> 40, //Porto Alegre
"CNS" -> 40, //Cairns
"PMO" -> 40, //Palermo
"DEL" -> 40,
"KZN" -> 40, //Kazan
"OKD" -> 40, //Sapporo
"BTH" -> 39, //Batam Island
"REC" -> 39, //Recife
"VIX" -> 39, //Vitória
"SHA" -> 39, //Shanghai China
"MAD" -> 38, //Madrid
"VAR" -> 38, //Varna
"MRS" -> 38, //Marseille
"RSW" -> 37, //Fort Myers
"CPT" -> 36, //Cape Town
"FLR" -> 36, //Firenze
"AKL" -> 36, //Auckland
"BPS" -> 36, //Porto Seguro
"TSV" -> 36, //Townsville
"BKK" -> 35, //Bangkok
"YVR" -> 35, //Vancouver
"PER" -> 35, //Perth
"HAN" -> 35, //Hanoi
"MED" -> 35, //Medina
"DBV" -> 35, //Dubrovnik
"LVI" -> 35, //Livingstone
"CCJ" -> 35, //Calicut
"ALC" -> 35, //Alicante
"KWL" -> 35, //Guilin City
"LYS" -> 35, //Lyon
"CNF" -> 35, //Belo Horizonte
"HGH" -> 34, //Hangzhou
"GDN" -> 34, //GdaÅ„sk
"NQN" -> 34, //Neuquen
"TAO" -> 34, //Qingdao
"MBJ" -> 32, //Montego Bay
"IGU" -> 32, //Foz Do IguaÃ§u
"BOS" -> 32,
"FLL" -> 31, //Miami
"HRG" -> 30, //Hurghada
"SSH" -> 30, //Sharm el-Sheikh
"CEB" -> 30, //Lapu-Lapu City
"SJU" -> 30, //San Juan
"MPH" -> 30, //Malay
"PSA" -> 30, //Pisa
"CFU" -> 30, //Kerkyra Island
"COK" -> 30, //Kochi
"IKA" -> 30, //Tehran
"VKO" -> 30, //Moscow
"THR" -> 30, //Tehran
"CJC" -> 30, //Calama
"ADZ" -> 30, //San Andrés
"CTU" -> 30, //Chengdu
"DCA" -> 30, //Washington
"VLC" -> 30, //Valencia
"RAK" -> 29, //Marrakech
"IGR" -> 29, //Puerto Iguazu
"FOR" -> 29, //Fortaleza
"RNO" -> 29, //Reno
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
"HIJ" -> 25, //Hiroshima
"KMQ" -> 25, //Kumamoto
"BWI" -> 25, //Washington
"LIN" -> 25, //Milan Italian Alps
"KGS" -> 24, //Kos Island
"ACE" -> 24, //Lanzarote Island
"NAS" -> 24, //Nassau
"CHQ" -> 24, //Heraklion
"CIA" -> 24, //Ostia Antica Italy
"BWN" -> 24, //Bandar Seri Begawan
"HAM" -> 24, //Hamburg
"AJA" -> 24, //Ajaccio/NapolÃ©on Bonaparte
"BIA" -> 24, //Bastia-Poretta
"NVT" -> 24, //Navegantes
"STI" -> 24, //Santiago
"TPA" -> 24, //Tampa
"YYC" -> 24, //Calgary
"SDQ" -> 24, //Santo Domingo
"JAI" -> 22, //Jaipur
"BRC" -> 22, //San Carlos de Bariloche
"SMR" -> 22, //Santa Marta
"IKT" -> 22, //Irkutsk
"SIP" -> 22, //Simferopol
"TFU" -> 22, //Chengdu
"JMK" -> 21, //Mykonos Island
"SSA" -> 21, //Salvador
"BUF" -> 21, //Buffalo
"CTM" -> 21, //Chetumal
"HTI" -> 21, //Hamilton Island Resort
"MID" -> 21, //Mérida
"IXB" -> 21, //Bagdogra Darjeeling
"DAD" -> 20, //Da Nang
"BCN" -> 20, //Barcelona
"SCL" -> 20, //Santiago
"NCE" -> 20, //Nice
"BER" -> 20, //Berlin
"SAI" -> 20, //Siem Reap
"FUE" -> 20, //Fuerteventura Island
"NAP" -> 20, //Nápoli
"JTR" -> 20, //Santorini Island
"SVO" -> 20, //Moscow
"KRK" -> 20, //Kraków
"LIR" -> 20, //Liberia Costa Rica
"GYD" -> 20, //Baku
"UVF" -> 20, //Vieux Fort
"XIY" -> 20, //Terracotta Army China
"BRI" -> 20, //Bari
"PNQ" -> 20, //Pune
"KNH" -> 20, //Kinmen
"NKG" -> 20, //Nanjing
"RUH" -> 20,
"LIH" -> 20, //Lihue
"FNC" -> 19, //Funchal
"AMM" -> 19, //Amman
"MYR" -> 19, //Myrtle Beach
"AMD" -> 19, //Ahmedabad
"ITO" -> 19, //Hilo
"PBI" -> 19,
"ZTH" -> 18, //Zakynthos Island
"YUL" -> 18, //Montreal
"NBO" -> 18, //Nairobi
"ANU" -> 18, //St. John's
"EFL" -> 18, //Kefallinia Island
"IOS" -> 18, //IlhÃ©us
"RVN" -> 18, //Rovaniemi
"CGB" -> 18, //Cuiabá
"DLC" -> 18, //Dalian
"PHL" -> 18,
"INN" -> 18, //Innsbruck
"MDQ" -> 18,
"KIN" -> 17, //Kingston
"SLL" -> 17, //Salalah
"EYW" -> 17, //Key West
"FTE" -> 17, //El Calafate
"SHJ" -> 17, //Dubai
"IXC" -> 17, //Chandigarh
"FOC" -> 17, //Fuzhou
"HUX" -> 16, //Huatulco
"MAH" -> 16, //Menorca Island
"GCM" -> 16, //Georgetown
"LLA" -> 16, //LuleÃ¥
"PPP" -> 16, //Whitsunday Coast Airport
"YQB" -> 16, //Quebec
"DYG" -> 16,
"BJV" -> 15, //Bodrum
"PEN" -> 15, //Penang
"IAD" -> 15, //Washington
"NLU" -> 15, //Mexico City
"LCA" -> 15, //Larnarca
"TIV" -> 15, //Tivat
"TGZ" -> 15, //Tuxtla Gutiérrez
"SPU" -> 15, //Split
"PLS" -> 15, //Providenciales Turks and Caicos
"JNB" -> 15, //Johannesburg
"ASP" -> 15, //Alice Springs
"ZIA" -> 15, //Moscow
"BLQ" -> 15, //Bologna
"SNA" -> 15, //Santa Ana
"YYT" -> 15, //St John
"XMN" -> 15, //Xiamen
"ISG" -> 15, //Ishigaki JP
"POP" -> 14, //Puerto Plata Dominican Republic
"BOJ" -> 14, //Burgas
"EDI" -> 14, //Edinburgh
"BME" -> 14, //Broome
"OTP" -> 14, //Bucharest
"YHZ" -> 14, //Halifax
"CWB" -> 14, //Curitiba
"VBY" -> 14, //Visby, SE
"LPQ" -> 12, //Luang Phabang
"MCZ" -> 12,
"YLW" -> 12, //Jasper National Park Canada
"NGO" -> 12, //Tokoname
"AGX" -> 12, //Agatti
"BAQ" -> 12, //Barranquilla
"BDS" -> 12, //Brindisi
"FEN" -> 12, //Fernando De Noronha
"KTN" -> 12, //Ketchikan
"TSN" -> 12, //Tianjin
"ATQ" -> 12, //Amritsar
"BSB" -> 12, //Brasília
"LKO" -> 12, //Lucknow
"SHE" -> 12, //Shenyang
"VCP" -> 12, //Campinas
"SBZ" -> 12, //Sibiu
"BZN" -> 11, //Bozeman
"FUK" -> 11, //Fukuoka
"VOG" -> 11, //Volgograd
"GYN" -> 11, //Goiânia
"AMS" -> 10, //Amsterdam
"LAP" -> 10, //La Paz
"MUB" -> 10, //Maun
"PLZ" -> 10, //Addo Elephant National Park South Africa
"STX" -> 10, //Christiansted
"FAT" -> 10, //Yosemite National Park USA
"AYQ" -> 10, //Ayers Rock
"UNA" -> 10, //Transamérica Resort Comandatuba Island
"GRQ" -> 10, //Grenoble French Alps
"FSZ" -> 10, //Fuji-Hakone-Izu National Park Japan
"BJL" -> 10, //Banjul
"FSC" -> 10, //Figari Sud-Corse
"HRB" -> 10, //Harbin
"CSX" -> 10, //Changsha
"ISG" -> 10, //Ishigaki
"SVG" -> 10,
"MTJ" -> 9, //Montrose (Ski resort)
"CHC" -> 9, //Christchurch
"TRN" -> 9, //Turin Italian Alps
"IXZ" -> 9, //Port Blair
"KTA" -> 9, //Blue Mountains National Park Australia
"ZAD" -> 9, //Zemunik (Zadar)
"BTV" -> 9, //Burlington Stowe/Sugarbush Vermont USA
"YYJ" -> 9,
"SAN" -> 9, //San Diego USA
"SZG" -> 8, //Salzburg Austrian Alps
"SZG" -> 8, //Berchtesgaden National Park Germany
"JAC" -> 8, //Jackson
"YXC" -> 8, //Banff National Park Canada
"LMP" -> 8, //Italy
"CLY" -> 8, //Calvi-Sainte-Catherine
"SLZ" -> 8, //São Luís
"THE" -> 8, //Teresina
"GCI" -> 8, //Jersey
"JER" -> 8, //Guernsey
"KTT" -> 8, //Kittilä FI
"RVN" -> 8, //Rovaniemi FI
"GCN" -> 7, //Grand Canyon
"ASE" -> 7, //Aspen
"YDF" -> 7, //Gros Morne National Park Canada
"MUH" -> 7, //El Alamein EG
"SUN" -> 6, //Hailey Sun Valley Idaho USA
"PNL" -> 6, //Italy
"EGE" -> 6, //Vail/Beaver Creek Colorado USA
"YKS" -> 6, //Serbia
"MZG" -> 6, //TW
"ECP" -> 6, //Panama City Beach
"HDS" -> 5, //Kruger National Park South Africa
"FAI" -> 5, //Fairbanks
"IPC" -> 5, //Isla De Pascua
"SGU" -> 5, //Zion National Park
"STS" -> 5,
"ZUH" -> 5, //Zhuhai
"CLQ" -> 5, //Nevado de Colima National Park Mexico
"CCK" -> 5,
"XCH" -> 5,
"NLK" -> 5,
"SUV" -> 5,
"LDH" -> 5,
"YTY" -> 5, //Yangzhou
"GPT" -> 5, //Gulf port
"AO1" -> 5, //Aogashima JP
"ACV" -> 5, //Eureka
"HYA" -> 4, //Cape Cod
"HDN" -> 4, //Hayden Steamboat Springs Colorado USA
"CUK" -> 4, //Belize
"CMF" -> 4, //Chambéry
"CPX" -> 4, //Culebra PR
"VQS" -> 4, //Vieques PR
"HHH" -> 4, //Hilton Head Island
"SLK" -> 4,
"DBB" -> 4, //EG
"LSI" -> 4, //Shetland
"DBB" -> 4, //EG
"KUM" -> 4,
"LSI" -> 4, //Shetland
"CHS" -> 4,
"GMZ" -> 3, //Canary Islands
"ACK" -> 3, //Nantucket
"TVC" -> 3, //Traverse City
"MFR" -> 3, //Crater lake
"OTH" -> 3, //North Bend
"MFR" -> 3,
"HGL" -> 3,
"BRW" -> 2,
 ),
    FINANCIAL_HUB -> Map[String, Int](
"SIN" -> 85, //Singapore
"JFK" -> 80, //New York
"HND" -> 75, //Tokyo
"LHR" -> 70, //London
"FRA" -> 70, //Frankfurt
"MUC" -> 65, //Munich
"YYZ" -> 60, //Toronto
"HKG" -> 60, //Hong Kong
"CDG" -> 60, //Paris
"NRT" -> 60, //
"ICN" -> 58, //Seoul
"PEK" -> 58, //Beijing
"EWR" -> 55, //New York
"DXB" -> 55, //Dubai
"JNB" -> 50, //Johannesburg
"ORD" -> 50, //Chicago
"SZX" -> 46, //Shenzhen
"TPE" -> 45, //Taipei
"STR" -> 45, //Stuttgart
"BRU" -> 45, //Brussels
"AMS" -> 45, //Amsterdam
"PVG" -> 45, //Shanghai
"KUL" -> 44, //Kuala Lumpur
"GVA" -> 44, //Geneva
"DUB" -> 44, //Dublin
"LAX" -> 44, //Los Angeles
"CAN" -> 43, //Guangzhou
"ZRH" -> 42, //Zurich
"AUH" -> 42, //Abu Dhabi
"DFW" -> 42, //Dallas Fort Worth
"LGW" -> 40, //London
"GRU" -> 40, //Sao Paulo
"BER" -> 40, //Berlin
"SYD" -> 39, //Sydney
"SFO" -> 38, //San Francisco
"SCL" -> 37, //Santiago
"MAD" -> 36, //Madrid
"YVR" -> 35, //Vancouver
"LGA" -> 35, //New York
"BOM" -> 35, //Mumbai
"OSL" -> 34, //Oslo
"MEL" -> 34, //Melbourne
"HAM" -> 34, //Hamburg
"DOH" -> 34, //Doha
"CPH" -> 34, //Copenhagen
"ARN" -> 34, //Stockholm
"TLV" -> 33, //Tel Aviv
"KWI" -> 33, //Kuwait City
"KIX" -> 33, //Osaka
"BOS" -> 32, //Boston
"YUL" -> 32, //Montreal
"VIE" -> 32, //Vienna
"ITM" -> 32, //Osaka
"DME" -> 31, //Moscow
"LUX" -> 30, //Luxembourg
"ATL" -> 30, //Atlanta
"SVO" -> 29, //Moscow
"EDI" -> 29, //Edinburgh
"AKL" -> 29, //Auckland
"PKX" -> 29, //Beijing
"PUS" -> 28, //Busan
"MXP" -> 28, //Milan
"IST" -> 28, //Istanbul
"DEL" -> 28, //New Delhi
"DEN" -> 28, //Denver
"EZE" -> 27, //Buenos Aires
"SHA" -> 27, //Shanghai
"CLT" -> 27, //Charlotte
"SEA" -> 27, //Seattle
"BOG" -> 26, //Bogota
"IAH" -> 26, //Houston
"YYC" -> 25, //Calgary
"ORY" -> 25, //Paris
"CPT" -> 25, //Cape Town
"BAH" -> 25, //Bahrain
"MEX" -> 24, //Mexico City
"LOS" -> 24, //Lagos
"GMP" -> 24, //Seoul
"MAN" -> 24, //Manchester
"MIA" -> 23, //Miami
"GIG" -> 22, //Rio de Janeiro
"FCO" -> 22, //Rome
"RUH" -> 21, //Riyadh
"NGO" -> 21, //Nagoya
"LIM" -> 21, //Lima
"TAS" -> 20, //Tashkent
"LIN" -> 20, //Milan
"LCY" -> 20, //London
"IAD" -> 20, //Washington DC
"HAJ" -> 20, //Hanover
"FUK" -> 20, //Fukuoka
"BUD" -> 20, //Budapest
"BKK" -> 20, //Bangkok
"WAW" -> 19, //Warsaw
"PRG" -> 19, //Prague
"JED" -> 19, //Jeddah
"SGN" -> 18, //Ho Chi Minh City
"MSP" -> 18, //Minneapolis
"DUS" -> 18, //Dusseldorf
"CGN" -> 18, //Cologne
"CGK" -> 18, //Jakarta
"BSB" -> 18, //Brasilia
"BLR" -> 18, //Bangalore
"BLQ" -> 18, //Bologna
"BCN" -> 18, //Barcelona
"PHX" -> 18, //Phoenix
"PHL" -> 18, //Philadelphia
"MNL" -> 17, //Manila
"DTW" -> 17, //Detroit
"TLL" -> 16, //Tallinn
"HYD" -> 16, //Hyderabad
"RTM" -> 15, //The Hague
"RMO" -> 15, //Chisinau
"HEL" -> 15, //Helsinki
"CGH" -> 15, //Sao Paulo
"MDW" -> 15, //Chicago
"TRN" -> 14, //Turin
"PER" -> 14, //Perth
"KHH" -> 14, //Kaohsiung
"IKA" -> 14, //Tehran
"DCA" -> 14, //Washington DC
"BNE" -> 14, //Brisbane
"AMD" -> 14, //GIFT City-Gujarat
"SJC" -> 14, //San Francisco
"ALG" -> 14, //Algiers
"TLS" -> 13, //Toulouse
"SLC" -> 13, //Salt Lake City
"ALA" -> 13, //Almaty
"TSN" -> 13, //Tianjin
"DAL" -> 13, //Dallas
"VNO" -> 12, //Vilnius
"TAO" -> 12, //Qingdao
"LEJ" -> 12, //Leipzig
"CBR" -> 12, //Canberra
"LED" -> 11, //St Petersburg
"KUN" -> 11, //Kaunas
"HAN" -> 11, //Hanoi
"DAC" -> 11, //Dhaka
"CMN" -> 11, //Casablanca
"BWI" -> 11, //Baltimore
"FLL" -> 11, //
"TSA" -> 10, //Taipei
"SDU" -> 10, //Rio de Janeiro
"RIX" -> 10, //Riga
"MAA" -> 10, //Chennai
"DMK" -> 10, //Bangkok
"BNA" -> 10, //Nashville
"AEP" -> 10, //Buenos Aires
"YQB" -> 10, //Quebec City
"YEG" -> 10, //Edmonton
"SAN" -> 10, //San Diego
"TFU" -> 9, //Chengdu
"PTY" -> 9, //Panama City
"NBO" -> 9, //Nairobi
"MLA" -> 9, //Malta
"MDE" -> 9, //Medellin
"BGO" -> 9, //Bergen
"BEG" -> 9, //Belgrade
"AUS" -> 9, //Austin
"ATH" -> 9, //Athens
"TPA" -> 9, //Tampa
"OTP" -> 8, //Bucharest
"NLU" -> 8, //Mexico City
"NKG" -> 8, //Nanjing
"NCL" -> 8, //Newcastle
"LYS" -> 8, //Grenoble
"LCA" -> 8, //Nicosia
"KHI" -> 8, //Karachi
"GOT" -> 8, //Gothenburg
"DUR" -> 8, //Durban
"CTU" -> 8, //Chengdu
"CKG" -> 8, //Jakarta
"CCU" -> 8, //Kolkata
"BTS" -> 8, //Bratislava
"BDA" -> 8, //Bermuda
"ADL" -> 8, //Adelaide
"POS" -> 8, //Port of Spain
"PNQ" -> 7, //Pune
"PDX" -> 7, //Portland
"NQZ" -> 7, //Nur-Sultan
"GYD" -> 7, //Baku
"DLC" -> 7, //Dalian
"HGH" -> 7, //Hangzhou
"ANC" -> 7, //Anchorage
"STL" -> 7, //
"OAK" -> 6, //San Francisco
"PIT" -> 6, //Pittsburgh
"SOF" -> 6, //Sofia
"NAS" -> 6, //Nassau
"AAL" -> 6, //Aalborg
"SMF" -> 6, //Sacramento
"XIY" -> 5, //Xi'an
"WLG" -> 5, //Wellington
"TRD" -> 5, //Trondheim
"MTY" -> 5, //Monterrey
"KGL" -> 5, //Kigali
"IOM" -> 5, //Castletown
"GLA" -> 5, //Glasgow
"DTM" -> 5, //Dortmund
"CLO" -> 5, //Cali
"BHX" -> 5, //Birmingham
"AHB" -> 5, //
"ADD" -> 5, //Addis Ababa
"BGI" -> 4, //Bridgetown
"KEF" -> 4, //Reykjavik
"DMM" -> 4, //
"YTZ" -> 4, //Toronto
"YXE" -> 4, //Saskatoon
"HOU" -> 4, //Houston
"SNA" -> 4, //
"ESB" -> 4, //Ankara
"ABV" -> 3, //
"MBA" -> 3, //Mombasa
"JNU" -> 3, //Juneau
"MCI" -> 3, //
"RDU" -> 2, //
"CLE" -> 2, //
"JRB" -> 2, //NYC Heliport
"JRA" -> 2, //NYC Heliport
"CB7" -> 2, //Vancouver Heliport
"BUR" -> 2, //
"ABQ" -> 2, //
"MKE" -> 2, //
"JAX" -> 2, //
"BOI" -> 2, //
"EBL" -> 2, //Arbil
"SW1" -> 1, //Rio Heliport
"HHP" -> 1, //Hong Kong
"GR0" -> 1, //Sao Paulo
"MB1" -> 1, //AQ
"SP1" -> 1, //AQ
"EG1" -> 1, //AQ
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
