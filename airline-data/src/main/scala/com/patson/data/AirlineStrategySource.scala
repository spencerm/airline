package com.patson.data

import scala.collection.mutable.ListBuffer
import com.patson.data.Constants._
import com.patson.model.AirlineStrategy
import com.patson.data.Meta

object AirlineStrategySource {

    //case class AirlineStrategy(airlineId : Int, hasEconomy : Boolean, hasBusiness : Boolean, hasFirst : Boolean, hasVacationPackages : Boolean, hasFlexibleTicketing : Boolean )

    def saveAirlineStrategy(airlineId : Int, strategy: AirlineStrategy ) = {
        val connection = Meta.getConnection()
        val preparedStatement = connection.prepareStatement("REPLACE INTO " + AIRLINE_OPERATIONS_STRATEGY + "(airline, economy, business, first, vacation_packages, flexible_ticketing) VALUES(?,?,?,?,?,?)")
        try {
            preparedStatement.setInt(1, strategy.airlineId)
            preparedStatement.setBoolean(2, strategy.hasEconomy)
            preparedStatement.setBoolean(3, strategy.hasBusiness)
            preparedStatement.setBoolean(4, strategy.hasFirst)
            preparedStatement.setBoolean(5, strategy.hasVacationPackages)
            preparedStatement.setBoolean(6, strategy.hasFlexibleTicketing)
            preparedStatement.executeUpdate()
            println("saving airline strategy " + airlineId)
        } finally {
            preparedStatement.close()
            connection.close()
        }
    }

    def loadAirlineStrategy(airlineId : Int = 0) : AirlineStrategy = {
        val connection = Meta.getConnection()
        try {
            val statement = connection.prepareStatement(s"SELECT * FROM $AIRLINE_OPERATIONS_STRATEGY WHERE airline = $airlineId")
            val resultSet = statement.executeQuery()
            val result : AirlineStrategy = if (resultSet.next()) {
                val airlineId = resultSet.getInt("airline")
                val hasEconomy = resultSet.getBoolean("economy")
                val hasBusiness = resultSet.getBoolean("business")
                val hasFirst = resultSet.getBoolean("first")
                val hasVacationPackages = resultSet.getBoolean("vacation_packages")
                val hasFlexibleTicketing = resultSet.getBoolean("flexible_ticketing")
                AirlineStrategy(airlineId, hasEconomy, hasBusiness, hasFirst, hasVacationPackages, hasFlexibleTicketing)
            } else {
                AirlineStrategy(0, true, false, false, false, false)
            }
            resultSet.close()
            statement.close()    
            result
            } finally {
                connection.close()
            }
    }

}