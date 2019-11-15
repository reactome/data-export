package org.reactome.server.export.gmt.model;

import java.util.Set;

public class GmtPathway {

    private static final String URI_PREFIX = "https://reactome.org/content/detail/";

    private String stId;

    private String name;

    private Set<String> identifiers;

    public void setStId(String stId) {
        this.stId = stId;
    }

    public void setName(String name) {
        this.name = name.trim().replaceAll(" +", "_").replaceAll("[^a-zA-Z0-9_]", "").toUpperCase();
    }

    public String getUri(){
        return URI_PREFIX + stId;
    }

    @SuppressWarnings("unused")
    public void setIdentifiers(Set<String> identifiers) {
        this.identifiers = identifiers;
    }

    public String getTabularFormat() {
        String abbr = stId.split("-")[1]; //R-HSA-XXXXXX -> HSA
        return String.format("REACTOME_%s_%s\t%s\t%s", abbr, name, getUri(), identifiers != null ? String.join("\t", identifiers) : "");
    }

    @Override
    public String toString() {
        return "GmtPathway{" +
                "stId='" + stId + '\'' +
                ", name='" + name + '\'' +
                ", uri='" + getUri() + '\'' +
                ", #identifiers=" + (identifiers == null ? 0 : identifiers.size()) +
                '}';
    }
}
