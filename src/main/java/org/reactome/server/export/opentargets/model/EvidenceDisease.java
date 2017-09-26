package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.reactome.server.export.opentargets.query.ReactomeEvidence;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvidenceDisease {

    //Converted to a single string for version 1.2.4 (21/02/2017)
    private String identifier;

    //Converted to a single string for version 1.2.5 (04/04/2017)
    private String name;

    public EvidenceDisease(ReactomeEvidence reactomeEvidence) {
        identifier = reactomeEvidence.getDiseaseIdentifier();
        name = reactomeEvidence.getDisease();
    }

    @JsonProperty(value = "id", required = true)
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty(value = "name", required = true)
    public String getName() {
        return name;
    }
}
