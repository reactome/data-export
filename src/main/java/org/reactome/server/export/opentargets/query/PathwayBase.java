package org.reactome.server.export.opentargets.query;

import org.neo4j.driver.Value;

public class PathwayBase {
    private String stId;
    private String displayName;

    public PathwayBase() { }

    public PathwayBase(String stId, String displayName) {
        this.stId = stId;
        this.displayName = displayName;
    }

    public static PathwayBase build(Value r) {
        return new PathwayBase(r.get("stId").asString(null), r.get("displayName").asString(null));
    }

    public String getStId() {
        return stId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
