package com.greenbank.api.database

import org.bukkit.entity.Player
import org.flameyosflow.greenbank.GreenBankMain
import com.greenbank.api.database.mongodb.MongoDBDatabaseConnect
import com.greenbank.api.database.sql.sqlite.SQLiteDatabaseConnect
import java.util.UUID
import java.util.concurrent.CompletableFuture

private class Database(private val greenBank: GreenBankMain) {
    fun createConnection(connector: DatabaseKnower): Any {
        return when (connector) {
            is MongoDBDatabaseConnect -> connector.instance.createConnection()
            is SQLiteDatabaseConnect -> connector.instance.createConnectionSQL()
            else -> greenBank.fireCriticalError("Unexpected database type", "This is likely the developer's fault, please take this to support.")
        }
    }

    fun getConnection(connector: DatabaseKnower): Any {
        return when (connector) {
            is SQLiteDatabaseConnect -> connector.instance.connection
            is MongoDBDatabaseConnect -> connector.instance.connection
            else -> greenBank.fireCriticalError("Unexpected database type", "This is likely the developer's fault, please take this to support.")
        }
    }

    fun getBalance(connector: DatabaseKnower, uuid: UUID): Double {
        return when (connector) {
            is SQLiteDatabaseConnect ->  connector.instance.getBalance(uuid)
            is MongoDBDatabaseConnect -> connector.instance.getBalance(uuid)
            else -> 0.0
        }
    }

    fun setBalance(connector: DatabaseKnower, uuid: UUID, balance: Double): Any {
        return when (connector) {
            is SQLiteDatabaseConnect -> connector.setBalance(balance, uuid)
            is MongoDBDatabaseConnect -> connector.setBalance(balance, uuid)
            else -> greenBank.fireCriticalError("Unexpected database type", "This is likely the developer's fault, please take this to support.")
        }
    }

    fun addNewPlayer(connector: DatabaseKnower, player: Player) {
        return when (connector) {
            is SQLiteDatabaseConnect -> connector.instance.addNewPlayer(player)
            is MongoDBDatabaseConnect -> connector.instance.addNewPlayer(player)
            else -> greenBank.logger.severe("Cannot add new player, this is likely the developer's fault, please take this to support.")
        }
    }

    fun playerNotNull(connector: DatabaseKnower, uuid: UUID): Boolean {
        return when (connector) {
            is SQLiteDatabaseConnect -> connector.instance.playerNotNull(uuid)
            is MongoDBDatabaseConnect -> connector.instance.playerNotNull(uuid)
            else -> greenBank.fireCriticalError("Unexpected database type", "This is likely the developer's fault, please take this to support.")
        }
    }

    fun closeConnection(connector: DatabaseKnower) {
        return when (connector) {
            is SQLiteDatabaseConnect -> connector.instance.closeConnection()
            is MongoDBDatabaseConnect -> connector.instance.closeConnection()
            else -> greenBank.logger.severe("Unexpected database type, this is likely the developer's fault, please take this to support.")
        }
    }
}

class DatabaseHandler(private val greenBank: GreenBankMain) {
    private val database = Database(greenBank)
    private val databaseType = greenBank.databaseSettings.databaseType;

    val instance = this
    fun createConnection(): Any {
        return if (databaseType.equals("SQLite", ignoreCase = true))
            database.createConnection(SQLiteDatabaseConnect(greenBank))
        else if (databaseType.equals("MongoDB", ignoreCase = true))
            database.createConnection(MongoDBDatabaseConnect(greenBank))
        else
            greenBank.fireCriticalError("Unexpected database type found in db.yml", "Please check db.yml database-type value.")
    }

    fun getBalance(uuid: UUID): Double {
        if (databaseType.equals("SQLite", ignoreCase = true))
            return database.getBalance(SQLiteDatabaseConnect(greenBank), uuid)
        else if (databaseType.equals("MongoDB", ignoreCase = true))
            return database.getBalance(MongoDBDatabaseConnect(greenBank), uuid)
        else
            greenBank.fireCriticalError("Unexpected database type found in db.yml", "Please check db.yml database-type value.")
        return 0.0
    }

    fun setBalance(uuid: UUID, balance: Double): Any {
        if (databaseType.equals("SQLite", ignoreCase = true))
            return database.setBalance(SQLiteDatabaseConnect(greenBank), uuid, balance)
        else if (databaseType.equals("MongoDB", ignoreCase = true))
            return database.getBalance(MongoDBDatabaseConnect(greenBank), uuid)
        else
            greenBank.fireCriticalError("Unexpected database type found in db.yml", "Please check db.yml database-type value.")
        return 0.0
    }

    fun addNewPlayer(player: Player): Boolean {
        if (databaseType.equals("SQLite", ignoreCase = true))
            database.addNewPlayer(SQLiteDatabaseConnect(greenBank), player)
        else if (databaseType.equals("MongoDB", ignoreCase = true))
            database.addNewPlayer(MongoDBDatabaseConnect(greenBank), player)
        else
            greenBank.fireCriticalError("Unexpected database type found in db.yml", "Please check db.yml database-type value.")
        return true
    }

    fun getConnection(): Any {
        if (databaseType.equals("SQLite", ignoreCase = true))
            return database.getConnection(SQLiteDatabaseConnect(greenBank))
        else if (databaseType.equals("MongoDB", ignoreCase = true))
            return database.getConnection(MongoDBDatabaseConnect(greenBank))
        else
            greenBank.fireCriticalError("Unexpected database type found in db.yml", "Please check db.yml database-type value.")
        return 0.0
    }

    fun playerNotNull(uuid: UUID): Boolean {
        return if (databaseType.equals("SQLite", ignoreCase = true))
            database.playerNotNull(SQLiteDatabaseConnect(greenBank), uuid)
        else if (databaseType.equals("MongoDB", ignoreCase = true))
            database.playerNotNull(MongoDBDatabaseConnect(greenBank), uuid)
        else
            greenBank.fireCriticalError("Unexpected database type found in db.yml", "Please check db.yml database-type value.")
    }

    fun closeConnection() {
        if (databaseType.equals("SQLite", ignoreCase = true))
            return database.closeConnection(SQLiteDatabaseConnect(greenBank))
        else if (databaseType.equals("MongoDB", ignoreCase = true))
            return database.closeConnection(MongoDBDatabaseConnect(greenBank))
        else
            greenBank.fireCriticalError("Unexpected database type found in db.yml", "Please check db.yml database-type value.")
    }
}
