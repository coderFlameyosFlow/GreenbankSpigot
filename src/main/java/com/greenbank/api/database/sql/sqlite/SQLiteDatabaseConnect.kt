package com.greenbank.api.database.sql.sqlite

import com.zaxxer.hikari.HikariConfig

import org.bukkit.entity.Player

import org.flameyosflow.greenbank.GreenBankMain
import com.greenbank.api.database.DatabaseKnower
import com.greenbank.api.database.sql.SQLConnector

import java.sql.SQLException
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID


@Suppress("SENSELESS_COMPARISON")
class SQLiteDatabaseConnect(private val greenBank: GreenBankMain) : SQLConnector(greenBank), DatabaseKnower {
    val instance = this

    /**
     * Creates a new config used to create the actual connection.
     *
     * @return the hikariConfig variable.
     */
    override fun createConfig(): HikariConfig {
        val config = HikariConfig()
        config.maximumPoolSize = greenBank.databaseSettings.maximumPoolSize
        config.poolName = "SQLite GreenBank Pool"
        config.jdbcUrl = connectionUrl
        config.dataSourceClassName = "org.sqlite.SQLiteDataSource"
        config.driverClassName = "org.sqlite.JDBC"
        return config
    }

    override val connectionUrl: String = "jdbc:sqlite:plugins/Greenbank/database.db"

    override fun closeConnection() = if (connection != null && !connection.isClosed) try { connection.close() } catch (exception: SQLException) { exception.printStackTrace() } else greenBank.logger.info("Already closed or not existent.")

    override fun getBalance(uuid: UUID): Double {
        val balanceThread = BalanceThread()
        Thread(balanceThread).start()
        return balanceThread.getBalance(uuid)
    }

    override fun addNewPlayer(player: Player) {
        Thread {
            connection.prepareStatement("INSERT INTO account (uuid, balance, bankbalance, hasinfinitemoney) VALUES (?, ?, ?, ?);").use {
                it.setString(1, player.uniqueId.toString())
                it.setDouble(2, greenBank.settings.defaultStartingBalance)
                it.setDouble(3, greenBank.settings.defaultStartingBankBalance)
                it.setBoolean(4, false)
                it.executeUpdate()
            }
        }.start()
    }

    override fun playerNotNull(uuid: UUID): Boolean {
        val playerThread = NotNullPlayerThread()
        Thread(playerThread).start()
        return playerThread.getPlayerNotNull(uuid)
    }

    override fun multipleBalanceTopHelper(limit: Int, uuid: UUID): MutableList<String>? {
        // implementation waiting
        return null
    }

    override fun setBalance(balance: Double, uuid: UUID) {
        Thread {
            connection.prepareStatement("INSERT OR REPLACE INTO account (balance) VALUES (?) WHERE uuid = '?';").use {
                it.setDouble(1, balance)
                it.setString(2, uuid.toString())
                it.executeUpdate()
            }
        }.start()
    }
}

private class NotNullPlayerThread : Runnable {
    private val greenBank: GreenBankMain = GreenBankMain().instance
    private val sqlite = SQLiteDatabaseConnect(greenBank).instance
    private lateinit var statement: PreparedStatement
    private lateinit var resultSet: ResultSet
    private var uuid: UUID? = null

    override fun run() {
        statement = sqlite.connection.prepareStatement("SELECT uuid FROM account WHERE uuid='$uuid'")
        statement.setString(1, uuid.toString())
        resultSet = statement.executeQuery()
    }

    fun getPlayerNotNull(uuid: UUID): Boolean {
        this.uuid = uuid
        statement.use { _ ->
            resultSet.use {
                return resultSet.getString("uuid") != null
            }
        }
    }
}

private class BalanceThread : Runnable {
    private val greenBank: GreenBankMain = GreenBankMain().instance
    private val sqlite = SQLiteDatabaseConnect(greenBank).instance
    private lateinit var statement: PreparedStatement
    private lateinit var resultSet: ResultSet
    private var uuid: UUID? = null

    override fun run() {
        statement = sqlite.connection.prepareStatement("select balance from account where uuid='?'")
        statement.setString(1, uuid.toString())
        resultSet = statement.executeQuery()
    }

    fun getBalance(uuid: UUID): Double {
        this.uuid = uuid
        statement.use { _ ->
            resultSet.use {
                return when {
                    it.next() -> it.getDouble("balance")
                    else -> 0.0
                }
            }
        }
    }
}