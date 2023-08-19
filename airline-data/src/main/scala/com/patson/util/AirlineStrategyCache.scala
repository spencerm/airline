package com.patson.util

import java.util.concurrent.TimeUnit
import com.patson.data.AirlineStrategySource
import com.patson.model.AirlineStrategy

import java.util.stream.{Collectors, StreamSupport}

/**
  * probably airline strategy should have extended AirlineCache / Source, but I don't know what I'm doing :) 
  */
object AirlineStrategyCache {
  import scala.jdk.CollectionConverters._
  import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}

  val simpleCache: LoadingCache[Int, AirlineStrategy] = CacheBuilder.newBuilder.maximumSize(1000).expireAfterAccess(10, TimeUnit.MINUTES).build(new SimpleLoader())

  def getAirlineStrategy(airlineId : Int) : AirlineStrategy = {
      simpleCache.get(airlineId)
  }

  def invalidateAirlineStrategy(airlineId : Int) = {
    simpleCache.invalidate(airlineId)
  }

  def invalidateAll() = {
    simpleCache.invalidateAll()
  }

  class SimpleLoader extends CacheLoader[Int, AirlineStrategy] {
    override def load(airlineId: Int) = {
      AirlineStrategySource.loadAirlineStrategy(airlineId)
    }
  }


}



