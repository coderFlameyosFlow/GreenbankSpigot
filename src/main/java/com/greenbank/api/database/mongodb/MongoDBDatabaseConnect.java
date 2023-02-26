package com.greenbank.api.database.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import lombok.Getter;

import com.greenbank.api.database.DatabaseKnower;
import org.flameyosflow.greenbank.GreenBankMain;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MongoDBDatabaseConnect extends MongoDatabaseConnect<MongoClient> implements DatabaseKnower {
    private final GreenBankMain greenBank;
    @Getter
    private final MongoDBDatabaseConnect instance;

    public MongoDBDatabaseConnect(GreenBankMain greenBank) {
        this.greenBank = greenBank;
        this.instance = this;
    }

    @Override
    public MongoClient createConnection() {
        return MongoClients.create(new ConnectionString(greenBank.getDatabaseSettings().getMongoConnectionUri()));
    }

    @Override
    public void closeConnection() {
        createConnection().close();
    }

    @Override
    public UpdateResult setBalance(double balance, UUID uuid) {
        return setDocumentPlayerData(balance, "balance", uuid.toString());
    }

    @Override
    public double getBalance(UUID uuid) {
        DocumentGetterThread documentGetterThread = new DocumentGetterThread(uuid.toString());
        new Thread(documentGetterThread).start();
        return documentGetterThread.getBalance();
    }

    @Override
    public void addNewPlayer(Player player) {
        new Thread(() ->
                getConnection().insertOne(new Document()
                    .append("uuid", player.getUniqueId().toString()) // Add unique identifier
                    .append("balance", greenBank.getSettings().getDefaultStartingBalance()) // Add balance
                    .append("bank-account", greenBank.getSettings().getDefaultStartingBankBalance()) // Add bank account
                    .append("has-infinite-money", false)))
                .start();
    }

    @Override
    public boolean playerNotNull(UUID uuid) {
        return getConnection().find(Filters.eq("uuid", uuid.toString())).first() != null;
    }

    @Override
    public List<String> multipleBalanceTopHelper(int limit, UUID uuid) {
        return null;
    }

    @Override
    @NotNull
    public Object getDocumentPlayerData(@NotNull String uuid) {
        DocumentGetterThread documentGetterThread = new DocumentGetterThread(uuid);
        new Thread(documentGetterThread).start();
        return documentGetterThread.getDocument();
    }

    @Override
    public UpdateResult setDocumentPlayerData(Object value, @NotNull String identifier, @NotNull String uuid) {
        DocumentSetterThread documentSetterThread = new DocumentSetterThread(value, identifier, uuid);
        new Thread(documentSetterThread).start();
        return documentSetterThread.getUpdateResult();
    }

    @Override
    public MongoCollection<Document> getConnection() {
        MongoDatabase mongoDatabase = createConnection().getDatabase(greenBank.getDatabaseSettings().getMongoDatabase());
        return mongoDatabase.getCollection(greenBank.getDatabaseSettings().getMongoCollection());
    }

    private static class DocumentSetterThread implements Runnable {
        private final String uuid;
        private UpdateResult updateResult;
        private final MongoDBDatabaseConnect mongodb = new MongoDBDatabaseConnect(new GreenBankMain().getInstance());
        private final Object value;
        private final String identifier;

        DocumentSetterThread(Object value, @NotNull String identifier, @NotNull String uuid) {
            this.value = value;
            this.uuid = uuid;
            this.identifier = identifier;
        }

        @Override
        public void run() {
            updateResult = mongodb.getConnection().updateOne(Filters.eq("uuid", uuid), Updates.set(identifier, value), new UpdateOptions().upsert(true));
        }

        public UpdateResult getUpdateResult() {
            return updateResult;
        }
    }

    private static class DocumentGetterThread implements Runnable {
        private Document document;
        private final MongoDBDatabaseConnect mongodb = new MongoDBDatabaseConnect(new GreenBankMain().getInstance());
        private final String uuid;

        DocumentGetterThread(String uuid) {
            this.uuid = uuid;
        }

        @Override
        public void run() {
            document = Objects.requireNonNull(mongodb.getConnection().find(Filters.eq("uuid", uuid)).first());
        }

        public Document getDocument() {
            return document;
        }

        public double getBalance() {
            return document.getDouble("balance");
        }
    }
}
