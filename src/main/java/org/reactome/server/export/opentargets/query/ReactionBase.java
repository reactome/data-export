package org.reactome.server.export.opentargets.query;

import org.neo4j.driver.Value;

public class ReactionBase {
    private String stId;
    private String displayName;

    public ReactionBase() { }

    public ReactionBase(String stId, String displayName) {
        this.stId = stId;
        this.displayName = displayName;
    }

    public String getStId() {
        return stId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReactionBase build(Value r) {
        return new ReactionBase(r.get("stId").asString(null), r.get("displayName").asString(null));
    }
}
