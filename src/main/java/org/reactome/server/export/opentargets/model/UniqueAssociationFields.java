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

    @JsonProperty(value = "biological_objects", required = true)
    public String biologicalObjects;

    @JsonProperty(value = "biological_subjects", required = true)
    public String biologicalSubjects;

    @JsonProperty(value = "reaction_id", required = true)
    public String reaction;

    @JsonProperty(required = true)
    public String mutations = null;

    public UniqueAssociationFields(ReactomeEvidence reactomeEvidence) {
        this.biologicalObjects = reactomeEvidence.getDiseaseIdentifier();
        this.biologicalSubjects = "http://www.identifier.org/" + reactomeEvidence.getResource().toLowerCase() + "/" + reactomeEvidence.getIdentifier();

        this.reaction = "http://reactome.org/PathwayBrowser/#/" + reactomeEvidence.getReaction();

        if(reactomeEvidence.getMutations()!=null && !reactomeEvidence.getMutations().isEmpty()) {
            this.mutations = "[" + StringUtils.join(reactomeEvidence.getMutations(), ";") + "]";
        }
    }
}
