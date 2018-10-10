package org.reactome.server.export.tasks.result;

public class Participant {

    private String dbName;
    private String id;

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
}
