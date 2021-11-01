package org.reactome.server.export.tasks.result;

import org.neo4j.driver.Value;

public class Participant {

    private String dbName;
    private String id;

    public Participant(String dbName, String id) {
        this.dbName = dbName;
        this.id = id;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = DatabaseNameMapper.getStandardName(dbName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return dbName + ':' + id;
    }

    public static Participant build(Value value) {
        return new Participant(value.get("dbName").asString(null), value.get("id").asString(null));
    }
}
