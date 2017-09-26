package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Mutation {

    @JsonProperty(value = "preferred_name", required = true)
    public String preferredName;

    @JsonProperty(value = "functional_consequence", required = true)
    public String functionalConsequence = "http://purl.obolibrary.org/obo/SO_0001059";

    public Mutation(String preferredName) {
        this.preferredName = preferredName;
    }
}
