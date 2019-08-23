package org.reactome.server.export.opentargets.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.reactome.server.export.opentargets.query.ReactomeEvidence;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UniqueAssociationFields {

    @JsonProperty(value = "target_id", required = true)
    public String target_id;

    @JsonProperty(value = "reaction_id", required = true)
    public String reaction;

    @JsonProperty(value = "disease_id", required = true)
    public String disease;

    @JsonProperty(value = "reaction_activity", required = true)
    public String activity;

    @JsonProperty(required = true)
    public String mutations = null;

    public UniqueAssociationFields(ReactomeEvidence reactomeEvidence) {
        this.target_id = "http://www.identifier.org/" + reactomeEvidence.getResource().toLowerCase() + "/" + reactomeEvidence.getIdentifier();

        this.reaction = "http://reactome.org/PathwayBrowser/#/" + reactomeEvidence.getReaction();
        this.disease = reactomeEvidence.getDiseaseIdentifier();
        this.activity = reactomeEvidence.getActivity();

        reactomeEvidence.getActivity();

        if(reactomeEvidence.getMutations()!=null && !reactomeEvidence.getMutations().isEmpty()) {
            this.mutations = "[" + StringUtils.join(reactomeEvidence.getMutations(), ";") + "]";
        }
    }
}
