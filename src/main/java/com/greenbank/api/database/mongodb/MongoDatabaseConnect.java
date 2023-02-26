package com.greenbank.api.database.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import com.greenbank.api.database.DatabaseConnector;

public abstract class MongoDatabaseConnect<T> implements DatabaseConnector<T> {
    public abstract Object getDocumentPlayerData(String uuid);
    public abstract UpdateResult setDocumentPlayerData(Object value, String identifier, String uuid);
    @Override public abstract MongoCollection<Document> getConnection();
}
