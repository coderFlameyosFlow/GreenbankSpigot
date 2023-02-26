package com.greenbank.api.database.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

import org.flameyosflow.greenbank.GreenBankMain

import com.greenbank.api.database.DatabaseConnector
import java.io.File
import java.sql.Connection

import java.sql.SQLException

abstract class SQLConnector(private val greenBank: GreenBankMain) : DatabaseConnector<HikariDataSource> {
    private var dataSource: HikariDataSource? = null

    /**
     * Creates a new config used to create the actual connection.
     *
     * @return the config.
     */
    abstract fun createConfig(): HikariConfig?
    override fun createConnection(): HikariDataSource {
        // if dataSource already exists, do nothing and just return the dataSource.
        if (dataSource != null) return dataSource as HikariDataSource

        // This code is executed if the dataSource is actually null, and WE DO NOT WANT THAT!
        val databaseFile = File(greenBank.dataFolder, "accounts.db")
        if (!databaseFile.exists()) databaseFile.mkdirs()
        dataSource = try {
            HikariDataSource(createConfig())
        } catch (e: SQLException) {
            greenBank.logger.severe("Could not connect to the database! " + e.message)
            e.printStackTrace()
            null
        }
        return dataSource!!
    }

    /**
     * Creates a new connection to the database
     *
     * @return The connection to the database.
     */
    override fun getConnection(): Connection = createConnection().connection

    fun createConnectionSQL(): HikariDataSource {
        val returnedConnection = createConnection()
        val connection = this.connection
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS account (uuid varchar(36) NOT NULL, balance double NOT NULL, hasInfiniteMoney boolean DEFAULT FALSE )").use {
            it.executeUpdate()
        }
        return returnedConnection
    }

    abstract val connectionUrl: String?
}