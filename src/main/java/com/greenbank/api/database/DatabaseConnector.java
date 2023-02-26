package com.greenbank.api.database;

import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


/**
 * This class is perfectly setup to work with HikariCP,
 * because we expect people to want SQL.
 *
 * @author FlameyosFlow
 */
public interface DatabaseConnector<T> {

    /**
     * Creates a new connection to the database
     *
     * @return A new connection to the database using the settings provided
     */
    T createConnection();

    /**
     * Creates a new connection to the database
     *
     * @return The connection to the database.
     */
    Object getConnection() throws SQLException;

    void closeConnection();

    Object setBalance(double balance, UUID uuid);

    double getBalance(UUID uuid);

    void addNewPlayer(Player player);

    boolean playerNotNull(UUID uuid);

    List<String> multipleBalanceTopHelper(int limit, UUID uuid);
}
